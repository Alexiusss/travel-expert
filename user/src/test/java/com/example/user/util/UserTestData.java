package com.example.user.util;

import com.example.common.util.MatcherFactory;
import com.example.common.util.JsonUtil;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.model.dto.AuthorDTO;
import com.example.user.model.dto.RegistrationDTO;
import lombok.experimental.UtilityClass;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@UtilityClass
public class UserTestData {
    public static final MatcherFactory.Matcher<User> USER_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(User.class, "createdAt", "modifiedAt", "password", "subscribers", "subscriptions");
    public static final MatcherFactory.Matcher<AuthorDTO> AUTHOR_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(AuthorDTO.class, "subscribers", "subscriptions");

    public static final String ADMIN_ID = "1";
    public static final String MODER_ID = "2";
    public static final String USER_ID = "3";
    public static final Instant USER_INSTANT = Timestamp.valueOf("2022-04-27 19:16:53.292882").toInstant();
    public static final String NOT_FOUND_ID = "1000";
    public static final String NOT_FOUND_MESSAGE = String.format("Entity with id=%s not found", NOT_FOUND_ID);
    public static final String ADMIN_MAIL = "admin@gmail.com";
    public static final String MODER_MAIL = "moder@gmail.com";
    public static final String USER_MAIL = "user@gmail.com";
    public static final String USERNAME = "userName";

    public static final User ADMIN = new User(ADMIN_ID, null, null, 0, ADMIN_MAIL, "Admin", "AdminLast", "adminUserName", "adminPassword", true, null, List.of(Role.ADMIN, Role.MODERATOR, Role.USER));
    public static final User MODER = new User(MODER_ID, null, null, 0, MODER_MAIL, "Moder", "ModerLast", "moderUserName", "moderPassword", true, null, List.of(Role.MODERATOR, Role.USER));
    public static final User USER = new User(USER_ID, USER_INSTANT, USER_INSTANT, 0, USER_MAIL, "User", "UserLast", USERNAME, "userPassword", true, null, Collections.singleton(Role.USER));

    public static final AuthorDTO AUTHOR = new AuthorDTO(USER_ID, getAuthorName(USER), USER.getUsername(), USER.getFileName(), USER.getCreatedAt(), Set.of(), Set.of());

    private static String getAuthorName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }

    public static User getNewUser() {
        return new User("new_user@gmail.com", "Newuser", "NewUserLast", "newUserName", "NewPassword", false, null, Collections.singleton(Role.USER));
    }

    public static RegistrationDTO getNewRegistration() {
        return new RegistrationDTO("new_user@gmail.com", "Newuser", "NewUserLast", "NewPassword");
    }

    public static User getUpdated() {
        return new User(USER_ID, null, null, 0, "user@gmail.com", "Updated user name", "Updated UserLast", "updatedUserName", "newPass", true, null, Collections.singleton(Role.USER));
    }

    public static String jsonWithPassword(User user, String passw) {
        return JsonUtil.writeAdditionProps(user, "password", passw);
    }

    public static String jsonWithPassword(RegistrationDTO registration, String passw) {
        return JsonUtil.writeAdditionProps(registration, "password", passw);
    }
}