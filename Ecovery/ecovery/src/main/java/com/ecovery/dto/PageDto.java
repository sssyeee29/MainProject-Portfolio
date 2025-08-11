package com.ecovery.dto;

import lombok.Getter;
import lombok.ToString;

/*
 * 페이징 처리에 필요한 계산 및 정보 전달을 위한 DTO 클래스
 * 화면에 보여줄 시작 페이지, 끝 페이지, 이전/다음 버튼 유무를 계산
 * @author : yeonsu
 * @fileName : PageDTO
 * @since : 250710
 */

@Getter // 조회만 하겠다는 의미
@ToString
public class PageDto {

    private int startPage;
    private int endPage;
    private boolean prev, next;

    private int total; // 전체 데이터 갯수

    private Criteria cri; // 페이징정보, 페이지당 데이터 갯수(pageNum, amount)

    public PageDto(Criteria cri, int total){
        this.cri = cri;
        this.total = total;

        // 현재 페이지 기준으로 한 번에 10페이지씩 묶어서 보여주기 위한 계산
        this.endPage = (int)(Math.ceil(cri.getPageNum()/10.0))*10;
        this.startPage = this.endPage - 9; // 시작페이지

        // 전체 게시글 수로 실제 마지막 페이지 번호 계산
        int realEnd = (int)(Math.ceil((total*1.0)/cri.getAmount()));

        if(realEnd < this.endPage){
            this.endPage = realEnd;
        }

        this.prev = this.startPage > 1;
        this.next = this.endPage < realEnd;
    }
}
