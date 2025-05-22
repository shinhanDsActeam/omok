package main.controller.room;

import main.db.RoomDAO;
import main.model.Room;

import java.io.IOException;
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
    private static final long serialVersionUID = 1L;

    // 방 목록을 저장하는 Set (메모리상에 보관)
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
            // AJAX 요청으로 방 목록 조회 - 단순 JSP 포워딩으로 변경
            request.setAttribute("roomList", getRooms());
            request.getRequestDispatcher("/WEB-INF/views/room/room-list-fragment.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");  // 한글 깨짐 방지
        String path = request.getServletPath();

        if ("/createRoom".equals(path)) {
            // 새 방 생성
            String roomName = request.getParameter("roomName");
            if (roomName == null || roomName.trim().isEmpty()) {
                roomName = "방 " + roomIdGenerator.get();
            }

            HttpSession session = request.getSession();
            String userId = (String) session.getAttribute("userId");

            // 사용자 ID가 없는 경우 임시 ID 생성
            if (userId == null) {
                userId = "player" + System.currentTimeMillis();
                session.setAttribute("userId", userId);
            }
//            int roomId = roomIdGenerator.getAndIncrement();
//            Room newRoom = new Room(roomId, roomName, userId);
//            roomSet.add(newRoom);
//            // 게임 방으로 리다이렉트
//            response.sendRedirect("game?roomId=" + roomId);

            int roomId = roomIdGenerator.getAndIncrement(); // 현재 값을 가져오고 1 증가 -> 고유한 방번호 생성
            Room newRoom = new Room(roomId, roomName, userId);
            newRoom.setName(roomName);
            newRoom.setCreator(userId);

            RoomDAO dao = new RoomDAO();
            boolean success = dao.insertRoom(newRoom); //DB에 저장

            if (success) {
                //세션에 방 정보 저장
                session.setAttribute("roomId", newRoom.getId());
                session.setAttribute("roomCreator", newRoom.getCreator());
                session.setAttribute("roomPlayers", newRoom.getPlayers());
                session.setAttribute("roomStatus", newRoom.getStatus());

                //response.sendRedirect("game?roomId=" + roomId);
                response.sendRedirect("lobby"); //로비로 리다이렉트 → DB에서 목록 조회
            } else {
                response.getWriter().write("방 생성 실패");
            }
        } else if ("/joinRoom".equals(path)) {
            // 방 참여
            String roomIdStr = request.getParameter("roomId");
            if (roomIdStr != null && !roomIdStr.trim().isEmpty()) {
                try {
                    int roomId = Integer.parseInt(roomIdStr);
                    Room room = findRoomById(roomId);

                    if (room != null && "대기중".equals(room.getStatus())) {
                        HttpSession session = request.getSession();
                        String userId = (String) session.getAttribute("userId");

                        // 사용자 ID가 없는 경우 임시 ID 생성
                        if (userId == null) {
                            userId = "player" + System.currentTimeMillis();
                            session.setAttribute("userId", userId);
                        }

                        // 플레이어 추가 및 상태 변경
                        room.addPlayer(userId);
                        room.setStatus("게임중");

                        // 게임 방으로 리다이렉트
                        response.sendRedirect("game?roomId=" + roomId);
                        return;
                    }
                } catch (NumberFormatException e) {
                    // 잘못된 방 ID
                }
            }

            // 참여 실패 시 로비로 리다이렉트
            response.sendRedirect("lobby");
        }
    }

    // ID로 방 찾기
    private Room findRoomById(int roomId) {
        for (Room room : roomSet) {
            if (room.getId() == roomId) {
                return room;
            }
        }
        return null;
    }

    //방 목록 조회
    private List<Room> getRooms() {
        RoomDAO dao = new RoomDAO();
        return dao.getAllRooms();  //DB에서 가져오기
    }
}
