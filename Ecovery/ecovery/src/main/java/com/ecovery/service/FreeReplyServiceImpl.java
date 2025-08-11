package com.ecovery.service;

import com.ecovery.domain.FreeReplyVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.FreeReplyDto;
import com.ecovery.mapper.FreeReplyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * 무료나눔 댓글 서비스 구현 클래스
 * FreeReplyMapper를 활용한 댓글 비즈니스 로직 처리
 *
 * 댓글 및 대댓글 등록, 조회, 수정, 삭제, 목록 조회, 수 카운트 기능 포함
 * Controller → Service → Mapper → DB 순으로 동작
 *
 * @author : yeonsu
 * @fileName : FreeReplyServiceImpl
 * @since : 250724
 */


@Service
@RequiredArgsConstructor
@Slf4j
public class FreeReplyServiceImpl implements FreeReplyService {

    private final FreeReplyMapper freeReplyMapper;

    // 댓글 등록 (회원 또는 관리자만 가능)
    @Override
    public void register(FreeReplyVO freeReply, Long loginMemberId, String role) {
        log.info("댓글 등록 요청: {}", freeReply);

        // 권한 체크 - 로그인한 사용자가 USER 또는 ADMIN이 아니면 예외발생
        if (!role.equals("USER") && !role.equals("ADMIN")) {
            throw new SecurityException("회원 또는 관리자만 댓글을 등록할 수 있습니다.");
        }

        // 요청으로 전달된 댓글 작성자 ID와 로그인한 사용자 ID가 다르면 예외
        if (!loginMemberId.equals(freeReply.getMemberId())) {
            throw new SecurityException("본인의 계정으로만 댓글을 등록할 수 있습니다.");
        }

        // 위 조건 통과 시 DB에 댓글 등록
        freeReplyMapper.insert(freeReply);
    }

    // 댓글 단건 조회
    @Override
    public FreeReplyDto get(Long replyId) {
        log.info("댓글 조회 요청: {}", replyId);
        return freeReplyMapper.read(replyId);
    }

    // 댓글 수정 (작성자 본인만 가능)
    @Override
    public boolean modify(FreeReplyVO freeReply, Long loginMemberId) {
        log.info("댓글 수정 요청: {}", freeReply);

        // 수정 대상 댓글을 DB에서 조회
        FreeReplyDto original = freeReplyMapper.read(freeReply.getReplyId());

        // 예외처리 - 해당 댓글이 존재하지 않을 경우
        if (original == null) {
            throw new IllegalArgumentException("댓글이 존재하지 않습니다.");
        }

        // 로그인한 사용자와 댓글 작성자가 다르면 수정 불가
        if (!loginMemberId.equals(original.getMemberId())) {
            throw new SecurityException("본인만 댓글을 수정할 수 있습니다.");
        }

        // 조건을 통과했을 경우 댓글 수정 실행
        return freeReplyMapper.update(freeReply) == 1;
    }

    // 댓글 삭제 (작성자 본인 또는 관리자만 가능)
    @Override
    public boolean remove(Long replyId, Long loginMemberId, String role) {
        log.info("댓글 삭제 요청: {}", replyId);

        // 삭제할 댓글 정보를 DB에서 조회
        FreeReplyDto reply = freeReplyMapper.read(replyId);

        // 예외 처리 - 댓글이 존재하지 않는 경우
        if (reply == null) {
            throw new IllegalArgumentException("댓글이 존재하지 않습니다.");
        }

        // 작성자가 아니고 관리자도 아니면 삭제 권한 없음
        if (!loginMemberId.equals(reply.getMemberId()) && !role.equals("ADMIN")) {
            throw new SecurityException("작성자 또는 관리자만 삭제할 수 있습니다.");
        }

        // 조건 통과 시 댓글 삭제 실행
        return freeReplyMapper.delete(replyId) == 1;
    }

    // 부모 댓글 목록 조회
    @Override
    public List<FreeReplyDto> getParentReplies(Long freeId, Criteria criteria, String sortType) {
        log.info("부모 댓글 목록 조회: freeId={}, sortType={}", freeId, sortType);
        return freeReplyMapper.getParentReplies(freeId, criteria, sortType);
    }

    // 대댓글 목록 조회
    @Override
    public List<FreeReplyDto> getChildReplies(Long parentId) {
        log.info("대댓글 목록 조회: parentId={}", parentId);
        return freeReplyMapper.getChildReplies(parentId);
    }

    // 전체 댓글 수 조회
    @Override
    public int getTotalReplyCount(Long freeId) {
        log.info("댓글 수 조회: freeId={}", freeId);
        return freeReplyMapper.getTotalReplyCount(freeId);
    }

    // 특정 댓글의 대댓글 수 조회
    @Override
    public int getChildReplyCount(Long parentId) {
        log.info("대댓글 수 조회: parentId={}", parentId);
        return freeReplyMapper.getChildReplyCount(parentId);
    }
}
