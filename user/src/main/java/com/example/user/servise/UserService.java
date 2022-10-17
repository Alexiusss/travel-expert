package com.example.user.servise;

import com.example.user.AuthUser;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.model.dto.AuthorDTO;
import com.example.user.repository.UserRepository;
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

import java.util.Set;

import static com.example.common.util.ValidationUtil.assureIdConsistent;
import static com.example.common.util.ValidationUtil.checkNew;
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
            if (!ObjectUtils.isEmpty(user.getPassword())) {
                userFromDB.setPassword(user.getPassword());
                prepareToSave(userFromDB);
            }
        return userFromDB;
    }

    @Transactional
    public void enableUser(String id, boolean enable) {
        User user = userRepository.getExisted(id);
        user.setEnabled(enable);
    }

    public void deleteUser(String id) {
        userRepository.deleteExisted(id);
    }

    public AuthorDTO getAuthor(String id) {
        User user = get(id);
        String authorName = user.getFirstName() + " " + user.getLastName().charAt(0) + ".";
        String fileName = user.getFileName();
        return new AuthorDTO(user.getId(), authorName, fileName);
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