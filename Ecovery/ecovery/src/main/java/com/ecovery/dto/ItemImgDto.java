package com.ecovery.dto;

import com.ecovery.domain.ItemImgVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

/*
 * 에코마켓 상품 이미지 DTO
 * @author : sehui
 * @fileName : ItemImgDto
 * @since : 250709
 */

@Getter
@Setter
@AllArgsConstructor
public class ItemImgDto {

    private Long itemImgId;
    private Long itemId;
    private String imgName;         //이미지 파일명
    private String oriImgName;      //원본 이미지명
    private String imgUrl;          //이미지 조회 경로
    private String repImgYn;        //대표 이미지 여부

    private boolean toBeDeleted; // 사용자에 의해 삭제 요청된 이미지 여부
}
