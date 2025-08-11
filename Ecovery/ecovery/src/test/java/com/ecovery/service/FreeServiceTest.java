package com.ecovery.service;

import com.ecovery.constant.DealStatus;
import com.ecovery.constant.ItemCondition;
import com.ecovery.domain.FreeVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.FreeDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

//문자 인코딩 방식(문자셋) 중 하나인 UTF-8 등을 안전하게 사용하기 위해 제공하는 표준 문자셋 상수 클래스
import java.nio.charset.StandardCharsets;


import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
class FreeServiceTest {

    @Autowired
    private FreeService freeService;

    //더미 이미지 파일 생성
    private List<MultipartFile> createDummyImages() {
        List<MultipartFile> files = new ArrayList<>();
        files.add(new MockMultipartFile("image1", "test1.jpg", "image/jpg", "test image1".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("image2", "test2.jpg", "image/jpg", "test image2".getBytes(StandardCharsets.UTF_8)));
        return files;
    }


    @Test
    @DisplayName("게시글 등록 테스트(이미지 포함 )")
    @Transactional
    public void testRegisterWithImage() throws Exception {

        // Given
        FreeDto dto = new FreeDto();
        dto.setTitle("이미지 포함 게시글");
        dto.setContent("테스트 내용입니다.");
        dto.setMemberId(1L);
        dto.setCategory("가전");
        dto.setRegionGu("강남구");
        dto.setRegionDong("역삼동");
        dto.setItemCondition(ItemCondition.MEDIUM);
        dto.setDealStatus(DealStatus.ONGOING);
        log.info("등록 요청 DTO 확인 : {}", dto);

        List<MultipartFile> imgFiles = createDummyImages();

        // When
        Long registeredId = freeService.register(dto, imgFiles);

        // Then
        assertThat(registeredId).isNotNull();
        FreeDto result = freeService.get(registeredId);
        log.info("읽은 VO: {}", result);

        assertThat(result.getTitle()).isEqualTo("이미지 포함 게시글");
        assertThat(result.getImgList()).hasSize(2);
        log.info("등록된 게시글: {}", result);
    }

    @Test
    @DisplayName("게시글 단건 조회 테스트")
    @Transactional
    public void testGet() throws Exception {
        // Given: 임시 게시글 DTO 생성
        FreeDto dto = new FreeDto();
        dto.setTitle("조회 테스트");
        dto.setContent("조회 내용");
        dto.setMemberId(2L);
        dto.setCategory("도서");
        dto.setRegionGu("관악구");
        dto.setRegionDong("봉천동");
        dto.setItemCondition(ItemCondition.MEDIUM);
        dto.setDealStatus(DealStatus.DONE);

        // 더미 이미지 파일 목록 생성
        List<MultipartFile> dummyImgList = createDummyImages();

        // 등록
        Long freeId = freeService.register(dto, dummyImgList);

        // When
        FreeDto result = freeService.get(freeId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("조회 테스트");
        assertThat(result.getImgList()).isNotEmpty(); // 이미지도 포함돼야 함
        log.info("조회된 게시글: {}", result);
    }

    @Test
    @DisplayName("게시글 전체 목록 조회 테스트")
    public void testGetAll() {
        Criteria cri = new Criteria(1, 10); // 1페이지, 10개씩
        List<FreeDto> list = freeService.getAll(cri);

        assertThat(list).isNotNull();
        assertThat(list.size()).isGreaterThanOrEqualTo(0); // 0 이상이면 성공
        list.forEach(dto -> log.info("게시글: {}", dto));
    }

    @Test
    @DisplayName("게시글 수정 테스트 (이미지 포함)")
    @Transactional
    void testModifyWithImage() throws Exception {
        // Given - 등록 먼저
        FreeDto dto = new FreeDto();
        dto.setTitle("수정 전 제목");
        dto.setContent("수정 전 내용");
        dto.setMemberId(1L);
        dto.setCategory("가구");
        dto.setRegionGu("송파구");
        dto.setRegionDong("잠실동");
        dto.setItemCondition(ItemCondition.LOW);
        dto.setDealStatus(DealStatus.ONGOING);

        List<MultipartFile> imgFiles = createDummyImages();
        Long id = freeService.register(dto, imgFiles);
        dto.setFreeId(id);

        // 이미지 ID 리스트를 조회해서 dto에 담기
        FreeDto loaded = freeService.get(id);
        dto.setImgList(loaded.getImgList());

        // 수정 내용
        dto.setTitle("수정된 제목");
        dto.setContent("수정된 내용");

        // When
//        boolean result = freeService.modify(dto, createDummyImages());

        // Then
//        assertThat(result).isTrue();
//        FreeDto updated = freeService.get(id);
//        assertThat(updated.getTitle()).isEqualTo("수정된 제목");
//        log.info("수정된 게시글: {}", updated);
    }


    @Test
    @DisplayName("게시글 삭제 테스트")
    @Transactional
    void testDeleteWithImages() throws Exception {
        // Given
        FreeDto dto = new FreeDto();
        dto.setTitle("삭제 테스트");
        dto.setContent("삭제할 게시글");
        dto.setMemberId(1L);
        dto.setCategory("기타");
        dto.setRegionGu("은평구");
        dto.setRegionDong("불광동");
        dto.setItemCondition(ItemCondition.MEDIUM);
        dto.setDealStatus(DealStatus.ONGOING);

        Long id = freeService.register(dto, createDummyImages());
        dto.setFreeId(id);

        // When
        boolean removed = freeService.remove(dto);

        // Then
        assertThat(removed).isTrue();
        assertThat(freeService.get(id)).isNull();
    }


    @Test
    @DisplayName("전체 게시글 수 조회 테스트")
    public void testGetTotalCount() {
        // Given: Criteria 객체 명시적으로 타입 선언
        Criteria cri = new Criteria(1, 10);

        // When
        int totalCount = freeService.getTotalCount(cri);

        // Then
        assertThat(totalCount).isGreaterThanOrEqualTo(0);
        log.info("전체 게시글 수: {}", totalCount);
    }


    @Test
    @DisplayName("조회수 증가 테스트")
    @Transactional
    public void testUpdateViewCount() throws Exception {
        // Given: 게시글 등록
        FreeDto dto = new FreeDto();
        dto.setTitle("조회수 테스트");
        dto.setContent("조회수 증가 전");
        dto.setMemberId(2L);
        dto.setCategory("전자제품");
        dto.setRegionGu("성동구");
        dto.setRegionDong("성수동");
        dto.setItemCondition(ItemCondition.HIGH);
        dto.setDealStatus(DealStatus.ONGOING);

        // 더미 이미지 추가
        List<MultipartFile> dummyImgList = createDummyImages();

        Long freeId = freeService.register(dto, dummyImgList);

        // When: 조회수 증가 전 값 저장
        FreeDto before = freeService.get(freeId);
        int prevViewCount = before.getViewCount();

        // 조회수 증가
        freeService.updateViewCount(freeId);

        // Then: 증가 후 값 확인
        FreeDto after = freeService.get(freeId);
        assertThat(after.getViewCount()).isEqualTo(prevViewCount + 1);
        log.info("조회수 변경: {} → {}", prevViewCount, after.getViewCount());
    }

}
