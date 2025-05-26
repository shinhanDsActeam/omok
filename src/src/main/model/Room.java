package src.main.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Room {
    private int id;
    private String name;
    private String status;  // "대기중", "게임중" 등
    private String creator;  // 방 생성자 ID
    private List<String> players;  // 참여 플레이어 ID 목록

    public Room(int id, String name, String creator) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.status = "대기중";
        this.players = new ArrayList<>();
        this.players.add(creator);  // 생성자를 첫 번째 플레이어로 추가
    }

    public Room() {
        this.players = new ArrayList<>();  // 중요: null 방지
        this.status = "대기중";  // 기본 상태도 설정
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players != null ? players : new ArrayList<>();
    }

    public void addPlayer(String playerId) {
        if (this.players == null) {
            this.players = new ArrayList<>();
        }
        if (!players.contains(playerId)) {
            players.add(playerId);
        }
    }

    public boolean isFull() {
        if (this.players == null) {
            return false;
        }
        // 1대1 게임이므로 2명이 차면 가득 참
        return players.size() >= 2;
    }

    // Set에서 사용하기 위한 equals와 hashCode 메서드 오버라이드
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return id == room.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}