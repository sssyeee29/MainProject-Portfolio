package com.ecovery.mapper;

import com.ecovery.dto.CategoryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/*
 * 에코마켓 상품 카테고리 Mapper
 * @author : sehui
 * @fileName : CategoryMapper
 * @since : 250718
 * @history
 *  - 250718 | sehui | 카테고리 목록 조회 기능 추가
 */

@Mapper
public interface CategoryMapper {

    //카테고리 목록 조회
    public List<CategoryDto> findAllCategories();
}
