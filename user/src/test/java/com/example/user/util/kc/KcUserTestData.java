package com.example.user.util.kc;

import com.example.clients.auth.AuthorDTO;
import com.example.clients.review.ReviewResponse;
import com.example.common.util.JsonUtil;
import com.example.common.util.MatcherFactory;
import com.example.user.model.dto.UserDTO;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.experimental.UtilityClass;
import org.keycloak.admin.client.Keycloak;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;

@UtilityClass
public class KcUserTestData {

    public static final MatcherFactory.Matcher<UserDTO> USER_DTO_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(UserDTO.class, "password");
    public static final MatcherFactory.Matcher<AuthorDTO> AUTHOR_DTO_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(AuthorDTO.class, "subscribers", "subscriptions");

    public static final String ADMIN_ID = "1";
    public static final String MODER_ID = "2";
    public static final String USER_ID = "3";
    public static final String DELETE_USER_ID = "4";
    public static final String UPDATE_USER_ID = "5";

    public static final String NOT_FOUND_ID = "1111";
    public static final String ADMIN_MAIL = "admin@gmail.com";
    public static final String MODER_MAIL = "moder@gmail.com";
    public static final String USER_MAIL = "user@gmail.com";
    public static final String USERNAME = "username";
    public static final String MODER_USERNAME = "moderusername";

    public static final UserDTO ADMIN_DTO = new UserDTO(ADMIN_ID, ADMIN_MAIL, "Admin", "AdminLast", "adminPassword", "adminusername", true, "adminFileName", List.of("ADMIN", "MODERATOR", "USER"));
    public static final UserDTO MODER_DTO = new UserDTO(MODER_ID, MODER_MAIL, "Moder", "ModerLast", "moderPassword", MODER_USERNAME, true, "moderFileName", List.of("MODERATOR", "USER"));
    public static final UserDTO USER_DTO = new UserDTO(USER_ID, USER_MAIL, "User", "UserLast", "userPassword", USERNAME, true, "userFileName", List.of("USER"));

    public static final AuthorDTO MODER_AUTHOR = new AuthorDTO(MODER_ID, "Moder ModerLast", MODER_USERNAME, "moderFileName", Instant.ofEpochMilli(1691142410700L), Set.of(), Set.of(), 0L);
    public static final AuthorDTO USER_AUTHOR = new AuthorDTO(USER_ID,"User UserLast", USERNAME, "userFileName", Instant.ofEpochMilli(1691142410777L), Set.of(), Set.of(), 0L);

    private static final ReviewResponse MODER_REVIEW_RESPONSE = new ReviewResponse(MODER_ID, 0L);
    private static final ReviewResponse USER_REVIEW_RESPONSE = new ReviewResponse(USER_ID, 0L);

    public final String NOT_FOUND_MESSAGE = "HTTP 404 Not Found";
    public final String MODIFICATION_FORBIDDEN_MESSAGE = "modification is forbidden";
    public final String DUPLICATE_EMAIL = "Duplicate email or username";

    public static String getKeycloakToken(Keycloak keycloakAdminClient) {
        String access_token = keycloakAdminClient.tokenManager().getAccessToken().getToken();
        return "Bearer " + access_token;
    }

    public static UserDTO getNewUser() {
        return new UserDTO(null, "new@gmail.com", "new name", "new lastname", "newpassword", "newusername", true, "new filename", List.of("USER"));
    }

    public static UserDTO getUpdatedUser() {
        return new UserDTO(UPDATE_USER_ID, "updated-profile@gmail.com", "updated", "updated", "updated-password", "updatedUsername", true, "updated-file-name", List.of("ADMIN", "USER", "MODERATOR"));
    }

    public static String jsonWithPassword(UserDTO user, String passw) {
        return JsonUtil.writeAdditionProps(user, "password", passw);
    }

    public void  stubReviewResponse() {
        stubFor(WireMock.post(WireMock.urlMatching("/api/v1/reviews/user/reviewCountActive"))
                .willReturn(okForContentType("application/json",JsonUtil.writeValue(List.of(MODER_REVIEW_RESPONSE, USER_REVIEW_RESPONSE)))));
    }
}