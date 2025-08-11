package com.ecovery.dto;

import com.ecovery.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisposalHistoryDto {

    private Long disposalHistoryId;
    private Long memberId;
    private String nickname;
    private String role;
    private String aiPrediction;
    private String regionGu;
    private LocalDateTime createdAt;
    private String finalItem;
    private Double aiConfidence;

    private String itemName;
    private String minPrice;
    private String maxPrice;
    private String reportUrl;

    private String disposalImgUrl;
}
