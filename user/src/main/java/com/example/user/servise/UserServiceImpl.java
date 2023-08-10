package com.example.user.servise;

import com.example.clients.auth.AuthorDTO;
import com.example.clients.review.ReviewClient;
import com.example.clients.review.ReviewResponse;
import com.example.user.AuthUser;
import com.example.user.model.User;
import com.example.user.model.dto.UserDTO;
import com.example.user.repository.UserRepository;
import com.example.user.util.UserUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.common.util.ValidationUtil.*;
import static com.example.user.util.UserUtil.*;

@Service
@Slf4j
@AllArgsConstructor
@Profile({ "!test_kc & !kc" })
@Transactional(readOnly = true)
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;
    private final ReviewClient reviewClient;

    @Override
    public UserDTO saveUser(UserDTO userDTO, List<String> roles) {
        User user = convertDtoToUser(userDTO);
        checkNew(user);
        user.setRoles(user.getRoles());
        User savedUser = userRepository.save(prepareToSave(user));
        return getUserDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(UserDTO user, String id) {
        assureIdConsistent(user, user.id());
        User userFromDB = userRepository.getExisted(id);
        userFromDB.setEmail(user.getEmail());
        userFromDB.setFirstName(user.getFirstName());
        userFromDB.setLastName(user.getLastName());
        userFromDB.setFileName(user.getFileName());
        userFromDB.setUsername(user.getUsername());
        userFromDB.setRoles(convertRoles(user.getRoles()));
        if (!ObjectUtils.isEmpty(user.getPassword())) {
            userFromDB.setPassword(user.getPassword());
            prepareToSave(userFromDB);
        }
        return getUserDTO(userFromDB);
    }

    @Override
    public UserDTO updateProfile(UserDTO user, String id) {
         return updateUser(user, id);
    }

    @Override
    public void subscribe(String authUserId, String userId) {
        User user = checkNotFoundWithId(userRepository.findByIdWithSubscriptions(userId), userId);
        user.getSubscribers().add(authUserId);
    }

    @Override
    public void unSubscribe(String authUserId, String userId) {
        User user = checkNotFoundWithId(userRepository.findByIdWithSubscriptions(userId), userId);
        user.getSubscribers().remove(authUserId);
    }

    @Override
    public void enableUser(String id, boolean enable) {
        User user = userRepository.getExisted(id);
        user.setEnabled(enable);
    }

    @Override
    public void deleteUser(String id) {
        userRepository.deleteExisted(id);
    }

    @Override
    public AuthorDTO getAuthorById(String id) {
        User user = checkNotFoundWithId(userRepository.findByIdWithSubscriptions(id), id);
        return getAuthorDTO(user);
    }

    @Override
    public UserDTO getProfile(Object principal) {
        if (principal instanceof AuthUser) {
            User user = ((AuthUser) principal).getUser();
            return getUserDTO(user);
        } else {
            throw new IllegalArgumentException("Unknown authentication principal type");
        }
    }

    @Override
    public List<AuthorDTO> getAllAuthorsById(String[] authors) {
        List<User> users = userRepository.findAllByIdWithSubscriptions(authors);
        List<ReviewResponse> list = reviewClient.getActiveList(authors);

        return users.stream()
                .map(UserUtil::getAuthorDTO)
                .peek(setReviewsCount(list))
                .collect(Collectors.toList());
    }

    @Override
    public AuthorDTO getAuthorByUserName(String username) {
        User user = checkNotFoundWithName(userRepository.findByUsername(username), username);
        return getAuthorDTO(user);
    }

    @Override
    public Page<User> getAll(int page, int size, String filter) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public UserDTO get(String id) {
        return getUserDTO(userRepository.getExisted(id));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Authenticating '{}'", email);
        return new AuthUser(userRepository.findByEmailIgnoreCase(email.toLowerCase()).orElseThrow(
                () -> new UsernameNotFoundException("User '" + email + "' was not found")
        ));
    }
}