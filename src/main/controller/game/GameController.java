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

@WebServlet(urlPatterns = {"/game"})
public class GameController extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;

    @Override
    public void init() throws ServletException {
        initializeGame();
    }

    private void initializeGame() {
        board = new Board(15);
        player1 = new Player("플레이어1", "black");
        player2 = new Player("플레이어2", "white");
        currentPlayer = player1;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //roomId 파라미터를 받아서 필요하다면 request에 저장
        String roomId = request.getParameter("roomId");
        request.setAttribute("roomId", roomId);

        // 게임 JSP로 포워딩
        request.getRequestDispatcher("WEB-INF/views/game/game.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String action = request.getParameter("action");
            if ("restart".equals(action)) {
                initializeGame();
                out.print("{\"success\":true, \"message\":\"게임이 초기화되었습니다.\"}");
                return;
            }

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
                currentPlayer = (currentPlayer == player1) ? player2 : player1;
            }

        } catch (Exception e) {
            out.print(jsonError("서버 오류: " + e.getMessage()));
        }
    }

    private String jsonError(String message) {
        return String.format("{\"success\":false, \"message\":\"%s\"}", message);
    }
}
