package com.ecovery.service;

import com.ecovery.domain.ItemVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.ItemFormDto;
import com.ecovery.dto.ItemListDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/*
 * 에코마켓 상품 Service
 * @author : sehui
 * @fileName : ItemService
 * @since : 250710
 * @history
 *  - 250710 | sehui | 상품 단건 조회 기능 추가
 *  - 250714 | sehui | 전체 상품 조회 기능 추가
 *  - 250715 | sehui | 전체 상품의 수 조회 기능 추가
 *  - 250715 | sehui | 전체 상품, 상품의 수 조회에 단일 조건 검색 추가
 *  - 250716 | sehui | 상품 등록 기능 추가
 *  - 250717 | sehui | 상품 수정 기능 추가
 *  - 250718 | sehui | 상품 삭제 기능 추가
 *  - 250722 | sehui | 주문 시 재고 수량 감소 기능 추가
 *  - 250722 | sehui | 상품 ID로 상품 정보 조회 기능 추가
 *  - 250724 | sehui | 상품 삭제 기능 제거
 */

public interface ItemService {

    //전체 상품 조회
    public List<ItemListDto> getItemList(String itemNm, String category, Criteria cri);

    //상품 단건 조회
    public ItemFormDto getItemDtl(Long itemId);

    //전체 상품의 수 조회
    public int getTotalCount(String itemNm, String category);

    //상품 등록
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception;

    //상품 수정
    public void updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception;

    // 상품 ID로 상품 정보 조회
    public ItemVO findByItemId(Long itemId);

    //주문 시 재고 수량 감소
    public void removeStock(Long itemId, int quantity);
}
