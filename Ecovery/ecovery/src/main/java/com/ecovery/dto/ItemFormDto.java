package com.ecovery.dto;

import com.ecovery.constant.ItemSellStatus;
import com.ecovery.domain.ItemVO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/*
 * 에코마켓 상품 정보 + 이미지 상세 페이지 및 상품 등록 DTO
 * @author : sehui
 * @fileName : ItemFormDto
 * @since : 250710
 * @history
 *  - 250710 | sehui | 상세 페이지 화면 출력용 변수, 객체 변환 생성자 추가
 *  - 250716 | sehui | 유효성 검사 추가
 *  - 250718 | sehui | 카테고리 -> 카테고리 Id로 변경
 */

@Getter
@Setter
@ToString
public class ItemFormDto {

    private Long itemId;                   //상품 코드

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemNm;             //상품명

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private int price;             //상품 가격

    @NotNull(message = "재고는 필수 입력 값입니다.")
    private int stockNumber;           //재고 수량

    @NotNull(message = "카테고리는 필수 선택 값입니다.")
    private Long categoryId;           //카테고리

    @NotNull(message = "상세 설명은 필수 입력 값입니다.")
    private String itemDetail;         //상품 상세 설명

    private ItemSellStatus itemSellStatus;      //상품 판매 상태

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();    //상품 이미지 list

    private List<Long> itemImgId = new ArrayList<>();   //ItemImgVO 개별 이미지 수정 용도

}
