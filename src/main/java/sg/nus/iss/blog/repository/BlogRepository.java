package sg.nus.iss.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.nus.iss.blog.model.Blog;

public interface BlogRepository extends JpaRepository<Blog, Integer>{
    
}
