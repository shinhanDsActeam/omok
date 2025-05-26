package src.main.controller.user;

import main.db.HistoryDAO;
import main.dto.HistoryDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;
import java.util.List;

@WebServlet(urlPatterns = {"/mypage"})
public class MemberController extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private HistoryDAO historyDAO = HistoryDAO.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if("/mypage".equals(path)) {
            response.setContentType("text/html; charset=UTF-8");

            PrintWriter out = response.getWriter();

            int currentPage = Integer.parseInt(request.getParameter("page") == null ? "1" : request.getParameter("page"));
            int pageSize = 10;

            int userId = 1;
//            TODO : 윤희님 코드 머지 후 userId는 세션에서 가져오기.
//            HttpSession session = request.getSession();
//            Integer userId = (Integer) session.getAttribute("userId");
//            if (userId == null) {
//                response.sendRedirect("login.jsp");
//                return;
//            }

            int totalCount = historyDAO.countByUser(userId); // 전체 개수
            int totalPages = (int)Math.ceil((double)totalCount / pageSize);
            int offset = (currentPage - 1) * pageSize;

            List<HistoryDTO> historyList = historyDAO.findByUser(userId, offset, pageSize);

            request.setAttribute("historyList", historyList);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.getRequestDispatcher("/WEB-INF/views/user/mypage.jsp").forward(request, response);
        }
    }
}
