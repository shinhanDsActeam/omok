package main.java.controller.rank;

import main.java.db.HistoryDAO;
import main.java.db.MemberDAO;
import main.java.dto.RankingDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.util.List;

@WebServlet(urlPatterns = {"/ranking"})
public class RankingController extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private HistoryDAO historyDAO = HistoryDAO.getInstance();
    private MemberDAO memberDAO = MemberDAO.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        int PAGE_SIZE = 8;

        if("/ranking".equals(path)) {
            int currentPage = Integer.parseInt(request.getParameter("page") == null ? "1" : request.getParameter("page"));


            int totalCount = memberDAO.countTotalUser(); // 유저 수
            int totalPages = (int)Math.ceil((double)totalCount / PAGE_SIZE);
            int offset = (currentPage - 1) * PAGE_SIZE;

            System.out.println("[ LOG ] : page = " + currentPage + ", pageSize = " + PAGE_SIZE + ", offset = " + offset
                    + ", totalCount = " + totalCount + ", totalPages = " + totalPages);

            List<RankingDTO> historyList = historyDAO.getRankingList(offset, PAGE_SIZE);

            request.setAttribute("pageSize", PAGE_SIZE);
            request.setAttribute("rankingList", historyList);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.getRequestDispatcher("/WEB-INF/views/ranking/ranking.jsp").forward(request, response);
        }
    }
}
