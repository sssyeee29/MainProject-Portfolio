package com.ecovery.service;

import com.ecovery.dto.CategoryDto;
import com.ecovery.mapper.CategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 에코마켓 상품 카테고리 Service Test
 * @author : sehui
 * @fileName : CategoryServiceTest
 * @since : 250721
 * @history
 *  - 250721 | sehui | 카테고리 목록 조회 기능 추가
 */

@SpringBootTest
@Slf4j
class CategoryServiceTest {

    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("전체 목록 조회")
    public void testList(){
        //when : 전체 목록 조회
        List<CategoryDto> categoryList = categoryMapper.findAllCategories();

        //then : 결과 검증
        assertNotNull(categoryList, "상품 목록이 null입니다.");
        assertFalse(categoryList.isEmpty(), "상품 목록이 비어있습니다.");

        log.info("categoryList >> {}", categoryList);
    }

}