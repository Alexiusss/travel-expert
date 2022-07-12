package com.example.user;

import com.example.user.model.User;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString(of = "user")
public class AuthUser extends org.springframework.security.core.userdetails.User {
    private static final long serialVersionUID = 1L;

    private final User user;

    public AuthUser(@NonNull User user) {
        super(user.getEmail(), user.getPassword(), user.isEnabled(),true, true, true, user.getRoles());
        this.user = user;
    }

    public String id() {
        return user.id();
    }
}