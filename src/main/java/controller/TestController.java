package main.java.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/test")
public class TestController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String> tests = Arrays.asList("Test1", "Test2", "Test3");
        request.setAttribute("tests", tests);

        request.getRequestDispatcher("/view/Test.jsp").forward(request, response);
    }
}
