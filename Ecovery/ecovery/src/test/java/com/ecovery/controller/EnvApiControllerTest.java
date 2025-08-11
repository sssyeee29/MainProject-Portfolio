package com.ecovery.controller;

import com.ecovery.dto.EnvDto;
import com.ecovery.dto.EnvFormDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * EnvApiController에 대한 통합 테스트 클래스
 * REST API 컨트롤러의 주요 기능(목록 조회, 등록, 수정, 삭제)에 대해 통합 테스트 수행
 * MockMvc를 사용하여 HTTP 요청/응답 시나리오를 시뮬레이션
 * @author : yukyeong
 * @fileName : EnvApiControllerTest
 * @since : 250722
 * @history
     - 250722 | yukyeong | 게시글 목록 조회 API 테스트 (검색 조건 포함/없음) 구현
     - 250722 | yukyeong | 게시글 등록 API 테스트 (정상 등록 케이스) 구현
     - 250722 | yukyeong | 게시글 수정 API 테스트 (정상 수정 케이스) 구현
     - 250722 | yukyeong | 게시글 삭제 API 테스트 (정상 삭제 케이스) 구현
     - 250724 | yukyeong | 공지사항 단건조회 성공, 등록/수정/단건조회/삭제 실패 테스트 케이스 추가 (유효성 검증 및 존재하지 않는 ID)
     - 250725 | yukyeong | 게시글 등록/수정 통합 테스트에 category 필드 테스트 추가
     - 250728 | yukyeong | 게시글 등록/수정 multipart 구조 대응 테스트 및 이미지 교체 테스트 추가
 */

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class EnvApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // JSON 응답 파싱용

    @Test
    @DisplayName("환경톡톡 게시글 목록 조회 API 테스트")
    public void testGetEnvList() throws Exception {

        // when: GET 요청 수행 (검색어: "게시글", 제목 검색 조건, 1페이지, 10개씩)
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/env/list")
                        .param("pageNum", "1")
                        .param("amount", "10")
                        .param("type", "T") // 제목 검색
                        .param("keyword", "게시글") // 검색어
                )
                .andExpect(status().isOk()) // HTTP 상태 코드 200 OK
                .andExpect(jsonPath("$.list").isArray()) // list가 배열인지 확인
                .andExpect(jsonPath("$.pageMaker").exists()) // pageMaker가 존재하는지 확인
                .andExpect(jsonPath("$.type").value("T")) // type 값 검증 (검색 타입)
                .andExpect(jsonPath("$.keyword").value("게시글")) // keyword 값 검증 (검색어)
                .andReturn();

        // then: 결과 응답 JSON 출력
        String responseBody = result.getResponse().getContentAsString();
        log.info("응답 JSON -----> {}", responseBody);
    }

    @Test
    @DisplayName("전체 게시글 목록 조회 API 테스트(검색어 없이)")
    public void testGetEnvListWithoutSearch() throws Exception {
        // when: 검색 조건 없이 전체 목록 요청 (1페이지, 10개씩)
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/env/list")
                        .param("pageNum", "1")
                        .param("amount", "10")
                )
                .andExpect(status().isOk()) // HTTP 상태 코드 200 OK
                .andExpect(jsonPath("$.list").isArray()) // list가 배열인지 확인
                .andExpect(jsonPath("$.pageMaker").exists()) // pageMaker가 존재하는지 확인
                .andReturn();

        // then: 응답 내용 출력
        String responseBody = result.getResponse().getContentAsString();
        log.info("응답 JSON -----> {}", responseBody);
    }

    @Test
    @DisplayName("API 컨트롤러 - 게시글 등록 성공 테스트")
    @WithMockUser(username = "test@test.com", roles = {"ADMIN"})
    public void testRegisterWithMultipart() throws Exception {
        // Given - 게시글 본문 DTO
        EnvDto dto = new EnvDto();
        dto.setTitle("테스트 제목");
        dto.setContent("테스트 내용");
        dto.setCategory("news");

        EnvFormDto formDto = new EnvFormDto(dto);

        // JSON 직렬화 (envFormDto → JSON)
        String json = objectMapper.writeValueAsString(formDto);

        // JSON을 MockMultipartFile로 변환
        MockMultipartFile jsonPart = new MockMultipartFile(
                "envFormDto", // @RequestPart name
                "envFormDto", // original filename (무관)
                "application/json", // content type
                json.getBytes(StandardCharsets.UTF_8) // content
        );

        // 이미지 파일이 없어도 테스트 가능 (빈 파일 전송)
        MockMultipartFile emptyImage = new MockMultipartFile(
                "envImgFiles",     // @RequestPart name
                "empty.jpg",       // filename
                "image/jpeg",      // content type
                new byte[0]        // 빈 파일
        );

        // When & Then
        mockMvc.perform(multipart("/api/env/register")
                        .file(jsonPart)
                        .file(emptyImage)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("API 컨트롤러 - 게시글 + 이미지 등록 성공 테스트")
    @WithMockUser(username = "test@test.com", roles = {"ADMIN"})
    public void testRegisterWithImage() throws Exception {
        // Given - 게시글 본문 DTO
        EnvDto dto = new EnvDto();
        dto.setTitle("테스트 제목");
        dto.setContent("테스트 내용");
        dto.setCategory("news");

        EnvFormDto formDto = new EnvFormDto(dto);

        // JSON 직렬화 (envFormDto → JSON)
        String json = objectMapper.writeValueAsString(formDto);

        // JSON 본문을 multipart 파일로 변환
        MockMultipartFile jsonPart = new MockMultipartFile(
                "envFormDto", // @RequestPart name
                "envFormDto", // original filename
                "application/json",
                json.getBytes(StandardCharsets.UTF_8)
        );

        // 실제 이미지 파일 (테스트용 임의 데이터)
        byte[] imageBytes = "fake image content".getBytes(StandardCharsets.UTF_8);

        MockMultipartFile imageFile = new MockMultipartFile(
                "envImgFiles", // @RequestPart name
                "test-image.jpg", // 이미지 이름
                "image/jpeg", // 이미지 타입
                imageBytes // 이미지 바이트
        );

        // When & Then
        mockMvc.perform(multipart("/api/env/register")
                        .file(jsonPart)
                        .file(imageFile)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    // 게시글 등록 실패 테스트 (유효성 실패)
    @Test
    @DisplayName("게시글 등록 실패 - 제목 없음")
    @WithMockUser(username = "test@test.com", roles = {"ADMIN"})
    public void testRegisterFail() throws Exception {

        // Given - 제목이 비어 있는 DTO 객체 생성
        EnvDto dto = new EnvDto(); // 게시글 DTO 객체 생성
        dto.setTitle(""); // 제목 누락 (유효성 검사 실패 유도)
        dto.setContent("API 통합 테스트 내용"); // 내용만 입력됨

        // DTO 객체를 JSON 문자열로 변환 (Controller는 @RequestBody로 JSON 받음)
        String json = objectMapper.writeValueAsString(dto);

        // When & Then - POST 요청 수행 후 응답 검증
        mockMvc.perform(post("/api/env/register") // 요청 경로: POST /api/env/register
                        .contentType(MediaType.APPLICATION_JSON) // 요청 본문의 타입을 application/json으로 지정
                        .content(json) // 변환된 JSON 데이터를 요청 본문에 포함
                        .with(csrf())) // CSRF 토큰 추가 (Spring Security 활성화 시 필요)
                .andDo(print()) // 요청 및 응답 내용을 콘솔에 출력
                .andExpect(status().isBadRequest()); // 응답 상태가 400인지 확인 (유효성 실패)
    }

    // 게시글 단건 조회 성공 테스트
    @Test
    @DisplayName("API 컨트롤러 - 게시글 단건 조회 성공 테스트")
    public void testGetOneEnv() throws Exception {

        // Given - 존재하는 게시글 ID
        Long envId = 11L;

        // When - 단건 조회 API 호출 & Then - 응답 검증
        mockMvc.perform(get("/api/env/get/{envId}", envId))
                .andExpect(status().isOk()) // 200 OK 상태 코드 확인
                .andExpect(jsonPath("$.envId").value(envId)) // 응답 JSON의 ID 확인
                .andExpect(jsonPath("$.title").exists()) // 제목 필드가 존재하는지 확인
                .andExpect(jsonPath("$.content").exists())
                .andDo(print()); // 내용 필드가 존재하는지 확인
    }

    // 단건 조회 실패 테스트 (존재하지 않는 ID)
    @Test
    @DisplayName("단건 조회 실패 - 존재하지 않는 ID")
    public void testGetOneEnvFail() throws Exception {

        // Given - 존재하지 않는 게시글 ID 설정
        // 실제 DB에 존재하지 않는 ID를 지정하여 404 응답을 유도
        Long envId = 9999L;

        // When & Then
        mockMvc.perform(get("/api/env/get/{envId}", envId)) // GET 요청을 보냄: /api/env/get/{envId}
                .andExpect(status().isNotFound()) // 기대하는 응답 상태는 404
                .andDo(print());
    }


    @Test
    @DisplayName("API 컨트롤러 - 게시글 수정 성공 테스트 (이미지 없음)")
    @WithMockUser(username = "test@test.com", roles = {"ADMIN"})
    public void testModifyWithMultipart() throws Exception {
        // Given - 수정 대상 게시글 정보를 DTO로 생성
        EnvDto dto = new EnvDto();
        dto.setEnvId(11L); // 실제 DB에 존재하는 게시글 ID
        dto.setTitle("수정된 제목입니다");
        dto.setContent("수정된 내용입니다");
        dto.setCategory("tips");

        // 위에서 작성한 EnvDto를 EnvFormDto에 래핑 (컨트롤러는 EnvFormDto를 받음)
        EnvFormDto formDto = new EnvFormDto(dto);

        // EnvFormDto를 JSON 문자열로 직렬화
        String json = objectMapper.writeValueAsString(formDto);

        // JSON 문자열을 multipart/form-data 요청에 포함시키기 위해 MockMultipartFile로 변환
        // 컨트롤러의 @RequestPart("form")과 이름이 반드시 일치해야 함
        MockMultipartFile jsonPart = new MockMultipartFile(
                "envFormDto", // part 이름: 컨트롤러 @RequestPart("envFormDto")과 동일해야 함
                "form.json", // 실제 파일 이름 (테스트에서 중요하지 않음)
                "application/json", // 컨텐츠 타입
                json.getBytes(StandardCharsets.UTF_8) // JSON 데이터를 바이트로 변환
        );

        // 이미지가 없는 경우에도 multipart 형식을 유지하기 위해 빈 파일 전송
        // part 이름은 컨트롤러의 @RequestPart("envImgFiles")과 일치해야 함
        MockMultipartFile dummyImg = new MockMultipartFile(
                "envImgFiles", // part 이름
                "empty.jpg", // 파일 이름
                "image/jpeg", // 컨텐츠 타입
                new byte[0]); // 내용 없음 (빈 파일)

        // When & Then - multipart 요청 전송
        mockMvc.perform(multipart("/api/env/modify/{envId}", 11L) // PUT 요청 URL 지정
                        .file(jsonPart) // JSON 본문 포함
                        .file(dummyImg) // 빈 이미지 파일 포함
                        .with(csrf()) // CSRF 토큰 포함
                        .with(request -> { // multipart는 기본 POST이므로 PUT으로 강제 설정
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk()) // 200 OK 응답을 기대
                .andExpect(jsonPath("$.message").value("수정 성공")) // JSON 응답 필드 message가 "수정 성공"인지 검증
                .andExpect(jsonPath("$.envId").value(11L)); // 응답에 반환된 envId가 11L인지 검증
    }


    @Test
    @DisplayName("게시글 이미지 교체 테스트 (등록 후 수정)")
    @WithMockUser(username = "test@test.com", roles = {"ADMIN"})
    public void testImageReplaceAfterRegister() throws Exception {
        // Given - 게시글 등록 + 초기 이미지
        EnvDto dto = new EnvDto();
        dto.setTitle("초기 이미지 등록");
        dto.setContent("기존 이미지 등록용");
        dto.setCategory("news");

        // EnvFormDto로 변환
        EnvFormDto formDto = new EnvFormDto(dto);

        // JSON으로 변환한 게시글 폼 데이터를 multipart 형식으로 구성
        String json = objectMapper.writeValueAsString(formDto);
        MockMultipartFile jsonPart = new MockMultipartFile(
                "envFormDto", // 컨트롤러에서 받는 필드명과 일치해야 함
                "form.json",
                "application/json",
                json.getBytes());

        // 첨부할 이미지 파일 1개 준비
        MockMultipartFile image = new MockMultipartFile(
                "envImgFiles",  // 컨트롤러에서 받는 이미지 리스트 이름
                "img1.jpg",
                "image/jpeg",
                "test image".getBytes());

        // When - 게시글 등록 요청 수행
        MvcResult result = mockMvc.perform(multipart("/api/env/register")
                        .file(jsonPart) // form 데이터
                        .file(image) // 이미지
                        .with(csrf())) // CSRF 토큰 포함
                .andExpect(status().isCreated()) // 201 응답 기대
                .andReturn(); // 결과 반환

        // Then - 등록된 게시글의 envId 추출
        String response = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(response);
        Long envId = node.get("envId").asLong(); // 등록된 게시글 ID


        // Given - 수정할 게시글 정보 및 새 이미지
        dto.setEnvId(envId); // 기존 게시글 ID 세팅
        dto.setTitle("수정 제목");
        dto.setContent("수정 내용");

        EnvFormDto modifyForm = new EnvFormDto(dto);
        String modifyJson = objectMapper.writeValueAsString(modifyForm);

        // 수정용 form 데이터 (application/json)
        MockMultipartFile form = new MockMultipartFile(
                "envFormDto",
                "form.json",
                "application/json",
                modifyJson.getBytes());

        // 교체할 새로운 이미지 파일 준비
        MockMultipartFile newImage = new MockMultipartFile(
                "envImgFiles",
                "newImg.jpg",
                "image/jpeg",
                "new image".getBytes());

        // When - PUT 방식으로 게시글 수정 요청
        mockMvc.perform(multipart("/api/env/modify/" + envId)
                        .file(form) // 수정할 폼 데이터
                        .file(newImage) // 새 이미지 파일
                        .with(csrf()) // CSRF 토큰
                        .with(request -> { // PUT 방식으로 강제 지정
                            request.setMethod("PUT");
                            return request;
                        }))
                // Then - 수정 성공 응답 확인
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.envId").value(envId))
                .andExpect(jsonPath("$.message").value("수정 성공"))
                .andDo(print());
    }


    // 게시글 수정 실패 테스트 (존재하지 않는 ID)
    @Test
    @DisplayName("게시글 수정 실패 - 존재하지 않는 ID")
    @WithMockUser(username = "test@test.com", roles = {"ADMIN"})
    public void testModifyFail_NotFound() throws Exception {
        // Given - 존재하지 않는 게시글 ID
        Long invalidId = 99999L;

        // 게시글 데이터 생성
        EnvDto dto = new EnvDto();
        dto.setTitle("수정 테스트 제목");
        dto.setContent("수정 테스트 내용");
        dto.setCategory("news");

        // EnvFormDto 래핑
        EnvFormDto formDto = new EnvFormDto(dto);

        // EnvFormDto → JSON 직렬화 (multipart 요청에서 JSON part로 사용)
        String json = objectMapper.writeValueAsString(formDto);
        MockMultipartFile formPart = new MockMultipartFile(
                "envFormDto", // @RequestPart 이름
                "form.json",
                "application/json",
                json.getBytes(StandardCharsets.UTF_8)
        );

        // 이미지 첨부는 필수가 아니므로 빈 파일로 대체 (null 방지)
        MockMultipartFile dummyImg = new MockMultipartFile(
                "envImgFiles",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        // When & Then - 수정 API 호출 → 응답 검증
        // multipart()를 사용하여 PUT 요청 강제 설정
        mockMvc.perform(multipart("/api/env/modify/{envId}", invalidId) // 존재하지 않는 ID로 요청
                        .file(formPart) // 게시글 JSON part
                        .file(dummyImg) // 이미지 파일 part
                        .with(csrf()) // CSRF 토큰 포함 (보안 설정)
                        .with(request -> { // multipart는 기본이 POST이므로 PUT으로 강제 설정
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest()) // 기대 결과: 400 Bad Request 응답
                .andExpect(content().string("수정에 실패했습니다.")) // 반환 메시지 확인
                .andDo(print());
    }


    @Test
    @DisplayName("API 컨트롤러 - 게시글 삭제 성공 테스트")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    public void testRemove() throws Exception {
        // Given - 삭제할 게시글 ID (DB에 존재해야 함)
        Long envId = 11L;

        // When & Then - 삭제 API 호출 및 응답 검증
        mockMvc.perform(delete("/api/env/remove/{envId}", envId) // DELETE 요청
                        .with(csrf())) // CSRF 토큰 포함
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.message").value("삭제되었습니다.")) // 메시지 검증
                .andExpect(jsonPath("$.envId").value(envId)); // 삭제된 ID 검증
    }

    // 게시글 삭제 실패 테스트 (존재하지 않는 ID)
    @Test
    @DisplayName("게시글 삭제 실패 - 존재하지 않는 ID")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    public void testDeleteFail_NotFound() throws Exception {

        // Given - 존재하지 않는 게시글 ID
        Long invalidId = 999L;

        // When & Then - 삭제 요청을 보내고, 실패 응답(400)을 검증
        mockMvc.perform(delete("/api/env/remove/{envId}", invalidId)  // DELETE 요청 전송
                        .with(csrf())) // CSRF 토큰 포함 (Spring Security 사용 시 필수)
                .andExpect(status().isBadRequest()) // 기대 응답: 400 Bad Request
                .andExpect(content().string("삭제에 실패했습니다.")) // 기대 메시지 본문 확인
                .andDo(print()); // 요청과 응답 전체 내용 콘솔에 출력
    }
}