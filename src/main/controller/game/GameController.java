package main.controller.game;

import main.dto.Player;
import main.service.Board;

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

    private final Map<String, Board> boards = new ConcurrentHashMap<>();
    private final Map<String, Player[]> players = new ConcurrentHashMap<>();
    private final Map<String, Player> currentPlayers = new ConcurrentHashMap<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

            board.placeStone(row, col, currentPlayer.stone);
            boolean isWin = board.checkWin(row, col, currentPlayer.stone);
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

    private String jsonError(String message) {
        return String.format("{\"success\":false, \"message\":\"%s\"}", message);
    }
}