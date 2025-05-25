package main.db;

import main.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static main.util.DBUtil.getConnection;

public class RoomDAO {

    public boolean insertRoom(Room room) {
        String sql = "INSERT INTO rooms (name, status) VALUES (?, ?)";

        try (
                Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, room.getName());
            pstmt.setString(2, room.getStatus());
            pstmt.executeUpdate();

            // DB에서 생성된 AUTO_INCREMENT ID 가져오기
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    room.setId(rs.getInt(1)); // 생성된 id를 room 객체에 저장
                    return true; // 성공
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; //실패
    }

    //목록 조회
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT id, name, status FROM rooms";

        try (
                Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setName(rs.getString("name"));
                room.setStatus(rs.getString("status")); // DB에서 상태 가져오기
                rooms.add(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rooms;
    }

    /**
     * 방 상태 업데이트
     * @param roomId 방 ID
     * @param status 새로운 상태
     * @return 성공 여부
     */
    public boolean updateRoomStatus(int roomId, String status) {
        String sql = "UPDATE rooms SET status = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, roomId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("방 상태 업데이트 실패: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    /**
     * DB에서 가장 큰 room_id 값을 조회
     * @return 마지막 방 ID, 방이 없으면 0 반환
     */
    public int getLastRoomId() {
        String sql = "SELECT MAX(id) FROM rooms";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1); // MAX(id) 값 반환
            }

        } catch (SQLException e) {
            System.err.println("마지막 방 ID 조회 실패: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return 0; // 실패하거나 방이 없으면 0 반환
    }

    //방의 총 개수
    public int getRoomcountAll(){
        int total = 0;
        String sql = "SELECT COUNT(*) FROM rooms";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return total = rs.getInt(1); //COUNT(*) 값 반환
            }
        } catch (SQLException e) {
            System.err.println("방 총 개수 조회 실패: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0;  // 실패하거나 방이 없으면 0 반환
    }

    //페이징 처리
    public List<Room> getRoomsByPage(int offset, int limit) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY id ASC LIMIT ?, ?";

        try (
                Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, offset);
            pstmt.setInt(2, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setName(rs.getString("name"));
                    room.setStatus(rs.getString("status"));
                    rooms.add(room);
                }
            }
        } catch (Exception e) {
            System.err.println("페이징처리 실패: " + e.getMessage());
            e.printStackTrace();
        }

        return rooms;
    }


}