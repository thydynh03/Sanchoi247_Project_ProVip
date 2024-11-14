package com.example.SanChoi247.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.SanChoi247.model.entity.Comment;
import com.example.SanChoi247.model.entity.Post;
import com.example.SanChoi247.model.repo.CommentRepository;
import com.example.SanChoi247.model.repo.PostRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    // Lấy danh sách bài viết mới nhất lên đầu
    public List<Post> getAllPosts() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public void delete(Long postId) {
        postRepository.deleteById(postId);
    }

    // Comment methods
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPost(Post post) {
        return commentRepository.findByPost(post);
    }
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }
    
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByAuthorId(userId);
    }
    
    
}
