package com.axell.tukutiket.pesenan;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(value = PesenanRestController.class)
@DirtiesContext
public class PesenanRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PesenanService pesenanService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void placeOrderSuccess() throws Exception {
        when(pesenanService.placeNewOrder(any(OrderRequest.class)))
                .thenReturn(OrderResponse.builder()
                        .id("1")
                        .build());

        mockMvc
                .perform(post("/api/pesenan")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(OrderWebRequest.builder()
                                .eventId("abc")
                                .bankId("def")
                                .userId("ghi")
                                .ticketQuantity(1)
                                .build()
                        )))
                .andExpect(jsonPath("$.httpCode", equalTo(HttpURLConnection.HTTP_OK)))
                .andExpect(jsonPath("$.errorCode", nullValue()))
                .andExpect(jsonPath("$.data.id", equalTo("1")));

        verify(pesenanService, times(1)).placeNewOrder(any(OrderRequest.class));
    }

    @Test
    public void getOrderDetailSuccess() throws Exception {
        when(pesenanService.getOrderDetail(anyString()))
                .thenReturn(OrderDetailResponse.builder()
                        .id("1")
                        .eventName("1")
                        .eventVenue("1")
                        .eventDate(new Date())
                        .bankName("1")
                        .bankAccountNumber("1")
                        .ticketQuantity(1)
                        .totalPrice(BigDecimal.valueOf(1000))
                        .build());

        mockMvc
                .perform(get("/api/pesenan/{orderId}", "1"))
                .andExpect(jsonPath("$.httpCode", equalTo(HttpURLConnection.HTTP_OK)))
                .andExpect(jsonPath("$.errorCode", nullValue()))
                .andExpect(jsonPath("$.data.id", equalTo("1")));

        verify(pesenanService, times(1)).getOrderDetail(anyString());
    }

    @Test
    public void getOrderDetailFailedOrderNotFound() throws Exception {
        when(pesenanService.getOrderDetail(anyString()))
                .thenThrow(new RuntimeException(ErrorCode.ORDER_NOT_FOUND.toString()));

        mockMvc
                .perform(get("/api/pesenan/{orderId}", "1"))
                .andExpect(jsonPath("$.httpCode", equalTo(HttpURLConnection.HTTP_OK)))
                .andExpect(jsonPath("$.errorCode", equalTo(ErrorCode.ORDER_NOT_FOUND.toString())))
                .andExpect(jsonPath("$.data", nullValue()));

        verify(pesenanService, times(1)).getOrderDetail(anyString());
    }

    @Test
    public void confirmPaymentSuccess() throws Exception {
        when(pesenanService.confirmPayment(anyString(), any(ConfirmPaymentRequest.class)))
                .thenReturn(OrderResponse.builder()
                        .id("1")
                        .build());

        mockMvc
                .perform(post("/api/pesenan/confirm/{orderId}", "1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(ConfirmPaymentWebRequest.builder()
                                .amountPaid(BigDecimal.valueOf(1000))
                                .build()
                        )))
                .andExpect(jsonPath("$.httpCode", equalTo(HttpURLConnection.HTTP_OK)))
                .andExpect(jsonPath("$.errorCode", nullValue()))
                .andExpect(jsonPath("$.data.id", equalTo("1")));

        verify(pesenanService, times(1)).confirmPayment(anyString(), any(ConfirmPaymentRequest.class));
    }
}