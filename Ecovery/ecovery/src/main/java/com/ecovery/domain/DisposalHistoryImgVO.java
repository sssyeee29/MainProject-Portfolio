package com.ecovery.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 대형폐기물 이미지 테이블(disposal_history_img)
 * @author : jihye Lee
 * @fileName : DisposalHistoryImgVO
 * @since : 20250708
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisposalHistoryImgVO {

    private Long disposalImgId;
    private Long disposalHistoryId;
    private String disposalImgName;
    private String oriDisposalImgName;
    private String disposalImgUrl;
}
