package com.ecovery.service;

import com.ecovery.domain.FreeReplyVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.FreeReplyDto;
import groovy.transform.TailRecursive;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * FreeReplyService 테스트 클래스
 * 댓글 및 대댓글 등록/조회/수정/삭제/카운트 기능 검증
 * 댓글 및 대댓글 등록 - 사용자 | 관리자
 * 댓글 및 대댓글 수정 - 댓글 작성한 회원인 사용자
 * 댓글 및 대댓글 삭제 - 사용자 | 관리자
 *
 * @author : yeonsu
 * @since : 250725
 */


@SpringBootTest
@Slf4j
class FreeReplyServiceTest {

    @Autowired
    private FreeReplyService freeReplyService;

    @Test
    @DisplayName("댓글 등록 테스트")
    public void testRegisterReply(){

        // given : 댓글 정보와 로그인 사용자 정보 준비
        FreeReplyVO reply = FreeReplyVO.builder()
                .freeId(23L)    // 게시글 ID
                .memberId(1L)   // 댓글 작성자 ID
                .content("테스트테스트")
                .parentId(null) // 부모 댓글이 없어서 null
                .build();

        Long loginMemberId = 1L;         // 로그인한 사용자
        String role = "USER";            // 사용자 권한 (USER or ADMIN)

        // when : 댓글 등록 요청
        freeReplyService.register(reply, loginMemberId, role);

        // then : 댓글 ID가 null이 아닌지 확인
        assertNotNull(reply.getReplyId(), "댓글 ID는 null이면 안됨");

        FreeReplyDto savedReply = freeReplyService.get(reply.getReplyId());
        assertNotNull(savedReply.getCreatedAt(), "댓글 등록 시간(createdAt)은 null이면 안 됩니다.");

        log.info("등록된 댓글 ID: {}", reply.getReplyId());
        log.info("등록된 댓글 시간: {}", savedReply.getCreatedAt());

    }

    @Test
    @DisplayName("대댓글 등록 테스트")
    public void testRegisterChildReply(){

        // given : 대댓글 정보와 로그인 사용자 정보 준비
        FreeReplyVO reply = FreeReplyVO.builder()
                .freeId(23L)    // 게시글 ID
                .memberId(1L)   // 대댓글 작성자 ID
                .content("대댓글대댓글")
                .parentId(32L)  // 부모 댓글 ID (기존 댓글의 ID)
                .build();

        Long loginMemberId = 1L;         // 로그인한 사용자 ID
        String role = "USER";            // 권한

        // when : 대댓글 등록 요청
        freeReplyService.register(reply, loginMemberId, role);

        // then : 대댓글 ID가 null이 아닌지 확인
        assertNotNull(reply.getReplyId(), "대댓글 ID는 null이면 안됨");
    }

    @Test
    @DisplayName("댓글 단건 조회 테스트")
    public void testGetReply(){

        // given : 조회할 댓글 ID 준비
        Long replyId = 30L;

        // when : 댓글 단건 조회 메소드 호출
        FreeReplyDto reply = freeReplyService.get(replyId);

        // then : 댓글이 null이 아니고, ID가 일치하는지 검증
        assertNotNull(reply, "댓글이 존재해야 합니다.");
        assertEquals(replyId, reply.getReplyId(), "조회된 댓글 ID가 일치해야 합니다.");

//        log.info("댓글 ID: {}, 작성자: {}, 내용: {}, 작성 시간: {}",
//                reply.getReplyId(), reply.getNickName(), reply.getContent(), reply.getCreatedAt());
    }

    @Test
    @DisplayName("대댓글 단건 조회 테스트")
    public void testGetChildReply(){

        // given : 조회할 대댓글 ID
        Long replyId = 30L;

        // when : 서비스 계층을 통해 대댓글 단건 조회
        FreeReplyDto reply = freeReplyService.get(replyId);

        // then : 결과 검증
        assertNotNull(reply, "대댓글이 존재해야 합니다.");
        assertEquals(replyId, reply.getReplyId(), "대댓글 ID가 일치해야 합니다.");
        assertNotNull(reply.getReplyId(), "대댓글은 반드시 parentId가 있어야 합니다");
        log.info("조회된 대댓글 : {}", reply);
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    public void testModifyReply(){

        // given : 수정할 댓글 정보와 로그인한 사용자 ID
        Long replyId = 30L;
        Long loginMemberId = 1L; // 실제 댓글 작성자의 ID로 설정 필요
        String newContent = "수정한다고";

        FreeReplyVO reply = FreeReplyVO.builder()
                .replyId(30L)
                .memberId(loginMemberId)
                .content("수정한다고")
                .build();

        // when : 댓글 수정 요청
        boolean result = freeReplyService.modify(reply, loginMemberId);

        // then : 수정 결과 확인
        assertTrue(result, "댓글 수정은 성공해야 합니다.");

        FreeReplyDto updated = freeReplyService.get(replyId);
        assertEquals(newContent, updated.getContent(), "수정된 내용이 일치해야 합니다.");
        log.info("수정된 댓글 내용 : {}", updated.getContent());
    }

    @Test
    @DisplayName("대댓글 수정 테스트")
    public void testModifyChildReply(){

        // given : 수정할 대댓글 ID, 로그인 사용자 ID, 수정할 내용 준비
        Long replyId = 31L;
        Long loginMemberId = 2L;
        String newContent = "대댓글 수정한다고";

        FreeReplyVO updatedReply = FreeReplyVO.builder()
                .replyId(replyId)
                .content(newContent)
                .build();

        // when : 대댓글 수정 메소드 호출
        boolean result = freeReplyService.modify(updatedReply, loginMemberId);

        // then : 수정 성공 여부 및 내용 확인
        assertTrue(result, "대댓글 수정은 성공해야 합니다.");
        FreeReplyDto modified = freeReplyService.get(replyId);
        assertEquals(newContent, modified.getContent(), "수정된 내용이 일치해야 합니다.");
        log.info("수정된 대댓글 내용 : {}", modified.getContent());
    }



    @Test
    @DisplayName("댓글 삭제 테스트 - 작성자")
    @Transactional
    public void testRemoveReply(){

        // given : 삭제할 댓글 ID, 로그인한 사용자 정보
        Long replyId = 30L; // 삭제할 댓글 ID
        Long loginMemberId = 1L; // 로그인한 사용자 ID (댓글 작성자여야 성공)
        String role = "USER";

        // when : 댓글 삭제 요청 수행
        boolean deleted = freeReplyService.remove(replyId, loginMemberId, role);

        // then : 삭제 결과 검증
        assertTrue(deleted, "댓글은 정상적으로 삭제되어야 합니다.");

        FreeReplyDto reply = freeReplyService.get(replyId);
        assertNull(reply, "삭제된 댓글은 조회시 null이어야 합니다.");

    }

    @Test
    @DisplayName("댓글 삭제 테스트 - 관리자")
    @Transactional
    public void testRemoveReplyByAdmin() {
        // given
        Long replyId = 15L;       // 이 댓글의 작성자가 아닌 다른 댓글
        Long loginMemberId = 2L; // 관리자 ID (댓글 작성자 아님)
        String role = "ADMIN";     // 관리자 권한

        // when
        boolean deleted = freeReplyService.remove(replyId, loginMemberId, role);

        // then
        assertTrue(deleted, "관리자는 댓글을 삭제할 수 있어야 합니다.");
        FreeReplyDto reply = freeReplyService.get(replyId);
        assertNull(reply, "삭제된 댓글은 null이어야 합니다.");
    }

    @Test
    @DisplayName("대댓글 삭제 테스트 - 작성자")
    @Transactional
    public void testDeleteChildReply(){

        // given : 삭제할 대댓글 ID와 작성자 정보
        Long replyId = 31L;
        Long loginMemberId = 2L; // 해당 대댓글의 작성자
        String role = "USER"; // 일반 사용자

        // when : 대댓글 삭제
        boolean deleted = freeReplyService.remove(replyId, loginMemberId, role);

        // then : 삭제 성공 여부 및 조회 결과 확인
        assertTrue(deleted, "작성자는 대댓글을 삭제할 수 있어야 합니다.");
        FreeReplyDto deletedReply = freeReplyService.get(replyId);
        assertNull(deletedReply, "삭제된 대댓글은 조회 시 null이어야 합니다.");
        log.info("작성자가 대댓글 삭제 완료 (replyId={})", replyId);
    }

    @Test
    @DisplayName("대댓글 삭제 테스트 - 관리자")
    @Transactional
    public void testDeleteChildReplyByAdmin() {

        // given : 삭제할 대댓글 ID와 관리자 정보
        Long replyId = 23L;       // 관리자 본인이 작성하지 않은 대댓글 ID
        Long loginMemberId = 2L; // 관리자 ID
        String role = "ADMIN";     // 관리자 권한

        // when : 대댓글 삭제 요청
        boolean deleted = freeReplyService.remove(replyId, loginMemberId, role);

        // then : 삭제 성공 여부 및 조회 결과 확인
        assertTrue(deleted, "관리자는 대댓글을 삭제할 수 있어야 합니다.");
        FreeReplyDto deletedReply = freeReplyService.get(replyId);
        assertNull(deletedReply, "삭제된 대댓글은 조회 시 null이어야 합니다.");
        log.info("관리자가 대댓글 삭제 완료 (replyId={})", replyId);
    }


    @Test
    @DisplayName("댓글 삭제 실패 테스트 - 작성자도 관리자도 아님")
    @Transactional
    public void testDeleteReplyByUnauthorizedUser() {

        // given : 삭제할 댓글 ID와 권한 없는 사용자 정보 준비
        Long replyId = 30L; // 삭제 대상 댓글 ID
        Long loginMemberId = 99L; // 댓글 작성자도 관리자도 아닌 다른 사용자 ID
        String role = "USER";   // 일반 사용자 권한

        // when & then : 예외 발생 여부 확인
        assertThrows(SecurityException.class, () -> {
            freeReplyService.remove(replyId, loginMemberId, role);
        }, "작성자나 관리자가 아니면 SecurityException이 발생해야 합니다.");
    }


    @Test
    @DisplayName("부모 댓글 목록 조회 테스트")
    public void testGetParentReply(){
        Criteria criteria = new Criteria(1, 10);
        List<FreeReplyDto> reply = freeReplyService.getParentReplies(8L, criteria, "recent");
        assertNotNull(reply);
        reply.forEach(r -> log.info("부모 댓글 : {}", r));
    }

    @Test
    @DisplayName("대댓글 목록 조회  테스트")
    public void testGetChildReplies(){
        List<FreeReplyDto> reply = freeReplyService.getChildReplies(12L);
        assertNotNull(reply);
        reply.forEach(r -> log.info("대댓글 : {}", r));
    }

    @Test
    @DisplayName("부모 댓글 수 조회 테스트")
    public void testGetParentReplyCount(){

        // given : 댓글 수를 조회할 게시글 ID
        Long freeId = 8L;

        // when : 부모 댓글 수 조회
        int count = freeReplyService.getTotalReplyCount(freeId);

        // then : 0 이상인지 검증
        assertThat(count >= 0); // 음수면 안되니까0 이상인지 확인
        log.info("해당 게시글의 부모 댓글 수 : {}", count);
    }
}