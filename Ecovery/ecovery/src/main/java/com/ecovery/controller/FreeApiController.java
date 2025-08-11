package com.ecovery.controller;

import com.ecovery.constant.Role;
import com.ecovery.domain.FreeVO;
import com.ecovery.domain.MemberVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.FreeDto;
import com.ecovery.dto.FreeImgDto;
import com.ecovery.dto.PageDto;
import com.ecovery.service.FreeImgService;
import com.ecovery.service.FreeService;
import com.ecovery.service.MemberService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/free")
@RequiredArgsConstructor
@Slf4j
public class FreeApiController {

    private final FreeService freeService;
    private final MemberService memberService;
    private final Validator validator;
    private final FreeImgService freeImgService;

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> register(
            @RequestPart("freeDto") FreeDto freeDto,
            @RequestPart("imgFile") List<MultipartFile> imgFiles,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Set<ConstraintViolation<FreeDto>> violations = validator.validate(freeDto);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<FreeDto> v : violations) {
                sb.append(v.getPropertyPath()).append(": ").append(v.getMessage()).append("\n");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효성 오류:\n" + sb);
        }

        String nickname = principal.getName();
        MemberVO member = memberService.getMemberByNickname(nickname);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원 정보를 찾을 수 없습니다. 다시 로그인해주세요.");
        }
        Long memberId = member.getMemberId();
        freeDto.setMemberId(memberId);

        try {
            freeService.register(freeDto, imgFiles);
            return ResponseEntity.status(HttpStatus.CREATED).body("게시글이 등록되었습니다.");
        } catch (Exception e) {
            log.error("게시글 등록 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(Criteria cri){
        List<FreeDto> list = freeService.getAll(cri);
        int total = freeService.getTotalCount(cri);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("pageMaker", new PageDto(cri, total));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/get/{freeId}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long freeId){
        freeService.updateViewCount(freeId);
        FreeDto free = freeService.get(freeId);

        Map<String, Object> result = new HashMap<>();
        result.put("free", free);

        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/modify/{freeId}")
    public ResponseEntity<String> modify(
            @PathVariable Long freeId,
            @RequestPart("freeDto") FreeDto freeDto,
            @RequestPart(value = "imgFile", required = false) List<MultipartFile> imgFiles,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String nickname = principal.getName();
        MemberVO member = memberService.getMemberByNickname(nickname);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원 정보를 찾을 수 없습니다. 다시 로그인해주세요.");
        }

        Long currentUserId = member.getMemberId();
        Role role = member.getRole();

        FreeDto origin = freeService.get(freeId);
        if (!origin.getMemberId().equals(currentUserId) && role != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("작성자 또는 관리자만 수정할 수 있습니다.");
        }

        // 4. itemId를 DTO에 설정 (강제 일치)
        freeDto.setFreeId(freeId);

        // 5. 대표 이미지 누락 체크 (기존 이미지 모두 삭제 후 새 이미지가 없으면 에러)
        boolean hasImagesToKeep = freeDto.getImgList().stream()
                .anyMatch(dto -> !dto.isToBeDeleted());

        boolean hasNewImages = imgFiles != null && !imgFiles.isEmpty()
                && imgFiles.stream().anyMatch(file -> !file.isEmpty());

        if (!hasImagesToKeep && !hasNewImages) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("최소 한 개 이상의 상품 이미지를 유지 또는 추가해야 합니다.");
        }

        try {
            // 이 부분에 로그를 추가합니다.
            log.info("Received freeDto's dealStatus: {}", freeDto.getDealStatus());

            freeService.modify(freeDto, imgFiles);
            return ResponseEntity.ok("게시글이 수정되었습니다.");
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 중 오류가 발생했습니다.");
        }
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/remove/{freeId}")
    public ResponseEntity<String> remove(@PathVariable Long freeId, Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String nickname = principal.getName();
        MemberVO member = memberService.getMemberByNickname(nickname);

        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원 정보를 찾을 수 없습니다. 다시 로그인해주세요.");
        }

        Long currentUserId = member.getMemberId();
        Role role = member.getRole();

        FreeDto dto = freeService.get(freeId);
        Long postOwnerId = dto.getMemberId();

        if (!currentUserId.equals(postOwnerId) && role != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }

        dto.setMemberId(postOwnerId);
        boolean result = freeService.remove(dto);

        return result
                ? ResponseEntity.ok("게시글이 삭제되었습니다.")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
    }

    @DeleteMapping("/image/{freeImgId}")
    public ResponseEntity<String> deleteImage(@PathVariable Long freeImgId) {
        try {
            freeImgService.deleteFreeImg(freeImgId);
            return ResponseEntity.ok("이미지 삭제 성공");
        } catch (Exception e) {
            log.error("이미지 삭제 실패 - imgId: {}", freeImgId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 삭제 중 오류 발생");
        }
    }
}
