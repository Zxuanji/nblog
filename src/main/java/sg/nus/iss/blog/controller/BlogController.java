package sg.nus.iss.blog.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import jakarta.servlet.http.HttpSession;
import sg.nus.iss.blog.model.Blog;
import sg.nus.iss.blog.model.BlogStatusEnum;
import sg.nus.iss.blog.model.BlogUser;
import sg.nus.iss.blog.model.Comment;
import sg.nus.iss.blog.service.BlogService;
import sg.nus.iss.blog.service.CommentService;
import sg.nus.iss.blog.service.UserService;

@Controller
@RequestMapping("/home")
public class BlogController {
    @Autowired
    private BlogService blogService;

    @Autowired 
    private UserService userService;

    @Autowired
    private CommentService commentService;

    // 首页
    @GetMapping("/list")
    public String recommendBlog(Model model, HttpSession sessionObj){
        List<Blog> allPostedBlogs = blogService.findAllPostedBlog();
        BlogUser activeUser = (BlogUser) sessionObj.getAttribute("activeUser");
        
        model.addAttribute("allPostedBlogs", allPostedBlogs);
        model.addAttribute("activeUser", activeUser);
        return "home-page";
    }

    // 点击写博客，呈现博客界面
    @GetMapping("/create")
    public String createBlog(Model model, HttpSession session){
        BlogUser user = (BlogUser) session.getAttribute("activeUser");
        model.addAttribute("blog", new Blog());
        model.addAttribute("activeUser", user);
        return "blog-create";
    }

    // 用户编写并上传blog
    @PostMapping("/create")
    public String postBlog(@ModelAttribute("blogPost") Blog blog,HttpSession session, Model model){
        blog.setReadingTime(calculateReadingTime(0));
        blog.setBlogTime(getPostTime());
        blog.setBlogStatus(BlogStatusEnum.POSTED);
        blog.setBlogLikeCount(countLikes());
        blog.setBlogCommentCount(countComments());

        model.addAttribute("blog", blog);


        BlogUser user = (BlogUser) session.getAttribute("activeUser");
        int userId = user.getUserId();
        BlogUser currentUser = userService.findUserById(userId).get();


        List<Blog> userPostedBlog = currentUser.getPostedBlogs();
        if (userPostedBlog == null) {
            userPostedBlog = new ArrayList<>();
            userPostedBlog.add(blog);
        }
        else {
            userPostedBlog.add(blog);
        }

        // 存储用户和博客之间的对应关系
        blog.setBlogUser(currentUser);
        currentUser.setPostedBlogs(userPostedBlog);

        blogService.saveBlog(blog);
        userService.createUser(currentUser);

        return "redirect:/home/create/history";
    }

    // 用户上传评论
    @PostMapping("/comment")
    public String postComment(@ModelAttribute("commentPost") Comment comment, @RequestParam("blogId") int blogId, HttpSession session){
        // 获取当前user
        BlogUser user = (BlogUser) session.getAttribute("activeUser");
        BlogUser currentuser = userService.findUserById(user.getUserId()).get();
        // 获取当前blog
        Blog blog = blogService.findPostedBlogByBlogId(blogId);
        // 保存comment
        comment.setCommentBlogUser(currentuser);
        comment.setCommentBlog(blog);
        

        // 更新user
        currentuser.getBlogComments().add(comment);

        // // 更新blog 
        blog.getBlogComments().add(comment);

        commentService.saveComment(comment);
        return "redirect:/home/show/" + blogId;
    }

    // 个人发博客历史
    @GetMapping("/create/history")
    public String createPostHistory(HttpSession session, Model model){
        BlogUser user = (BlogUser) session.getAttribute("activeUser");
        
        int userId = user.getUserId();

        List<Blog> postedList = blogService.findPostedBlogById(userId);
        if(postedList == null){
            postedList = new ArrayList<Blog>();
        }

        model.addAttribute("postedList", postedList);
        model.addAttribute("activeUser", user);
        // model.addAttribute("userName", userName);

        return "blog-history";
    }

    // 查看书签记录
    @GetMapping("/bookmark")
    public String viewBookMark(HttpSession session, Model model){
        BlogUser user = (BlogUser) session.getAttribute("activeUser");
        BlogUser currentUser = userService.findUserById(user.getUserId()).get();


        List<String> FavouritedBlogList = new ArrayList<>(Arrays.asList(currentUser.getFavouriteBlogIds().split(",")));

        List<Integer> favouritedBlogListInt = FavouritedBlogList.stream()
            .map(s -> Integer.parseInt(s)) // 将每个字符串转换为整数
            .collect(Collectors.toList()); // 收集结果到一个新的 List

        List<Blog> FavouritedBlog = blogService.findByblogIdIn(favouritedBlogListInt);



        model.addAttribute("FavouritedBlog", FavouritedBlog);
      
        model.addAttribute("activeUser", user);
        return "blog-bookmark";
    }

    // 看其他人的发博客历史
    @GetMapping("/@{userDisplayName}")
    public String createPostHistoryOfSelectedUser(@PathVariable("userDisplayName") String displayName, Model model){
        BlogUser viewUser = userService.findUserByDisplayName(displayName);
        List<Blog> viewUserPosts = blogService.findPostedBlogById(viewUser.getUserId());
        model.addAttribute("postedList", viewUserPosts);

        return "home";
    }

    // 根据用户点击的文章，显示对应的文章
    @GetMapping("/show/{id}")
    public String showBlog(@PathVariable Integer id, Model model, HttpSession session){
        // 找到当前blog
        Blog blog = blogService.findPostedBlogByBlogId(id);
        // 找到当前用户
        BlogUser user = (BlogUser) session.getAttribute("activeUser");
        int userId = user.getUserId();
        //找到当前的user
        BlogUser currentUser = userService.findUserById(userId).get();
        // 评论区部分数据
        List<Object[]> commentsData = commentService.findAllCommentsWithUserAndBlog(id);

        boolean isliked = isLiked(currentUser.getLikedBlogIds(), blog.getBlogId());
        boolean isfavourited = isFavourited(currentUser.getFavouriteBlogIds(), blog.getBlogId());

        model.addAttribute("isliked", isliked);
        model.addAttribute("isfavourited", isfavourited);
        model.addAttribute("likecount", blog.getBlogLikeCount());
        model.addAttribute("showedBlog", blog);
        model.addAttribute("commentsData", commentsData);
        model.addAttribute("commentPost", new Comment());
        model.addAttribute("blogId", id);
        model.addAttribute("activeUser", user);
        
        return "blog-page";
    }

    // 处理用户点赞逻辑
    @PostMapping("/like")
    public String handleLike(@RequestParam("blogId") int blogId, @RequestParam("isliked") boolean isliked, HttpSession session ,Model model) {

        Blog blog = blogService.findPostedBlogByBlogId(blogId);
        BlogUser user = (BlogUser) session.getAttribute("activeUser");
        int userId = user.getUserId();
        //找到当前的user
        BlogUser currentUser = userService.findUserById(userId).get();

        //更新blog的点赞数 和 user的点赞blog list
        if(!isliked){
            blog.setBlogLikeCount(blog.getBlogLikeCount() + 1);
            currentUser.setLikedBlogIds(currentUser.getLikedBlogIds() + "," + blog.getBlogId());
        }
        else{
            blog.setBlogLikeCount(blog.getBlogLikeCount() - 1);
            List<String> likedBlogIdList = new ArrayList<>(Arrays.asList(currentUser.getLikedBlogIds().split(",")));
            likedBlogIdList.remove(String.valueOf(blogId));
            String updatedLikedBlogIds = String.join(",", likedBlogIdList);
            currentUser.setLikedBlogIds(updatedLikedBlogIds);
        }
        
        userService.saveUser(currentUser);
        blogService.saveBlog(blog);

        return "redirect:/home/show/" + blogId;
    }

    // 处理收藏逻辑
    @PostMapping("/favourite")
    public String handleFavourite(@RequestParam("blogId") int blogId, @RequestParam("isfavourited") boolean isfavourited, HttpSession session ,Model model){

        Blog blog = blogService.findPostedBlogByBlogId(blogId);
        BlogUser user = (BlogUser) session.getAttribute("activeUser");
        int userId = user.getUserId();
        //找到当前的user
        BlogUser currentUser = userService.findUserById(userId).get();

        //更新user的收藏 blog list
        if(!isfavourited){
            currentUser.setFavouriteBlogIds(currentUser.getFavouriteBlogIds() + "," + blog.getBlogId());
        }
        else{
            List<String> favouritedBlogIdList = new ArrayList<>(Arrays.asList(currentUser.getFavouriteBlogIds().split(",")));
            favouritedBlogIdList.remove(String.valueOf(blogId));
            String updatedFavouritedBlogIds = String.join(",", favouritedBlogIdList);
            currentUser.setFavouriteBlogIds(updatedFavouritedBlogIds);
        }
        
        userService.saveUser(currentUser);
        blogService.saveBlog(blog);

        return "redirect:/home/show/" + blogId;

    }

    
    // 查看 blog是否已经点赞了
    public boolean isLiked(String likedBlogIds, int blogId){
        String numberAsString = Integer.toString(blogId);

        if(likedBlogIds.contains(numberAsString)){
            return true;
        }
        else{
            return false;
        }
    }

    // 查看 blog是否已经收藏了
    public boolean isFavourited(String favouritedBlogIds, int blogId){
        String numberAsString = Integer.toString(blogId);

        if(favouritedBlogIds.contains(numberAsString)){
            return true;
        }
        else{
            return false;
        }
    }

    // 返回格式化的创建时间
    public String getPostTime(){

        LocalDate createdTime = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String formattedDate = createdTime.format(formatter);

        return formattedDate;
    }

    // 根据文章字数计算阅读时间
    public int calculateReadingTime(int words){
        return words / 200;
    }

    // 计算点赞数量（待完成）
    public int countLikes(){
        return 0;
    }

    // 计算评论数量（待完成）
    public int countComments(){
        return 0;
    }
}
