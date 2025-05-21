<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%
    List<String> tests = (List<String>) request.getAttribute("tests");
%>
<html>
<body>
  <h2>Test List</h2>
  <ul>
  <%
    for(String t : tests) {
  %>
    <li><%= t %></li>
  <%
    }
  %>
  </ul>
</body>
</html>
