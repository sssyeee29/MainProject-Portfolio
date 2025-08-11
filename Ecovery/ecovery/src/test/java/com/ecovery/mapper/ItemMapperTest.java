package com.ecovery.mapper;

import com.ecovery.constant.ItemSellStatus;
import com.ecovery.domain.ItemVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.ItemListDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

/*
 * 에코마켓 상품 Mapper Test
 * @author : sehui
 * @fileName : ItemMapperTest
 * @since : 250710
 * @history
 *  - 250710 | sehui | 상품 단 건 조회 Test 실행
 *  - 250714 | sehui | 전체 상품 조회 Test 실행
 *  - 250715 | sehui | 전체 상품의 수 조회 Test 실행
 *  - 250715 | sehui | 상품 단일 조건 검색 Test 실행
 *  - 250716 | sehui | 상품 등록 Test 실행
 *  - 250717 | sehui | 상품 수정 Test 실행
 *  - 250718 | sehui | 상품 삭제 Test 실행
 *  - 250722 | sehui | 재고 수량 감소 Test 실행
 *  - 250729 | sehui | 전체 상품 조회 - 상품명 조건 검색 Test 실행
 *  - 250729 | sehui | 전체 상품 조회 - 카테고리 조건 검색 Test 실행
 *  - 250729 | sehui | 전체 상품 조회 - 전체 조건 검색 Test 실행
 *  - 250729 | sehui | 전체 상품의 수 조회 - 전체 검색 조건 Test 실행
 */

@SpringBootTest
@Transactional
@Rollback(false)    //DB 반영
@Slf4j
class ItemMapperTest {

    @Autowired
    private ItemMapper itemMapper;

    @Test
    @DisplayName("상품 전체 조회 - 조건 없이")
    public void testGetList(){

        //given : 페이징 처리 조건 생성
        String itemNm = "";
        String category = "";
        Criteria cri = new Criteria(1, 10);     //페이지 1, 10개씩 조회

        //when : 전체 상품 조회
        List<ItemListDto> itemList = itemMapper.getListWithPage(itemNm, category, cri);

        //then : 결과 검증
        assertNotNull(itemList, "조회된 상품 목록이 null입니다.");
        assertFalse(itemList.isEmpty(), "조회된 상품 목록이 비어있습니다.");

        for(ItemListDto item : itemList) {
            log.info("Item >> : {} ", item);
        }
    }

    @Test
    @DisplayName("전체 상품의 수 조회 - 조건 없이")
    public void testGetTotalCount(){

        //given : 조건 없이
        String itemNm = "";
        String category = "";

        //when : 전체 상품의 수 조회
        int totalCount = itemMapper.getTotalCount(itemNm, category);

        //then : 결과 검증
        assertNotNull(totalCount, "조회된 상품의 수가 null입니다.");

        log.info("Total Count >> : {}", totalCount);
    }

    @Test
    @DisplayName("상품 단 건 조회")
    public void testGetItem() {

        //given : 조회할 상품 ID
        Long itemId = 10L;

        //when : 상품 단 건 조회
        ItemVO item = itemMapper.getItemDtl(itemId);

        //then : 결과 검증
        assertNotNull(item, "해당 ID의 상품이 존재하지 않습니다.");

        log.info("Item : {}", item);
    }

    @Test
    @DisplayName("상품 단일 조건 검색 - 상품명")
    public void testSearch(){

        //given : 조건 설정
        String itemNm = "test";
        String category = "";
        Criteria cri = new Criteria(1, 10);

        //when : 상품 조건 검색 조회
        List<ItemListDto> list = itemMapper.getListWithPage(itemNm, category, cri);

        //then : 결과 검증
        assertNotNull(list, "조건을 만족하는 상품이 존재하지 않습니다.");

        list.forEach(item -> System.out.println("list >> " + item));
    }

    @Test
    @DisplayName("상품 등록")
    public void testInsert(){

        //given : 상품 정보 생성
        ItemVO item = ItemVO.builder()
                .itemName("test")
                .price(3000)
                .stockNumber(5)
                .categoryId(1L)
                .itemDetail("test 가전제품")
                .itemSellStatus(ItemSellStatus.SELL)
                .build();

        //when : 상품 등록
        itemMapper.insertItem(item);

        //then : itemId 자동 생성되므로 값 설정 확인
        assertNotNull(item.getItemId(),"상품 ID가 생성되지 않았습니다.");
    }

    @Test
    @DisplayName("상품 수정")
    public void testUpdate(){

        //given : 상품 수정 정보 생성
        ItemVO item = ItemVO.builder()
                .itemId(11L)
                .itemName("수정 제품")
                .price(10000)
                .stockNumber(5)
                .categoryId(2L)
                .itemDetail("수정 Test 제품")
                .itemSellStatus(ItemSellStatus.SOLD_OUT)
                .build();

        //when : 상품 수정
        itemMapper.updateItem(item);

        //then : 결과 검증
        ItemVO updateItem = itemMapper.getItemDtl(item.getItemId());

        assertEquals("수정 제품", updateItem.getItemName(), "상품명이 수정되지 않았습니다.");
        assertEquals(5, updateItem.getStockNumber(), "재고 수량이 수정되지 않았습니다.");

        log.info("updateItem >> {}", updateItem);
    }

    @Test
    @DisplayName("상품 삭제")
    public void testDelete(){

        //given : 삭제할 상품 Id
        Long itemId = 3L;

        //when : 상품 삭제
        itemMapper.deleteItem(itemId);

        //then : 결과 검증
        ItemVO deleteItem = itemMapper.getItemDtl(itemId);
        assertNull(deleteItem, "상품이 삭제되지 않았습니다.");
    }

    @Test
    @DisplayName("itemId로 상품 조회")
    public void testFindByItemId() {

        //given : 상품 Id 설정
        Long itemId = 10L;

        //when : 상품 조회
        ItemVO item = itemMapper.findByItemId(itemId);

        //then : 결과 검증
        assertNotNull(item, "조회조건을 만족하는 상품이 존재하지 않습니다.");
        log.info("Item : {}", item);
    }

    @Test
    @DisplayName("재고 수량 감소")
    public void testDecreaseStock(){

        //given : 상품 Id, 주문 수량 설정
        Long itemId = 11L;
        int quantity = 3;

        //기존 재고 확인
        ItemVO beforeItem = itemMapper.getItemDtl(itemId);
        int beforeStock = beforeItem.getStockNumber();

        //when : 재고 수량 감소
        itemMapper.removeStock(itemId, quantity);

        //then : 결과 검증
        ItemVO afterItem = itemMapper.getItemDtl(itemId);
        int afterStock = afterItem.getStockNumber();

        assertEquals(beforeStock - quantity, afterStock, "재고 수량이 주문 수량만큼 감소하지 않았습니다.");

        log.info("beforeItem >> {}", beforeItem);
        log.info("beforeStock >> {}", beforeStock);
        log.info("afterItem >> {}", afterItem);
        log.info("afterStock >> {}", afterStock);

    }

    @Test
    @DisplayName("전체 상품 조회 - 상품명 조건 검색")
    public void testSearchByItemName() {

        //given : 검색 조건 설정
        String itemNm = "수정";
        String category = "";
        Criteria cri = new Criteria(1, 10);

        //when : 조건 검색을 포함한 전체 상품 조회
        List<ItemListDto> result = itemMapper.getListWithPage(itemNm, category, cri);

        //then : 결과 검증
        assertNotNull(result);
        assertFalse(result.isEmpty());

        for(ItemListDto item : result) {
            assertTrue(item.getItemNm().contains("수정"));
            log.info("Item >> {}", item);
        }
    }

    @Test
    @DisplayName("전체 상품 조회 - 카테고리 조건 검색")
    public void testSearchByCategory() {

        //given : 검색 조건 설정
        String itemNm = "";
        String category = "가구";
        Criteria cri = new Criteria(1, 10);

        //when : 조건 검색을 포함한 전체 상품 조회
        List<ItemListDto> result = itemMapper.getListWithPage(itemNm, category, cri);

        //then : 결과 검증
        assertNotNull(result);
        assertFalse(result.isEmpty());

        for(ItemListDto item : result) {
            assertTrue(item.getCategory().contains("가구"));
            log.info("Item >> {}", item);
        }
    }

    @Test
    @DisplayName("전체 상품 조회 - 전체 조건 검색")
    public void testSearchByAll() {

        //given : 검색 조건 설정
        String keyword = "제품";
        Criteria cri = new Criteria(1, 10);

        //when : 조건 검색을 포함한 전체 상품 조회
        List<ItemListDto> result = itemMapper.getListWithPage(keyword, keyword, cri);

        //then : 결과 검증
        assertNotNull(result);
        assertFalse(result.isEmpty());

        for(ItemListDto item : result) {
            boolean nameMatch = item.getItemNm() != null && item.getItemNm().contains("제품");
            boolean categoryMatch = item.getCategory() != null && item.getCategory().contains("제품");
            assertTrue(nameMatch || categoryMatch);
            log.info("Item >> {}", item);
        }
    }

    @Test
    @DisplayName("전체 상품의 수 조회 - 상품명 조건 검색")
    public void testGetTotalCountByItemNm() {
        // given
        String itemNm = "수정";  // 실제 DB에 존재하는 상품명 일부 또는 전체
        String category = "";

        // when
        int totalCount = itemMapper.getTotalCount(itemNm, category);

        // then
        assertTrue(totalCount > 0, "상품명이 포함된 결과가 있어야 합니다.");
        log.info("Total Count by ItemNm >> : {}", totalCount);
    }
}