package com.mygate.visitor.service;

import com.mygate.visitor.repository.UserPushTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
@Slf4j
public class ExpoPushService {

    private final UserPushTokenRepository pushTokenRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    public ExpoPushService(UserPushTokenRepository pushTokenRepository) {
        this.pushTokenRepository = pushTokenRepository;
    }

    /**
     * Send push notification to a user via their stored Expo push token
     */
    public void sendToUser(String userId, String title, String body, String priority) {
        try {
            List<String> tokens = pushTokenRepository.findTokensByUserId(userId);
            if (tokens.isEmpty()) {
                log.debug("No push tokens found for user: {}", userId);
                return;
            }

            for (String token : tokens) {
                sendPush(token, title, body, priority);
            }
        } catch (Exception e) {
            log.error("Error sending push to user {}: {}", userId, e.getMessage());
        }
    }

    private void sendPush(String expoPushToken, String title, String body, String priority) {
        try {
            if (!expoPushToken.startsWith("ExponentPushToken")) {
                log.warn("Invalid Expo push token format: {}", expoPushToken);
                return;
            }

            Map<String, Object> message = new HashMap<>();
            message.put("to", expoPushToken);
            message.put("title", title);
            message.put("body", body);
            message.put("sound", "default");
            message.put("priority", "high".equals(priority) || "URGENT".equals(priority) ? "high" : "normal");
            message.put("channelId", "URGENT".equals(priority) ? "mygate-urgent" : "mygate-default");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            headers.set("Accept-Encoding", "gzip, deflate");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(EXPO_PUSH_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Push sent to token: {}", expoPushToken.substring(0, 20) + "...");
            }
        } catch (Exception e) {
            log.error("Error sending push notification: {}", e.getMessage());
        }
    }
}
