package main.java.db;

import main.java.domain.Member;
import main.java.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MemberDAO {
    private static MemberDAO instance = new MemberDAO();

    private MemberDAO() {
    }

    public static MemberDAO getInstance() {
        return instance;
    }

    public boolean insertJoin(Member member) {
        String checkSql = "SELECT COUNT(*) FROM member WHERE username = ?";
        String insertSql = "INSERT INTO member (username, password, nickname) VALUES (?, ?, ?)";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement checkStmt  = conn.prepareStatement(checkSql)
        ) {
            checkStmt.setString(1, member.getUsername());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("중복된 아이디입니다.");
                return false;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, member.getUsername());
                pstmt.setString(2, member.getPassword());
                pstmt.setString(3, member.getNickname());
                pstmt.executeUpdate();
                return true; // 성공
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // 실패
    }


    // 닉네임 중복체크
    public boolean checkDuplicateNickname(String nickname) {
        String sql = "SELECT COUNT(*) FROM member WHERE nickname = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, nickname);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // 중복된 닉네임이 있으면 true
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // 실패 또는 중복 아님
    }

    public Member findUserByUsername(String username) {
        String sql = "SELECT id, username, password, nickname FROM member WHERE username = ?";
        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Member(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("nickname")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int countTotalUser(){
        String sql = "SELECT COUNT(*) FROM member";
        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}