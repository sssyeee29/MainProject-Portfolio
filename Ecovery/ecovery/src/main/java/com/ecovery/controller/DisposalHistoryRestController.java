package com.ecovery.controller;

import com.ecovery.dto.DisposalHistoryDto; // 최종 응답용 DTO
import com.ecovery.dto.AIFinalDisposalRequestDto; // 2단계 요청 DTO
import com.ecovery.dto.AIInitialDisposalResponseDto; // 1단계 응답 DTO
import com.ecovery.service.AIDisposalRequestProcessingService; // 2단계 처리 서비스 (사용자가 제공한 AIDisposalRequestProcessingServiceImpl 기준)
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController // REST 컨트롤러임을 선언 (JSON/XML 응답)
@RequestMapping("/api/disposal") // 기본 URL 경로 설정
@RequiredArgsConstructor // final 필드를 주입받는 생성자 자동 생성
@Slf4j // Lombok을 이용한 로깅 기능 활성화
public class DisposalHistoryRestController {

    // 의존성 주입: 폐기물 요청 처리 서비스만 주입
    private final AIDisposalRequestProcessingService aiDisposalRequestProcessingService;

    /**
     * 1단계: 대형폐기물 초기 처리 요청 (AI 예측 및 2차 분류 옵션 제공)
     * POST /api/disposal/initial-request
     * - 이미지 파일과 지역구 정보를 받아 AI 예측을 수행하고 초기 응답 데이터를 반환합니다.
     * - @RequestPart: multipart/form-data 요청에서 파일을 받기 위해 사용
     * - @RequestParam: 폼 데이터 필드를 받기 위해 사용
     */
    @PostMapping("/initial-request")
    public ResponseEntity<?> processInitialDisposalRequest(
            @RequestPart("file") MultipartFile multipartFile, // 'file'이라는 이름으로 전송된 파일
            @RequestParam("regionGu") String regionGu,       // 'regionGu'라는 이름으로 전송된 문자열
            @RequestParam(value = "memberId", required = false) Long memberId // 선택 사항
    ) {
        log.info("filename: {}", multipartFile.getOriginalFilename());
        log.info("Initial disposal request received: regionGu={}, memberId={}", regionGu, memberId);
        try {
            AIInitialDisposalResponseDto responseDto = aiDisposalRequestProcessingService.processInitialDisposalRequest(
                    multipartFile, regionGu, memberId
            );
            return new ResponseEntity<>(responseDto, HttpStatus.OK); // 성공 시 200 OK
        } catch (IllegalArgumentException e) {
            log.error("Validation error for initial disposal request: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 유효성 검증 실패 시 400 Bad Request
        } catch (Exception e) {
            log.error("Error processing initial disposal request: {}", e.getMessage(), e);
            return new ResponseEntity<>("초기 폐기물 처리 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR); // 그 외 서버 오류 시 500 Internal Server Error
        }
    }

    /**
     * 2단계: 대형폐기물 최종 분류 확정 요청
     * POST /api/disposal/finalize-request
     * - 초기 요청에서 받은 historyId와 사용자가 최종 선택한 품목을 받아 이력을 업데이트합니다.
     * - @RequestBody: JSON 형태의 요청 본문을 DTO로 매핑
     */
    @PostMapping("/finalize-request")
    public ResponseEntity<?> finalizeDisposalRequest(
            @RequestBody AIFinalDisposalRequestDto requestDto // DTO 이름 변경
    ) {
        log.info("Final disposal request received: historyId={}, selectedFinalItem={}",
                requestDto.getDisposalHistoryId(), requestDto.getSelectedFinalItem());
        try {
            DisposalHistoryDto responseDto = aiDisposalRequestProcessingService.finalizeDisposalRequest(requestDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK); // 성공 시 200 OK
        } catch (IllegalArgumentException e) {
            log.error("Validation error for final disposal request: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 유효성 검증 실패 시 400 Bad Request
        } catch (Exception e) {
            log.error("Error processing final disposal request: {}", e.getMessage(), e);
            return new ResponseEntity<>("최종 폐기물 처리 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR); // 그 외 서버 오류 시 500 Internal Server Error
        }
    }
}