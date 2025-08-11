package com.ecovery.controller;

import com.ecovery.domain.FreeReplyVO;
import com.ecovery.domain.MemberVO;
import com.ecovery.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * 댓글 등록 API 테스트
 * 실제 서비스, DB와 연동되는 통합 테스트 방식으로 검증
 * @author : yeonsu
 * @since : 250725
 */

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class FreeReplyApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberService memberService;

    private FreeReplyVO replyVO;



    // 테스트 전에 공통으로 쓸 댓글 객체 생성
    @BeforeEach
    void setUp() {
        replyVO = new FreeReplyVO();
        replyVO.setFreeId(7L);
        replyVO.setContent("테스트 댓글입니다.");

        // 로그인한 사용자 이메일로 memberId 조회
        String email = "test@test.com";
        MemberVO member = memberService.getMemberByEmail(email);
        replyVO.setMemberId(member.getMemberId());
    }


    @Test
    @DisplayName("댓글 등록 - 인증된 사용자")
    @WithMockUser(username = "test@test.com", roles = {"USER"})
    public void testRegisterReply() throws Exception {

        // Given: JSON 형태로 변환
        String json = new ObjectMapper().writeValueAsString(replyVO);

        // When & Then: 댓글 등록 요청 수행 및 응답 확인
        mockMvc.perform(post("/api/replies/register")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(user("test@test.com").roles("USER")))
                .andExpect(status().isOk());
    }

}


