package com.ecovery.dto;

/*
 * 마이페이지 DTO
 * 여러 테이블에서 모은 정보를 하나의 객체로 전달해야함(프론트에 전달할 때 한 꺼번에 전달하기 위해)
 * @author : 방희경
 * @fileName : MemberPageDto
 * @since : 250714
 */

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberPageDto {

    private String nickname;
    private int point;

}
