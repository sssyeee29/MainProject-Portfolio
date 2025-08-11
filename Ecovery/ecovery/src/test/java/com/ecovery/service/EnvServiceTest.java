package com.ecovery.service;

import com.ecovery.domain.EnvVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.EnvDto;
import com.ecovery.dto.EnvFormDto;
import com.ecovery.dto.EnvImgDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 환경톡톡 게시글 Service 테스트
 * Service 계층의 CRUD 기능 단위 테스트 클래스
 * @author : yukyeong
 * @fileName : EnvServiceTest
 * @since : 250715
 * @history
     - 250715 | yukyeong | 게시글 등록, 단건 조회, 수정, 삭제 테스트 작성
     - 250716 | yukyeong | 게시글 목록 조회 (페이징 포함), 게시글 총 개수 조회, 조회수 증가 테스트 작성
     - 250718 | yukyeong | 테스트 전반적으로 EnvDto 기반으로 통일 및 로그 개선
     - 250725 | yukyeong | 게시글 등록/수정 테스트에 category 필드 추가 및 검증
     - 250728 | yukyeong | EnvDto 단독 사용 → EnvFormDto 기반으로 수정, 게시글 + 이미지 등록, 수정, 삭제 테스트 추가
     - 250811 | yukyeong | 게시글 + 이미지 등록 테스트, 이미지 없이 게시글만 등록 테스트 수정
 */

@SpringBootTest
@Slf4j
class EnvServiceTest {

    @Autowired
    private EnvService envService;

    @Autowired
    private EnvImgService envImgService;

    @Test
    @DisplayName("게시글 + 이미지 등록 테스트")
    @Transactional
    public void testRegisterWithImage() throws Exception{

        // Given - 테스트용 게시글 DTO 생성 및 값 세팅
        EnvDto envDto = new EnvDto();
        envDto.setMemberId(1L);
        envDto.setTitle("통합 테스트 제목");
        envDto.setContent("통합 테스트 내용");
        envDto.setCategory("tips");

        // 첨부 이미지(MockMultipartFile)
        MockMultipartFile mockFile = new MockMultipartFile(
                "imgFile",
                "test.jpg",
                "image/jpeg",
                "sample image content".getBytes()
        );

        // EnvFormDto 안에 DTO와 이미지 리스트를 함께 담는다
        // 실제 서비스 메서드에서는 게시글 정보와 파일 리스트를 동시에 받으므로 DTO와 파일을 함께 넘기기 위한 구조
        EnvFormDto formDto = EnvFormDto.builder()
                .envDto(envDto)
                .envImgFiles(List.of(mockFile))   // ★ 핵심 변경점
                // .contentImgUrls(List.of("http://localhost/ecovery/env/abc.jpg")) // (옵션) 본문 이미지 URL 등록 검증 시
                .build();

        // When - 단일 파라미터로 호출
        envService.register(formDto);

        // 게시글 등록 후 자동 생성된 ID 확인
        Long envId = envDto.getEnvId();
        assertNotNull(envId, "게시글 ID는 null이면 안 됩니다.");

        // Then - 이미지까지 함께 등록되었는지 확인
        List<EnvImgDto> imgList = envImgService.getListByEnvId(envId);
        assertEquals(1, imgList.size(), "이미지는 1건 등록되어야 합니다."); // 이미지가 1건 등록되었는지 확인

        // 원본 이미지명과 게시글 ID가 일치하는지 확인
        EnvImgDto attached = imgList.get(0);
        assertEquals("test.jpg", attached.getOriImgName());
        assertEquals(envId, attached.getEnvId());

        log.info("게시글 + 이미지 등록 성공. 등록된 이미지: {}", attached);

    }

    @Test
    @DisplayName("이미지 없이 게시글만 등록 테스트")
    @Transactional
    public void testRegisterWithoutImage() throws Exception {
        // Given - 게시글 정보만 설정
        EnvDto envDto = new EnvDto();
        envDto.setMemberId(1L);
        envDto.setTitle("이미지 없는 게시글 제목");
        envDto.setContent("이미지 없이 등록되는 게시글 내용");
        envDto.setCategory("news");

        // EnvFormDto 생성 (이미지 없음)
        // 실제 서비스에서는 EnvFormDto를 통해 게시글과 이미지가 함께 전달되므로 구조 동일
        EnvFormDto formDto = EnvFormDto.builder()
                .envDto(envDto)
                .build();

        // When - 게시글만 등록
        envService.register(formDto);

        // Then
        // 1. 등록된 게시글 ID가 null이 아닌지 확인 (등록 성공 여부)
        Long envId = envDto.getEnvId();
        assertNotNull(envId, "게시글 ID는 null이면 안 됩니다.");

        // 2. 등록된 게시글을 DB에서 다시 조회하여 값 확인
        EnvDto saved = envService.get(envId);
        assertNotNull(saved, "등록된 게시글은 null이면 안 됩니다.");
        assertEquals("이미지 없는 게시글 제목", saved.getTitle());
        assertEquals("이미지 없이 등록되는 게시글 내용", saved.getContent());

        // 3. 이미지가 하나도 저장되지 않았는지 확인
        List<EnvImgDto> imgList = envImgService.getListByEnvId(envId);
        assertTrue(imgList.isEmpty(), "이미지가 없어야 합니다.");

        log.info("이미지 없이 게시글만 등록 성공: {}", saved);
    }


//    @Test
//    @DisplayName("게시글 단건 조회")
//    public void testGet() {
//        // Given - 조회할 게시글의 ID 지정
//        Long targetId = 1L;
//
//        // When - 해당 ID로 게시글 단건 조회 (DTO 반환)
//        EnvDto envDto = envService.get(targetId);
//
//        //Then
//        assertNotNull(envDto, "조회 결과가 null이면 안됨.");
//        // 제목과 내용 확인
//        log.info("조회된 게시글 제목 : {}", envDto.getTitle());
//        log.info("조회된 게시글 내용 : {}", envDto.getContent());;
//    }
//
//
//    @Test
//    @DisplayName("게시글 + 이미지 수정 테스트")
//    @Transactional
//    public void testModifyWithImage() throws Exception {
//        // Given - 게시글 등록 + 이미지 1장 등록
//        EnvDto envDto = new EnvDto();
//        envDto.setMemberId(1L);
//        envDto.setTitle("수정 테스트 제목");
//        envDto.setContent("수정 테스트 내용");
//        envDto.setCategory("tips");
//
//        // 기존 이미지
//        MockMultipartFile originalImage = new MockMultipartFile(
//                "imgFile",
//                "original.jpg",
//                "image/jpeg",
//                "original image content".getBytes()
//        );
//
//        EnvFormDto formDto = EnvFormDto.builder()
//                .envDto(envDto)
//                .build();
//
//        envService.register(formDto, List.of(originalImage)); // 등록
//        Long envId = envDto.getEnvId();
//        assertNotNull(envId, "등록된 게시글 ID는 null이면 안됩니다.");
//
//        // 기존 이미지 ID 확인
//        List<EnvImgDto> originalImgList = envImgService.getListByEnvId(envId);
//        assertEquals(1, originalImgList.size(), "초기 이미지 1개 등록되어야 함");
//        Long toDeleteImgId = originalImgList.get(0).getEnvImgId();
//
//        // When - 게시글 내용 + 이미지 수정
//        envDto.setTitle("수정된 제목");
//        envDto.setContent("수정된 내용");
//        envDto.setCategory("issue");
//
//        // 새 이미지
//        MockMultipartFile newImage = new MockMultipartFile(
//                "imgFile",
//                "new.jpg",
//                "image/jpeg",
//                "new image content".getBytes()
//        );
//
//        EnvFormDto modifiedFormDto = EnvFormDto.builder()
//                .envDto(envDto)
//                .deleteImgIds(List.of(toDeleteImgId)) // 기존 이미지 삭제
//                .build();
//
//        boolean result = envService.modify(modifiedFormDto, List.of(newImage)); // 수정 호출
//
//        // Then - 수정 검증
//        assertTrue(result, "수정 결과가 false면 안 됩니다.");
//
//        EnvDto updated = envService.get(envId);
//        assertNotNull(updated);
//        assertEquals("수정된 제목", updated.getTitle());
//        assertEquals("수정된 내용", updated.getContent());
//        assertEquals("issue", updated.getCategory());
//
//        // 이미지 검증: 기존 이미지 삭제되고, 새 이미지 1장 등록되어야 함
//        List<EnvImgDto> updatedImgList = envImgService.getListByEnvId(envId);
//        assertEquals(1, updatedImgList.size(), "이미지 1장만 남아야 함");
//        assertEquals("new.jpg", updatedImgList.get(0).getOriImgName());
//
//        log.info("게시글 + 이미지 수정 성공: {}", updated);
//    }
//
//    @Test
//    @DisplayName("이미지 없이 게시글만 수정 테스트")
//    @Transactional
//    public void testModifyWithoutImage() throws Exception {
//        // Given - 게시글만 등록 (이미지 없음)
//        EnvDto envDto = new EnvDto();
//        envDto.setMemberId(1L);
//        envDto.setTitle("초기 제목");
//        envDto.setContent("초기 내용");
//        envDto.setCategory("news");
//
//        EnvFormDto registerForm = EnvFormDto.builder()
//                .envDto(envDto)
//                .build();
//
//        envService.register(registerForm, List.of()); // 이미지 없이 등록
//        Long envId = envDto.getEnvId();
//        assertNotNull(envId, "게시글 ID는 null이면 안 됩니다.");
//
//        // When - 제목, 내용, 카테고리만 수정
//        EnvDto modifiedDto = EnvDto.builder()
//                .envId(envId)
//                .memberId(envDto.getMemberId()) // 수정 시 작성자 ID 필요
//                .title("수정된 제목")
//                .content("수정된 내용")
//                .category("event")
//                .build();
//
//        EnvFormDto modifyForm = EnvFormDto.builder()
//                .envDto(modifiedDto)
//                .build();
//
//        boolean result = envService.modify(modifyForm, List.of()); // 이미지 없음
//
//        // Then - 수정 결과 및 필드 확인
//        assertTrue(result, "수정 결과는 true여야 합니다.");
//        EnvDto updated = envService.get(envId);
//        assertNotNull(updated, "수정 후 게시글이 null이면 안 됩니다.");
//        assertEquals("수정된 제목", updated.getTitle());
//        assertEquals("수정된 내용", updated.getContent());
//        assertEquals("event", updated.getCategory());
//
//        // 이미지가 여전히 없어야 함
//        List<EnvImgDto> imgList = envImgService.getListByEnvId(envId);
//        assertTrue(imgList.isEmpty(), "수정 후에도 이미지가 없어야 합니다.");
//
//        log.info("이미지 없이 게시글만 수정 성공: {}", updated);
//    }
//
//
//    @Test
//    @DisplayName("게시글 + 이미지 삭제 테스트")
//    @Transactional
//    public void testRemoveWithImage() throws Exception {
//        // Given
//        //  1. 게시글 정보 설정
//        EnvDto envDto = new EnvDto();
//        envDto.setMemberId(1L);
//        envDto.setTitle("삭제 테스트 제목");
//        envDto.setContent("삭제 테스트 내용");
//        envDto.setCategory("event");
//
//        // 2. Mock 이미지 파일 생성 (가짜 이미지 데이터를 포함한 MultipartFile)
//        MockMultipartFile imageFile = new MockMultipartFile(
//                "imgFile",
//                "delete.jpg",
//                "image/jpeg",
//                "image to delete".getBytes()
//        );
//
//        // 3. EnvFormDto 생성 (게시글 + 이미지 등록에 사용)
//        EnvFormDto formDto = EnvFormDto.builder()
//                .envDto(envDto)
//                .build();
//
//        // 4. 서비스 호출을 통해 게시글 + 이미지 등록
//        envService.register(formDto, List.of(imageFile)); // 등록 수행
//
//        // 5. 등록된 게시글의 ID 확인 (null이 아니어야 정상 등록)
//        Long envId = envDto.getEnvId();
//        assertNotNull(envId, "게시글 ID는 null이면 안 됩니다.");
//
//        // 6. 이미지가 정상 등록되었는지 확인 (이미지 1장이어야 함)
//        List<EnvImgDto> imgList = envImgService.getListByEnvId(envId);
//        assertEquals(1, imgList.size(), "이미지 1건이 등록되어야 합니다.");
//
//        // When - 게시글 삭제 (해당 ID 기준 삭제)
//        boolean deleted = envService.remove(envId);
//
//        // Then
//        // 삭제 결과가 true인지 확인 (삭제 성공)
//        assertTrue(deleted, "게시글 삭제는 성공해야 합니다.");
//
//        // 삭제된 게시글을 조회하면 null이어야 함
//        EnvDto deletedPost = envService.get(envId);
//        assertNull(deletedPost, "삭제된 게시글은 null이어야 합니다.");
//
//        // 해당 게시글의 이미지도 모두 삭제되었는지 확인
//        List<EnvImgDto> afterImgList = envImgService.getListByEnvId(envId);
//        assertTrue(afterImgList.isEmpty(), "이미지도 함께 삭제되어야 합니다.");
//
//        log.info("게시글 및 이미지 삭제 성공: ID = {}", envId);
//    }
//
//
//    @Test
//    @DisplayName("게시글 페이징 + 검색 목록 조회 테스트")
//    @Transactional
//    public void testGetList(){
//        // Given - 검색 조건과 페이징 정보 세팅
//        Criteria cri = new Criteria();
//        cri.setPageNum(1); // 1페이지
//        cri.setAmount(10); // 한 페이지에 10건
//        cri.setType("T"); // T = 제목 검색
//        cri.setKeyword("게시글"); // 검색어
//
//        // When - 조건에 맞는 게시글 목록 조회 (DTO 리스트 반환)
//        List<EnvDto> list = envService.getList(cri);
//
//        // Thwn
//        assertNotNull(list, "조회 결과가 null값이면 안됨");
//        log.info("조회된 게시글 수 : {}", list.size());
//
//        for (EnvDto envDto : list) {
//            log.info("게시글: {}", envDto);
//        }
//    }
//
//    @Test
//    @DisplayName("게시글 전체 개수 조회 테스트(검색 조건 포함)")
//    @Transactional
//    public void testGetTotal(){
//        // Given
//        Criteria cri = new Criteria();
//        cri.setType("T"); // T = 제목
//        cri.setKeyword("게시글"); // 검색어
//
//        // When
//        int totalCount = envService.getTotal(cri);
//
//        // Then
//        log.info("검색 조건에 해당하는 전체 게시글 수 : {}", totalCount);
//        assertTrue(totalCount >= 0, "게시글 수는 0 이상");
//    }
//
//    @Test
//    @DisplayName("게시글 조회수 증가 테스트")
//    @Transactional
//    public void testUpdateViewCount() throws Exception{
//        // Given
//        // 1) 테스트용 DTO 객체 생성
//        EnvDto envDto = new EnvDto();
//        envDto.setMemberId(1L); // 작성자 ID 설정
//        envDto.setTitle("조회수 증가 테스트 제목");
//        envDto.setContent("조회수 증가 테스트 내용");
//
//        // 2) 게시글 등록 (이미지 없이)
//        EnvFormDto formDto = EnvFormDto.builder()
//                .envDto(envDto)
//                .build();
//
//        envService.register(formDto, Collections.emptyList()); // 이미지 없이 등록
//
//        // 등록 후 envDto에서 ID 추출
//        Long insertedId = envDto.getEnvId();
//        assertNotNull(insertedId, "등록된 게시글 ID는 null이면 안 됩니다.");
//
//        // When
//        // 1) 조회수 증가 전 값 조회
//        EnvDto before = envService.get(insertedId);
//        int beforeCount = before.getViewCount(); // 증가 전 조회수 값
//        log.info("조회수 증가 전: {}", beforeCount);
//        // 2) 조회수 1 증가 실행
//        envService.increaseViewCount(insertedId);
//
//        // Then
//        // 1) 조회수 1 증가 후 조회
//        EnvDto after = envService.get(insertedId);
//        int afterCount = after.getViewCount(); // 증가 후 조회수 값
//
//        log.info("조회수 증가 후: {}", afterCount);
//
//        // 2) 증가된 값이 예상대로 1 증가했는지 검증
//        assertEquals(beforeCount + 1, afterCount, "조회수는 1 증가");
//    }


}