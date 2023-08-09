package com.example.user.servise;

import com.example.clients.auth.AuthorDTO;
import com.example.user.model.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {

    UserDTO get(String id);

    AuthorDTO getAuthorById(String id);

    UserDTO getProfile(Object principal);

    List<AuthorDTO> getAllAuthorsById(String[] authors);

    AuthorDTO getAuthorByUserName(String username);

    Page<?> getAll(int page, int size, String filter);

    @Transactional
    UserDTO saveUser(UserDTO userDTO, List<String> roles);

    @Transactional
    UserDTO updateUser(UserDTO user, String id);

    @Transactional
    UserDTO updateProfile(UserDTO user, String id) ;

    @Transactional
    void deleteUser(String userId);

    @Transactional
    void enableUser(String id, boolean enable);

    @Transactional
    void subscribe(String authUserId, String userId);

    @Transactional
    void unSubscribe(String authUserId, String userId);
}
