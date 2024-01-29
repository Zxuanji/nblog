package sg.nus.iss.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.nus.iss.blog.model.Blog;
import sg.nus.iss.blog.repository.BlogRepository;

@Service
@Transactional(readOnly = true)
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogRepository blogRepository;

    @Override
    @Transactional(readOnly = false)
    public Blog saveBlog(Blog blog) {
        return blogRepository.save(blog);
    }
    
}
