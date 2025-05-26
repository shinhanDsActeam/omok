package src.main.controller.websocket;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/chat/{roomId}")
public class ChatWebSocket {
    private static final Map<String, Set<Session>> roomSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId) {
        System.out.println("채팅 소켓 연결 - roomId: " + roomId);
        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("roomId") String roomId) throws IOException {
        for (Session s : roomSessions.getOrDefault(roomId, Set.of())) {
            if (s.isOpen()) s.getBasicRemote().sendText(message);
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("roomId") String roomId) {
        Set<Session> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) roomSessions.remove(roomId);
        }
    }
}
