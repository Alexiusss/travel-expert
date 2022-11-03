package com.example.user.repository;

import com.example.common.BaseRepository;
import com.example.user.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {

    @Override
    @EntityGraph(attributePaths = {"roles"})
    List<User> findAll();

    Optional<User> findByEmailIgnoreCase(String email);

    User findByActivationCode(String code);

    Optional<User> findByUsername(String username);

    //https://dba.stackexchange.com/a/285241
    //https://stackoverflow.com/a/13659984
    @Query(value = "SELECT MAX(CAST(coalesce(nullif(regexp_replace(username, '[^0-9]', '', 'gi'), ''), '0') AS INTEGER)) " +
                   "FROM users " +
                   "WHERE LOWER(username) ~ LOWER(CONCAT('^[0-9]{0,}', :username, '$'))",
                    nativeQuery = true)
    Optional<Integer> findMaxUsernameIndex(String username);
}