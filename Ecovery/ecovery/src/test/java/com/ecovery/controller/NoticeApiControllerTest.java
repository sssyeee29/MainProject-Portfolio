package com.ecovery.controller;

import com.ecovery.dto.NoticeDto;
import com.ecovery.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * NoticeApiController에 대한 통합 테스트 클래스
 * 공지사항 REST API 컨트롤러의 주요 기능(목록 조회, 등록, 수정, 삭제, 단건 조회)에 대해 통합 테스트 수행
 * MockMvc를 사용하여 HTTP 요청/응답 시나리오를 시뮬레이션하고 실제 서비스 계층과의 연동을 검증함
 * @author : yukyeong
 * @fileName : NoticeApiControllerTest
 * @since : 250723
 * @history
      - 250723 | yukyeong | 공지사항 목록 조회 API 테스트 (검색 조건 포함/없음) 구현
      - 250723 | yukyeong | 공지사항 등록 API 테스트 (정상 등록 케이스) 구현
      - 250723 | yukyeong | 공지사항 수정 API 테스트 (정상 수정 케이스) 구현
      - 250723 | yukyeong | 공지사항 삭제 API 테스트 (정상 삭제 케이스) 구현
      - 250723 | yukyeong | 공지사항 단건 조회 API 테스트 (정상 케이스) 구현
      - 250724 | yukyeong | 공지사항 등록/수정/단건조회/삭제 실패 테스트 케이스 추가 (유효성 검증 및 존재하지 않는 ID)
      - 250729 | yukyeong | 게시글 등록/수정 통합 테스트에 category 필드 테스트 추가
 */

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class NoticeApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // JSON 응답 파싱용


    @Test
    @DisplayName("공지사항 게시글 목록 조회 API 테스트")
    public void testGetNoticeList() throws Exception {

        // when: GET 요청 수행 (검색어: "게시글", 제목 검색 조건, 1페이지, 10개씩)
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/notice/list")
                        .param("pageNum", "1")
                        .param("amount", "10")
                        .param("type", "T") // 제목 검색
                        .param("keyword", "공지사항") // 검색어
                )
                .andExpect(status().isOk()) // HTTP 상태 코드 200 OK
                .andExpect(jsonPath("$.list").isArray()) // list가 배열인지 확인
                .andExpect(jsonPath("$.pageMaker").exists()) // pageMaker가 존재하는지 확인
                .andExpect(jsonPath("$.type").value("T")) // type 값 검증 (검색 타입)
                .andExpect(jsonPath("$.keyword").value("공지사항")) // keyword 값 검증 (검색어)
                .andReturn();

        // then: 결과 응답 JSON 출력
        String responseBody = result.getResponse().getContentAsString();
        log.info("응답 JSON -----> {}", responseBody);
    }

    @Test
    @DisplayName("전체 게시글 목록 조회 API 테스트(검색어 없이)")
    public void testGetEnvListWithoutSearch() throws Exception {
        // when: 검색 조건 없이 전체 목록 요청 (1페이지, 10개씩)
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/notice/list")
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
    public void testRegister() throws Exception {
        // Given - 게시글 제목/내용
        NoticeDto dto = new NoticeDto(); // 게시글 DTO 객체 생성
        dto.setTitle("API 통합 테스트 제목"); // 제목 설정
        dto.setContent("API 통합 테스트 내용"); // 내용 설정
        dto.setCategory("event"); // category 추가

        // ObjectMapper를 사용해 DTO 객체를 JSON 문자열로 변환
        // 실제 컨트롤러가 @RequestBody로 JSON을 받을 것이기 때문에 이 형식으로 요청을 보냄
        String json = objectMapper.writeValueAsString(dto);

        // When - mockMvc를 사용하여 컨트롤러에 POST 요청 수행
        MvcResult result = mockMvc.perform(post("/api/notice/register") // 요청 경로: POST /api/notice/register
                        .contentType(MediaType.APPLICATION_JSON) // 요청 본문의 타입을 application/json으로 지정
                        .content(json) // 변환된 JSON 데이터를 요청 본문에 포함
                        .with(csrf())) // CSRF 토큰 추가 (Spring Security 활성화 시 필요)
                .andExpect(MockMvcResultMatchers.status().isCreated()) // 기대하는 응답 상태: 201 Created
                .andReturn(); // 결과(MvcResult) 반환

        // Then - 응답 결과(JSON 문자열)를 출력해서 실제 반환 값을 확인
        String response = result.getResponse().getContentAsString(); // 응답 본문을 문자열로 추출
        log.info("====> 응답 본문: " + response);  // 응답 내용 로그 출력 (예: {"noticeId":30})

    }

    // 게시글 등록 실패 테스트 (유효성 실패)
    @Test
    @DisplayName("게시글 등록 실패 - 제목 없음")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    public void testRegisterFail() throws Exception {

        // Given - 제목이 비어 있는 DTO 객체 생성
        NoticeDto dto = new NoticeDto(); // 게시글 DTO 객체 생성
        dto.setTitle(""); // 제목 누락 (유효성 검사 실패 유도)
        dto.setContent("API 통합 테스트 내용"); // 내용만 입력됨

        // DTO 객체를 JSON 문자열로 변환 (Controller는 @RequestBody로 JSON 받음)
        String json = objectMapper.writeValueAsString(dto);

        // When & Then - POST 요청 수행 후 응답 검증
        mockMvc.perform(post("/api/notice/register") // 요청 경로: POST /api/notice/register
                        .contentType(MediaType.APPLICATION_JSON) // 요청 본문의 타입을 application/json으로 지정
                        .content(json) // 변환된 JSON 데이터를 요청 본문에 포함
                        .with(csrf())) // CSRF 토큰 추가 (Spring Security 활성화 시 필요)
                .andDo(print()) // 요청 및 응답 내용을 콘솔에 출력
                .andExpect(status().isBadRequest()); // 응답 상태가 400인지 확인 (유효성 실패)
    }


    @Test
    @DisplayName("API 컨트롤러 - 게시글 단건 조회 성공 테스트")
    public void testGetOneNotice() throws Exception {

        // Given - 존재하는 게시글 ID
        Long noticeId = 5L;

        // When - 단건 조회 API 호출 & Then - 응답 검증
        mockMvc.perform(get("/api/notice/get/{noticeId}", noticeId))
                .andExpect(status().isOk()) // 200 OK 상태 코드 확인
                .andExpect(jsonPath("$.noticeId").value(noticeId)) // 응답 JSON의 ID 확인
                .andExpect(jsonPath("$.title").exists()) // 제목 필드가 존재하는지 확인
                .andExpect(jsonPath("$.content").exists()); // 내용 필드가 존재하는지 확인
    }

    // 단건 조회 실패 테스트 (존재하지 않는 ID)
    @Test
    @DisplayName("단건 조회 실패 - 존재하지 않는 ID")
    public void testGetOneNoticeFail() throws Exception {

        // Given - 존재하지 않는 게시글 ID 설정
        // 실제 DB에 존재하지 않는 ID를 지정하여 404 응답을 유도
        Long noticeId = 9999L;

        // When & Then
        mockMvc.perform(get("/api/notice/get/{noticeId}", noticeId)) // GET 요청을 보냄: /api/notice/get/{noticeId}
                .andExpect(status().isNotFound()) // 기대하는 응답 상태는 404
                .andDo(print());
    }


    @Test
    @DisplayName("API 컨트롤러 - 게시글 수정 성공 테스트")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    public void testModify() throws Exception {
        // Given - 수정할 게시글 데이터 (게시글 ID가 DB에 실제로 존재해야 함)
        NoticeDto dto = new NoticeDto();
        dto.setNoticeId(5L); // 기존 게시글 ID
        dto.setTitle("수정된 제목입니다"); // 수정할 제목
        dto.setContent("수정된 내용입니다"); // 수정할 내용
        dto.setCategory("important"); // category 추가

        // EnvDto 객체를 JSON 문자열로 변환 (HTTP 요청 본문에 담기 위해)
        String json = objectMapper.writeValueAsString(dto);

        // When & Then - 수정 API 호출 및 응답 검증
        mockMvc.perform(put("/api/notice/modify/5") // 요청 URI (PUT 방식)
                        .contentType(MediaType.APPLICATION_JSON) // 요청 본문 타입 설정
                        .content(json) // JSON 데이터 전송
                        .with(csrf())) // CSRF 토큰 포함
                .andExpect(status().isOk()) // 응답 상태 코드가 200 OK인지 검증
                .andExpect(jsonPath("$.message").value("수정 성공")) // 응답 JSON의 message 값 검증
                .andExpect(jsonPath("$.noticeId").value(5L)); // 응답 JSON의 envId 값 검증
    }

    // 게시글 수정 실패 테스트 (존재하지 않는 ID)
    @Test
    @DisplayName("게시글 수정 실패 - 존재하지 않는 ID")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    public void testModifyFail_NotFound() throws Exception {
        // Given - 존재하지 않는 게시글 ID와 수정 데이터
        Long invalidId = 99999L; // 존재하지 않는 ID

        // 수정할 게시글 데이터 설정
        NoticeDto dto = new NoticeDto();
        dto.setTitle("수정 테스트 제목");
        dto.setContent("수정 테스트 내용");

        // DTO를 JSON 문자열로 변환 (Controller가 @RequestBody로 JSON을 받기 때문)
        String json = objectMapper.writeValueAsString(dto);

        // When & Then - 수정 API 호출 → 응답 검증
        mockMvc.perform(put("/api/notice/modify/{noticeId}", invalidId) // PUT 요청
                        .contentType(MediaType.APPLICATION_JSON) // 요청 본문은 JSON 형식
                        .content(json) // JSON 본문 데이터 전송
                        .with(csrf())) // CSRF 토큰 포함
                .andDo(print()) // 요청/응답 로그 출력
                .andExpect(status().isBadRequest()) // 응답 상태가 400 (Bad Request) 인지 확인
                .andExpect(content().string("수정에 실패했습니다."));  // 응답 본문 메시지 확인 (예상 메시지와 일치하는지)
    }


    @Test
    @DisplayName("API 컨트롤러 - 게시글 삭제 성공 테스트")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    public void testRemove() throws Exception {
        // Given - 삭제할 게시글 ID (DB에 존재해야 함)
        Long noticeId = 5L;

        // When & Then - 삭제 API 호출 및 응답 검증
        mockMvc.perform(delete("/api/notice/remove/{noticeId}", noticeId) // DELETE 요청
                        .with(csrf())) // CSRF 토큰 포함
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.message").value("삭제되었습니다.")) // 메시지 검증
                .andExpect(jsonPath("$.noticeId").value(noticeId)); // 삭제된 ID 검증
    }

    // 게시글 삭제 실패 테스트 (존재하지 않는 ID)
    @Test
    @DisplayName("게시글 삭제 실패 - 존재하지 않는 ID")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    public void testDeleteFail_NotFound() throws Exception {

        // Given - 존재하지 않는 게시글 ID
        Long invalidId = 999L;

        // When & Then - 삭제 요청을 보내고, 실패 응답(400)을 검증
        mockMvc.perform(delete("/api/notice/remove/{noticeId}", invalidId)  // DELETE 요청 전송
                        .with(csrf())) // CSRF 토큰 포함 (Spring Security 사용 시 필수)
                .andExpect(status().isBadRequest()) // 기대 응답: 400 Bad Request
                .andExpect(content().string("삭제에 실패했습니다.")) // 기대 메시지 본문 확인
                .andDo(print()); // 요청과 응답 전체 내용 콘솔에 출력
    }

}