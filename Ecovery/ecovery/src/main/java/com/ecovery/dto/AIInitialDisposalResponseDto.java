package com.ecovery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//1단계 API 호출의 응답으로 클라이언트에게 전달될 데이터를 담는 DTO
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AIInitialDisposalResponseDto {

    private Long disposalHistoryId;  //새로 생성된 임시 이력 id
    private String aiPrediction;  //ai 예측 결과
    private List<String> secondaryOptions;  //2차 분류 옵션 리스트
    private String tempImgName;
    private Double aiConfidence;
    private String regionGu;


    //임시 이미지 정보(history가 만들어지기 전에 저장할 수 없으므로)
    private String tempOriImgName;
    private String tempImgUrl;
}
