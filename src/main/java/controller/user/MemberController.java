package main.java.controller.user;

import main.db.HistoryDAO;
import main.dto.HistoryDTO;
import main.domain.Member;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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
            int currentPage = Integer.parseInt(request.getParameter("page") == null ? "1" : request.getParameter("page"));
            int pageSize = 10;

            HttpSession session = request.getSession();
            Member member = (Member) session.getAttribute("loginUser");
            if (member == null) {
                response.sendRedirect("login");
                return;
            }

            int memberId = member.getId();

            int totalCount = historyDAO.countByMemberId(member.getId()); // 전체 개수
            int totalPages = (int)Math.ceil((double)totalCount / pageSize);
            int offset = (currentPage - 1) * pageSize;

            List<HistoryDTO> historyList = historyDAO.findByMemberIdWithPaging(memberId, offset, pageSize);

            request.setAttribute("historyList", historyList);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.getRequestDispatcher("/WEB-INF/views/user/mypage.jsp").forward(request, response);
        }
    }
}
