package com.example.user.repository;

import com.example.common.BaseRepository;
import com.example.user.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {

    @Override
    @EntityGraph(attributePaths = { "roles" })
    List<User> findAll();

    Optional<User> findByEmailIgnoreCase(String email);

    User findByActivationCode(String code);

    Optional<User> findByUsername(String username);
}