package main.java.controller.game;

import main.java.dto.Player;
import main.java.service.Board;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet(urlPatterns = {"/game"})
public class GameController extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final Map<String, Board> boards = new ConcurrentHashMap<>();
    private static final Map<String, Player[]> players = new ConcurrentHashMap<>();
    private static final Map<String, Player> currentPlayers = new ConcurrentHashMap<>();

    public static Board getBoard(String roomId) {
        return boards.get(roomId);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ✅ 세션에서 로그인 사용자 확인
        var session = request.getSession(false);
        var loginUser = (session != null) ? session.getAttribute("loginUser") : null;

        if (loginUser == null) {
            // 로그인 안 된 상태면 로그인 페이지로 리다이렉트
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 정상 접근 시 게임 JSP로 이동
        String roomId = request.getParameter("roomId");
        request.setAttribute("roomId", roomId);
        request.getRequestDispatcher("WEB-INF/views/game/game.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String roomId = request.getParameter("roomId");
            if (roomId == null || roomId.trim().isEmpty()) {
                out.print(jsonError("roomId가 필요합니다."));
                return;
            }

            String action = request.getParameter("action");
            if ("restart".equals(action)) {
                initializeRoom(roomId);
                out.print("{\"success\":true, \"message\":\"게임이 초기화되었습니다.\"}");
                return;
            }

            initializeRoom(roomId); // 없으면 초기화함

            Board board = boards.get(roomId);
            Player[] roomPlayers = players.get(roomId);
            Player currentPlayer = currentPlayers.get(roomId);

            int row = Integer.parseInt(request.getParameter("row"));
            int col = Integer.parseInt(request.getParameter("col"));

            if (!board.isInBounds(row, col)) {
                out.print(jsonError("좌표가 보드를 벗어났습니다."));
                return;
            }

            if (!board.isEmpty(row, col)) {
                out.print(jsonError("이미 놓인 자리입니다."));
                return;
            }

            String stone = currentPlayer.stone.equals("black") ? "O" : "X";
            board.placeStone(row, col, stone);
            boolean isWin = board.checkWin(row, col, stone);
            String msg = currentPlayer.name + (isWin ? " 승리!" : "의 차례입니다");

            out.printf(
                    "{\"success\":true, \"stone\":\"%s\", \"message\":\"%s\", \"gameOver\":%b}",
                    currentPlayer.stone,
                    msg,
                    isWin
            );

            if (!isWin) {
                currentPlayers.put(roomId, currentPlayer == roomPlayers[0] ? roomPlayers[1] : roomPlayers[0]);
            }

        } catch (Exception e) {
            out.print(jsonError("서버 오류: " + e.getMessage()));
        }
    }

    private void initializeRoom(String roomId) {
        boards.putIfAbsent(roomId, new Board(15));
        players.putIfAbsent(roomId, new Player[]{
                new Player("플레이어1", "black"),
                new Player("플레이어2", "white")
        });
        currentPlayers.putIfAbsent(roomId, players.get(roomId)[0]);
    }

    public static void resetRoom(String roomId) {
        boards.put(roomId, new Board(15));
        players.put(roomId, new Player[]{
                new Player("플레이어1", "black"),
                new Player("플레이어2", "white")
        });
        currentPlayers.put(roomId, players.get(roomId)[0]);
    }

    private String jsonError(String message) {
        return String.format("{\"success\":false, \"message\":\"%s\"}", message);
    }
}