package main.java.controller.websocket;

import main.java.controller.game.GameController;
import main.java.service.Board;
import org.json.JSONObject;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint(value = "/ws/omok/{roomId}", configurator = main.java.config.HttpSessionConfigurator.class)
public class OmokWebSocket {
    private static final Map<String, List<Session>> roomSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config, @PathParam("roomId") String roomId) throws IOException {
        if (roomId == null || roomId.isBlank()) {
            System.err.println("❌ roomId is null! WebSocket 경로 잘못됨.");
            session.close();
            return;
        }

        roomSessions.putIfAbsent(roomId, new CopyOnWriteArrayList<>());
        List<Session> sessions = roomSessions.get(roomId);
        sessions.add(session);

        // ✅ 세션에서 닉네임 가져오기
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());

        if (httpSession == null) {
            System.err.println("❌ WebSocket 연결 중 HttpSession이 null입니다. 로그인 안 된 상태에서 연결 시도됨.");
            session.close(); // 연결 차단
            return;
        }

        String nickname = (String) httpSession.getAttribute("nickname");

        if (nickname == null) {
            System.err.println("❌ HttpSession에는 nickname이 없습니다. 로그인 정보 세팅 확인 필요.");
            session.close();
            return;
        }

        session.getUserProperties().put("nickname", nickname);

        System.out.println("방 [" + roomId + "] 입장: " + session.getId() + ", 닉네임: " + nickname);

        boolean ready = (sessions.size() == 2);

        // ✅ 두 명 다 입장한 경우 닉네임 정보 전송
        if (ready) {
            String hostNickname = (String) sessions.get(0).getUserProperties().get("nickname");
            String guestNickname = (String) sessions.get(1).getUserProperties().get("nickname");

            for (int i = 0; i < sessions.size(); i++) {
                Session s = sessions.get(i);
                boolean isHost = (i == 0);

                try {
                    JSONObject msg = new JSONObject();
                    msg.put("type", "userJoined");
                    msg.put("ready", true);
                    msg.put("isHost", isHost);
                    msg.put("hostNickname", hostNickname);
                    msg.put("guestNickname", guestNickname);
                    s.getBasicRemote().sendText(msg.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // 1명만 입장한 경우 기본 메시지 전송
            try {
                JSONObject msg = new JSONObject();
                msg.put("type", "userJoined");
                msg.put("ready", false);
                msg.put("isHost", true); // 최초 입장자는 방장
                msg.put("hostNickname", nickname); // 자기 닉네임만 알려줌
                msg.put("guestNickname", JSONObject.NULL);
                session.getBasicRemote().sendText(msg.toString());
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