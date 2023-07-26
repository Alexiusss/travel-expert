package com.example.user.repository.kc;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Repository
@Profile("kc")
@AllArgsConstructor
public class SubscriptionsRepository {

    private final DataSource dataSource;

    public void subscribe(String authUserId, String userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO subscriptions (channel_id, subscriber_id) VALUES (?, ?)")) {
            statement.setString(1, userId);
            statement.setString(2, authUserId);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unsubscribe(String authUserId, String userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM subscriptions WHERE channel_id=? AND subscriber_id=?")) {
            statement.setString(1, userId);
            statement.setString(2, authUserId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getAllSubscriptionsById(String authorId) {
        return getSubscribersOrSubscriptions(authorId, "subscriber_id");
    }

    public Set<String> getAllSubscribersById(String authorId) {
        return getSubscribersOrSubscriptions(authorId, "channel_id");
    }

    private Set<String> getSubscribersOrSubscriptions(String id, String fieldName) {
        Set<String> subsSet = new HashSet<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT " + fieldName + " FROM subscriptions WHERE " + (fieldName.equals("subscriber_id") ? "channel_id" : "subscriber_id") + "=?")) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    subsSet.add(resultSet.getString(fieldName));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subsSet;
    }

    public Map<String, Set<String>> getAllSubscribersByIds(List<String> authorsIds) {
        Map<String, Set<String>> map = new HashMap<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT channel_id, subscriber_id FROM subscriptions WHERE channel_id=ANY(?)")) {

            Array idsArray = connection.createArrayOf("VARCHAR", authorsIds.toArray());
            statement.setArray(1, idsArray);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    map.computeIfAbsent(resultSet.getString("channel_id"), channelId -> new HashSet<>()).add(resultSet.getString("subscriber_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}