package main.controller.room;

import main.db.RoomDAO;
import main.model.Room;

import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/lobby", "/createRoom", "/joinRoom", "/getRoomList"})
public class RoomController extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    // 방 목록을 저장하는 Set (메모리상에 보관) - 실제 게임 진행 정보
    private static final Set<Room> roomSet = ConcurrentHashMap.newKeySet();
    private static final AtomicInteger roomIdGenerator = new AtomicInteger(1);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/lobby".equals(path)) {
            // 로비 페이지 표시
            request.setAttribute("roomList", getRooms());
            request.getRequestDispatcher("/WEB-INF/views/room/room.jsp").forward(request, response);
        } else if ("/getRoomList".equals(path)) {
            // AJAX 요청으로 방 목록 조회
            request.setAttribute("roomList", getRooms());
            request.getRequestDispatcher("/WEB-INF/views/room/room-list-fragment.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();

        if ("/createRoom".equals(path)) {
            // 새 방 생성
            String roomName = request.getParameter("roomName");
            if (roomName == null || roomName.trim().isEmpty()) {
                roomName = "방 " + roomIdGenerator.get();
            }

            HttpSession session = request.getSession();
            String userId = (String) session.getAttribute("userId");

            if (userId == null) {
                userId = "player" + System.currentTimeMillis();
                session.setAttribute("userId", userId);
            }

            int roomId = roomIdGenerator.getAndIncrement();
            Room newRoom = new Room(roomId, roomName, userId);

            RoomDAO dao = new RoomDAO();
            boolean success = dao.insertRoom(newRoom);

            if (success) {
                roomSet.add(newRoom);

                session.setAttribute("roomId", newRoom.getId());
                session.setAttribute("roomCreator", newRoom.getCreator());
                session.setAttribute("roomPlayers", newRoom.getPlayers());
                session.setAttribute("roomStatus", newRoom.getStatus());

                response.sendRedirect("lobby");
            } else {
                response.getWriter().write("방 생성 실패");
            }
        } else if ("/joinRoom".equals(path)) {
            String roomIdStr = request.getParameter("roomId");
            if (roomIdStr != null && !roomIdStr.trim().isEmpty()) {
                try {
                    int roomId = Integer.parseInt(roomIdStr);
                    Room room = findRoomById(roomId);

                    if (room != null && "대기중".equals(room.getStatus())) {
                        HttpSession session = request.getSession();
                        String userId = (String) session.getAttribute("userId");

                        if (userId == null) {
                            userId = "player" + System.currentTimeMillis();
                            session.setAttribute("userId", userId);
                        }

                        room.addPlayer(userId);
                        room.setStatus("게임중");

                        response.sendRedirect("game?roomId=" + roomId);
                        return;
                    }
                } catch (NumberFormatException e) {
                    // 잘못된 방 ID
                }
            }
            response.sendRedirect("lobby");
        }
    }

    private Room findRoomById(int roomId) {
        for (Room room : roomSet) {
            if (room.getId() == roomId) {
                return room;
            }
        }
        return null;
    }

    private List<Room> getRooms() {
        List<Room> displayRooms = new ArrayList<>();

        // DB에서 방 기본 정보 가져오기
        RoomDAO dao = new RoomDAO();
        List<Room> dbRooms = dao.getAllRooms();

        for (Room dbRoom : dbRooms) {
            // 메모리에서 해당 방의 실제 상태 찾기
            Room memoryRoom = findRoomById(dbRoom.getId());

            if (memoryRoom != null) {
                // 메모리에 있는 방: 실제 상태 사용
                displayRooms.add(memoryRoom);
            } else {
                // 메모리에 없는 방: DB 정보 + 기본 상태
                dbRoom.setStatus("대기중");
                if (dbRoom.getPlayers() == null) {
                    dbRoom.setPlayers(new ArrayList<>());
                }
                displayRooms.add(dbRoom);

                // 향후 참여를 위해 메모리에도 추가
                roomSet.add(dbRoom);
            }
        }

        return displayRooms;
    }
}