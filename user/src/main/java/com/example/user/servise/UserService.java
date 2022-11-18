package com.example.user.servise;

import com.example.user.AuthUser;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.model.dto.AuthorDTO;
import com.example.user.repository.UserRepository;
import com.example.user.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.common.util.ValidationUtil.*;
import static com.example.user.util.UserUtil.getAuthorDTO;
import static com.example.user.util.UserUtil.prepareToSave;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Transactional
    public User saveUser(User user) {
        checkNew(user);
        user.setRoles(Set.of(Role.USER));
        return userRepository.save(prepareToSave(user));
    }

    @Transactional
    public User updateUser(User user, String id) {
        assureIdConsistent(user, user.id());
        User userFromDB = userRepository.getExisted(id);
            userFromDB.setEmail(user.getEmail());
            userFromDB.setFirstName(user.getFirstName());
            userFromDB.setLastName(user.getLastName());
            userFromDB.setFileName(user.getFileName());
            userFromDB.setUsername(user.getUsername());
            if (!ObjectUtils.isEmpty(user.getPassword())) {
                userFromDB.setPassword(user.getPassword());
                prepareToSave(userFromDB);
            }
        return userFromDB;
    }

    @Transactional
    public void subscribe(AuthUser authUser, String userId){
        User user = checkNotFoundWithId(userRepository.findByIdWithSubscriptions(userId), userId);
        user.getSubscribers().add(authUser.id());
    }

    @Transactional
    public void unSubscribe(AuthUser authUser, String userId){
        User user = checkNotFoundWithId(userRepository.findByIdWithSubscriptions(userId), userId);
        user.getSubscribers().remove(authUser.id());
    }

    @Transactional
    public void enableUser(String id, boolean enable) {
        User user = userRepository.getExisted(id);
        user.setEnabled(enable);
    }

    public void deleteUser(String id) {
        userRepository.deleteExisted(id);
    }

    public AuthorDTO getAuthorById(String id) {
        User user = checkNotFoundWithId(userRepository.findByIdWithSubscriptions(id), id);
        return getAuthorDTO(user);
    }

    public List<AuthorDTO> getAllAuthorsById(String[] authors) {
        List<User> users = userRepository.findAllByIdWithSubscriptions(authors);
        return users.stream().map(UserUtil::getAuthorDTO).collect(Collectors.toList());
    }

    public AuthorDTO getAuthorByUserName(String username) {
        User user = checkNotFoundWithName(userRepository.findByUsername(username), username);
        return getAuthorDTO(user);
    }


    public User get(String id) {
        return userRepository.getExisted(id);
    }

    public Page<User> findAllPaginated(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Authenticating '{}'", email);
       return new AuthUser(userRepository.findByEmailIgnoreCase(email.toLowerCase()).orElseThrow(
               () -> new UsernameNotFoundException("User '" + email + "' was not found")
       ));
    }
}