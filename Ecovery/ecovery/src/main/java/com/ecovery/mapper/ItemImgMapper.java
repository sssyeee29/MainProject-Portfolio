package com.ecovery.mapper;

/*
 * 에코마켓 상품 이미지 Mapper
 * @author : sehui
 * @fileName : ItemImgMapper
 * @since : 250710
 * @history
 *  - 250710 | sehui | 상품 이미지 전체 조회 기능 추가
 *  - 250716 | sehui | 상품 이미지 등록 기능 추가
 *  - 250716 | sehui | 상품 이미지 수정 기능 추가
 *  - 250716 | sehui | 상품 이미지 단건 조회 기능 추가
 *  - 250718 | sehui | 상품 이미지 삭제 기능 추가
 *  - 250724 | sehui | 상품 이미지 삭제 기능 제거
 *  - 250724 | sehui | 대표 이미지 조회 기능 추가
 */

import com.ecovery.domain.ItemImgVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ItemImgMapper {

    //상품 이미지 전체 조회
    public List<ItemImgVO> getItemImgList(Long itemId);

    //상품 이미지 등록
    public void insertItemImg(ItemImgVO itemImgVO);

    //상품 이미지 단건 조회
    public ItemImgVO getItemImgById(Long itemImgId);

    //상품 이미지 수정을 위한 삭제
    public int deleteImagesByItemId(Long ItemId);

    //대표 이미지 조회
    public ItemImgVO findRepImgByItemId (Long itemId);

    //수정된 이미지 대표 이미지로 업데이트
    public void updateRepImgYn(Long itemImgId, String repImgYn);
}
