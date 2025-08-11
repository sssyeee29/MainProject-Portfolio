package com.ecovery.mapper;

import com.ecovery.domain.EnvVO;
import com.ecovery.dto.Criteria;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/*
 * 환경톡톡 게시글 Mapper 테스트
 * Mapper 기능 단위 테스트 클래스
 * @author : yukyeong
 * @fileName : EnvMapperTest
 * @since : 250711
 * @history
     - 250711 | yukyeong | 게시글 전체 목록 조회, 단건 조회 테스트 작성
     - 250714 | yukyeong | 게시글 등록, 삭제, 수정 테스트 작성
     - 250716 | yukyeong | 게시글 페이징 + 검색 목록 조회, 전체 게시글 개수 조회, 조회수 증가 테스트 작성
     - 250725 | yukyeong | 카테고리 필드 추가에 따른 등록, 수정, 단건 조회(category 포함) 테스트 작성
 */

@SpringBootTest
@Slf4j
class EnvMapperTest {

    @Autowired
    private EnvMapper envMapper;

    @Test
    @DisplayName("게시글 전체 목록 조회")
    public void testGetList() {
        List<EnvVO> list = envMapper.getList();
        log.info("전체 게시글 수: {}", list.size());

        for (EnvVO vo : list) {
            log.info("게시글: {}", vo);
        }
    }

    @Test
    @DisplayName("게시글 단건 조회")
    public void testRead() {
        Long targetId = 1L; // 조회할 게시글 ID
        EnvVO vo = envMapper.read(targetId);
        if (vo != null) {
            log.info(vo.toString());
        } else {
            log.info("조회 결과가 없습니다.");
        }
    }

    @Test
    @DisplayName("게시글 단건 조회 - category 포함 확인")
    @Transactional
    public void testReadWithCategory() {
        // Given: 테스트용 게시글 등록
        EnvVO vo = new EnvVO();
        vo.setMemberId(1L);
        vo.setTitle("조회 테스트 제목");
        vo.setContent("조회 테스트 내용");
        vo.setCategory("policy"); // 카테고리 지정

        envMapper.insert(vo);
        Long insertedId = vo.getEnvId();
        assertNotNull(insertedId, "등록된 ID는 null이면 안 됩니다.");

        // When: 단건 조회
        EnvVO readVo = envMapper.read(insertedId);

        // Then: category도 함께 조회되었는지 확인
        assertNotNull(readVo);
        assertEquals("policy", readVo.getCategory()); // category 확인
        log.info("단건 조회 결과: {}", readVo);
    }


    @Test
    @DisplayName("게시글 등록 테스트")
    @Transactional
    public void testInsert() {

        // Given (준비)
        EnvVO vo = new EnvVO();
        vo.setMemberId(1L);
        vo.setTitle("게시글 등록 테스트 제목입니다.");
        vo.setContent("게시글 등록 테스트 내용입니다.");
        vo.setCategory("news"); // 카테고리 추가

        // When (실행)
        envMapper.insert(vo); // insert() 호출로 DB에 게시글 저장

        // Then (검증)
        // 1) insert 후 envId가 자동으로 채워졌는지 (useGeneratedKeys가 정상동작했는지 확인)
        assertNotNull(vo.getEnvId(), "id는 null값이면 안됩니다.");

        // 2) 방금 insert한 데이터 조회 (PK로 단건 조회)
        EnvVO inserted = envMapper.read(vo.getEnvId());
        assertNotNull(inserted, "insert 이후에는 null값이면 안됩니다.");

        // 3) DB에 저장된 데이터가 내가 입력한 값과 같은지 검증 (예상값과 실제값 비교)
        assertEquals("게시글 등록 테스트 제목입니다.", inserted.getTitle());
        assertEquals("게시글 등록 테스트 내용입니다.", inserted.getContent());
        assertEquals("news", inserted.getCategory()); // category 검증

        log.info("삽입된 게시글: {}", inserted);
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    @Transactional
    public void testDelete() {

        // Given (준비): 테스트용 게시글 생성
        EnvVO vo = new EnvVO();
        vo.setMemberId(1L);
        vo.setTitle("게시글 삭제 테스트 제목");
        vo.setContent("게시글 삭제 테스트 내용");

        // 삭제할 게시글 등록
        envMapper.insert(vo);
        Long insertedId = vo.getEnvId();
        assertNotNull(insertedId, "Id는 null값이면 안됩니다.");

        // When (실행): 게시글 삭제
        int deletedCount = envMapper.delete(insertedId);

        // Then (검증)
        // 1) 삭제된 행의 개수가 1개인지 확인
        assertEquals(1, deletedCount, "삭제된 행의 개수는 1개");

        // 2) 삭제 후 조회 시 null인지 확인
        EnvVO deleted = envMapper.read(insertedId);
        assertNull(deleted, "삭제 후에는 null값이여야 함");

        log.info("게시글 삭제 완료. 삭제된 ID = {}", insertedId);

    }

    @Test
    @DisplayName("게시글 수정 테스트")
    @Transactional
    public void testUpdate() {

        // Given (준비) : 테스트용 게시글 생성
        // 1) 더미 데이터 생성
        EnvVO vo = new EnvVO();
        vo.setMemberId(1L);
        vo.setTitle("수정 테스트 제목1");
        vo.setContent("수정 테스트 내용1");
        vo.setCategory("tips"); // 초기 카테고리

        // 2) 게시글 등록
        envMapper.insert(vo);
        Long insertedId = vo.getEnvId();
        assertNotNull(insertedId, "등록된 ID는 null값이면 안됩니다.");

        // When (실행): 게시글 제목과 내용 수정
        vo.setTitle("수정된 제목");
        vo.setContent("수정된 내용");
        vo.setCategory("issue"); // 카테고리도 수정

        int updatedCount = envMapper.update(vo);

        // Then (검증)
        // 1) 수정된 행이 1개인지 확인
        assertEquals(1, updatedCount, "수정된 행의 개수는 1개");

        // 2) 수정된 내용이 DB에 반영되었는지 확인
        EnvVO updated = envMapper.read(insertedId);
        assertNotNull(updated, "수정 후 결과가 null이면 안됩니다.");
        assertEquals("수정된 제목", updated.getTitle());
        assertEquals("수정된 내용", updated.getContent());
        assertEquals("issue", updated.getCategory());

        log.info("수정된 게시글: {}", updated);
    }


    @Test
    @DisplayName("게시글 페이징 + 검색 목록 조회 테스트")
    @Transactional
    public void testGetListWithPaging() {
        // Given
        Criteria cri = new Criteria();
        cri.setPageNum(1); // 1페이지
        cri.setAmount(10); // 한 페이지에 10건
        cri.setType("T"); // T = 제목
        cri.setKeyword("게시글"); // 검색어

        // When
        List<EnvVO> list = envMapper.getListWithPaging(cri);

        // Then
        assertNotNull(list, "결과 리스트는 null값이면 안됨");
        log.info("조회된 게시글 수 : {}", list.size());

        for (EnvVO vo : list) {
            log.info("게시글: {}", vo);
        }
    }

    @Test
    @DisplayName("게시글 전체 개수 조회 테스트(검색 조건 포함)")
    @Transactional
    public void testGetTotalCount(){
        // Given
        Criteria cri = new Criteria();
//        cri.setPageNum(1);
//        cri.setAmount(10);
        cri.setType("T"); // T = 제목
        cri.setKeyword("테스트"); // 검색어

        // When
        int totalCount = envMapper.getTotalCount(cri);

        // Then
        log.info("검색 조건에 해당하는 전체 게시글 수 : {}", totalCount);
        assertTrue(totalCount >= 0, "게시글 수는 0 이상");
    }


    @Test
    @DisplayName("게시글 조회수 증가 테스트")
    @Transactional
    public void testUpdateViewCount(){
        // Given
        // 1) 테스트용 데이터 생성
        EnvVO vo = new EnvVO();
        vo.setMemberId(1L); // 작성자 ID 설정
        vo.setTitle("조회수 증가 테스트 제목");
        vo.setContent("조회수 증가 테스트 내용");
        // 2) 게시글 등록
        envMapper.insert(vo);
        Long insertedId = vo.getEnvId();
        assertNotNull(insertedId, "등록된 ID는 null값이면 안됩니다.");

        // When
        // 1) 조회수 증가 전 값 조회
        EnvVO before = envMapper.read(insertedId);
        int beforeCount = before.getViewCount(); // 증가 전 조회수 값
        log.info("조회수 증가 전: {}", beforeCount);
        // 2) 조회수 1 증가 실행
        envMapper.updateViewCount(insertedId);

        // Then
        // 1) 조회수 1 증가 후 조회
        EnvVO after = envMapper.read(insertedId);
        int afterCount = after.getViewCount(); // 증가 후 조회수 값

        log.info("조회수 증가 후: {}", afterCount);

        // 2) 증가된 값이 예상대로 1 증가했는지 검증
        assertEquals(beforeCount + 1, afterCount, "조회수는 1 증가");
    }

}