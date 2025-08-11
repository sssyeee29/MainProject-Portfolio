package com.ecovery.controller;

import com.ecovery.constant.DealStatus;
import com.ecovery.constant.ItemCondition;
import com.ecovery.dto.FreeDto;
import com.ecovery.dto.FreeImgDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;


import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/*
* 무료나눔 게시판 컨트롤러 테스트
* 등록, 조회, 수정, 삭제 기능에 대한 인증 및 유효성 검증 테스트 포함
* 로그인, 권한 체크, 예외 처리 중심 테스트
*
 * @author : yeonsu
 * @fileName : FreeControllerTest
 * @since : 250724
*/
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
public class FreeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private FreeDto freeDto;

    // 각 테스트 실행 전 공통으로 사용할 FreeDto 객체를 미리 생성해 초기화하는 메소드
    @BeforeEach
    public void setUp() {
        freeDto = new FreeDto();
        freeDto.setTitle("테스트 제목");
        freeDto.setContent("테스트 내용");
        freeDto.setCategory("가구");
        freeDto.setRegionGu("강남구");
        freeDto.setRegionDong("역삼동");
        freeDto.setItemCondition(ItemCondition.HIGH);
    }


    @Test
    @DisplayName("게시글 목록 조회")
    public void testList() throws Exception {

        // when : Get 방식으로 /api/free/list 요청을 보내고 응답 본문을 문자열로 추출
        String responseContent = mockMvc.perform(get("/api/free/list"))
                .andReturn()// 응답 결과 반환
                .getResponse()// 응답 객체
                .getContentAsString(); // 응답 본문을 문자열로 반환

        // then : 응답 내용 출력
        log.info("FreeList: {}", responseContent);
}

    @Test
    @DisplayName("게시글 상세 조회 요청")
    public void testGet() throws Exception {

        // given : 게시글 ID, 테스트용 이메일 설정
        Long freeId = 1L;

        // when : 게시글 상세 조회
        String responContent = mockMvc.perform(get("/free/{freeId}", freeId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then : 응답 내용 출력
        log.info("FreeGet : {}", responContent);

    }

    @Test
    @DisplayName("게시글 등록 페이지 접근 테스트 - 로그인 사용자")
    @WithMockUser(username = "test@test.com", roles = {"USER"})
    public void testRegisterPage() throws Exception {

        // given : 로그인된 사용자 설정은 @WithMockUser 어노테이션으로 처리됨

        // when : 게시글 등록 페이지(GET/free/register) 요청 실행
        mockMvc.perform(get("/free/register"))

                // then : 200 OK 응답 및 "free/register" 뷰가 반환되는지 검증
                .andExpect(status().isOk()) // 200 OK 응답을 기대
                .andExpect(view().name("free/register")); // free/register 뷰가 반환되는지 확인
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 게시글 등록 페이지 접근 시 로그인 페이지 이동 테스트")
    public void testRegisterLogin() throws Exception {

        // given : 로그인되지 않은(비인증) 사용자 상태

        // when : 게시글 등록 페이지 ("/free/register")에 GET 요청

        // then : 로그인 페이지로 리다이렉트 (3xx 상태코드 및 /member/login URL 포함)
        mockMvc.perform(get("/free/register"))
                .andExpect(status().is3xxRedirection()) // 3xx 리다이렉션 응답을 기대
                .andExpect(redirectedUrlPattern("**/member/login")); // 실제 로그인 페이지 URL 패턴에 맞게 조정

    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 게시글 수정 페이지 접근 시 로그인 페이지 이동 테스트")
    public void testModifyPageRedirectToLoginPage() throws Exception {

        // given: 인증되지 않은 사용자 (비로그인 상태)

        // when: 게시글 수정 페이지("/free/modify/1")에 GET 요청

        // then: 로그인 페이지로 리다이렉트 (3xx 응답 + /member/login으로 이동)
        mockMvc.perform(get("/free/modify/1")) // 임의의 freeId 사용
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/member/login"));
    }


    @Test
    @DisplayName("로그인한 사용자가 게시글 수정 페이지 접근 시 성공적으로 페이지가 로드되는지 테스트")
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void modifyPagedUser() throws Exception {

        // given: 로그인된 사용자 (USER 권한)

        // when: 게시글 수정 페이지("/free/modify/1")에 GET 요청

        // then: 응답 코드 200 OK, 뷰 이름이 "free/modify"인지 확인
        mockMvc.perform(get("/free/modify/1")) // 임의의 freeId 사용
                .andExpect(status().isOk())
                .andExpect(view().name("free/modify"));
    }

    @Test
    @DisplayName("게시글 등록 - 인증된 사용자")
    @WithMockUser(username = "test@test.com", roles = {"USER"})
    public void testRegister() throws Exception {
        // given : 등록할 게시글 데이터 준비
        FreeDto freeDto = FreeDto.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .category("전자제품")
                .regionGu("강남구")
                .regionDong("역삼동")
                .itemCondition(ItemCondition.HIGH)
                .dealStatus(DealStatus.DONE)
                .imgUrl("tttt.jpg")
                .build();

        // JSON으로 변환
        String jsonDto = new ObjectMapper().writeValueAsString(freeDto);

        // multipart 요청에서 JSON DTO 구성
        MockMultipartFile freeDtoPart = new MockMultipartFile(
                "freeDto", // 반드시 @RequestPart("freeDto")와 이름 동일
                "",
                "application/json",
                jsonDto.getBytes(StandardCharsets.UTF_8)
        );

        // 이미지 파일 구성
        MockMultipartFile imgFile = new MockMultipartFile(
                "imgFile", "test.png", "image/png", "fake-image".getBytes()
        );

        // when : 게시글 등록 API 요청 전송
        mockMvc.perform(multipart("/api/free/register")
                        .file(freeDtoPart)
                        .file(imgFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
                        .with(user("test@test.com").roles("USER")))
                // then : 응답 코드 검증
                .andExpect(status().isCreated()); // 201 코드 기대
    }

    @Test
    @DisplayName("제목 누락으로 게시글 등록 실패 테스트")
    @WithMockUser(username = "test@test.com", roles = "USER")
    void testRegisterValidationFail() throws Exception {
        // 제목(null) 누락된 FreeDto 생성
        FreeDto dto = FreeDto.builder()
                .content("내용만 있고 제목 없음")
                .category("전자제품")
                .regionGu("강남구")
                .regionDong("역삼동")
                .itemCondition(ItemCondition.MEDIUM)
                .build();

        // DTO를 JSON으로 변환
        MockMultipartFile freeDtoPart = new MockMultipartFile(
                "freeDto",
                "",
                "application/json",
                new ObjectMapper().writeValueAsBytes(dto)
        );

        // 임시 이미지 파일
        MockMultipartFile imgFile = new MockMultipartFile(
                "imgFile",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake image data".getBytes()
        );

        // when : 게시글 등록 API에 POST 요청수행
        mockMvc.perform(multipart("/api/free/register")
                        .file(freeDtoPart)
                        .file(imgFile)
                        .with(request -> {
                            request.setMethod("POST"); return request;
                        })
                        .with(csrf()) // CSRF 토큰 포함
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                // then : 400 Bad Request 응답 및 에러 메시지 포함 기대
                .andExpect(status().isBadRequest()) // 유효성 실패로 400 기대
                .andExpect(content().string(org.hamcrest.Matchers.containsString("유효성 오류")));
    }



    @Test
    @DisplayName("게시글 수정 테스트")
    @WithMockUser(username = "test@test.com", roles = "USER")
    public void testModify() throws Exception {

        // given : 수정할 게시글 정보와 이미지 파일 준비
        FreeDto dto = FreeDto.builder()
                .memberId(1L)
                .freeId(7L)
                .title("테스트 제목")
                .content("테스트 내용")
                .category("전자제품")
                .regionGu("강남구")
                .regionDong("역삼동")
                .itemCondition(ItemCondition.HIGH)
                .dealStatus(DealStatus.DONE)
                .imgUrl("tttt.jpg")
                .build();

        // 대표 이미지 포함 (수정 시 포함될 이미지 ID)
        dto.setImgList(List.of(
                FreeImgDto.builder()
                        .freeImgId(5L)
                        .build()
        ));

        // DTO를 JSON 형태의 multipart 데이터로 변환
        MockMultipartFile freeDtoPart = new MockMultipartFile(
                "freeDto", "", "application/json",
                new ObjectMapper().writeValueAsBytes(dto)
        );

        // 더미 이미지 파일 구성
        MockMultipartFile imgFile = new MockMultipartFile(
                "imgFile", "test.jpg", MediaType.IMAGE_JPEG_VALUE,
                "dummy image data".getBytes()
        );

        // when : 게시글 수정 요청 (Put 방식 multipart 요청)
        String response = mockMvc.perform(multipart("/api/free/modify/{freeId}", 7L)
                        .file(freeDtoPart)
                        .file(imgFile)
                        .with(request -> { request.setMethod("PUT"); return request; })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                // then : 수정 성공 시 200 OK 응답 확인 및 응답 출력
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        log.info("Free Modify Response: {}", response);
    }


    @Test
    @DisplayName("수정 실패 테스트 - 작성자도 아니고 관리자도 아님")
    @WithMockUser(username = "maru@maru.com", roles = "USER") // 작성자 X, 관리자 X
    public void testModifyForbidden() throws Exception {

        // given : 수정 대상 게시글 Id와 수정 시도할 사용자 정보
        Long freeId = 8L;

        // 수정 시도할 게시글 정보 준비
        FreeDto dto = FreeDto.builder()
                .title("수정 시도")
                .content("작성자도 아니고 관리자도 아님")
                .category("기타")
                .regionGu("강북구")
                .regionDong("미아동")
                .itemCondition(ItemCondition.HIGH)
                .dealStatus(DealStatus.ONGOING)
                .build();

        // DTO를 multipart 데이터로 변환
        MockMultipartFile freeDtoPart = new MockMultipartFile(
                "freeDto", "", "application/json",
                new ObjectMapper().writeValueAsBytes(dto)
        );

        // 더미 이미지 파일 준비
        MockMultipartFile dummyImage = new MockMultipartFile(
                "imgFile", "dummy.jpg", MediaType.IMAGE_JPEG_VALUE,
                "fake image".getBytes()
        );

        // when : 수정 요청 수행 (작성자가 아님)
        mockMvc.perform(multipart("/api/free/modify/{freeId}", freeId)
                        .file(freeDtoPart)
                        .file(dummyImage)
                        .with(request -> { request.setMethod("PUT"); return request; })
                        .with(csrf()) // csrf 토큰 포함
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                // then : 권한 없음으로 인해 403 Forbidden 응답 기대
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("게시글 수정 실패 테스트 - 유효성 검사 실패 (제목 누락)")
    @WithMockUser(username = "test@test.com", roles = "USER")
    void testModifyValidationFail() throws Exception {

        // given : 제목이 누락된 FreeDto 생성 (유효성 검사를 통과하지 못하도록 설계)
        Long freeId = 7L;
        FreeDto dto = FreeDto.builder()
                .content("내용만 있고 제목 없음")
                .category("의류")
                .regionGu("중구")
                .regionDong("필동")
                .itemCondition(ItemCondition.HIGH)
                .dealStatus(DealStatus.DONE)
                .build();

        MockMultipartFile freeDtoPart = new MockMultipartFile(
                "freeDto", "", "application/json",
                new ObjectMapper().writeValueAsBytes(dto)
        );

        MockMultipartFile imgFile = new MockMultipartFile(
                "imgFile", "test.jpg", MediaType.IMAGE_JPEG_VALUE,
                "fake image".getBytes()
        );

        // when : 제목이 빠진 상태로 수정 요청 실행
        mockMvc.perform(multipart("/api/free/modify/{freeId}", freeId)
                        .file(freeDtoPart)
                        .file(imgFile)
                        .with(request -> { request.setMethod("PUT"); return request; })
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                // then : 유효성 검사 실패로 인해 400 Bad Request 기대
                .andExpect(status().isBadRequest());
    }



    @Test
    @DisplayName("게시글 삭제 테스트 - 작성자 or 관리자 권한")
    @WithMockUser(username = "test@test.com", roles = "USER") // 관리자 테스트 시 roles = {"ADMIN"}
    public void testDelete() throws Exception {
        // given : 삭제 대상 게시글 ID
        Long freeId = 8L;

        // when : delete 방식으로 게시글 삭제 요청 (multipart로 강제 전송)
        String response = mockMvc.perform(multipart("/api/free/remove/{freeId}", freeId)
                        .with(request -> {
                            request.setMethod("DELETE"); // multipart는 기본 POST이므로 강제로 DELETE로 설정
                            return request;
                        })
                        .with(csrf()) // CSRF 토큰 포함
                        .contentType(MediaType.MULTIPART_FORM_DATA) // multipart 요청 명시
                )
                // then : 정상 삭제 응답 (200 OK) 기대
                .andExpect(status().isOk()) // 응답 코드 200 기대
                .andReturn()
                .getResponse()
                .getContentAsString();

        log.info("삭제 응답: {}", response);
    }

    @Test
    @DisplayName("삭제 실패 테스트 - 작성자 아님")
    @WithMockUser(username = "maru@maru.com", roles = "USER")
    public void testDeleteNotOwner() throws Exception {

        // given : 삭제 대상 게시글 ID
        Long freeId = 8L;

        // when : 게시글 삭제 요청 (작성자가 아님)
        mockMvc.perform(multipart("/api/free/remove/{freeId}", freeId)
                        .with(request -> {
                            request.setMethod("DELETE"); // DELETE 방식으로 강제 설정
                            return request;
                        })
                        .with(csrf()) // CSRF 토큰 포함
                        .contentType(MediaType.MULTIPART_FORM_DATA) // multipart 요청
                )
                // then : 권한 없음으로 403 Forbidden 응답 기대
                .andExpect(status().isForbidden()); // 삭제 권한 없음
    }



}





