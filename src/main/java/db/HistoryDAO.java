package main.java.db;

import main.java.dto.HistoryDTO;
import main.java.dto.RankingDTO;
import main.java.dto.MemberInfoDTO;
import main.java.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistoryDAO {
    private static HistoryDAO instance = new HistoryDAO();
    private HistoryDAO() {}
    public static HistoryDAO getInstance() {
        return instance;
    }

    // 전체 전적 수 조회
    public int countByMemberId(int memberId) {
        String sql = "SELECT COUNT(*) FROM history WHERE member_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
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
    public List<HistoryDTO> findByMemberIdWithPaging(int memberId, int offset, int pageSize) {
        List<HistoryDTO> list = new ArrayList<>();
        String sql = "SELECT  " +
                "    CASE  " +
                "        WHEN m.member_id = ? THEN opp.nickname  " +
                "        ELSE mem.nickname  " +
                "    END AS opponent,  " +
                "    CASE  " +
                "        WHEN m.member_id = ? THEN m.result  " +
                "        ELSE  " +
                "            CASE m.result  " +
                "                WHEN 'WIN' THEN 'LOSE'  " +
                "                WHEN 'LOSE' THEN 'WIN'  " +
                "                ELSE m.result  " +
                "            END  " +
                "    END AS result,  " +
                "    m.match_date  " +
                "FROM history m  " +
                "JOIN member mem ON m.member_id = mem.id  " +
                "JOIN member opp ON m.opponent_id = opp.id  " +
                "WHERE m.member_id = ? OR m.opponent_id = ?  " +
                "ORDER BY m.match_date DESC  " +
                "LIMIT ? OFFSET ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.setInt(2, memberId);
            pstmt.setInt(3, memberId);
            pstmt.setInt(4, memberId);
            pstmt.setInt(5, pageSize);
            pstmt.setInt(6, offset);

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
    public void addHistory(int memberId, int opponentId, String result) {
        String sql = "INSERT INTO history (member_id, opponent_id, result) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            pstmt.setInt(2, opponentId);
            pstmt.setString(3, result);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MemberInfoDTO getRankingByMemberId(int memberId) {
        MemberInfoDTO memberInfo = new MemberInfoDTO();
        String sql = "SELECT " +
                "    m.nickname AS nickname, " +
                "    SUM(CASE " +
                "        WHEN h.member_id = m.id AND h.result = 'WIN' THEN 1 " +
                "        WHEN h.opponent_id = m.id AND h.result = 'LOSE' THEN 1 " +
                "        ELSE 0 END) AS win_count, " +
                "    SUM(CASE " +
                "        WHEN h.member_id = m.id AND h.result = 'LOSE' THEN 1 " +
                "        WHEN h.opponent_id = m.id AND h.result = 'WIN' THEN 1 " +
                "        ELSE 0 END) AS lose_count, " +
                "    SUM(CASE " +
                "        WHEN h.result = 'DRAW' AND (h.member_id = m.id OR h.opponent_id = m.id) THEN 1 " +
                "        ELSE 0 END) AS draw_count, " +
                "    COUNT(h.id) AS total_count, " +
                "    COALESCE(ROUND(( " +
                "        SUM(CASE " +
                "            WHEN h.member_id = m.id AND h.result = 'WIN' THEN 1 " +
                "            WHEN h.opponent_id = m.id AND h.result = 'LOSE' THEN 1 " +
                "            ELSE 0 END) " +
                "        / COUNT(h.id) " +
                "    ) * 100, 2),0) AS win_rate " +
                "FROM member m " +
                "LEFT JOIN history h ON (h.member_id = m.id OR h.opponent_id = m.id) " +
                "WHERE m.id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    memberInfo.setNickname(rs.getString("nickname"));
                    memberInfo.setTotalCount(rs.getInt("total_count"));
                    memberInfo.setWinCount(rs.getInt("win_count"));
                    memberInfo.setLoseCount(rs.getInt("lose_count"));
                    memberInfo.setDrawCount(rs.getInt("draw_count"));
                    memberInfo.setWinRate(rs.getDouble("win_rate"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return memberInfo;
    }

    public List<RankingDTO> getRankingList(int offset, int pageSize) {
        List<RankingDTO> list = new ArrayList<>();
        String sql = "SELECT  " +
                "    RANK() OVER (ORDER BY score DESC) AS ranking,  " +
                "    member_id,  " +
                "    member_nickname,  " +
                "    total_count, win_count, draw_count, lose_count, " +
                "    win_rate,  " +
                "    score  " +
                "FROM (  " +
                "    SELECT  " +
                "    m.id AS member_id,  " +
                "    m.nickname AS member_nickname,  " +
                "    SUM(CASE  " +
                "        WHEN h.member_id = m.id AND h.result = 'WIN' THEN 1  " +
                "        WHEN h.opponent_id = m.id AND h.result = 'LOSE' THEN 1  " +
                "        ELSE 0 END) AS win_count,  " +
                "    SUM(CASE  " +
                "        WHEN h.member_id = m.id AND h.result = 'LOSE' THEN 1  " +
                "        WHEN h.opponent_id = m.id AND h.result = 'WIN' THEN 1  " +
                "        ELSE 0 END) AS lose_count,  " +
                "    SUM(CASE  " +
                "        WHEN h.result = 'DRAW' AND (h.member_id = m.id OR h.opponent_id = m.id) THEN 1  " +
                "        ELSE 0 END) AS draw_count,  " +
                "    COUNT(h.id) AS total_count,  " +
                "    COALESCE(ROUND((  " +
                "        SUM(CASE  " +
                "            WHEN h.member_id = m.id AND h.result = 'WIN' THEN 1  " +
                "            WHEN h.opponent_id = m.id AND h.result = 'LOSE' THEN 1  " +
                "            ELSE 0 END)  " +
                "        / COUNT(h.id)  " +
                "    ) * 100, 2),0) AS win_rate,  " +
                "    COALESCE(ROUND((  " +
                "        (SUM(CASE  " +
                "            WHEN h.member_id = m.id AND h.result = 'WIN' THEN 1  " +
                "            WHEN h.opponent_id = m.id AND h.result = 'LOSE' THEN 1  " +
                "            ELSE 0 END) / COUNT(h.id)) * 100  " +
                "    ) * LOG(GREATEST(COUNT(h.id), 1)), 2),0) AS score  " +
                "    FROM member m  " +
                "    LEFT JOIN history h ON (h.member_id = m.id OR h.opponent_id = m.id)  " +
                "    GROUP BY m.id  " +
                ") AS ranked  " +
                "ORDER BY ranking "+
                "LIMIT ? OFFSET ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pageSize);
            pstmt.setInt(2, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RankingDTO dto = new RankingDTO();
                    dto.setMemberId(rs.getInt("member_id"));
                    dto.setRank(rs.getInt("ranking"));
                    dto.setNickname(rs.getString("member_nickname"));
                    dto.setTotalCount(rs.getInt("total_count"));
                    dto.setWinCount(rs.getInt("win_count"));
                    dto.setDrawCount(rs.getInt("draw_count"));
                    dto.setLoseCount(rs.getInt("lose_count"));
                    dto.setWinRate(rs.getDouble("win_rate"));
                    dto.setScore(rs.getDouble("score"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for(RankingDTO r : list){
            System.out.println(r.toString());
        }
        return list;
    }
}