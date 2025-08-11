package com.ecovery.mapper;

import com.ecovery.dto.CategoryDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 에코마켓 상품 카테고리 Mapper Test
 * @author : sehui
 * @fileName : CategoryMapperTest
 * @since : 250718
 * @history
 *  - 250710 | sehui | 상품 카테고리 목록 조회 Test 추가
 */

@SpringBootTest
@Slf4j
class CategoryMapperTest {

    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("카테고리 목록 조회")
    public void testFindAll() {

        //when : 카테고리 목록 조회
        List<CategoryDto> categories = categoryMapper.findAllCategories();

        //then : 결과 검증
        assertNotNull(categories, "카테고리를 조회할 수 없습니다.");

        categories.forEach(category -> log.info(category.toString()));
    }

}