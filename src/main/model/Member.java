package main.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Member {
    private int id;           // pk
    private String username;  // 사용자 id
    private String pw;
    private String nickname;
}
