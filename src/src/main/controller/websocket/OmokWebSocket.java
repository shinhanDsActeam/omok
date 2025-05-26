package src.main.controller.websocket;

import main.controller.game.GameController;
import main.service.Board;
import org.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint("/ws/omok/{roomId}")
public class OmokWebSocket {
    private static final Map<String, List<Session>> roomSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId) {
        roomSessions.putIfAbsent(roomId, new CopyOnWriteArrayList<>());
        List<Session> sessions = roomSessions.get(roomId);
        sessions.add(session);
        System.out.println("방 [" + roomId + "] 입장: " + session.getId());

        boolean ready;
        if (sessions.size() == 2) ready = true;
        else ready = false;
        System.out.println(ready);

        for (int i = 0; i < sessions.size(); i++) {
            Session s = sessions.get(i);
            boolean isHost = (i == 0); // 첫 번째 입장자는 방장
            try {
                JSONObject msg = new JSONObject();
                msg.put("type", "userJoined");
                msg.put("ready", ready);
                msg.put("isHost", isHost);
                s.getBasicRemote().sendText(msg.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("roomId") String roomId) {
        System.out.println("📩 메시지 수신 (room " + roomId + "): " + message);
        try {
            JSONObject data = new JSONObject(message);
            if (data.has("type")) {
                String type = data.getString("type");
                System.out.println("➡ 메시지 타입: " + type);

                switch (type) {
                    case "startGame":
                        broadcast(roomId, new JSONObject().put("type", "startGame").toString());
                        return;
                    case "rematchRequest":
                        GameController.resetRoom(roomId);
                        broadcast(roomId, new JSONObject().put("type", "rematchRequest").toString());
                        return;
                    case "syncRequest":
                        Board board = GameController.getBoard(roomId);
                        if (board == null) return;

                        List<JSONObject> stones = new ArrayList<>();
                        for (int row = 0; row < 15; row++) {
                            for (int col = 0; col < 15; col++) {
                                String stone = board.getStone(row, col); // null, "black", "white"
                                if (stone != null) {
                                    JSONObject obj = new JSONObject();
                                    obj.put("row", row);
                                    obj.put("col", col);
                                    obj.put("stone", stone);
                                    stones.add(obj);
                                }
                            }
                        }

                        JSONObject response = new JSONObject();
                        response.put("type", "syncBoard");
                        response.put("stones", stones);

                        session.getBasicRemote().sendText(response.toString());
                        return;
                }
            }
            broadcast(roomId, message);
        } catch (Exception e) {
            System.out.println("❌ 메시지 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("roomId") String roomId) {
        List<Session> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            System.out.println("방 [" + roomId + "] 퇴장: " + session.getId());
            if (sessions.isEmpty()) {
                roomSessions.remove(roomId);
            }
        }
    }

    private void broadcast(String roomId, String message) {
        List<Session> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            for (Session s : sessions) {
                if (s.isOpen()) {
                    try {
                        s.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}