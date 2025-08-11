package com.ecovery.domain;

import lombok.*;

import java.time.LocalDateTime;

/*
 * 에코마켓 상품 이미지 VO
 * @author : sehui
 * @fileName : ItemImgVO
 * @since : 250709
 */

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemImgVO {

    private Long itemImgId;
    private Long itemId;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repImgYn;
    private LocalDateTime createdAt;

}
