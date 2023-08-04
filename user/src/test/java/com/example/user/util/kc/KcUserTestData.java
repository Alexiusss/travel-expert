package com.example.user.util.kc;

import com.example.common.util.JsonUtil;
import com.example.common.util.MatcherFactory;
import com.example.user.model.User;
import com.example.user.model.dto.UserDTO;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.experimental.UtilityClass;
import org.keycloak.admin.client.Keycloak;

import java.util.List;

@UtilityClass
public class KcUserTestData {

    public static final MatcherFactory.Matcher<UserDTO> USER_DTO_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(UserDTO.class, "password");

    public static final String ADMIN_ID = "1";
    public static final String MODER_ID = "2";
    public static final String USER_ID = "3";
    public static final String ADMIN_MAIL = "admin@gmail.com";
    public static final String MODER_MAIL = "moder@gmail.com";
    public static final String USER_MAIL = "user@gmail.com";
    public static final String USERNAME = "username";

    public static final UserDTO ADMIN_DTO = new UserDTO(ADMIN_ID, ADMIN_MAIL, "Admin", "AdminLast", "adminPassword", "adminusername", true, "adminFileName", List.of("ADMIN", "MODERATOR", "USER"));
    public static final UserDTO MODER_DTO = new UserDTO(MODER_ID, MODER_MAIL, "Moder", "ModerLast", "moderPassword", "moderusername", true, "moderFileName", List.of("MODERATOR", "USER"));
    public static final UserDTO USER_DTO = new UserDTO(USER_ID, USER_MAIL, "User", "UserLast", "userPassword", USERNAME, true, "userFileName", List.of("USER"));

    public static String getKeycloakToken(Keycloak keycloakAdminClient) {
        String access_token = keycloakAdminClient.tokenManager().getAccessToken().getToken();
        return "Bearer " + access_token;
    }

    public static UserDTO getNewUser() {
        return new UserDTO(null, "new@gmail.com", "new name", "new lastname", "newpassword", "newusername", true, "new filename", List.of("USER"));
    }

    public static String jsonWithPassword(UserDTO user, String passw) {
        return JsonUtil.writeAdditionProps(user, "password", passw);
    }

    public void  stubReviewResponse() {
        stubFor(WireMock.post(WireMock.urlMatching("/api/v1/reviews/user/reviewCountActive"))
                .willReturn(okForContentType("application/json",JsonUtil.writeValue(List.of(MODER_REVIEW_RESPONSE, USER_REVIEW_RESPONSE)))));
    }
}