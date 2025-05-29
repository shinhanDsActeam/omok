package main.java.domain;
import lombok.Getter;
import lombok.Setter;
import main.java.dto.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Room {
    private int id;
    private String name;   // 방제
    private String status; // "대기중", "게임중" 등
    private int creator;   // 방 생성자 ID
    private List<Integer> players;  // 참여 플레이어 id 목록

    public Room(int id, String name) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.status = "대기중";
        this.players = new ArrayList<>();
    }

    public Room() {
        this.players = new ArrayList<>();  // 중요: null 방지
        this.status = "대기중";  // 기본 상태도 설정
    }

    public void setPlayers(List<Integer> players) {
        this.players = players != null ? players : new ArrayList<>();
    }

    public int getPlayersSize(){
        if(players == null) return 0;
        return players.size();
    }

    public boolean addPlayer(int player) {
        if (this.players == null) {
            this.players = new ArrayList<>();
        }
        if (!players.contains(player)) {
            players.add(player);
            return true;
        }
        return false;
    }

    public boolean deletePlayer(int player) {
        if (this.players == null) {
            return false;
        }
        if (players.contains(player)) {
            players.remove(player);
            return true;
        }
        return false;
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