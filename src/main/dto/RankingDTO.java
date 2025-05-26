package main.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class RankingDTO {
    private int memberId;
    private int rank;
    private String nickname;
    private int totalCount;
    private int winCount;
    private int drawCount;
    private int loseCount;
    private double winRate;
    private double score;
}