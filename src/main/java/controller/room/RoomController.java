package main.java.controller.room;

import main.java.db.RoomDAO;
import main.java.domain.Member;
import main.java.dto.Paging;
import main.java.domain.Room;
import main.java.dto.Player;

import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;
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

@WebServlet(urlPatterns = {"/lobby", "/createRoom", "/joinRoom", "/getRoomList", "/leaveRoom", "/deleteRoom"})
public class RoomController extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    private static RoomDAO roomDAO = RoomDAO.getInstance();

    // 방 목록을 저장하는 Set (메모리상에 보관) - 실제 게임 진행 정보
    private static final Set<Room> roomSet = ConcurrentHashMap.newKeySet();
//    private static final AtomicInteger roomIdGenerator = new AtomicInteger(1);
//
//    static {
//        try {
//            int lastRoomId = roomDAO.getLastRoomId();
//            if (lastRoomId > 0) {
//                roomIdGenerator.set(lastRoomId + 1);
//            }
//        } catch (Exception e) {
//            System.err.println("방 ID 초기화 실패: " + e.getMessage());
//        }
//    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        if (!checkLogin(request, response)) return;

        String pageParam = request.getParameter("page");
        int currentPage = 1;
        if (pageParam != null) {
            try {
                currentPage = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        int postPage = 7; // 한 페이지당 방 수
        int pageNum = 7;  // 페이지 네비게이션 범위
        int total = roomDAO.getRoomCountAll(); //전체 방 수
        int totalPages = ((total - 1) / postPage) + 1; //전체 페이지 개수

        int startPage = ((currentPage - 1) / pageNum) * pageNum + 1;
        //현재 페이지의 마지막 번호
        int endPage = startPage + pageNum - 1;
        if (endPage > totalPages) {
            endPage = totalPages;
        }

        //이전&다음
        boolean prev = startPage > 1;
        boolean next = endPage < totalPages;

        int offset = (currentPage - 1) * postPage;

        Paging paging = new Paging();
        paging.setCurrentpage(currentPage);
        paging.setPageNum(pageNum);
        paging.setStart(prev);
        paging.setEnd(next);
        paging.setStartPage(startPage);
        paging.setEndPage(endPage);
        paging.setTotalPages(totalPages);

        //디버깅용 로그
//        System.out.println("==페이징처리==");
//        System.out.println("현재 페이지: "+currentPage+", 시작 페이지: "+startPage+", 마지막 페이지: "+endPage+", 총 페이징 수: "+totalPages);

        //해당 페이징에 필요한 방만 가져오기
        List<Room> dbRooms = roomDAO.getRoomsByPage(offset, postPage);
        List<Room> displayRooms = getDisplayRooms(dbRooms);

        // 페이지 표시
        request.setAttribute("roomList", displayRooms);
        request.setAttribute("paging", paging);


        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute("loginUser");
        session.setAttribute("userId", member.getId());

        //session.setAttribute("roomCreator", displayRooms);

        if ("/lobby".equals(path)) {
            request.getRequestDispatcher("/WEB-INF/views/room/room.jsp").forward(request, response);
        } else if ("/leaveRoom".equals(path)) {
            String roomIdStr = request.getParameter("roomId");
//            HttpSession session = request.getSession();
//            Member member = (Member) session.getAttribute("loginUser");

            if (roomIdStr != null && member != null) {
                try {
                    int roomId = Integer.parseInt(roomIdStr);
                    boolean roomDeleted = leaveRoom(roomId, member.getId());

                    // 세션에서 방 관련 정보 제거
                    session.removeAttribute("roomId");
                    session.removeAttribute("roomCreator");
                    session.removeAttribute("roomPlayers");
                    session.removeAttribute("roomStatus");

                    if (roomDeleted) {
                        System.out.println("방 " + roomId + "가 모든 플레이어가 나가서 삭제되었습니다.");
                    }

                    response.sendRedirect("lobby");
                    return; // 중요: 리턴으로 메서드 종료
                } catch (NumberFormatException e) {
                    response.sendRedirect("lobby");
                    return;
                }
            } else {
                response.sendRedirect("lobby");
                return;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();

        if (!checkLogin(request, response)) return;

        if ("/createRoom".equals(path)) {
            // 새 방 생성
            String roomName = request.getParameter("roomName");
            if (roomName == null || roomName.trim().isEmpty()) {
                roomName = "방";
            }

            HttpSession session = request.getSession();
            Member member = (Member) session.getAttribute("loginUser");

            if (member == null) {
                response.sendRedirect("login");
                return;
            }

            Room newRoom = new Room();
            newRoom.setName(roomName);
            newRoom.setCreator(member.getId());
            newRoom.setStatus("대기중");
            // 플레이어 목록에 방장 추가
            newRoom.addPlayer(member.getId());

            boolean success = roomDAO.insertRoom(newRoom); // DB에서 id 세팅됨

            if (success) {
                int dbRoomId = newRoom.getId(); // DB에서 받아온 ID

                roomSet.add(newRoom);

                session.setAttribute("roomId", dbRoomId);
                session.setAttribute("roomCreator", newRoom.getCreator());
                session.setAttribute("roomPlayers", newRoom.getPlayers());
                session.setAttribute("roomStatus", newRoom.getStatus());

                // 생성된 DB ID를 기반으로 redirect
                response.sendRedirect("game?roomId=" + dbRoomId);
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
                        Member member = (Member) session.getAttribute("loginUser");

                        if (member == null) {
                            response.sendRedirect("login");
                            return;
                        }

                        if(!room.addPlayer(member.getId())){
                            System.out.println("[/joinRoom] : 플레이어 추가 실패 - 중복");
                        }

                        // 디버깅용 로그
                        System.out.println("=== 방 참여 [/joinRoom] ===");
                        System.out.println("방 ID: " + roomId + ", 참여자: " + member.getId());
                        System.out.println("참여 후 인원수: " + room.getPlayers().size());
                        System.out.println("플레이어 목록: " + room.getPlayers());
                        System.out.println("현재 방 상태: " + room.getStatus());

                        // 참여자가 2명이 되었을 때 게임중으로 변경
                        if (room.getPlayers().size() >= 2) {
                            room.setStatus("게임중");

                            // DB에도 상태 업데이트
                            boolean statusUpdated = roomDAO.updateRoomStatus(roomId, "게임중");

                            System.out.println("=== 상태 변경 ===");
                            System.out.println("방 " + roomId + " 상태를 '게임중'으로 변경");
                            System.out.println("DB 업데이트 결과: " + statusUpdated);
                        } else {
                            System.out.println("아직 인원이 부족합니다. 현재: " + room.getPlayers().size() + "명");
                        }

                        session.setAttribute("roomId", roomId);
                        session.setAttribute("roomCreator", room.getCreator());
                        session.setAttribute("roomPlayers", room.getPlayers());
                        session.setAttribute("roomStatus", room.getStatus());

                        // TODO : host 관리
                        // 참여자는 게임 화면으로 (host=false)
                        response.sendRedirect("game?roomId=" + roomId);
                        return;
                    } else if (room != null && "게임중".equals(room.getStatus())) {
                        // 이미 게임 중인 방에는 입장 불가
                        response.getWriter().write("이미 게임이 진행 중인 방입니다.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    // 잘못된 방 ID
                }
            }
            response.sendRedirect("lobby");
        }else if ("/leaveRoom".equals(path)) {
            // 방 나가기 처리
            String roomIdStr = request.getParameter("roomId");
            HttpSession session = request.getSession();
            Member member = (Member) session.getAttribute("loginUser");

            if (roomIdStr != null && member != null) {
                try {
                    int roomId = Integer.parseInt(roomIdStr);
                    boolean roomDeleted = leaveRoom(roomId, member.getId());

                    // 세션에서 방 관련 정보 제거
                    session.removeAttribute("roomId");
                    session.removeAttribute("roomCreator");
                    session.removeAttribute("roomPlayers");
                    session.removeAttribute("roomStatus");

                    if (roomDeleted) {
                        System.out.println("방 " + roomId + "가 모든 플레이어가 나가서 삭제되었습니다.");
                    }

                    response.sendRedirect("lobby");
                } catch (NumberFormatException e) {
                    response.sendRedirect("lobby");
                }
            } else {
                response.sendRedirect("lobby");
            }
        }
        else if ("/deleteRoom".equals(path)) {
            // 방 삭제 처리
            String roomIdStr = request.getParameter("roomId");

            HttpSession session = request.getSession();
            Member member = (Member) session.getAttribute("loginUser");
            session.setAttribute("userId", member.getId());

            if (roomIdStr != null && !roomIdStr.trim().isEmpty()) {
                try {
                    int roomId = Integer.parseInt(roomIdStr);
                    boolean deleted = deleteRoom(findRoomById(roomId));

                    if (deleted) {
                        response.getWriter().write("삭제 성공");
                        System.out.println("방 " + roomId + " 삭제 완료");
                    } else {
                        response.getWriter().write("삭제 실패");
                        System.out.println("방 " + roomId + " 삭제 실패");
                    }
                } catch (NumberFormatException e) {
                    response.getWriter().write("잘못된 방 ID");
                }
            } else {
                response.getWriter().write("방 ID가 필요합니다");
            }
        }
    }

    private static Room findRoomById(int roomId) {
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
        List<Room> dbRooms = roomDAO.getAllRooms();

        for (Room dbRoom : dbRooms) {
            // 메모리에서 해당 방의 실제 상태 찾기
            Room memoryRoom = findRoomById(dbRoom.getId());

            if (memoryRoom != null) {
                // 메모리에 있는 방: 실제 상태 사용
                displayRooms.add(memoryRoom);
            } else {
                // 메모리에 없는 방: DB 정보를 그대로 사용
                if (dbRoom.getPlayers() == null) {
                    dbRoom.setPlayers(new ArrayList<>());
                }
                displayRooms.add(dbRoom);

                // 향후 참여를 위해 메모리에도 추가 (DB 상태 유지)
                roomSet.add(dbRoom);
            }
        }

        return displayRooms;
    }

    /**
     * 화면에 보여지는 목룍만 추출해주는 메서드
     * (페이징 처리한 목록의 roomid를 가져옴)
     * */
    private List<Room> getDisplayRooms(List<Room> dbRooms) {
        List<Room> displayRooms = new ArrayList<>();
        for (Room dbRoom : dbRooms) {
            Room memoryRoom = findRoomById(dbRoom.getId());
            if (memoryRoom != null) {
                displayRooms.add(memoryRoom);
            } else {
                if (dbRoom.getPlayers() == null) {
                    dbRoom.setPlayers(new ArrayList<>());
                }
                displayRooms.add(dbRoom);
                roomSet.add(dbRoom);
            }
        }
        return displayRooms;
    }

    /**
     * 방에서 플레이어가 나가는 처리
     * @param roomId 방 ID
     * @param memberId 나가는 사용자 ID
     * @return 방이 삭제되었는지 여부
     */
    public static boolean leaveRoom(int roomId, int memberId) {
        Room room = findRoomById(roomId);

        if (room != null && room.getPlayers() != null) {
            // 💡 값으로 삭제하도록 수정
            room.getPlayers().remove((Object) memberId);

            System.out.println("=== 방 나가기 ===");
            System.out.println("방 ID: " + roomId + ", 나간 사용자: " + memberId);
            System.out.println("남은 인원수: " + room.getPlayers().size());
            System.out.println("남은 플레이어: " + room.getPlayers());

            // 💡 방에 아무도 없으면 삭제
            if (room.getPlayers().isEmpty()) {
                return deleteRoom(room);
            }
        }

        return false;
    }

    /**
     * 게임 종료 시 방 상태를 다시 '대기중'으로 변경하는 메서드
     * (GameController에서 호출 가능)
     */
    public static void resetRoomStatus(int roomId) {
        Room room = null;
        for (Room r : roomSet) {
            if (r.getId() == roomId) {
                room = r;
                break;
            }
        }

        if (room != null) {
            room.setStatus("대기중");
            room.getPlayers().clear(); // 플레이어 목록 초기화

            // DB에도 상태 업데이트
            roomDAO.updateRoomStatus(roomId, "대기중");
        }
    }

    /**
     * 방을 완전히 삭제하는 메서드
     * @param room 삭제할 방
     * @return 삭제 성공 여부
     */
    private static boolean deleteRoom(Room room) {
        // 메모리에서 방 제거
        if (room != null) {
            roomSet.remove(room);

            // DB에서도 방 삭제
            boolean deleted = roomDAO.deleteRoom(room.getId());

            System.out.println("=== 방 삭제 ===");
            System.out.println("방 ID: " + room.getId() + " 삭제 결과: " + deleted);

            return deleted;
        }

        return false;
    }

    /**
     * 게임 종료 시 모든 플레이어를 방에서 내보내고 방을 삭제하는 메서드
     * (GameController에서 게임 종료 시 호출)
     */
//    public static void endGameAndDeleteRoom(int roomId) {
//        deleteRoom(findRoomById(roomId));
//    }

    private boolean checkLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }


}
