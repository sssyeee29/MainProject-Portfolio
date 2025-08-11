package com.ecovery.controller;

import com.ecovery.dto.OrderDto;
import com.ecovery.dto.OrderItemDto;
import com.ecovery.dto.OrderItemRequestDto;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/*
 * 에코마켓 OrderApiController 통합 테스트
 * 에코마켓 주문 REST API Controller의 기능(주문 페이지 요청, 주문 저장, 주문 페이지 재구성)에 대한 통합 테스트
 * @author : sehui
 * @fileName : OrderApiControllerTest
 * @since : 250728
 * @history
 *  - 250728 | sehui | 주문 페이지 요청 Test 실행
 *  - 250728 | sehui | 주문 저장 요청 Test 실행
 *  - 250728 | sehui | 주문 페이지 재구성 Test 실행
 */

@SpringBootTest
@AutoConfigureMockMvc       //MockMvc 설정 자동 적용
@Transactional
@Rollback(false)
@Slf4j
class OrderApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test2@email.com", roles = {"USER"})
    @DisplayName("주문 페이지 요청")
    public void testPrepareOrder() throws Exception {

        //given : OrderItemRequestDto 리스트 생성
        List<OrderItemRequestDto> orderItemRequests = new ArrayList<>();
        OrderItemRequestDto req = new OrderItemRequestDto();
        req.setItemId(9L);          //DB에 itemImg 존재하는 itemId 사용
        req.setCount(3);
        orderItemRequests.add(req);

        //when : 주문 페이지 요청
        String responseContent = mockMvc.perform(MockMvcRequestBuilders.post("/api/order/prepare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderItemRequests)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //JSON -> OrderDto 변환
        OrderDto orderDto = objectMapper.readValue(responseContent, OrderDto.class);

        //then : 결과 검증
        assertNotNull(orderDto);
        assertNotNull(orderDto.getOrderUuid());
        assertNotNull(orderDto.getOrderItems());
        assertTrue(orderDto.getOrderItems().size() > 0);

        log.info("OrderDto >> {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(orderDto));
    }

    @Test
    @WithMockUser(username = "test2@email.com", roles = {"USER"})
    @DisplayName("주문 저장 요청")
    public void testSaveOrder() throws Exception {

        //given : 주문 OrderDto 생성
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderUuid("test_orderUuid");
        orderDto.setName("tester");
        orderDto.setZipcode("12345");
        orderDto.setRoadAddress("서울시 00구 0000로");
        orderDto.setDetailAddress("000동 000호");
        orderDto.setPhoneNumber("010-1111-2222");

        List<OrderItemDto> oderItems = new ArrayList<>();

        OrderItemDto item1 = OrderItemDto.builder()
                .itemId(11L)
                .itemName("수정 제품")
                .price(1000)
                .count(1)
                .build();

        OrderItemDto item2 = OrderItemDto.builder()
                .itemId(12L)
                .itemName("test 수정Item")
                .price(5000)
                .count(3)
                .build();

        oderItems.add(item1);
        oderItems.add(item2);

        orderDto.setOrderItems(oderItems);

        //when : 주문 저장
        String responseContent = mockMvc.perform(MockMvcRequestBuilders.post("/api/order/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //then : JSON 파싱 후 결과 검증
        JsonNode jsonResponse = objectMapper.readTree(responseContent);
        assertNotNull(jsonResponse.get("orderId"));

        log.info("orderId >> {}", jsonResponse.get("orderId"));
        log.info("Request OrderDto >> {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(orderDto));
    }

    @Test
    @WithMockUser(username = "test2@email.com", roles = {"USER"})
    @DisplayName("주문 페이지 재구성")
    public void testRetryOrder() throws Exception {

        //given : 주문 id 설정
        Long orderId = 20L;

        //when : 주문 페이지 재구성
        String responseContent = mockMvc.perform(MockMvcRequestBuilders.get("/api/order/retry/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //응답 JSON 파싱
        JsonNode jsonResponse = objectMapper.readTree(responseContent);

        //then : 결과 검증
        assertNotNull(jsonResponse.get("order"), "order 피드가 존재해야 합니다.");

        JsonNode orderNode = jsonResponse.get("order");
        assertNotNull(orderNode.get("orderId"), "orderId가 null이면 안 됩니다.");
        assertNotNull(orderNode.get("orderUuid"));
        assertTrue(orderNode.get("orderItems").isArray());
        assertTrue(orderNode.get("orderItems").size() > 0, "orderItems는 비어 있으면 안 됩니다.");

        log.info("RetryOrder >> {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonResponse));

    }

}