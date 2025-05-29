package main.java.service;

import main.java.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class HistoryService {

    public static void recordMatchResult(long hostId, long guestId, String winner) {
        String sql = "INSERT INTO history (member_id, opponent_id, result) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if ("host".equalsIgnoreCase(winner)) {
                // host 승
                pstmt.setLong(1, hostId);
                pstmt.setLong(2, guestId);
                pstmt.setString(3, "WIN");
                pstmt.executeUpdate();
            } else if ("guest".equalsIgnoreCase(winner)) {
                // guest 승
                pstmt.setLong(1, hostId);
                pstmt.setLong(2, guestId);
                pstmt.setString(3, "LOSE");
                pstmt.executeUpdate();

            } else if ("draw".equalsIgnoreCase(winner)) {
                // 무승부
                pstmt.setLong(1, hostId);
                pstmt.setLong(2, guestId);
                pstmt.setString(3, "DRAW");
                pstmt.executeUpdate();
            }

        } catch (Exception e) {
            System.err.println("⚠ 전적 저장 중 오류 발생:");
            e.printStackTrace();
        }
    }
}

