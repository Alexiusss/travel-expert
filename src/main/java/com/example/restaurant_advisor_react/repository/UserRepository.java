package com.example.restaurant_advisor_react.repository;

import com.example.restaurant_advisor_react.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {

    @Override
    @EntityGraph(attributePaths = { "roles" })
    List<User> findAll();

    Optional<User> findByEmailIgnoreCase(String email);

    User findByActivationCode(String code);
}