package main.java.controller.user;
import main.java.db.MemberDAO;
import main.java.domain.Member;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;

@WebServlet(urlPatterns = {"/login", "/join", "/check-username", "/check-nickname"})
public class MemberAuthController extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    MemberDAO repo = MemberDAO.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/join".equals(path)) {
            // 회원가입 페이지 표시
            request.getRequestDispatcher("/WEB-INF/views/user/join.jsp").forward(request, response);
        } else if ("/login".equals(path)) {
            // 로그인 페이지 표시
            request.getRequestDispatcher("/WEB-INF/views/user/login.jsp").forward(request, response);
        } else if ("/mypage".equals(path)) {
            // 마이페이지 표시
            request.getRequestDispatcher("/WEB-INF/views/user/mypage.jsp").forward(request, response);
        }  else if ("/check-username".equals(path)) {
            // 아이디 중복 체크
            String username = request.getParameter("username");
            boolean duplicate = repo.findUserByUsername(username) != null;

            response.setContentType("application/json");
            response.getWriter().write("{\"duplicate\":" + duplicate + "}");
        } else if ("/check-nickname".equals(path)) {
            // 닉네임 중복 체크
            String nickname = request.getParameter("nickname");
            boolean duplicate = repo.checkDuplicateNickname(nickname);

            response.setContentType("application/json");
            response.getWriter().write("{\"duplicate\":" + duplicate + "}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/join".equals(path)) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String nickname = request.getParameter("nickname");

            // 유효성 검사 (간단 예시)
            if (username == null || password == null || nickname == null ||
                    username.isBlank() || password.isBlank() || nickname.isBlank()) {
                request.setAttribute("error", "모든 항목을 입력해주세요.");
                request.getRequestDispatcher("/WEB-INF/views/user/join.jsp").forward(request, response);
                return;
            }

            // 닉네임 중복 체크
            if (repo.checkDuplicateNickname(nickname)) {
                request.setAttribute("error", "중복된 닉네임입니다.");
                request.getRequestDispatcher("/WEB-INF/views/user/join.jsp").forward(request, response);
                return;
            }

            Member user = new Member(username, password, nickname);
            boolean success = repo.insertJoin(user);

            if (success) {
                request.setAttribute("message", "회원가입 성공! 로그인 해주세요.");
                response.sendRedirect(request.getContextPath() + "/login"); // 회원가입 성공 시 로그인 페이지로 이동
            } else {
                request.setAttribute("error", "회원가입 실패. 다시 시도해주세요.");
                request.getRequestDispatcher("/WEB-INF/views/user/join.jsp").forward(request, response);
            }

            System.out.println("username=" + username + ", password=" + password + ", nickname=" + nickname);
        } else if("/login".equals(path)) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            Member member = repo.findUserByUsername(username);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();

            if (member != null && member.getPassword().equals(password)) {
                // 로그인 성공
                request.getSession().setAttribute("loginUser", member);
                out.print("{\"success\": true}");
            } else {
                // 로그인 실패
                out.print("{\"success\": false}");
            }

            out.flush();
        }


    }
}
