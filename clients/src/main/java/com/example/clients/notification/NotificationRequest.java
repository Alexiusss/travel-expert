package com.example.clients.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {
    String toRecipientId;
    String toRecipientName;
    String message;
    String subject;
    String sender;
}