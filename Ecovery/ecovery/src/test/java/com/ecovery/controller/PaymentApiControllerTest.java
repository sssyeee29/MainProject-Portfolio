package com.ecovery.controller;

import com.ecovery.dto.PaymentResultDto;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/*
 * 에코마켓 PaymentApiController 통합 테스트
 * 에코마켓 결제 REST API Controller의 기능(결제 성공, 결제 실패)에 대한 통합 테스트
 * @author : sehui
 * @fileName : PaymentApiControllerTest
 * @since : 250728
 * @history
 *  - 250728 | sehui | 결제 성공 시 결제 정보 저장 Test 작성 (프론트에서 결제 API 연결 후 Test 실행 예정)
 *  - 250728 | sehui | 결제 실패 시 주문 상태 변경 Test 작성 (프론트에서 결제 API 연결 후 Test 실행 예정)
 */

@SpringBootTest
@AutoConfigureMockMvc       //MockMvc 설정 자동 적용
@Transactional
@Slf4j
class PaymentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /*
    @Test
    @WithMockUser(username = "test2@email.com", roles = {"USER"})
    @DisplayName("결제 성공 시 결제 정보 저장")
    public void testSavePayment() throws Exception {

        //given : PaymentResultDto 생성
        PaymentResultDto paymentResult = new PaymentResultDto();
        paymentResult.setOrderUuid("test_order_uuid");
        paymentResult.setPaymentKey("pay_123456");
        paymentResult.setAmount(3);

        //when : 결제 정보 저장 요청
        String responseContent = mockMvc.perform(MockMvcRequestBuilders.post("/api/payment/success")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentResult)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //then : 결과 검증
        Long savedPaymentId = objectMapper.readValue(responseContent, Long.class);
        assertNotNull(savedPaymentId, "결제 ID는 null이면 안 됩니다.");

        log.info("결제 저장 성공 PaymentId >> {}", savedPaymentId);
    }

    @Test
    @WithMockUser(username = "test2@email.com", roles = {"USER"})
    @DisplayName("결제 실패 시 주문 상태 변경")
    public void testFailPayment() throws Exception {

        // given : PaymentResultDto 생성
        PaymentResultDto paymentResult = new PaymentResultDto();
        paymentResult.setOrderUuid("test-order_uuid");
        paymentResult.setPaymentKey("pay_fail_12345");
        paymentResult.setAmount(3);

        // when : POST 요청
        String responseContent = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/payment/fail")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(paymentResult)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then : JSON 검증
        JsonNode jsonResponse = objectMapper.readTree(responseContent);

        assertNotNull(jsonResponse.get("orderId"), "orderId는 null이면 안 됩니다.");
        assertEquals("결제 실패", jsonResponse.get("message").asText());

        log.info("FailPayment Response >> {}",
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonResponse));
    }
    */
}