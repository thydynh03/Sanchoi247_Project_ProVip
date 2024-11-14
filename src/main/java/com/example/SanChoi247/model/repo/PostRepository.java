package com.example.SanChoi247.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.SanChoi247.model.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.comments WHERE p.authorId = :userId")
    List<Post> findByAuthorId(@Param("userId") Long userId);

}
