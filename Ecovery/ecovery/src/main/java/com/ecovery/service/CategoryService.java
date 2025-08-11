package com.ecovery.service;

/*
 * 에코마켓 상품 카테고리 Service
 * @author : sehui
 * @fileName : CategoryService
 * @since : 250718
 * @history
 *  - 250718 | sehui | 카테고리 목록 조회 기능 추가
 */

import com.ecovery.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    //카테고리 목록 조회
    public List<CategoryDto> findAllCategories();
}
