package com.ecovery.service;

import com.ecovery.dto.EnvDto;
import com.ecovery.dto.EnvFormDto;
import com.ecovery.dto.EnvImgDto;
import com.ecovery.mapper.EnvImgMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EnvImgServiceTest
 * - 환경톡톡 게시판의 이미지 관련 기능(등록, 조회, 삭제, 수정)에 대한 단위 테스트 클래스
 * - EnvImgService 구현체 및 EnvService 연동 통합 테스트 수행
 * - 테스트를 위해 게시글(EnvDto)과 이미지(EnvImgDto)를 함께 사용
 *
 * @author   : yukyeong
 * @fileName : EnvImgServiceTest
 * @since    : 250726
 * @history
    - 250726 | yukyeong | 이미지 등록, 목록 조회, 단일 삭제, 전체 삭제 테스트 구현
    - 250728 | yukyeong | EnvFormDto 기반 게시글 + 이미지 등록 방식으로 테스트 전체 수정
                          게시글 수정 시 이미지 일부 삭제 테스트 추가
    - 250811 | yukyeong | 테스트 프로퍼티 적용: envImgLocation/uploadPath를 OS 임시경로로 분리 지정, 테스트 시작 시 임시 디렉터리 생성(Files.createDirectories)
    - 250811 | yukyeong | EnvService.register 단일 파라미터로 호출하도록 변경(formDto.setEnvImgFiles(...)), 권한 필요한 테스트에 @WithMockUser 추가
    - 250811 | yukyeong | 본문 이미지(URL 기반) 등록 테스트(testRegisterContentImage) 추가
    - 250811 | yukyeong | 이미지 목록 조회/단일 삭제/전체 삭제 테스트 강화: 파일 시스템 검증(Files.exists / Files.notExists) 추가
    - 250811 | yukyeong | 게시글 수정 시 이미지 일부 삭제 테스트(testModifyWithOneImageDeleted) 추가 및 잔존 파일 검증 로직 보강
    - 250811 | yukyeong | uploadPath를 파일시스템 경로로 설정(URI 스킴 제거)해 업로드/삭제 경로 일치 문제 해결
 */

@SpringBootTest
@TestPropertySource(properties = {
        // 테스트 전용 경로 (원하면 제거 가능)
        "envImgLocation=${java.io.tmpdir}/ecovery/env",
        "uploadPath=${java.io.tmpdir}/ecovery/"
})
@Slf4j
class EnvImgServiceTest {

    @Autowired
    private EnvImgService envImgService; // 테스트 대상 서비스

    @Autowired
    private EnvService envService; // 테스트 대상 서비스

    @Autowired
    private EnvImgMapper envImgMapper; // 게시글 등록용 서비스


    @Test
    @DisplayName("이미지 등록 테스트")
    @Transactional
    @WithMockUser(roles = "ADMIN")  // 관리자만 등록 가능
    public void testRegister() throws Exception{

        // 테스트에서 사용할 임시 저장 폴더 생성
        Path root = Paths.get(System.getProperty("java.io.tmpdir"), "ecovery", "env");
        Files.createDirectories(root);

        // Given - 테스트 준비 단계
        // 1. 테스트용 게시글 객체 생성
        EnvDto envDto = new EnvDto();
        envDto.setMemberId(1L); // 테스트용 회원 ID 설정 (DB에 1번 회원이 존재해야 함)
        envDto.setTitle("이미지 테스트 게시글"); // 게시글 제목 설정
        envDto.setContent("이미지 등록 전용 게시글"); // 게시글 본문 내용 설정
        envDto.setCategory("tip"); // 게시글 카테고리 설정

        // 2. Mock 이미지 파일 생성 (가짜 파일) - 실제 업로드 파일처럼 테스트용으로 메모리상에서 가상 이미지 파일 생성
        MockMultipartFile mockFile = new MockMultipartFile(
                "imgFile",  // form 필드 이름 (서비스에서 받는 필드명과 일치해야 함)
                "sample.jpg", // 원본 파일명
                "image/jpeg", // MIME 타입
                "fake image content".getBytes() // 파일 내용 (바이트 배열 형태로 전달)
        );

        // 3. EnvFormDto 생성 (게시글 + 이미지 ID를 담는 Form 객체) - 실제 컨트롤러에서 사용하는 구조와 유사하게 감쌈
        EnvFormDto formDto = EnvFormDto.builder()
                .envDto(envDto) // 게시글 본문 데이터 포함
                .build();

        // EnvFormDto에 이미지 파일 리스트 설정 (여기서는 1장)
        formDto.setEnvImgFiles(java.util.List.of(mockFile));

        // When - 게시글 + 이미지 등록
        envService.register(formDto);  // formDto 내부의 envDto(게시글)와 envImgFiles(이미지 목록)가 함께 처리됨
        Long envId = formDto.getEnvDto().getEnvId(); // 등록된 게시글 ID 확인
        assertNotNull(envId, "등록된 게시글 ID는 null이면 안 됩니다."); // 게시글 ID가 정상적으로 생성되었는지 확인 (null이면 실패)

        // Then
        // 해당 게시글 ID로 이미지 목록을 조회
        List<EnvImgDto> resultList = envImgService.getListByEnvId(envId);

        assertEquals(1, resultList.size(), "이미지는 1건 등록되어야 합니다."); // 이미지가 1건만 등록되었는지 검증
        assertEquals("sample.jpg", resultList.get(0).getOriImgName()); // 등록된 이미지의 원본 파일명이 "sample.jpg"인지 확인
        assertEquals(envId, resultList.get(0).getEnvId()); // 이미지가 등록된 게시글 ID와 일치하는지 확인

        log.info("등록된 이미지 정보: {}", resultList.get(0));
    }


    @Test
    @DisplayName("본문 이미지 URL 등록 테스트")
    @Transactional
    @WithMockUser(roles = "ADMIN")
    void testRegisterContentImage() throws Exception{
        // 1. 테스트용 게시글 생성 (본문 이미지 테스트용)
        EnvDto envDto = new EnvDto();
        envDto.setMemberId(1L);
        envDto.setTitle("본문 이미지 테스트");
        envDto.setContent("<p>본문에 이미지가 들어갑니다</p>");
        envDto.setCategory("tip");

        EnvFormDto formDto = EnvFormDto.builder()
                .envDto(envDto)
                .build();

        envService.register(formDto); // 게시글만 등록
        Long envId = formDto.getEnvDto().getEnvId();
        assertNotNull(envId, "등록된 게시글 ID는 null이면 안 됩니다.");

        // 2. 본문 이미지 URL 생성 (실제로는 업로드 후 생성된 URL)
        String contentImgUrl = "/ecovery/env/test-content-image.jpg";

        // 3. EnvImgDto 생성 후 URL만 등록
        EnvImgDto imgDto = new EnvImgDto();
        imgDto.setEnvId(envId);
        imgDto.setImgUrl(contentImgUrl);
        imgDto.setOriImgName(null); // 본문 이미지는 원본파일명 없음
        imgDto.setImgName("test-content-image.jpg");

        envImgService.register(imgDto); // 파일 없이 URL만 등록

        // 4. DB 조회로 등록 여부 확인
        List<EnvImgDto> resultList = envImgService.getListByEnvId(envId);
        assertEquals(1, resultList.size(), "본문 이미지는 1건 등록되어야 합니다.");
        assertEquals(contentImgUrl, resultList.get(0).getImgUrl());
    }


    @Test
    @DisplayName("이미지 목록 조회 테스트")
    @Transactional
    @WithMockUser(roles = "ADMIN") // 관리자만 등록 가능
    public void testGetListByEnvId() throws Exception{

        // 임시 저장 폴더 생성 (없으면 생성)
        Path root = Paths.get(System.getProperty("java.io.tmpdir"), "ecovery", "env");
        Files.createDirectories(root);

        // Given - 1. 테스트용 게시글 DTO 준비
        EnvDto envDto = new EnvDto();
        envDto.setMemberId(1L); // 작성자 ID
        envDto.setTitle("이미지 목록 테스트 게시글"); // 게시글 제목
        envDto.setContent("여러 이미지를 등록합니다."); // 게시글 내용
        envDto.setCategory("news"); // 게시글 카테고리

        // 2. 업로드를 가장한 가짜 이미지 파일 2개 준비 (메모리 상에서 MultipartFile 생성)
        MockMultipartFile file1 = new MockMultipartFile(
                "imgFile", // 파라미터 이름
                "img1.jpg", // 원본 파일명
                "image/jpeg", // MIME 타입
                "fake image content 1".getBytes()); // 파일 내용
        MockMultipartFile file2 = new MockMultipartFile(
                "imgFile",
                "img2.jpg",
                "image/jpeg",
                "fake image content 2".getBytes()
        );

        //3. EnvFormDto로 게시글과 이미지 함께 등록 (게시글 정보를 EnvFormDto로 감싸서 이미지 파일과 함께 등록)
        EnvFormDto formDto = EnvFormDto.builder()
                .envDto(envDto) // 게시글 정보 포함
                .build();
        formDto.setEnvImgFiles(List.of(file1, file2)); // 첨부 이미지 2건 세팅

        // When - 게시글 + 이미지 등록 요청
        // EnvService를 통해 게시글과 이미지 2개를 DB 및 파일 시스템에 등록
        envService.register(formDto); // 단일 인자

        // 등록 후 서비스가 DTO에 세팅한 PK 확인
        Long envId = formDto.getEnvDto().getEnvId();
        assertNotNull(envId, "게시글 ID는 null이 아니어야 함");

        // 해당 게시글의 이미지 목록 조회
        List<EnvImgDto> imgList = envImgService.getListByEnvId(envId); // 이미지 서비스에서 게시글 ID에 해당하는 이미지 목록을 불러온다

        // Then - 이미지 2건이 조회되고 파일명이 정확해야 함
        assertEquals(2, imgList.size(), "이미지 2건이 등록되어야 함"); // 총 2개 이미지여야 함
        assertTrue(imgList.stream().anyMatch(img -> "img1.jpg".equals(img.getOriImgName()))); // 이미지 목록 중 하나라도 oriImgName이 "img1.jpg"인지 확인
        assertTrue(imgList.stream().anyMatch(img -> "img2.jpg".equals(img.getOriImgName()))); // "img2.jpg"인지 확인

        log.info("조회된 이미지 목록: {}", imgList);
    }

    @Test
    @DisplayName("단일 이미지 삭제 테스트")
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void testDeleteById() throws Exception {

        // 임시 저장 폴더 생성 (없으면 생성)
        Path root = Paths.get(System.getProperty("java.io.tmpdir"), "ecovery", "env");
        Files.createDirectories(root);

        // Given - 1. 테스트용 게시글 + 이미지 등록
        EnvDto envDto = new EnvDto();
        envDto.setMemberId(1L);
        envDto.setTitle("삭제 테스트 게시글");
        envDto.setContent("삭제용 이미지 등록");
        envDto.setCategory("event");

        // 2. 이미지 등록 - 테스트용 mock 이미지 파일 생성
        MockMultipartFile mockFile = new MockMultipartFile(
                "imgFile",
                "delete.jpg",
                "image/jpeg",
                "fake delete image".getBytes()
        );

        // EnvFormDto 생성: 게시글 정보를 감싸는 DTO로 이미지와 함께 전달됨
        EnvFormDto formDto = EnvFormDto.builder()
                .envDto(envDto)
                .build();
        formDto.setEnvImgFiles(List.of(mockFile)); // 파일은 formDto 안에 넣기

        // When - 등록 실행 (단일 인자)
        // EnvService를 통해 게시글과 이미지 등록 처리
        envService.register(formDto); // 게시글 + 이미지 등록

        // 등록된 게시글 ID 추출 및 null 확인
        Long envId = envDto.getEnvId();
        assertNotNull(envId, "게시글 ID는 null이 아니어야 합니다.");

        // 3. 등록된 이미지 목록 조회 후 ID 추출
        List<EnvImgDto> imgList = envImgService.getListByEnvId(envId); // 해당 게시글의 이미지 리스트 조회
        assertEquals(1, imgList.size(), "이미지는 1건 등록되어야 합니다."); // 이미지 1건 존재 확인
        Long envImgId = imgList.get(0).getEnvImgId(); // 삭제할 이미지의 ID 추출
        String savedFileName = imgList.get(0).getImgName();

        // 실제 파일이 존재하는지 확인
        Path savedPath = root.resolve(savedFileName);
        assertTrue(Files.exists(savedPath), "업로드된 파일이 실제로 있어야 합니다.");

        // 단일 이미지 삭제 수행
        boolean deleted = envImgService.deleteById(envImgId); // 이미지 삭제 실행

        // Then - 삭제 성공 여부 확인
        assertTrue(deleted, "이미지 삭제가 성공해야 합니다.");

        // 삭제 후 다시 이미지 목록 조회 → 이미지가 0건인지 확인
        List<EnvImgDto> afterDeleteList = envImgService.getListByEnvId(envId);
        assertEquals(0, afterDeleteList.size(), "삭제 후 이미지 목록은 비어 있어야 합니다.");

        // 파일 시스템에서도 실제 삭제되었는지 확인
        assertTrue(Files.notExists(savedPath), "파일 시스템에서도 파일이 삭제되어야 합니다.");

        log.info("단일 이미지 삭제 성공. 이미지 ID: {}", envImgId);
    }

    @Test
    @DisplayName("게시글 ID로 이미지 전체 삭제 테스트")
    @Transactional
    public void testDeleteByEnvId() throws Exception{

        // 임시 저장 폴더 생성
        Path root = Paths.get(System.getProperty("java.io.tmpdir"), "ecovery", "env");
        Files.createDirectories(root);

        // Given - 1. 테스트용 게시글 및 이미지 2개 등록
        EnvDto envDto = new EnvDto();
        envDto.setMemberId(1L);
        envDto.setTitle("전체 삭제 테스트 게시글");
        envDto.setContent("이미지 여러 개 등록 후 전체 삭제 테스트");
        envDto.setCategory("news");

        // Mock 이미지 2개
        MockMultipartFile file1 = new MockMultipartFile(
                "imgFile",
                "img1.jpg",
                "image/jpeg",
                "fake image content 1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "imgFile",
                "img2.jpg",
                "image/jpeg",
                "fake image content 2".getBytes());

        // EnvFormDto에 파일 담아서 단일 인자로 호출
        EnvFormDto formDto = EnvFormDto.builder()
                .envDto(envDto)
                .build();
        formDto.setEnvImgFiles(List.of(file1, file2));

        // When - 등록
        envService.register(formDto);

        // 등록된 게시글 ID 추출
        Long envId = formDto.getEnvDto().getEnvId();
        assertNotNull(envId, "게시글 ID는 null이면 안 됩니다.");

        // 등록된 이미지 목록 조회 (삭제 전 상태 확인)
        List<EnvImgDto> beforeDeleteList = envImgService.getListByEnvId(envId);
        assertEquals(2, beforeDeleteList.size(), "이미지는 2건 등록되어야 합니다.");

        // 실제 파일 존재 확인
        List<Path> savedPaths = beforeDeleteList.stream()
                .map(img -> root.resolve(img.getImgName()))
                .toList();
        for (Path p : savedPaths) {
            assertTrue(Files.exists(p), "업로드된 파일이 실제로 있어야 합니다: " + p);
        }

        // 게시글 ID로 전체 삭제
        int deletedCount = envImgService.deleteByEnvId(envId);

        // Then - 삭제된 개수 확인 및 최종 리스트 검증
        assertEquals(2, deletedCount, "삭제된 이미지 개수는 2개여야 합니다."); // 반환된 삭제 수 확인

        // 삭제 후 이미지 목록 다시 조회 → 비어 있어야 함
        List<EnvImgDto> afterDeleteList = envImgService.getListByEnvId(envId);
        assertTrue(afterDeleteList.isEmpty(), "삭제 후 이미지 목록은 비어 있어야 합니다.");

        // 파일 시스템에서도 삭제되었는지 확인
        for (Path p : savedPaths) {
            assertTrue(Files.notExists(p), "파일 시스템에서도 파일이 삭제되어야 합니다: " + p);
        }

        log.info("게시글 ID로 이미지 전체 삭제 성공. 삭제된 개수: {}", deletedCount);
    }

    @Test
    @DisplayName("게시글 수정 시 이미지 하나만 삭제 테스트")
    @Transactional
    public void testModifyWithOneImageDeleted() throws Exception {

        // 임시 저장 폴더 생성 (파일 시스템 검증용)
        Path root = Paths.get(System.getProperty("java.io.tmpdir"), "ecovery", "env");
        Files.createDirectories(root);

        // Given - 게시글 + 이미지 2개 등록
        EnvDto envDto = new EnvDto();
        envDto.setMemberId(1L);
        envDto.setTitle("수정 테스트 게시글");
        envDto.setContent("이미지 2개 등록 후 하나 삭제 테스트");
        envDto.setCategory("event");

        // 테스트용 이미지 2개 생성
        MockMultipartFile file1 = new MockMultipartFile("imgFile", "img1.jpg", "image/jpeg", "content1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("imgFile", "img2.jpg", "image/jpeg", "content2".getBytes());

        // 게시글 등록용 FormDto 생성
        EnvFormDto registerDto = EnvFormDto.builder()
                .envDto(envDto)
                .build();
        registerDto.setEnvImgFiles(List.of(file1, file2));

        // When - 등록
        envService.register(registerDto);

        // 등록된 게시글 ID 확인
        Long envId = registerDto.getEnvDto().getEnvId();
        assertNotNull(envId, "등록 후 envId가 세팅되어야 합니다.");

        // 등록된 이미지 ID 목록 조회
        List<EnvImgDto> beforeList = envImgService.getListByEnvId(envId);
        assertEquals(2, beforeList.size(), "초기에는 이미지 2개가 있어야 합니다.");

        // ID ↔ 파일명 매핑
        Map<Long, String> idToName = beforeList.stream()
                .collect(Collectors.toMap(EnvImgDto::getEnvImgId, EnvImgDto::getImgName));

        // 삭제 대상/잔존 대상 결정
        Long deleteImgId = beforeList.get(0).getEnvImgId();
        Long expectedRemainId = beforeList.get(1).getEnvImgId();

        // 파일 존재 확인
        Path deletePath = root.resolve(idToName.get(deleteImgId));
        Path remainPath = root.resolve(idToName.get(expectedRemainId));
        assertTrue(Files.exists(deletePath), "삭제 대상 파일이 실제로 존재해야 합니다.");
        assertTrue(Files.exists(remainPath), "남을 파일이 실제로 존재해야 합니다.");

        // When - 게시글 수정: 이미지 1개 삭제
        // 수정용 게시글 정보 생성 (수정된 제목/내용)
        EnvDto modifiedDto = EnvDto.builder()
                .envId(envId) // 수정 대상 게시글 ID
                .memberId(envDto.getMemberId()) // memberId는 수정 시에도 필요
                .title("수정된 제목")
                .content("수정된 내용")
                .category("event")
                .build();

        // 수정용 FormDto 생성 (삭제할 이미지 ID만 포함, 새로 추가할 이미지는 없음)
        EnvFormDto modifyDto = EnvFormDto.builder()
                .envDto(modifiedDto)
                .deleteImgIds(List.of(deleteImgId)) // 삭제할 이미지 ID 전달
                .build();

        modifyDto.setEnvImgFiles(List.of()); // 추가 이미지는 없음

        // 게시글 수정 실행 (이미지 1개 삭제만 수행, 추가 이미지 없음)
        envService.modify(modifyDto); // 새로 추가된 이미지는 없음

        // Then - 이미지 목록이 1개만 남아야 함
        List<EnvImgDto> afterList = envImgService.getListByEnvId(envId); // 수정 후 이미지 목록 다시 조회
        assertEquals(1, afterList.size(), "수정 후 1개 이미지만 남아야 합니다."); // 이미지 수가 1개인지 검증
        assertNotEquals(deleteImgId, afterList.get(0).getEnvImgId(), "삭제된 이미지 ID가 남아 있으면 안 됩니다."); // 삭제한 이미지 ID가 목록에 남아있지 않은지 검증
        assertEquals(expectedRemainId, afterList.get(0).getEnvImgId(), "남아 있어야 할 이미지 ID가 일치해야 합니다.");

        // 파일 시스템 검증: 삭제 대상은 없어지고, 나머지는 남아 있어야 함
        assertTrue(Files.notExists(deletePath), "파일 시스템에서도 삭제 대상 파일이 제거되어야 합니다.");
        assertTrue(Files.exists(remainPath), "남아야 하는 파일은 그대로 존재해야 합니다.");

        log.info("수정 후 남은 이미지: {}", afterList.get(0));
    }

}
