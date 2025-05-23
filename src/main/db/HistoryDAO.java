package main.db;

import main.dto.HistoryDTO;
import main.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistoryDAO {
    private static HistoryDAO instance = new HistoryDAO();

    private HistoryDAO() {
    }

    public static HistoryDAO getInstance() {
        return instance;
    }

    // 전체 전적 수 조회
    public int countByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM history WHERE member_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 특정 유저의 전적 페이징 조회
    public List<HistoryDTO> findByUser(int userId, int offset, int pageSize) {
        List<HistoryDTO> list = new ArrayList<>();
        String sql = "SELECT h.result, h.match_date, m.nickname AS opponent " +
                "FROM history h " +
                "JOIN member m ON h.opponent_id = m.id " +
                "WHERE h.member_id = ? " +
                "ORDER BY h.match_date DESC " +
                "LIMIT ? OFFSET ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, pageSize);
            pstmt.setInt(3, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HistoryDTO dto = new HistoryDTO();
                    dto.setResult(rs.getString("result"));
                    dto.setMatchDate(rs.getDate("match_date").toString());
                    dto.setOpponent(rs.getString("opponent"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    // 3. 결과 저장
    public void addHistory(int userId, int opponentId, String result) {
        String sql = "INSERT INTO history (member_id, opponent_id, result) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, opponentId);
            pstmt.setString(3, result);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}