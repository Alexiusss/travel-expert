package com.example.restaurant_advisor_react.controller;

import com.example.restaurant_advisor_react.model.Role;
import com.example.restaurant_advisor_react.model.User;
import com.example.restaurant_advisor_react.servise.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;

import static com.example.restaurant_advisor_react.util.validation.ValidationUtil.assureIdConsistent;
import static com.example.restaurant_advisor_react.util.validation.ValidationUtil.checkNew;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = UserController.REST_URL, produces = APPLICATION_JSON_VALUE)
public class UserController {
    static final String REST_URL = "/api/v1/users";
    @Autowired
    UserService userService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        final Optional<User> user = userService.get(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user.get());
    }

    @GetMapping
    public Page<User> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return userService.findAllPaginated(PageRequest.of(page, size));
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        checkNew(user);
        user.setRoles(Set.of(Role.USER));
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user, @PathVariable String id) {
        assureIdConsistent(user, id);
        Optional<User> updatedUser = userService.updateUser(user, id);
        if (updatedUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser.get());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<HttpStatus> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}