package com.example.restaurant_advisor_react.repository;

import com.example.restaurant_advisor_react.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User>{

    @Override
    @Modifying
    @Transactional
    User save(User user);

    @Override
    @EntityGraph(attributePaths = {"roles"})
    List<User> findAll();

    Optional<User> findByEmailIgnoreCase(String email);
}