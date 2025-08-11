package com.ecovery.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
 * 에코마켓 상품 카테고리 DTO
 * @author : sehui
 * @fileName : CategoryDto
 * @since : 250718
 */

@Getter
@Setter
@ToString
public class CategoryDto {

    private Long categoryId;
    private String categoryName;
}
