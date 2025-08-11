package com.ecovery.service;

import com.ecovery.dto.Criteria;
import com.ecovery.dto.NoticeDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 환경톡톡 게시글 Service 테스트 클래스
 * NoticeService의 게시글 CRUD 및 페이징·검색 관련 기능에 대한 단위 테스트를 수행
 * 모든 테스트는 NoticeDto 기반으로 동작하며, 등록 → 조회 → 수정/삭제 → 목록/조회수까지 전반적인 흐름을 검증
 * @author : yukyeong
 * @fileName : NoticeServiceTest
 * @since : 250723
 * @history
     - 250723 | yukyeong | 게시글 등록, 조회, 수정, 삭제, 목록(페이징+검색), 전체 개수, 조회수 증가, 예외 상황 테스트까지 전반적인 단위 테스트 작성 완료
     - 250729 | yukyeong | 게시글 등록/수정 테스트에 category 필드 추가 및 검증
 */

@SpringBootTest
@Slf4j
class NoticeServiceTest {

    @Autowired
    private NoticeService noticeService;

    @Test
    @DisplayName("게시글 등록 테스트")
    @Transactional
    public void testRegister(){

        // Given - 테스트용 게시글 DTO 생성 및 값 세팅
        NoticeDto noticeDto = new NoticeDto();
        noticeDto.setMemberId(1L);
        noticeDto.setTitle("서비스 등록 테스트 제목");
        noticeDto.setContent("서비스 등록 테스트 내용");
        noticeDto.setCategory("important"); // 카테고리: 중요 공지

        // When - 게시글 등록 (DTO → VO 변환 후 insert 수행)
        noticeService.register(noticeDto);

        // Then
        // 1) insert 후 자동 생성된 noticeId가 DTO에 세팅되었는지 확인
        assertNotNull(noticeDto.getNoticeId(), "id는 null값이면 안됩니다.");

        // 2) 방금 등록한 게시글을 ID로 다시 조회 (DTO 반환)
        NoticeDto inserted = noticeService.get(noticeDto.getNoticeId());
        assertNotNull(inserted, "insert 이후에는 null값이면 안됩니다.");

        // 3) 등록한 제목과 내용이 DB에서 정상적으로 조회되는지 확인
        assertEquals("서비스 등록 테스트 제목", inserted.getTitle());
        assertEquals("서비스 등록 테스트 내용", inserted.getContent());
        assertEquals("important", inserted.getCategory()); // 카테고리 검증

        log.info("삽입된 게시글: {}", inserted);

    }


    @Test
    @DisplayName("게시글 단건 조회")
    public void testGet() {
        // Given - 조회할 게시글의 ID 지정
        Long targetId = 1L;

        // When - 해당 ID로 게시글 단건 조회 (DTO 반환)
        NoticeDto noticeDto = noticeService.get(targetId);

        //Then
        assertNotNull(noticeDto, "조회 결과가 null이면 안됨.");
        // 제목과 내용 확인
        log.info("조회된 게시글 제목 : {}", noticeDto.getTitle());
        log.info("조회된 게시글 내용 : {}", noticeDto.getContent());;
    }


    @Test
    @DisplayName("게시글 수정 테스트")
    @Transactional
    public void testModify() {
        // Given
        // 테스트용 게시글 DTO 생성 및 등록
        NoticeDto noticeDto = new NoticeDto();
        noticeDto.setMemberId(1L); // 작성자 ID
        noticeDto.setTitle("수정 테스트 제목1"); // 초기 제목
        noticeDto.setContent("수정 테스트 내용1"); // 초기 내용
        noticeDto.setCategory("general"); // 초기 카테고리: 일반 공지

        noticeService.register(noticeDto); // 게시글 등록
        Long insertedId = noticeDto.getNoticeId(); // 등록된 게시글의 ID 확인 (PK)
        assertNotNull(insertedId, "등록된 ID는 null값이면 안됩니다."); // 등록된 게시글이 잘 등록되었는지

        // When
        // 1) 제목과 내용을 수정
        noticeDto.setTitle("수정된 제목");
        noticeDto.setContent("수정된 내용");
        noticeDto.setCategory("important"); // 수정된 카테고리: 중요 공지

        // 2) 수정 메서드 호출
        boolean result = noticeService.modify(noticeDto);

        // Then
        // 1) 수정 결과가 true(수정 성공)인지 확인
        assertTrue(result, "수정결과가 false면 안됨.");
        // 2) 수정된 게시글을 다시 조회
        NoticeDto updated = noticeService.get(insertedId);
        // 3) 조회 결과가 null이 아닌지 검증
        assertNotNull(updated, "수정 후 결과가 null이면 안됩니다.");
        // 4) 제목과 내용이 수정되었는지 검증
        assertEquals("수정된 제목", updated.getTitle());
        assertEquals("수정된 내용", updated.getContent());
        assertEquals("important", updated.getCategory()); // 카테고리 검증

        log.info("수정된 게시글: {}", updated);

    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정 시 실패 테스트")
    @Transactional
    public void testModifyNonExist() {

        // Given - 존재하지 않는 게시글 ID를 가진 DTO 생성
        NoticeDto noticeDto = new NoticeDto();
        noticeDto.setNoticeId(999999L); // DB에 없는 ID
        noticeDto.setTitle("수정 실패 테스트");
        noticeDto.setContent("수정 실패 내용");

        // When - 수정 시도
        boolean result = noticeService.modify(noticeDto);

        // Then - 수정 실패 여부 확인
        assertFalse(result, "존재하지 않는 게시글 수정은 실패해야 함");
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    @Transactional
    public void testRemove() {
        // Given
        // 테스트용 게시글 DTO 생성 및 등록
        NoticeDto noticeDto = new NoticeDto();
        noticeDto.setMemberId(1L);
        noticeDto.setTitle("삭제 테스트 제목");
        noticeDto.setContent("삭제 테스트 내용");

        noticeService.register(noticeDto); // 게시글 등록
        Long insertedId = noticeDto.getNoticeId(); // 등록된 게시글의 ID 가져오기
        assertNotNull(insertedId, "등록된 ID는 null값이면 안됨");

        // When - 게시글 삭제
        boolean result = noticeService.remove(insertedId);

        // Then
        // 1) 삭제되었는지 확인
        assertTrue(result, "삭제 결과가 false면 실패.");
        // 2) 삭제된 게시글을 다시 조회했을 때 null값이 나오는지 확인
        NoticeDto deleted = noticeService.get(insertedId);
        assertNull(deleted, "삭제 후 조회결과는 null.");

        log.info("삭제된 게시글 ID: {}", insertedId);

    }

    @Test
    @DisplayName("존재하지 않는 게시글 삭제 시 실패 테스트")
    @Transactional
    public void testRemoveNonExist() {

        // Given - 존재하지 않는 게시글 ID
        Long nonExistId = 999999L;

        // When - 삭제 시도
        boolean result = noticeService.remove(999999L);

        // Then - 삭제 실패 여부 확인
        assertFalse(result, "존재하지 않는 게시글 삭제는 실패해야 함");
    }


    @Test
    @DisplayName("게시글 페이징 + 검색 목록 조회 테스트")
    @Transactional
    public void testGetList(){
        // Given - 검색 조건과 페이징 정보 세팅
        Criteria cri = new Criteria();
        cri.setPageNum(1); // 1페이지
        cri.setAmount(10); // 한 페이지에 10건
        cri.setType("T"); // T = 제목 검색
        cri.setKeyword("공지사항"); // 검색어

        // When - 조건에 맞는 게시글 목록 조회 (DTO 리스트 반환)
        List<NoticeDto> list = noticeService.getList(cri);

        // Thwn
        assertNotNull(list, "조회 결과가 null값이면 안됨");
        log.info("조회된 게시글 수 : {}", list.size());

        for (NoticeDto noticeDto : list) {
            log.info("게시글: {}", noticeDto);
        }
    }

    @Test
    @DisplayName("게시글 전체 개수 조회 테스트(검색 조건 포함)")
    @Transactional
    public void testGetTotal(){
        // Given
        Criteria cri = new Criteria();
        cri.setType("T"); // T = 제목
        cri.setKeyword("테스트"); // 검색어

        // When
        int totalCount = noticeService.getTotal(cri);

        // Then
        log.info("검색 조건에 해당하는 전체 게시글 수 : {}", totalCount);
        assertTrue(totalCount >= 0, "게시글 수는 0 이상");
    }


    @Test
    @DisplayName("게시글 조회수 증가 테스트")
    @Transactional
    public void testUpdateViewCount(){
        // Given
        // 1) 테스트용 DTO 객체 생성
        NoticeDto noticeDto = new NoticeDto();
        noticeDto.setMemberId(1L); // 작성자 ID 설정
        noticeDto.setTitle("조회수 증가 테스트 제목");
        noticeDto.setContent("조회수 증가 테스트 내용");
        // 2) 게시글 등록
        noticeService.register(noticeDto);
        Long insertedId = noticeDto.getNoticeId(); // 등록된 ID 확인
        assertNotNull(insertedId, "등록된 ID는 null값이면 안됩니다.");

        // When
        // 1) 조회수 증가 전 값 조회
        NoticeDto before = noticeService.get(insertedId);
        int beforeCount = before.getViewCount(); // 증가 전 조회수 값
        log.info("조회수 증가 전: {}", beforeCount);
        // 2) 조회수 1 증가 실행
        noticeService.increaseViewCount(insertedId);

        // Then
        // 1) 조회수 1 증가 후 조회
        NoticeDto after = noticeService.get(insertedId);
        int afterCount = after.getViewCount(); // 증가 후 조회수 값

        log.info("조회수 증가 후: {}", afterCount);

        // 2) 증가된 값이 예상대로 1 증가했는지 검증
        assertEquals(beforeCount + 1, afterCount, "조회수는 1 증가");
    }


}