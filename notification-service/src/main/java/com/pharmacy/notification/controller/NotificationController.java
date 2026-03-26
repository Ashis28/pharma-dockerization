package com.pharmacy.notification.controller;

import com.pharmacy.notification.dto.NotificationRequest;
import com.pharmacy.notification.listener.NotificationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationListener notificationListener;

    public NotificationController(NotificationListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestBody NotificationRequest req) {
        notificationListener.handleNotification(req);
        return ResponseEntity.ok("Notification processed for: " + req.getRecipientEmail());
    }
}
