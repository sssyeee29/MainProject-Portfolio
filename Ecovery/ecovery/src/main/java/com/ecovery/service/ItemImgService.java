package com.ecovery.service;

import com.ecovery.domain.ItemImgVO;
import com.ecovery.dto.ItemImgDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/*
 * 에코마켓 상품 이미지 Service
 * @author : sehui
 * @fileName : ItemService
 * @since : 250710
 * @history
 *  - 250716 | sehui | 상품 이미지 등록 기능 추가
 *  - 250716 | sehui | 상품 이미지 수정 기능 추가
 *  - 250724 | sehui | 상품 전체 이미지 조회 기능 추가
 *  - 250724 | sehui | 상품 이미지 단건 조회 기능 추가
 *  - 250724 | sehui | 대표 이미지 조회 Test 추가
 */

public interface ItemImgService {

    //상품 이미지 등록
    public void saveItemImg(ItemImgVO itemImgVO, MultipartFile itemImgFile) throws Exception;

    //상품 이미지 수정
    public void updateItemImg(Long itemId, List<ItemImgDto> itemImgDtoList, List<MultipartFile> newImgFiles) throws Exception;

    //상품 전체 이미지 조회
    public List<ItemImgVO> getItemImgList(Long itemId);

    //상품 이미지 단건 조회
    public ItemImgVO getItemImg (Long itemImgId);

    //대표 이미지 조회
    public ItemImgVO getRepImgByItemId(Long itemId);
}
