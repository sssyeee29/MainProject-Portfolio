package com.ecovery.mapper;

import com.ecovery.domain.FreeReplyVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.FreeReplyDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 무료나눔 댓글 Mapper 테스트
 * FreeReplyMapper의 댓글 및 대댓글 crud 기능 정상작동 여부 단위테스트
 *
 * 일반 댓글 및 대댓글 등록 테스트
 * 특정 댓글 및 게시글 전체 댓글 수 조회 테스트
 * 댓글/대댓글 수정 테스트
 * 댓글 삭제시 Cascade 설정을 통한 대댓글 자동 삭제 테스트 포함
 *
 * @author : yeonsu
 * @fileName : FreeReplyMapperTest
 * @since : 250716
 * @edit : 250724 - 부모댓글과 자식댓글 각각 필요한 테스트 및 전체 테스트 코드 수정
 */

@SpringBootTest
@Slf4j
class FreeReplyMapperTest {

    @Autowired
    private FreeReplyMapper freeReplyMapper;

    @Test
    @DisplayName("일반 댓글 등록 테스트")
    public void testInsertReply() {

        // Given : 새로 등록할 댓글 정보 생성
        FreeReplyVO vo = FreeReplyVO.builder()
                .freeId(8L) // 게시글 ID
                .memberId(1L) // 작성자 ID
                .content("요것도")
                .parentId(null) // 일반 댓글 (대댓글은 부모ID 입력)
                .build();
        // createdAT은 DB에서 자동 처리

        // When : 댓글 등록 메소드 호출
        freeReplyMapper.insert(vo);     // Mapper 호출

        // Then : 결과확인 (replyId가 생성되었는지 확인)
        assertNotNull(vo.getReplyId(), "댓글ID가 null이면 안됩니다.");
        log.info("삽입된 댓글 ID = {}", vo.getReplyId()); // @Options가 설정된 경우에만 값 확인 가능
    }

    @Test
    @DisplayName("대댓글 등록 테스트")
    public void testInsertChildReply() {

        // Given : 대댓글 정보를 생성 (free_id, member_id, content, parent_id 필수)
        FreeReplyVO childReply = FreeReplyVO.builder()
                .freeId(7L)   // 대댓글이 달릴 게시글 ID
                .memberId(2L) // 작성자 ID
                .content("목요일 대댓글")
                .parentId(12L) // 대댓글이 달릴 부모 댓글 ID (reply_id)
                .build();

        // When : 대댓글 등록
        freeReplyMapper.insert(childReply);

        // Then : 대댓글 ID가 정상 생성되었는지 확인
        assertNotNull(childReply.getReplyId(), "대댓글 ID가 null이면 안됩니다.");
        log.info("등록된 대댓글 ID = {}", childReply.getReplyId());
    }

    @Test
    @DisplayName("특정 게시글에 달린 일반 댓글 목록 조회 테스트")
    public void testGetReply() {

        // Given : 게시글 ID와 페이징/정렬 조건을 준비
        Long freeId = 8L; // 게시글 ID
        Criteria criteria = new Criteria();
        criteria.setPageNum(1);
        criteria.setAmount(10);
        String sortType = "recent"; // 정렬 기준 : 최신순

        // When : 부모 댓글 목록 조회 실행
        List<FreeReplyDto> replies = freeReplyMapper.getParentReplies(freeId, criteria, sortType);

        // Then : 결과 검증 (null 아님, 비어있지 않음, 부모댓글만 존재해야함)
        assertNotNull(replies);
        assertFalse(replies.isEmpty(), "댓글 목록이 비어있으면 안 됩니다");

        for (FreeReplyDto dto : replies) {
            assertNull(dto.getParentId(), "부모댓글만 조회되어야 하므로 parentId는 null이어야 합니다");
            assertEquals(freeId, dto.getFreeId(), "조회된 댓글의 게시글 ID가 일치해야 합니다.");
            log.info("부모댓글: {}", dto);
        }
    }

    @Test
    @DisplayName("특정 부모 댓글에 달린 대댓글 목록 조회 테스트")
    public void testGetChild() {

        // Given : 대댓글을 조회할 부모 댓글 ID 설정
        Long parentReplyId = 12L; // 실제로 존재하는 부모 댓글 ID로 변경해야 함

        // When : 해당 부모 댓글에 달린 대댓글 목록을 Mapper를 통해 조회
        List<FreeReplyDto> childReplies = freeReplyMapper.getChildReplies(parentReplyId);

        // Then : 결과 검증 (null 아님, 모든 대댓글의 parentId가 주어진 부모 ID와 일치해야함)
        assertNotNull(childReplies); // 대댓글 목록이 null이면 안됨
        log.info("대댓글 수: {}", childReplies.size());

        for (FreeReplyDto reply : childReplies) {
            assertEquals(parentReplyId, reply.getParentId(), "모든 대댓글은 해당 부모 댓글을 참조해야 합니다.");
            log.info("↪ 대댓글: {}", reply);
        }
    }


    @Test
    @DisplayName("특정 게시글 댓글 수 조회 테스트")
    public void testGetTotalReplyCount() {

        // Given : 댓글 수를 조회할 게시글 ID
        Long freeId = 8L;

        // When : 해당 게시글의 전체 댓글 수를 Mapper를 통해 조회
        int totalCount = freeReplyMapper.getTotalReplyCount(freeId);

        // Then : 결과 출력 및 검증
        if (totalCount > 0) {
            log.info("총 댓글 수 : {}", totalCount); // 댓글이 1개 이상 있을 경우 출력
        } else {
            log.info("해당 게시글에는 댓글이 없습니다."); // 댓글이 없을 경우 출력
        }
    }

    @Test
    @DisplayName("일반 댓글 수정 테스트")
    public void testUpdateReply() {

        // Given : 이미 존재하는 댓글 ID
        Long replyId = 4L;

        // When : 해당 댓글 내용을 수정
        FreeReplyVO vo = FreeReplyVO.builder()
                .replyId(replyId)
                .content("수정할래말래")
                .build();
        int Count = freeReplyMapper.update(vo);

        // Then : 수정 결과 확인
        assertEquals(1, Count, "수정된 행이 1개여야 합니다");

        // 수정된 내용 확인
        FreeReplyDto dto = freeReplyMapper.read(replyId);
        assertEquals("수정할래말래", dto.getContent());
        log.info("수정된 댓글 내용 : {}", dto.getContent());
    }

    @Test
    @DisplayName("대댓글 수정 테스트")
    public void testGetChildReply() {

        // Given : 수정할 대댓글 ID
        Long replyId = 23L;

        // When : 해당 대댓글 내용 수정
        FreeReplyVO vo = FreeReplyVO.builder()
                .replyId(replyId)
                .content("대댓글 수정할래말래")
                .build();
        int result = freeReplyMapper.update(vo);

        // Then : 수정 성공 여부 및 내용 확인
        assertEquals(1, result, "수정된 행 수는 1개여야 합니다.");

        FreeReplyDto dto = freeReplyMapper.read(replyId);
        assertEquals("대댓글 수정할래말래", dto.getContent());
        log.info("수정된 대댓글 내용 : {}", dto.getContent());

    }

    // 댓글에 연결된 대댓글도 같이 삭제됨
    @Test
    @DisplayName("댓글 삭제 테스트")
    public void testDeleteReply() {

        // Given : 삭제할 댓글 ID가 존재함
        Long replyId = 4L;

        // When : 댓글이 부모댓글이라면 대댓글 수 확인 후 삭제
        int childCount = freeReplyMapper.getChildReplyCount(replyId);
        int deletedCount = freeReplyMapper.delete(replyId); // 부모만 삭제(대댓글은 cascade로 자동삭제)

        // Then : 삭제 결과 확인
        log.info("삭제된 대댓글 수 : {}", childCount);
        log.info("총 삭제된 댓글 수 : {}", childCount + deletedCount);

        if (deletedCount > 0) {
            log.info("댓글 삭제 테스트 성공 ");
        } else {
            log.info(" 댓글 삭제 테스트 실패: 댓글이 존재하지 않음", replyId);
        }

    }

    @Test
    @DisplayName("부모 댓글 페이징 조회 테스트")
    public void testGetParentRepliesWithPaging() {

        // Given : 페이징 조건, 정렬 방식, 게시글 ID 설정
        Criteria criteria = new Criteria();
        criteria.setPageNum(2);
        criteria.setAmount(5);
        String sortType = "recent"; // 정렬 방식 : 최신순
        Long freeId = 8L;

        // When : 부모 댓글 목록 조회
        List<FreeReplyDto> replies = freeReplyMapper.getParentReplies(freeId, criteria, sortType);

        // Then : 결과 검증
        assertNotNull(replies);
        replies.forEach(reply -> log.info("부모 댓글: {}", reply));


    }

    @Test
    @DisplayName("특정 부모 댓글의 대댓글 목록 조회 테스트")
    public void testGetChildReplies() {

        // Given : 조회할 부모 댓글 ID 설정
        Long parentId = 12L;

        // When : 해당 부모 댓글에 달린 대댓글 목록 조회
        List<FreeReplyDto> childReplies = freeReplyMapper.getChildReplies(parentId);

        // Then : 결과 검증
        assertNotNull(childReplies, "대댓글 목록이 null이면 안 됩니다.");
        assertFalse(childReplies.isEmpty(), "대댓글 목록이 비어있으면 안 됩니다.");

        for (FreeReplyDto reply : childReplies) {
            assertEquals(parentId, reply.getParentId(), "모든 대댓글의 parentId는 요청한 부모 ID와 같아야 합니다.");
            log.info("↪ 대댓글: {}", reply);
        }
    }


//    @Test
//    @DisplayName("댓글/대댓글 닉네임 포함 여부 테스트")
//    public void testReplyContainsNickname() {
//
//        // Given : 닉네임을 확인할 대상 댓글 ID (DB에 실제로 존재해야함)
//        Long replyId = 16L; // 실제 존재하는 댓글 ID
//
//        // When : 해당 댓글을 조회 (FreeReplyDto에는 닉네임이 포함되어 있어야함)
//        FreeReplyDto dto = freeReplyMapper.read(replyId);
//
//        // Then : 조회 결과와 닉네임 존재 여부 검증
//        assertNotNull(dto, "댓글이 존재해야 합니다."); // 댓글 자체가 null이 아니어야 함
//        assertNotNull(dto.getNickName(), "닉네임이 null이면 안 됩니다."); // 닉네임 필드 확인
//        log.info("조회된 댓글 닉네임: {}", dto.getNickName());
//    }


    @Test
    @DisplayName("댓글 정렬 방식 검증 테스트")
    public void testReplySortOrder() {

        // Given : 페이지당 5개의 댓글을 조회하는 조건, 게시글 ID 설정
        Criteria criteria = new Criteria(1, 5); // 1페이지에 5개씩
        Long freeId = 8L; // 댓글이 달린 게시글 ID

        // When : 동일한 조건으로 정렬 방식만 다르게 댓글 목록 조회
        List<FreeReplyDto> recentList = freeReplyMapper.getParentReplies(freeId, criteria, "recent");
        List<FreeReplyDto> oldestList = freeReplyMapper.getParentReplies(freeId, criteria, "oldest");

        // Then : 두 목록 모두 null 또는 비어있으면 안됨
        assertNotNull(recentList);
        assertNotNull(oldestList);
        assertFalse(recentList.isEmpty(), "최신순 목록이 비어 있으면 안 됩니다.");
        assertFalse(oldestList.isEmpty(), "오래된순 목록이 비어 있으면 안 됩니다.");

        // 시간 비교 (최신순 첫 번째 댓글이 오래된순 첫 번째 댓글보다 나중이어야 함)
        FreeReplyDto recentFirst = recentList.get(0);
        FreeReplyDto oldestFirst = oldestList.get(0);

        log.info("최신순 첫 댓글 시간: {}", recentFirst.getCreatedAt());
        log.info("오래된순 첫 댓글 시간: {}", oldestFirst.getCreatedAt());

        assertTrue(recentFirst.getCreatedAt().isAfter(oldestFirst.getCreatedAt()) ||
                        recentFirst.getCreatedAt().isEqual(oldestFirst.getCreatedAt()),
                "최신순 첫 댓글이 오래된순 첫 댓글보다 나중이어야 합니다.");
    }

    @Test
    @DisplayName("댓글 등록 시 필수값 누락 테스트")
    public void testInsertReplyWithMissingFields() {

        // Given : content(댓글 내용) 누락한 댓글 객체 생성
        FreeReplyVO vo = FreeReplyVO.builder()
                .freeId(8L)
                .memberId(1L)
                // .content("내용 없음") // 고의로 누락
                .parentId(null)
                .build();

        // When / Then : 예외가 발생하는지 확인 (content가 null이므로 DB 제약조건 위반 예상)
        Exception exception = assertThrows(Exception.class, () -> {
            freeReplyMapper.insert(vo); // content 없이 insert 시도
        });

        // 예외 메시지 출력
        log.info("예외 발생 메시지: {}", exception.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 댓글 조회 시 null 반환 테스트")
    public void testReadNonExistingReply() {

        // Given : 존재하지 않는 댓글 ID 설정
        Long invalidReplyId = 99999L; // DB에 존재하지 않는 ID

        // When : 해당 댓글 ID로 댓글 조회 시도
        FreeReplyDto dto = freeReplyMapper.read(invalidReplyId);

        // Then : 결과가 null인지 확인 (조회되지 않음이 정상)
        assertNull(dto, "존재하지 않는 댓글을 조회하면 null이어야 합니다.");
    }
}

