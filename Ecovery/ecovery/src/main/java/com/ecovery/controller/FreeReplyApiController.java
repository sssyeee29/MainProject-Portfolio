package com.ecovery.controller;

import com.ecovery.domain.FreeReplyVO;
import com.ecovery.domain.MemberVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.FreeReplyDto;
import com.ecovery.service.FreeReplyService;
import com.ecovery.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
 * 무료나눔 댓글 API 컨트롤러
 * 댓글 및 대댓글 등록, 조회, 수정, 삭제 기능을 JSON 기반으로 처리
 * 인증된 사용자만 접근 가능
 *
 * 권한 조건:
 * - 등록: 로그인한 사용자(회원 or 관리자)
 * - 수정: 댓글 작성자 본인
 * - 삭제: 댓글 작성자 본인 또는 관리자
 *
 * 부모댓글 목록은 정렬조건(최신순/등록순)에 따라 조회 가능
 *
 * @author : yeonsu
 * @fileName : FreeReplyApiController
 * @since : 250725
 */

@RestController
@RequestMapping("/api/replies")
@RequiredArgsConstructor
@Slf4j
public class FreeReplyApiController {

    private final FreeReplyService freeReplyService;
    private final MemberService memberService;

    // 댓글 등록 (회원 또는 관리자만 가능)
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> register(@RequestBody @Valid FreeReplyVO freeReply,
                                           Principal principal) {
        // 로그인 확인
        if (principal == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

//        // 현재 로그인 사용자 정보 조회 (이메일 -> memberId, role)
//        String email = principal.getName();
//        Long loginMemberId = memberService.getMemberByEmail(email).getMemberId();
//        String role = memberService.getMemberByEmail(email).getRole().name();

        // 닉네임으로 로그인 사용자 조회
        String nickname = principal.getName();
        MemberVO member = memberService.getMemberByNickname(nickname);
        if (member == null) {
            log.error("로그인된 사용자의 닉네임({})으로 회원 정보를 찾을 수 없습니다.", nickname);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원 정보를 찾을 수 없습니다. 다시 로그인해주세요.");
        }

        Long memberId = member.getMemberId();
        String role = member.getRole().name();

        // 서버에서 memberId 강제 설정
        freeReply.setMemberId(memberId);

        // 댓글 등록 서비스 호출
        try {
            freeReplyService.register(freeReply, memberId, role);
            return ResponseEntity.ok("댓글이 등록되었습니다.");
        } catch (Exception e) {
            log.error("댓글 등록 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 등록 중 오류가 발생했습니다.");
        }
    }

    // 댓글 단건 조회 - 댓글 ID를 통해 해당 댓글 정보 조회
    @GetMapping("/{replyId}")
    public ResponseEntity<FreeReplyDto> getReply(@PathVariable Long replyId) {

        FreeReplyDto reply = freeReplyService.get(replyId);

        return ResponseEntity.ok(reply);
    }

    // 댓글 수정 - 댓글 작성시 본인만 수정 가능 || 수정 성공 시 200 OK, 실패시 403 반환
    @PutMapping("/modify/{replyId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> modify(@PathVariable Long replyId,
                                         @RequestBody @Valid FreeReplyVO reply,
                                         Principal principal) {

        // 로그인을 하지 않은 경우 

        if (principal == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // ✅ 로그인한 사용자의 nickname으로 Member 조회
        String nickname = principal.getName();
        MemberVO member = memberService.getMemberByNickname(nickname);
        if (member == null) {
            return ResponseEntity.status(401).body("회원 정보를 찾을 수 없습니다.");
        }

        Long loginMemberId = member.getMemberId();

        // 댓글 ID 설정 (경로에서 받은 ID를 VO에 반영)
        reply.setReplyId(replyId);

        boolean result = freeReplyService.modify(reply, loginMemberId);

        return result
                ? ResponseEntity.ok("댓글이 수정되었습니다.")
                : ResponseEntity.status(403).body("수정 권한이 없습니다.");
    }


    // 댓글 삭제 - 작성자 본인 또는 관리자만 가능
    @DeleteMapping("/remove/{replyId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> remove(@PathVariable Long replyId,
                                         Principal principal) {

        // 로그인 하지 않은 경우
        if (principal == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // nickname으로 Member 정보 조회
        String nickname = principal.getName();
        MemberVO member = memberService.getMemberByNickname(nickname);
        if (member == null) {
            return ResponseEntity.status(401).body("회원 정보를 찾을 수 없습니다.");
        }

        Long loginMemberId = member.getMemberId();
        String role = member.getRole().name();

        boolean result = freeReplyService.remove(replyId, loginMemberId, role);

        return result
                ? ResponseEntity.ok("댓글이 삭제되었습니다.")
                : ResponseEntity.status(403).body("삭제 권한이 없습니다.");
    }

    // 부모 댓글 목록 조회 - 최신순, 등록순 정렬 가능
    @GetMapping("/parent/{freeId}")
    public ResponseEntity<Map<String, Object>> getParentReplies(@PathVariable Long freeId,
                                                                @RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "10") int amount,
                                                                @RequestParam(defaultValue = "recent") String sortType) {
        // 페이징 정보를 Criteria 객체로 생성
        Criteria criteria = new Criteria(page, amount);

        // 부모 댓글 목록 조회 (페이징 + 정렬 적용)
        List<FreeReplyDto> replies = freeReplyService.getParentReplies(freeId, criteria, sortType);

        // 전체 부모 댓글 수 조회 (페이징 처리를 위해 필요)
        int totalCount = freeReplyService.getTotalReplyCount(freeId);

        // 결과 데이터 Map에 담기
        Map<String, Object> result = new HashMap<>();
        result.put("list", replies); // 부모 댓글 리스트
        result.put("total", totalCount); // 전체 부모 댓글 수

        return ResponseEntity.ok(result);
    }

    // 대댓글 목록 조회 - 특정 부모 댓글에 달린 대댓글 리스트 반환
    // URL 경로에서 부모 댓글 ID를 받아 대댓글을 조회
    @GetMapping("/child/{parentId}")
    public ResponseEntity<List<FreeReplyDto>> getChildReplies(@PathVariable Long parentId) {
        
        // 서비스에서 해당 부모 댓글 ID에 대한 대댓글 목록 조회 
        List<FreeReplyDto> replies = freeReplyService.getChildReplies(parentId);
        
        // 200 OK 상태코드와 함께 대댓글 리스트 반환
        return ResponseEntity.ok(replies);
    }

    /**
     * 대댓글 등록
     * - 로그인한 회원 또는 관리자만 가능
     * - 2단계까지만 허용 (대댓글의 대댓글 등록 방지)
     */
    @PostMapping("/register/child")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> registerChildReply(@RequestBody @Valid FreeReplyVO freeReply,
                                                     Principal principal) {


        // 현재 로그인 사용자 정보 (nickname 기반)
        String nickname = principal.getName();
        MemberVO member = memberService.getMemberByNickname(nickname);
        if (member == null) {
            log.error("닉네임 '{}'에 해당하는 회원을 찾을 수 없습니다.", nickname);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원 정보를 찾을 수 없습니다.");
        }

        Long loginMemberId = member.getMemberId();
        String role = member.getRole().name();

        // 서버에서 댓글 작성자 ID 주입 (NPE 방지 - NullPointerException)
        freeReply.setMemberId(loginMemberId);

        // 2단계 대댓글 제한 - parentId가 있는 경우, 해당 댓글이 이미 대댓글인지 확인
        if (freeReply.getParentId() != null) {
            FreeReplyDto parent = freeReplyService.get(freeReply.getParentId());
            if (parent != null && parent.getParentId() != null) {
                return ResponseEntity.badRequest().body("대댓글은 2단계까지만 허용됩니다.");
            }
        }

        // 등록 처리
        freeReplyService.register(freeReply, loginMemberId, role);

        return ResponseEntity.ok("대댓글이 등록되었습니다.");
    }


}




















