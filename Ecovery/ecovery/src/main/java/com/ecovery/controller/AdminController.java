package com.ecovery.controller;

import com.ecovery.domain.MemberVO;
import com.ecovery.domain.PointVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.PageDto;
import com.ecovery.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 페이지 > 회원 관리 컨트롤러
 * - 전체 회원 목록 조회
 * - 회원 상세 정보 조회 (옵션)
 * - 회원 정보 수정
 * 작성자: 방희경
 */
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/admin/member")
@Slf4j
public class AdminController {

    private final MemberService memberService;

    // 전체 회원 목록 조회
    @GetMapping
    public String getAllMembers(Criteria cri, Model model) {
        List<MemberVO> memberList = memberService.getAllMembers(cri);
        model.addAttribute("memberList", memberList);

        int total = memberService.getTotal(cri);
        model.addAttribute("memberPage", new PageDto(cri, total));

        //html null 오류 방지
        Map<String, Object> memberStats = new HashMap<>();
        memberStats.put("totalMembers", memberList.size());  // 또는 임의의 숫자
        // 다른 필요한 key도 형태 맞춰서 넣기
        model.addAttribute("memberStats", memberStats);
        // 존재하지 않을 경우에도 아래처럼 "더미 값"으로라도 전부 세팅
        memberStats.put("totalMembersGrowth", 0);
        memberStats.put("newMembersThisMonth", 0);
        memberStats.put("newMembersGrowth", 0);
        memberStats.put("activeMembers", 0);
        memberStats.put("totalTransactionAmount", 0);
        memberStats.put("transactionGrowth", 0);
        memberStats.put("activeMembersGrowth", 0);
        if (!model.containsAttribute("searchParams")) {
            model.addAttribute("searchParams", new MemberVO()); // DTO일 경우
            // 또는 model.addAttribute("searchParams", new HashMap<String, Object>());
        }


        return "admin/memberList";
    }

    // 회원 상세 정보 조회(이메일, 닉네임, 포인트 변동 내역)
    @GetMapping(value = "/{memberId}")
    public String getMemberDetail(@PathVariable("memberId") Long memberId, Model model) {
        MemberVO memberVO = memberService.getMemberById(memberId);
        List<PointVO> pointHistoryList = memberService.getPointHistoryMemberById(memberId);

        model.addAttribute("memberVO", memberVO);
        model.addAttribute("pointHistoryList", pointHistoryList);

        return "admin/memberDetail";
    }

    @GetMapping("/detail/{memberId}")
    @ResponseBody
    public ResponseEntity<MemberVO> getMemberDetail(@PathVariable Long memberId) {
        MemberVO member = memberService.getMemberById(memberId);
        if (member == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(member);
    }

    @PostMapping("/update/role/{memberId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateMemberRole(@PathVariable("memberId") Long memberId,@RequestBody MemberVO memberVO) {
        // memberId를 MemberVO에 직접 설정
        memberVO.setMemberId(memberId);

        log.info("memberVO: {}", memberVO);
        log.info("role: {}", memberVO.getRole());

        try {
            memberService.updateMemberByAdmin(memberVO);
            // 성공 응답: success: true, message: "..."
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "권한이 성공적으로 변경되었습니다."
            ));
        } catch (Exception e) {
            log.error("권한 변경 중 오류 발생: {}", e.getMessage());
            // 실패 응답: success: false, message: "..."
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "권한 변경에 실패했습니다."
            ));
        }
    }


}
