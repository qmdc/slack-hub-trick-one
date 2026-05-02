package com.slack.slackjarservice.habitformed.websocket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class HabitWebSocketConfig {

    @Value("${socketio.port:9092}")
    private int port;

    @Value("${socketio.host:localhost}")
    private String host;

    private SocketIOServer server;

    private final ConcurrentHashMap<Long, String> userSessionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> sessionUserMap = new ConcurrentHashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    public void startServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(port);

        server = new SocketIOServer(config);

        server.addConnectListener(onConnect());
        server.addDisconnectListener(onDisconnect());

        server.addEventListener("auth", String.class, (client, token, ackRequest) -> {
            try {
                String userIdStr = token;
                if (userIdStr != null && userIdStr.startsWith("user_")) {
                    userIdStr = userIdStr.substring(5);
                }
                Long userId = Long.parseLong(userIdStr);
                String sessionId = client.getSessionId().toString();

                userSessionMap.put(userId, sessionId);
                sessionUserMap.put(sessionId, userId);

                client.set("userId", userId);
                client.joinRoom("user_" + userId);

                log.info("用户 {} WebSocket认证成功，SessionId: {}", userId, sessionId);
                client.sendEvent("auth_success", "认证成功");
            } catch (Exception e) {
                log.error("WebSocket认证失败: {}", e.getMessage());
                client.sendEvent("auth_error", "认证失败");
            }
        });

        server.addEventListener("join_feed", Long.class, (client, userId, ackRequest) -> {
            client.joinRoom("feed_" + userId);
            log.info("用户 {} 加入动态房间", userId);
        });

        server.addEventListener("leave_feed", Long.class, (client, userId, ackRequest) -> {
            client.leaveRoom("feed_" + userId);
            log.info("用户 {} 离开动态房间", userId);
        });

        server.start();
        log.info("Socket.IO服务器启动成功: {}:{}", host, port);
    }

    private ConnectListener onConnect() {
        return client -> {
            log.info("客户端连接: {}", client.getSessionId());
            client.sendEvent("connected", "请先进行认证");
        };
    }

    private DisconnectListener onDisconnect() {
        return client -> {
            String sessionId = client.getSessionId().toString();
            Long userId = sessionUserMap.remove(sessionId);

            if (userId != null) {
                userSessionMap.remove(userId);
                log.info("用户 {} 断开连接，SessionId: {}", userId, sessionId);
            } else {
                log.info("未认证客户端断开连接: {}", sessionId);
            }
        };
    }

    @PreDestroy
    public void stopServer() {
        if (server != null) {
            server.stop();
            log.info("Socket.IO服务器已停止");
        }
    }

    public void pushToUser(Long userId, String event, Object data) {
        String sessionId = userSessionMap.get(userId);
        if (sessionId != null && server != null) {
            com.corundumstudio.socketio.SocketIOClient client = server.getClient(
                java.util.UUID.fromString(sessionId)
            );
            if (client != null && client.isChannelOpen()) {
                client.sendEvent(event, data);
                log.debug("向用户 {} 推送消息: {}", userId, event);
            }
        }
    }

    public void pushToRoom(String room, String event, Object data) {
        if (server != null) {
            server.getRoomOperations(room).sendEvent(event, data);
            log.debug("向房间 {} 推送消息: {}", room, event);
        }
    }

    public void pushToFriendFeed(Long userId, Object data) {
        pushToRoom("feed_" + userId, "new_feed", data);
    }

    public void pushFriendRequest(Long targetUserId, Object data) {
        pushToUser(targetUserId, "friend_request", data);
    }

    public void pushCheckinReminder(Long userId, Object data) {
        pushToUser(userId, "checkin_reminder", data);
    }

    public void pushAchievementUnlock(Long userId, Object data) {
        pushToUser(userId, "achievement_unlock", data);
    }

    public void pushLikeNotification(Long targetUserId, Object data) {
        pushToUser(targetUserId, "like_notification", data);
    }

    public void pushCommentNotification(Long targetUserId, Object data) {
        pushToUser(targetUserId, "comment_notification", data);
    }

    public boolean isUserOnline(Long userId) {
        String sessionId = userSessionMap.get(userId);
        if (sessionId == null || server == null) {
            return false;
        }
        com.corundumstudio.socketio.SocketIOClient client = server.getClient(
            java.util.UUID.fromString(sessionId)
        );
        return client != null && client.isChannelOpen();
    }

    public int getOnlineUserCount() {
        return userSessionMap.size();
    }
}
