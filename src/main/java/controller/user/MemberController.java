package main.java.controller.user;

import main.java.db.HistoryDAO;
import main.java.dto.HistoryDTO;
import main.java.domain.Member;
import main.java.dto.MemberInfoDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serial;
import java.util.List;

@WebServlet(urlPatterns = {"/myhistory", "/mypage"})
public class MemberController extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private HistoryDAO historyDAO = HistoryDAO.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int PAGE_SIZE = 8;
        String path = request.getServletPath();
        if("/myhistory".equals(path)) {
            int currentPage = Integer.parseInt(request.getParameter("page") == null ? "1" : request.getParameter("page"));

            HttpSession session = request.getSession();
            Member member = (Member) session.getAttribute("loginUser");
            if (member == null) {
                response.sendRedirect("login");
                return;
            }

            int memberId = member.getId();

            int totalCount = historyDAO.countByMemberId(member.getId()); // 전체 개수
            int totalPages = (int)Math.ceil((double)totalCount / PAGE_SIZE);
            int offset = (currentPage - 1) * PAGE_SIZE;

            List<HistoryDTO> historyList = historyDAO.findByMemberIdWithPaging(memberId, offset, PAGE_SIZE);

            request.setAttribute("pageSize", PAGE_SIZE);
            request.setAttribute("historyList", historyList);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.getRequestDispatcher("/WEB-INF/views/user/myhistory.jsp").forward(request, response);
        }else if("/mypage".equals(path)) {
            HttpSession session = request.getSession();
            Member member = (Member) session.getAttribute("loginUser");
            if (member == null) {
                response.sendRedirect("login");
                return;
            }

            MemberInfoDTO dto = historyDAO.getRankingByMemberId(member.getId());
            request.setAttribute("info", dto);
            request.setAttribute("winRate", dto.getWinRate());
            request.getRequestDispatcher("/WEB-INF/views/user/mypage.jsp").forward(request, response);
        }
    }
}
