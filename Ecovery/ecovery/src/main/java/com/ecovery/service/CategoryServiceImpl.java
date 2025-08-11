package com.ecovery.service;


import com.ecovery.dto.CategoryDto;
import com.ecovery.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * 에코마켓 상품 카테고리 ServiceImpl
 * @author : sehui
 * @fileName : CategoryServiceImpl
 * @since : 250718
 * @history
 *  - 250718 | sehui | 카테고리 목록 조회 기능 추가
 */

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    //카테고리 목록 조회
    @Override
    public List<CategoryDto> findAllCategories() {
        return categoryMapper.findAllCategories();
    }
}
