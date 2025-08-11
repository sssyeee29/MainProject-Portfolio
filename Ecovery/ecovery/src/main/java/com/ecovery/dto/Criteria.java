package com.ecovery.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/*
 * 페이징 처리 Criteria
 * 게시판 검색 및 페이징 처리를 위한 기준 클래스
 * @author : yeonsu
 * @fileName : Criteria
 * @since : 250710
 * @history
     - 250710 | yeonsu | 페이징 처리를 위한 Criteria 작성
     - 250714 | yukyeong | getOffset() 추가
     - 250731 | yukyeong | 카테고리 필터링을 위해 필드 추가
 */

@Getter
@Setter
@ToString
public class Criteria {

    private int pageNum; // 페이지
    private int amount; // 한페이지에 보여줄 게시글 수

    private String type; // 검색조건 (free - 제목, 내용, 지역구, 작성자)
    private String keyword; // 실제 검색 키워드

    // Env 게시판 카테고리 필터링을 위한 필드 추가
    private String category;

    public Criteria(){
        this(1,10);
    }

    // 원하는 페이지, 게시글 수 직접 설정
    public Criteria(int pageNum, int amount){
        this.pageNum = pageNum;
        this.amount = amount;
    }

    public String[] getTypeArr(){

        return type == null? new String[] {}: type.split("");
    }

    // MySQL용 offset
    public int getOffset() {

        return (pageNum - 1) * amount;
    }


    //disposal 검색 조건을 위해 추가
    private String regionGu;
    private String aiPrediction;
    private String aiConfidenceRange;
    private String dateRange;

    //memberList 검색 조건을 위해 추가
    private String role;
    private String provider;
}
