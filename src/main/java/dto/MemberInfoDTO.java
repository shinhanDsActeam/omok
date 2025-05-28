package main.java.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberInfoDTO {
    private String nickname;
    private int totalCount;
    private int winCount;
    private int drawCount;
    private int loseCount;
    private double winRate;
}