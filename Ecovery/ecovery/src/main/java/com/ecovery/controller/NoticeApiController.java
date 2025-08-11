package com.ecovery.controller;

import com.ecovery.domain.MemberVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.NoticeDto;
import com.ecovery.dto.PageDto;
import com.ecovery.security.CustomUserDetails;
import com.ecovery.service.MemberService;
import com.ecovery.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 공지사항 게시글 API 컨트롤러
 * 게시글 등록, 조회, 수정, 삭제, 목록 조회(페이징) 기능의 REST API 컨트롤러 역할을 수행
 * 클라이언트의 요청을 받아 Service 계층(NoticeService)과 상호작용하여 JSON 응답 처리
 * @author   : yukyeong
 * @fileName : NoticeApiController
 * @since    : 250723
 * @history
     - 250723 | yukyeong | 공지사항 목록 조회 API(GET /list), 단건 조회 API(GET /get/{noticeId}) 구현
     - 250723 | yukyeong | 게시글 등록 API (POST /register) 구현 - 유효성 검사 및 로그인 사용자 처리
     - 250723 | yukyeong | 게시글 수정 API (PUT /modify/{noticeId}) 구현 - 수정 성공/실패 처리 포함
     - 250723 | yukyeong | 게시글 삭제 API (DELETE /remove/{noticeId}) 구현
     - 250724 | yukyeong | 게시글 삭제 실패 시 400 Bad Request 응답 처리
     - 250801 | yukyeong | 게시글 등록 API 수정:
 *                        - Principal → Authentication으로 변경하여 사용자 정보 추출
 *                        - memberId 조회 로직을 auth 기반으로 수정
 */

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
@Slf4j
public class NoticeApiController {

    private final NoticeService noticeService;
    private final MemberService memberService;

    // 목록 조회
    // @RequestParam(required = false)은 검색 조건이 없어도 요청 가능하게 함
    //Criteria cri는 페이징 및 검색 조건 객체
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(@RequestParam(required = false) String type,
                                                    @RequestParam(required = false) String keyword,
                                                    Criteria cri) {
        log.info("공지사항 게시글 목록 API 호출");

        // 게시글 목록 조회 (페이징 및 검색 조건 포함)
        List<NoticeDto> list = noticeService.getList(cri);
        // 전체 게시글 수 조회 (페이징 계산용)
        int total = noticeService.getTotal(cri);

        // 응답 데이터 구성
        // 응답은 list, pageMaker, type, keyword를 포함한 JSON 형태
        Map<String, Object> response = new HashMap<>();
        response.put("list", list); // 게시글 목록
        response.put("pageMaker", new PageDto(cri, total)); // 페이지 정보
        response.put("type", type); // 검색 타입
        response.put("keyword", keyword); // 검색어

        // HTTP 200 OK 응답 반환
        return ResponseEntity.ok(response);
    }


    // 게시글 등록 처리
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody NoticeDto noticeDto,
                           BindingResult bindingResult,
                                      Authentication auth) {

        // 1. @Valid 유효성 검사 + BindingResult로 검증
        // NoticeDto에 설정한 유효성 검증에 실패하면 400 Bad Request 응답 반환
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // 2. 유효성 검사 통과한 경우에만 로그인 사용자 이메일 → memberId 조회
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String email = userDetails.getEmail();

        MemberVO member = memberService.getMemberByEmail(email);
        if (member == null) {
            log.warn("등록 요청자의 회원 정보를 찾을 수 없습니다. email: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원 정보가 존재하지 않습니다.");
        }

        Long memberId = member.getMemberId();
        noticeDto.setMemberId(memberId);

        log.info("게시글 등록 처리 전: {}", noticeDto); // 등록 전 NoticeDto 상태 출력 (등록 전에 memberId가 잘 들어갔는지 확인용)

        // 3. 게시글 등록 처리
        try {
            noticeService.register(noticeDto); // DB에 INSERT 수행

            log.info("게시글 등록 처리 후: {}", noticeDto); // 등록 후 NoticeDto 상태 출력 (noticeId 등 자동 생성된 필드 확인 가능)

            // 응답 데이터 구성: 등록된 게시글의 ID(noticeId)를 JSON으로 반환
            Map<String, Object> response = new HashMap<>();
            response.put("noticeId", noticeDto.getNoticeId()); // INSERT 후 Mapper에서 noticeId가 세팅되었다고 가정
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // 상태 코드 201(CREATED)와 함께 응답 본문에 noticeId 포함
        } catch (Exception e) {
            // 등록 중 예외 발생 시 로그 출력 후 500 INTERNAL_SERVER_ERROR 반환
            log.error("게시글 등록 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 서버 에러 시 500
        }
    }


    // 게시글 단건 조회, 수정 버튼 노출 조건 처리
    @GetMapping("/get/{noticeId}")
    public ResponseEntity<?> get(@PathVariable Long noticeId) {
        log.info("게시글 단건 조회 API 호출 - noticeId: {}", noticeId);

        try {
            // 1. 조회수 증가
            noticeService.increaseViewCount(noticeId);

            // 2. 게시글 단건 조회
            NoticeDto noticeDto = noticeService.get(noticeId);

            if (noticeDto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("해당 게시글을 찾을 수 없습니다.");
            }

            // 3. 결과 반환
            return ResponseEntity.ok(noticeDto); // 상태코드 200 OK + 게시글 정보 반환

        } catch (Exception e) {
            log.error("게시글 단건 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 조회 중 오류가 발생했습니다.");
        }
    }


    // 게시글 수정 처리
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/modify/{noticeId}")
    public ResponseEntity<?> modify(@PathVariable Long noticeId,
                                    @Valid @RequestBody NoticeDto noticeDto,
                            BindingResult bindingResult) {

        log.info("게시글 수정 요청 (API): {}", noticeDto);

        // 1. 유효성 검사 실패 시 에러 응답 반환
        if (bindingResult.hasErrors()) {
            // @Valid 검증에 실패한 경우 400 Bad Request 상태 코드와 메시지 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("입력값을 다시 확인해주세요.");
        }

        noticeDto.setNoticeId(noticeId); // 경로에서 받은 ID를 DTO에 주입

        // 2. 수정 로직 처리
        try {
            boolean success = noticeService.modify(noticeDto); // 서비스 계층에서 게시글 수정 수행
            if (success) {
                // 수정 성공 시 200 OK와 함께 응답 데이터 반환
                Map<String, Object> response = new HashMap<>();
                response.put("message", "수정 성공");
                response.put("noticeId", noticeDto.getNoticeId()); // 수정된 게시글 ID도 같이 반환
                return ResponseEntity.ok(response);
            } else {
                // 수정 실패 시 400 Bad Request 상태 코드와 실패 메시지 반환
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("수정에 실패했습니다.");
            }
        } catch (Exception e) {
            // 예외 발생 시 500 Internal Server Error와 함께 오류 메시지 반환
            log.error("게시글 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다.");
        }
    }


    // 게시글 삭제 처리
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/remove/{noticeId}")
    public ResponseEntity<?> remove(@PathVariable Long noticeId) {

        log.info("게시글 삭제 요청 (API): {}", noticeId);

        try {
            boolean success = noticeService.remove(noticeId); // 게시글 삭제 시도

            if (success) {
                // 삭제 성공 시 메시지 포함해서 200 OK 응답
                Map<String, Object> response = new HashMap<>();
                response.put("message", "삭제되었습니다.");
                response.put("noticeId", noticeId);
                return ResponseEntity.ok(response);
            } else {
                String errorMessage = "삭제에 실패했습니다.";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }

        } catch (Exception e) {
            String errorMessage = "삭제 중 에러가 발생하였습니다.";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

}