package main.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Paging {
    private int currentpage; // 현재 페이지
    private int pageNum;     // 페이지 네비게이션 수
    private boolean start;   // 이전 버튼 필요 여부
    private boolean end;     // 다음 버튼 필요 여부
    private int endPage;     // 마지막 페이지 번호
    private int startPage;   // 시작 페이지 번호
    private int totalPages;  // 총 페이지 수
}


