package com.ecovery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//2단계 API 호출시 클라이언트에서 서버로 보낼 데이터를 담는 DTO
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AIFinalDisposalRequestDto {

    private Long disposalHistoryId;  //1단계에서 받은 이력 id
    private String selectedFinalItem;  //사용자가 선택한 2차 분류 품목

}
