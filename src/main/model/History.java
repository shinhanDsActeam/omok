package main.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class History {
    private int id;        // pk
    private int memberId;  // 사용자 pk
    private int win;       // 이긴 횟수
    private int lose;      // 진 횟수
}
