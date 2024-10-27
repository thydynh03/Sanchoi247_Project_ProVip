package com.example.SanChoi247.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SanChoi247.model.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
