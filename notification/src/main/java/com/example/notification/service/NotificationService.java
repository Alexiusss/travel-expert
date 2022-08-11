package com.example.notification.service;

import com.example.clients.notification.NotificationRequest;
import com.example.notification.model.Notification;
import com.example.notification.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MailSender mailSender;

    public void send(NotificationRequest notificationRequest) {
        save(notificationRequest);
        sendMail(notificationRequest);
    }

    private void save(NotificationRequest notificationRequest) {
        notificationRepository.save(
                Notification.builder()
                        .toRecipientId(notificationRequest.getToRecipientId())
                        .toRecipientEmail(notificationRequest.getToRecipientName())
                        .sender(notificationRequest.getSender())
                        .message(notificationRequest.getMessage())
                        .build()
        );
    }

    private void sendMail(NotificationRequest notificationRequest) {
        mailSender.send(notificationRequest.getToRecipientName(),
                notificationRequest.getSubject(),
                notificationRequest.getMessage());
    }
}