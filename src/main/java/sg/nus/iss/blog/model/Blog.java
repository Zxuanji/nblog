package sg.nus.iss.blog.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int BlogId;
    private String imageUrl;
    private String title;
    private int readingTime;
    private String contentTitle;
    private String content;
    private LocalDate blogTime;
    private CategoryNameEnum blogCategory;
    private BlogStatusEnum blogStatus;
    private int blogCommentCount;
    private int blogLikeCount;

    @ManyToOne
    private User user;

    public Blog() {
    }

    public Blog(String imageUrl, String title, int readingTime, String contentTitle, String content, LocalDate blogTime,
            CategoryNameEnum blogCategory, BlogStatusEnum blogStatus, int blogCommentCount, int blogLikeCount,
            User user) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.readingTime = readingTime;
        this.contentTitle = contentTitle;
        this.content = content;
        this.blogTime = blogTime;
        this.blogCategory = blogCategory;
        this.blogStatus = blogStatus;
        this.blogCommentCount = blogCommentCount;
        this.blogLikeCount = blogLikeCount;
        this.user = user;
    }

    public int getBlogId() {
        return BlogId;
    }

    public void setBlogId(int blogId) {
        BlogId = blogId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getReadingTime() {
        return readingTime;
    }

    public void setReadingTime(int readingTime) {
        this.readingTime = readingTime;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getBlogTime() {
        return blogTime;
    }

    public void setBlogTime(LocalDate blogTime) {
        this.blogTime = blogTime;
    }

    public CategoryNameEnum getBlogCategory() {
        return blogCategory;
    }

    public void setBlogCategory(CategoryNameEnum blogCategory) {
        this.blogCategory = blogCategory;
    }

    public BlogStatusEnum getBlogStatus() {
        return blogStatus;
    }

    public void setBlogStatus(BlogStatusEnum blogStatus) {
        this.blogStatus = blogStatus;
    }

    public int getBlogCommentCount() {
        return blogCommentCount;
    }

    public void setBlogCommentCount(int blogCommentCount) {
        this.blogCommentCount = blogCommentCount;
    }

    public int getBlogLikeCount() {
        return blogLikeCount;
    }

    public void setBlogLikeCount(int blogLikeCount) {
        this.blogLikeCount = blogLikeCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Blog [BlogId=" + BlogId + ", imageUrl=" + imageUrl + ", title=" + title + ", readingTime=" + readingTime
                + ", contentTitle=" + contentTitle + ", content=" + content + ", blogTime=" + blogTime
                + ", blogCategory=" + blogCategory + ", blogStatus=" + blogStatus + ", blogCommentCount="
                + blogCommentCount + ", blogLikeCount=" + blogLikeCount + ", user=" + user + "]";
    }

    
}
