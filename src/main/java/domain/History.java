package main.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class History {
    private int id;            // pk
    private int memberId;      // (fk) 사용자 pk
    private int opponentId;    // (fk) 상대유저 pk
    private String result;     // WIN, LOSE, DRAW
    private String matchDate;  // 대결 날짜
}

