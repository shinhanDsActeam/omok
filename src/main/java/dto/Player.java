package main.java.dto;

import main.java.domain.Member;

public class Player {
    public Member member;
    public String stone; // black || white
    public Player(Member member, String stone) {
        this.member = member;
        this.stone = stone;
    }

    public String getName(){
        return member.getNickname();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player) {
            Player p = (Player) obj;
            return this.member.equals(p.member);
        }
        return false;
    }
}
