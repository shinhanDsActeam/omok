package main.java.controller.rank;

import main.db.HistoryDAO;
import main.db.MemberDAO;
import main.dto.RankingDTO;

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
        if("/ranking".equals(path)) {
            int currentPage = Integer.parseInt(request.getParameter("page") == null ? "1" : request.getParameter("page"));
            int pageSize = 10;

            int totalCount = memberDAO.countTotalUser(); // 유저 수
            int totalPages = (int)Math.ceil((double)totalCount / pageSize);
            int offset = (currentPage - 1) * pageSize;

            System.out.println("[ LOG ] : page = " + currentPage + ", pageSize = " + pageSize + ", offset = " + offset
                    + ", totalCount = " + totalCount + ", totalPages = " + totalPages);

            List<RankingDTO> historyList = historyDAO.getRanking(offset, pageSize);

            request.setAttribute("rankingList", historyList);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.getRequestDispatcher("/WEB-INF/views/user/ranking.jsp").forward(request, response);
        }
    }
}
