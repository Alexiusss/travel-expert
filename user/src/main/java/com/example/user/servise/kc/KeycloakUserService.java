package com.example.user.servise.kc;

import com.example.clients.auth.AuthorDTO;
import com.example.clients.review.ReviewClient;
import com.example.clients.review.ReviewResponse;
import com.example.user.model.dto.UserDTO;
import com.example.user.model.kc.UserRepresentationWithRoles;
import com.example.user.repository.kc.SubscriptionsRepository;
import com.example.user.servise.UserService;
import com.example.user.util.KeycloakUtil;
import com.example.user.util.UserUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.common.util.ValidationUtil.checkNew;
import static com.example.user.util.UserUtil.*;

@Service
@Slf4j
@AllArgsConstructor
@Profile({"test_kc", "kc"})
@Transactional(readOnly = true)
public class KeycloakUserService implements UserService {

    private final KeycloakUtil keycloakUtil;
    private final ReviewClient reviewClient;
    private final SubscriptionsRepository subscriptionsRepository;

    @Override
    public UserDTO saveUser(UserDTO user, List<String> roles) {
        checkNew(user);
        return keycloakUtil.createKeycloakUser(user, roles);
    }

    @Override
    public UserDTO updateProfile(UserDTO user, String id) {
        checkModificationAllowed(id);
        keycloakUtil.updateKeycloakUser(user, List.of(), id);
        return user;
    }

    @Override
    public UserDTO updateUser(UserDTO user, String id) {
        checkModificationAllowed(id);
        keycloakUtil.updateKeycloakUser(user, user.getRoles(), id);
        return user;
    }

    @Override
    public void enableUser(String id, boolean enable) {
        keycloakUtil.enableUser(id, enable);
    }

    @Override
    public void deleteUser(String userId) {
        keycloakUtil.deleteKeycloakUser(userId);
        subscriptionsRepository.deleteAllByUserId(userId);
    }

    @Override
    public UserDTO get(String id) {
        UserRepresentationWithRoles userRepresentation = keycloakUtil.findUserById(id);
        return getUserDTO(userRepresentation);
    }

    @Override
    public Page<UserDTO> getAll(int page, int size, String filter) {
        List<UserRepresentationWithRoles> userRepresentations;
        if (filter.length() > 0) {
            userRepresentations = keycloakUtil.searchKeycloakUsers(filter);
        } else {
            userRepresentations = keycloakUtil.findAll(page, size);
        }

        List<UserDTO> userList = userRepresentations.stream()
                .map(UserUtil::getUserDTO)
                .collect(Collectors.toList());

        int countOfUsers = keycloakUtil.getTotalCountOfUsers();
        PageRequest pageable = PageRequest.of(page, size);

        return new PageImpl<>(userList, pageable, countOfUsers);
    }

    @Override
    public AuthorDTO getAuthorById(String id) {
        AuthorDTO author = getAuthorDTO(keycloakUtil.findUserById(id));
        setSubscriptionsAndSubscribers(author);
        return author;
    }

    @Override
    public UserDTO getProfile(Object principal) {
        if (principal instanceof Jwt) {
            UserDTO profile = get(((Jwt) principal).getSubject());
            addRoles(profile, (Jwt) principal);
            return profile;
        }
        throw new IllegalArgumentException("Unknown authentication principal type");

    }

    @Override
    public AuthorDTO getAuthorByUserName(String username) {
        AuthorDTO author = getAuthorDTO(keycloakUtil.findByUserName(username));
        setSubscriptionsAndSubscribers(author);
        return author;
    }

    @Override
    public List<AuthorDTO> getAllAuthorsById(String[] authors) {
        List<String> authorsList = List.of(authors);
        List<ReviewResponse> list = reviewClient.getActiveList(authors);

        List<AuthorDTO> authorList = keycloakUtil.findAll()
                .stream()
                .filter(userRepresentation -> authorsList.contains(userRepresentation.getId()))
                .map(UserUtil::getAuthorDTO)
                .collect(Collectors.toList());

        setReviewCounts(authorList, list);
        setSubscribersForAuthors(authorList);

        return authorList;
    }

    private void setReviewCounts(List<AuthorDTO> authors, List<ReviewResponse> reviewResponses) {
        Map<String, Long> reviewCounts = reviewResponses.stream()
                .collect(Collectors.toMap(ReviewResponse::getId, ReviewResponse::getCount));

        authors.forEach(author -> {
            Long count = reviewCounts.getOrDefault(author.getAuthorId(), 0L);
            author.setReviewsCount(count);
        });
    }

    private void setSubscribersForAuthors(List<AuthorDTO> authors) {
        List<String> authorIds = authors.stream()
                .map(AuthorDTO::getAuthorId)
                .collect(Collectors.toList());

        Map<String, Set<String>> subscribersMap = subscriptionsRepository.getAllSubscribersByIds(authorIds);

        authors.forEach(author -> {
            Set<String> subscribers = subscribersMap.getOrDefault(author.getAuthorId(), new HashSet<>());
            author.setSubscribers(subscribers);
        });
    }

    private void setSubscriptionsAndSubscribers(AuthorDTO author) {
        setSubscriptions(author);
        setSubscribers(author);
    }

    private void setSubscriptions(AuthorDTO author) {
        Set<String> subscriptions = subscriptionsRepository.getAllSubscriptionsById(author.getAuthorId());
        author.setSubscriptions(subscriptions);
    }

    private void setSubscribers(AuthorDTO author) {
        Set<String> subscribers = subscriptionsRepository.getAllSubscribersById(author.getAuthorId());
        author.setSubscribers(subscribers);
    }

    @Override
    public void subscribe(String authUserId, String userId) {
        subscriptionsRepository.subscribe(authUserId, userId);
    }

    @Override
    public void unSubscribe(String authUserId, String userId) {
        subscriptionsRepository.unsubscribe(authUserId, userId);
    }
}