package com.example.restaurant_advisor_react.util;

import com.example.restaurant_advisor_react.controller.MatcherFactory;
import com.example.restaurant_advisor_react.model.Role;
import com.example.restaurant_advisor_react.model.User;
import lombok.experimental.UtilityClass;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class UserTestData {
    public static final MatcherFactory.Matcher<User> USER_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(User.class, "createdAt", "password");

    public static final String ADMIN_ID = "1";
    public static final String MODER_ID = "2";
    public static final String USER_ID = "3";

    public static final User ADMIN = new User(ADMIN_ID, null, null, 0, "admin@gmail.com", "Admin", "AdminLast", "1", true, null, List.of(Role.ADMIN, Role.MODERATOR, Role.USER));
    public static final User MODER = new User(MODER_ID, null, null, 0, "moder@gmail.com", "Moder", "ModerLast", "1", true, null, List.of(Role.MODERATOR, Role.USER));
    public static final User USER = new User(USER_ID, null, null, 0, "user@gmail.com", "User", "UserLast", "1", true, null, Collections.singleton(Role.USER));

    public static final List<User> USER_LIST = List.of(ADMIN, MODER, USER);
}