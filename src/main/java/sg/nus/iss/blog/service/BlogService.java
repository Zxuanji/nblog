package sg.nus.iss.blog.service;

import sg.nus.iss.blog.model.Blog;

public interface BlogService {
    Blog saveBlog(Blog blog);
    List<Blog> findAllPostedBlog()
}
