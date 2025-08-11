package com.ecovery.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 대형폐기물 표준 안내 정보 테이블(disposal_info)
 * @author : jihye Lee
 * @fileName : DisposalInfoVO
 * @since : 20250708
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisposalInfoVO {

    private Long infoId;
    private String region;
    private String itemName;
    private String minPrice;
    private String maxPrice;
    private String reportUrl;
}
