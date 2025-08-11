package com.ecovery.service;

import com.ecovery.constant.ItemSellStatus;
import com.ecovery.domain.ItemImgVO;
import com.ecovery.domain.ItemVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.ItemFormDto;
import com.ecovery.dto.ItemListDto;
import com.ecovery.mapper.ItemImgMapper;
import com.ecovery.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/*
 * 에코마켓 상품 Service Test
 * @author : sehui
 * @fileName : ItemServiceTest
 * @since : 250710
 * @history
 *  - 250710 | sehui | 상품 단 건 조회 Test 실행
 *  - 250715 | sehui | 전체 상품 조회 Test 실행
 *  - 250717 | sehui | 상품 수정 Test 실행
 *  - 250722 | sehui | 주문 시 재고 수량 감소 Test 실행
 *  - 250723 | sehui | 상품 삭제 Test 실행
 *  - 250724 | sehui | 상품 삭제 Test 제거
 *  - 250724 | sehui | 상품 등록 예외 처리 Test 실행
 */

@SpringBootTest
@Transactional
//@Rollback(false)
@Slf4j
class ItemServiceTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private ItemImgMapper itemImgMapper;

    @Test
    @DisplayName("전체 상품 조회")
    public void testItemList(){

        //given : 페이징 처리 조건 생성
        String itemNm = "";
        String category = "";
        Criteria cri = new Criteria();

        //when : 전체 상품 조회
        List<ItemListDto> itemList = itemService.getItemList(itemNm, category, cri);

        //then : 결과 검증
        assertNotNull(itemList, "상품 목록이 null입니다.");
        assertFalse(itemList.isEmpty(), "상품 목록이 비어있습니다.");

        for(ItemListDto item : itemList) {
            log.info("Item >> : {} ", item);
        }
    }

    @Test
    @DisplayName("상품 단 건 조회")
    public void testGetItem(){

        //given : 조회할 상품 ID
        Long itemId = 10L;

        //when : itemId로 상품 단 건 조회
        ItemFormDto item = itemService.getItemDtl(itemId);

        //then : 결과 검증
        assertNotNull(item, "조회된 상품이 null입니다.");
        assertEquals(itemId, item.getItemId(), "상품 ID가 일치하지 않습니다.");

        log.info("Item : {}", item);
    }
    @Test
    @DisplayName("상품 등록")
    public void testSaveItem() throws Exception{

        //given : 상품 정보 생성
        ItemFormDto item = new ItemFormDto();
        item.setItemNm("test 등록1");
        item.setPrice(1000);
        item.setStockNumber(5);
        item.setCategoryId(1L);
        item.setItemDetail("test 상세 설명1");
        item.setItemSellStatus(ItemSellStatus.SELL);

        //MockMultipartFile을 사용해 가짜 이미지 파일 생성
        MockMultipartFile mockFile1 = new MockMultipartFile(
                "itemImgFile1",
                "testImage1.jpg",
                "image/jpeg",
                "test image content 1".getBytes()
        );

        MockMultipartFile mockFile2 = new MockMultipartFile(
                "itemImgFile2",
                "testImage2.jpg",
                "image/jpeg",
                "test image content 2".getBytes()
        );

        List<MultipartFile> itemImgList = Arrays.asList(mockFile1, mockFile2);

        //when : 상품 등록
        Long savedItemId = itemService.saveItem(item, itemImgList);

        //then : 결과 검증
        assertNotNull(savedItemId, "상품 등록 ID가 null 값입니다.");
        assertTrue(savedItemId > 0, "상품 ID가 0보다 작습니다.");

        log.info("savedItemId >> {}", savedItemId);
    }

    @Test
    @DisplayName("상품 등록 예외 처리")
    public void testSaveItemError() throws Exception{

        //given : 상품 정보 생성
        ItemFormDto item = new ItemFormDto();
        item.setItemNm("test 등록1");
        item.setPrice(1000);
        item.setStockNumber(5);
        item.setCategoryId(1L);
        item.setItemDetail("test 상세 설명1");
        item.setItemSellStatus(ItemSellStatus.SELL);

        List<MultipartFile> itemImgList = new ArrayList<>();

        //when : 상품 등록
        Long savedItemId = itemService.saveItem(item, itemImgList);

        //then : 결과 검증
        assertNotNull(savedItemId, "상품 등록 ID가 null 값입니다.");
        assertTrue(savedItemId > 0, "상품 ID가 0보다 작습니다.");

        log.info("savedItemId >> {}", savedItemId);
    }


    @Test
    @DisplayName("상품 수정")
    public void testUpdateItem() throws Exception{

        //given : 상품 수정 정보 생성
        ItemFormDto item = new ItemFormDto();
        item.setItemId(5L);
        item.setItemNm("test 수정2");
        item.setPrice(10000);
        item.setStockNumber(1);
        item.setCategoryId(2L);
        item.setItemDetail("test 상세 설명 수정222");
        item.setItemSellStatus(ItemSellStatus.SELL);

        List<Long> imgId = Arrays.asList(20L);     //실제 DB에 존재하는 ItemImgId (수정하려는 이미지)
        item.setItemImgId(imgId);

        //MockMultipartFile을 사용해 가짜 이미지 파일 생성
        MockMultipartFile mockFile1 = new MockMultipartFile(
                "itemImgFile",            //<input type=file> 폼 필드 이름
                "testImage333.jpg",             //새 이미지 파일 oriImgName
                "image/jpeg",
                "test image content 3".getBytes()
        );

        List<MultipartFile> itemImgList = Arrays.asList(mockFile1);

        //when : 상품 수정
        itemService.updateItem(item, itemImgList);

        //then : 결과 검증
        ItemVO updateItem = itemMapper.getItemDtl(item.getItemId());
        ItemImgVO updateItemImg = itemImgMapper.getItemImgById(imgId.get(0));

        assertNotNull(updateItem, "상품 정보가 수정되지 않았습니다.");

        log.info("updateItem info >> {} ", updateItem);
        log.info("ItemImg info >> {} ", updateItemImg);

    }

    @Test
    @DisplayName("상품 수정_이미지 유지")
    public void testUpdateItem2() throws Exception{

        //given : 상품 수정 정보 생성
        ItemFormDto item = new ItemFormDto();
        item.setItemId(12L);
        item.setItemNm("test 수정Item");
        item.setPrice(5000);
        item.setStockNumber(5);
        item.setCategoryId(2L);
        item.setItemDetail("test 상세 설명 수정");
        item.setItemSellStatus(ItemSellStatus.SELL);

        List<Long> imgId = Arrays.asList(10L);     //실제 DB에 존재하는 ItemImgId (수정하려는 이미지)
        item.setItemImgId(imgId);

        List<MultipartFile> itemImgList = new ArrayList<>();    //이미지 파일 새로 업로드X

        //when : 상품 수정
        itemService.updateItem(item, itemImgList);

        //then : 결과 검증
        ItemVO updateItem = itemMapper.getItemDtl(item.getItemId());
        ItemImgVO updateItemImg = itemImgMapper.getItemImgById(imgId.get(0));

        assertNotNull(updateItem, "상품 정보가 수정되지 않았습니다.");

        log.info("updateItem info >> {} ", updateItem);
        log.info("ItemImg info >> {} ", updateItemImg);

    }

    @Test
    @DisplayName("재고 수량 감소")
    public void testRemoveStock() {

        //given : 상품 Id, 주문 수량 설정
        Long itemId = 12L;
        int quantity = 3;

        //기존 재고 확인
        ItemVO beforeItem = itemMapper.getItemDtl(itemId);
        int beforeStock = beforeItem.getStockNumber();

        //when : 재고 수량 감소
        itemService.removeStock(itemId, quantity);

        //then : 결과 검증
        ItemVO afterItem = itemMapper.getItemDtl(itemId);
        int afterStock = afterItem.getStockNumber();

        assertEquals(beforeStock - quantity, afterStock, "재고 수량이 주문 수량만큼 감소하지 않았습니다.");

        log.info("beforeItem >> {}", beforeItem);
        log.info("beforeStock >> {}", beforeStock);
        log.info("afterItem >> {}", afterItem);
        log.info("afterStock >> {}", afterStock);
    }

}