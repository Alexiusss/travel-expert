package com.example.restaurant_advisor_react.util;

import com.example.restaurant_advisor_react.controller.MatcherFactory;
import com.example.restaurant_advisor_react.model.Role;
import com.example.restaurant_advisor_react.model.User;
import lombok.experimental.UtilityClass;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class UserTestData {
    public static final MatcherFactory.Matcher<User> USER_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(User.class,"createdAt","modifiedAt", "password");

    public static final String ADMIN_ID = "1";
    public static final String MODER_ID = "2";
    public static final String USER_ID = "3";
    public static final Instant USER_INSTANT = Timestamp.valueOf("2022-04-27 19:16:53.292882").toInstant();
    public static final String NOT_FOUND = "1000";

    public static final User ADMIN = new User(ADMIN_ID, null, null, 0, "admin@gmail.com", "Admin", "AdminLast", "adminPassword", true, null, List.of(Role.ADMIN, Role.MODERATOR, Role.USER));
    public static final User MODER = new User(MODER_ID, null, null, 0, "moder@gmail.com", "Moder", "ModerLast", "moderPassword", true, null, List.of(Role.MODERATOR, Role.USER));
    public static final User USER = new User(USER_ID, USER_INSTANT, USER_INSTANT, 0, "user@gmail.com", "User", "UserLast", "userPassword", true, null, Collections.singleton(Role.USER));

    public static User getNew() {
       return new User("new_user@gmail.com", "New user", "NewUserLast", "NewPassword", false, null, Collections.singleton(Role.USER));
    }

    public static User getUpdated() {
       return new User(USER_ID, null, null, 0, "user@gmail.com", "Updated user name", "Updated UserLast", "newPass", true, null, Collections.singleton(Role.USER));
    }

    public static String jsonWithPassword(User user, String passw) {
        return JsonUtil.writeAdditionProps(user, "password", passw);}
}