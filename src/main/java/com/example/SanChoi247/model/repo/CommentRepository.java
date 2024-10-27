package com.example.SanChoi247.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SanChoi247.model.entity.Comment;
import com.example.SanChoi247.model.entity.Post;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
}
