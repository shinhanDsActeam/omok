package main.db;

import main.model.Room;
import main.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public boolean insertRoom(Room room) {
        String sql = "INSERT INTO rooms (name) VALUES (?)";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, room.getName());
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
        String sql = "SELECT id, name FROM rooms";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setName(rs.getString("name"));
                room.setStatus("대기중"); // 기본값 사용
                rooms.add(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rooms;
    }
}