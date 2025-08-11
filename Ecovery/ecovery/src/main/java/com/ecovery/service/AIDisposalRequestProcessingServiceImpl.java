package com.ecovery.service;

import com.ecovery.domain.DisposalHistoryVO; // 대형폐기물 이력 도메인 객체
import com.ecovery.domain.DisposalHistoryImgVO; // 대형폐기물 이미지 이력 도메인 객체
import com.ecovery.dto.AIPredictionResultDto; // AI 예측 결과 DTO (예측 품목만 포함)
import com.ecovery.dto.DisposalHistoryDto; // 최종 반환용 이력 상세 정보 DTO
import com.ecovery.dto.AIFinalDisposalRequestDto; // 2단계 요청 DTO
import com.ecovery.dto.AIInitialDisposalResponseDto; // 1단계 응답 DTO (2차 분류 옵션 포함)

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service // 스프링 서비스 빈으로 등록
@Transactional // 이 클래스의 모든 퍼블릭 메서드에 트랜잭션 적용
@RequiredArgsConstructor // final 필드들을 주입받는 생성자 자동 생성
@Slf4j // Lombok을 이용한 로깅 기능 활성화
public class AIDisposalRequestProcessingServiceImpl implements AIDisposalRequestProcessingService {

    // 의존성 주입 (다른 서비스들을 사용)
    private final DisposalHistoryService disposalHistoryService;
    private final DisposalHistoryImgService disposalHistoryImgService;
    private final AIService aiService; // AI 서버와 통신하는 서비스

    // 2차 분류 옵션을 관리하는 맵 (AI 예측 결과에 따른 2차 분류 목록)
    // 주의: 현재는 코드 내부에 하드코딩되어 있으나, 실제 운영에서는 데이터베이스에서 관리하는 것이 권장됩니다.
    private static final Map<String, List<String>> SECONDARY_OPTIONS_MAP = new HashMap<>();

    // 클래스 로딩 시 맵 초기화
    static {
        SECONDARY_OPTIONS_MAP.put("의류", List.of("의류-상의", "의류-하의", "의류-잡화"));
        SECONDARY_OPTIONS_MAP.put("가구", List.of("가구-침대", "가구-소파", "가구-책상"));
        SECONDARY_OPTIONS_MAP.put("가전제품", List.of("가전제품-냉장고", "가전제품-세탁기", "가전제품-TV"));
        SECONDARY_OPTIONS_MAP.put("기타", List.of("재활용 불가 품목", "기타 대형폐기물"));
        // TODO: AI 모델이 예측할 수 있는 모든 주요 품목에 대해 적절한 2차 분류 옵션을 추가해야 합니다.
        // TODO: DB에서 동적으로 불러오는 로직으로 변경을 고려하세요.
    }

    /**
     * 1단계: 대형폐기물 초기 처리 요청을 수행합니다.
     * 사용자로부터 이미지와 지역구 정보를 받아 AI 예측을 수행하고,
     * 예측 결과에 기반하여 2차 분류 옵션을 생성하여 임시 이력 정보와 함께 반환합니다.
     *
     * @param multipartFile 사용자가 업로드한 이미지 파일
     * @param regionGu      사용자가 입력한 지역구 정보
     * @param memberId      로그인한 회원 ID (선택 사항, null일 수 있음)
     * @return InitialDisposalResponseDto (새로 생성된 이력 ID, AI 예측 결과, 2차 분류 옵션, 임시 이미지 정보 포함)
     * @throws IllegalArgumentException 필수 파라미터가 누락되거나 유효하지 않을 경우
     * @throws RuntimeException         파일 처리, AI 통신, DB 저장 등 시스템 오류 발생 시
     */
    @Override
    public AIInitialDisposalResponseDto processInitialDisposalRequest(
            MultipartFile multipartFile, String regionGu, Long memberId
    ) {
        // 1. 필수 파라미터 유효성 검증
        if (multipartFile == null || multipartFile.isEmpty()) {
            log.error("processInitialDisposalRequest: 이미지 파일이 비어 있습니다.");
            throw new IllegalArgumentException("이미지 파일이 없습니다. 파일을 첨부해주세요.");
        }
        if (regionGu == null || regionGu.trim().isEmpty()) {
            log.error("processInitialDisposalRequest: 지역구 정보가 비어 있습니다: '{}'", regionGu);
            throw new IllegalArgumentException("지역구 정보를 입력해주세요.");
        }
        // memberId는 서비스 로직에 따라 필수 여부 판단 (현재는 null 허용)

        try {
            // 2. AI 서버 호출: 이미지와 지역 정보를 보내 AI의 예측 결과(주요 품목)를 받아옵니다.
            AIPredictionResultDto aiResult = aiService.predictDisposal(multipartFile, regionGu);
            String aiPrediction = aiResult.getPredictedClass(); // AI가 예측한 주요 품목 (예: "의류")
            Double aiConfidence = aiResult.getAiConfidence(); //ai 예측 정확도
            log.info("reguionGu : {}", regionGu);
            log.info("processInitialDisposalRequest: AI 예측 결과: '{}'", aiPrediction);

            // 3. AI 예측 결과(aiPrediction)를 바탕으로 스프링 서버에서 2차 분류 옵션 목록을 생성/조회합니다.
            // SECONDARY_OPTIONS_MAP에서 aiPrediction에 해당하는 2차 분류 목록을 가져오고,
            // 매핑되는 것이 없으면 "기타" 품목에 대한 2차 분류 목록을 기본값으로 제공합니다.
            List<String> secondaryOptions = SECONDARY_OPTIONS_MAP.getOrDefault(aiPrediction, SECONDARY_OPTIONS_MAP.get("기타"));
            // 만약 "기타"에도 매핑되는 2차 분류가 없거나 맵이 비어있다면, 기본 메시지를 제공합니다.
            if (secondaryOptions == null || secondaryOptions.isEmpty()) {
                secondaryOptions = List.of("정의된 2차 분류 옵션 없음");
            }
            log.info("processInitialDisposalRequest: 생성된 2차 분류 옵션: {}", secondaryOptions);

            // 4. disposal_history 테이블에 초기 이력 데이터를 저장하고, 새로 생성된 historyId를 확보합니다.
            DisposalHistoryVO initialHistory = DisposalHistoryVO.builder()
                    .memberId(memberId)      // 회원 ID (null 허용)
                    .aiPrediction(aiPrediction) // AI 예측 결과 저장
                    .aiConfidence(aiConfidence)  // ai 예측 정확도 저장
                    .regionGu(regionGu)      // 지역구 정보 저장
                    .finalItem(null)         // 2단계에서 사용자 선택에 따라 업데이트될 예정이므로 초기값은 null
                    // created_at은 DB 테이블의 DEFAULT 값(CURRENT_DATE)을 사용하므로 여기서 설정할 필요 없음
                    .build();

            // disposalHistoryService의 saveHistory 메서드는 저장 후 생성된 ID를 반환해야 합니다.
            Long disposalHistoryId = disposalHistoryService.saveHistory(initialHistory);
            log.info("processInitialDisposalRequest: 초기 DisposalHistory가 ID {}로 성공적으로 저장되었습니다.", disposalHistoryId);

            // 5. 업로드된 이미지 파일을 저장하고, disposal_history_img 테이블에 이미지 정보를 기록합니다.
            // 이때, 위에서 확보한 disposalHistoryId를 사용하여 이력과 이미지를 연결합니다.
            DisposalHistoryImgVO savedImgVO = disposalHistoryImgService.saveDisposalImageFile(disposalHistoryId, multipartFile);
            log.info("processInitialDisposalRequest: Disposal 이미지 (ID: {})가 '{}' 경로에 저장되었습니다.", disposalHistoryId, savedImgVO.getDisposalImgUrl());

            // 6. 클라이언트에게 응답할 InitialDisposalResponseDto 객체를 구성하여 반환합니다.
            return AIInitialDisposalResponseDto.builder()
                    .disposalHistoryId(disposalHistoryId) // 새로 생성된 이력 ID
                    .aiPrediction(aiPrediction)           // AI 예측 결과
                    .aiConfidence(aiConfidence)           // AI 예측 정확도
                    .regionGu(regionGu)
                    .secondaryOptions(secondaryOptions)   // 스프링 서버에서 생성한 2차 분류 옵션 목록
                    .tempImgName(savedImgVO.getDisposalImgName())     // 저장된 이미지 파일명
                    .tempOriImgName(savedImgVO.getOriDisposalImgName()) // 원본 이미지 파일명
                    .tempImgUrl(savedImgVO.getDisposalImgUrl())       // 웹 접근 가능한 이미지 URL
                    .build();

        } catch (IOException e) { // 파일 처리(저장) 중 발생할 수 있는 예외
            log.error("processInitialDisposalRequest: 이미지 파일 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("이미지 파일 처리 중 시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", e);
        } catch (IllegalArgumentException e) { // 유효성 검증 실패 시 발생한 예외는 그대로 재던지기
            log.warn("processInitialDisposalRequest: 요청 유효성 검증 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) { // AI 통신 오류, DB 오류 등 예상치 못한 모든 종류의 예외를 포괄적으로 처리
            log.error("processInitialDisposalRequest: 대형폐기물 초기 처리 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            // AI Service에서 던진 구체적인 메시지가 있다면 활용할 수 있도록 e.getMessage()를 포함합니다.
            throw new RuntimeException("대형폐기물 초기 처리 중 시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", e);
        }
    }

    /**
     * 2단계: 대형폐기물 최종 분류 확정 요청을 수행합니다.
     * 사용자가 선택한 최종 품목 정보를 받아 해당 이력을 업데이트하고,
     * 최종 업데이트된 이력의 상세 정보를 반환합니다.
     *
     * @param requestDto FinalDisposalRequestDto (disposalHistoryId와 selectedFinalItem 포함)
     * @return DisposalHistoryDto (최종 업데이트된 이력의 상세 정보)
     * @throws IllegalArgumentException 필수 파라미터가 누락되거나 유효하지 않을 경우
     * @throws RuntimeException         DB 업데이트 또는 조회 중 시스템 오류 발생 시
     */
    @Override
    public DisposalHistoryDto finalizeDisposalRequest(AIFinalDisposalRequestDto requestDto) {
        // 1. 필수 파라미터 유효성 검증
        if (requestDto.getDisposalHistoryId() == null) {
            log.error("finalizeDisposalRequest: 최종 분류 요청에 Disposal History ID가 없습니다.");
            throw new IllegalArgumentException("유효하지 않은 요청입니다. 이력 ID가 누락되었습니다.");
        }
        if (requestDto.getSelectedFinalItem() == null || requestDto.getSelectedFinalItem().trim().isEmpty()) {
            log.error("finalizeDisposalRequest: 최종 분류 요청에 선택된 최종 품목이 없습니다. History ID: {}", requestDto.getDisposalHistoryId());
            throw new IllegalArgumentException("최종 분류 품목을 선택해주세요.");
        }

        try {
            // 2. disposal_history 테이블의 final_item 컬럼을 업데이트합니다.
            // DisposalHistoryVO 객체를 생성하여 업데이트에 필요한 ID와 최종 품목을 설정합니다.
            DisposalHistoryVO historyToUpdate = DisposalHistoryVO.builder()
                    .disposalHistoryId(requestDto.getDisposalHistoryId())
                    .finalItem(requestDto.getSelectedFinalItem())
                    .build();

            // disposalHistoryService의 updateHistory 메서드를 호출하여 DB를 업데이트합니다.
            disposalHistoryService.updateHistory(historyToUpdate);
            log.info("finalizeDisposalRequest: DisposalHistory (ID: {})의 최종 품목이 '{}'으로 성공적으로 업데이트되었습니다.",
                    requestDto.getDisposalHistoryId(), requestDto.getSelectedFinalItem());

            // 3. 최종적으로 업데이트된 DisposalHistoryDto를 조회하여 반환합니다.
            // 이 메서드는 이미지 정보, 품목 정보 등 모든 관련 데이터를 조인하여 가져와야 합니다.
            DisposalHistoryDto finalDisposalDto = disposalHistoryService.getHistory(requestDto.getDisposalHistoryId());
            if (finalDisposalDto == null) {
                log.error("finalizeDisposalRequest: 업데이트 후 History (ID: {})를 찾을 수 없습니다. 데이터 불일치 가능성.", requestDto.getDisposalHistoryId());
                throw new RuntimeException("업데이트된 이력을 찾을 수 없습니다. 시스템 오류가 발생했습니다.");
            }
            return finalDisposalDto;

        } catch (IllegalArgumentException e) { // 유효성 검증 실패 시 발생한 예외는 그대로 재던지기
            log.warn("finalizeDisposalRequest: 최종 분류 요청 유효성 검증 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) { // DB 업데이트/조회 중 발생할 수 있는 모든 종류의 예외를 포괄적으로 처리
            log.error("finalizeDisposalRequest: 대형폐기물 최종 처리 중 예상치 못한 오류 발생 (History ID: {}): {}",
                    requestDto.getDisposalHistoryId(), e.getMessage(), e);
            throw new RuntimeException("대형폐기물 최종 처리 중 시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", e);
        }
    }
}