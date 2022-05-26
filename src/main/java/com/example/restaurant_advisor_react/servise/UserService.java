package com.example.restaurant_advisor_react.servise;

import com.example.restaurant_advisor_react.model.User;
import com.example.restaurant_advisor_react.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public Optional<User> updateUser(User user, String id) {
        Optional<User> userFromDB = userRepository.findById(id);
        if (userFromDB.isPresent()) {
            userFromDB.get().setEmail(user.getEmail());
            userFromDB.get().setFirstName(user.getFirstName());
            userFromDB.get().setLastName(user.getLastName());

            if (!ObjectUtils.isEmpty(user.getPassword())) {
                userFromDB.get().setPassword(user.getPassword());
            }
        }
        return userFromDB;
    }

    @Transactional
    public void enableUser(String id, boolean enable) {
        User user = userRepository.getById(id);
        user.setEnabled(enable);
    }

    public void deleteUser(String id) {
        userRepository.deleteExisted(id);
    }

    public Optional<User> get(String id) {
        return userRepository.findById(id);
    }

    public Page<User> findAllPaginated(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}