package com.example.SanChoi247.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SanChoi247.model.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    // JpaRepository already provides findById, so no additional code is needed here
}
