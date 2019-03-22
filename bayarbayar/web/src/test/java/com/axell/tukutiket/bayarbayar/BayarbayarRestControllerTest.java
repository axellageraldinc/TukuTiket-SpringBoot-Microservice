package com.axell.tukutiket.bayarbayar;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.NONE)
@DirtiesContext
public class BayarbayarRestControllerTest {

    @Value("${local.server.port}")
    private int serverPort;

    @MockBean
    private BayarbayarService bayarbayarService;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = serverPort;
    }

    @Test
    public void findAllBankInfoSuccess() {
        Mockito.when(bayarbayarService.findAllBankInfo())
                .thenReturn(Collections.singletonList(
                        BankResponse.builder()
                                .id("1")
                                .name("1")
                                .accountNumber("1")
                                .build()));

        RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .get("/api/bayarbayar")
                .then()
                    .body("httpCode", equalTo(HttpURLConnection.HTTP_OK))
                    .body("errorCode", nullValue())
                    .body("data[0].id", equalTo("1"));

        Mockito.verify(bayarbayarService, Mockito.times(1)).findAllBankInfo();
    }

    @Test
    public void getBankDetailSuccess() {
        Mockito.when(bayarbayarService.getBankDetail(anyString()))
                .thenReturn(BankDetailResponse.builder()
                        .name("1")
                        .accountNumber("1")
                        .build());

        RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .when()
                    .get("/api/bayarbayar/1")
                .then()
                    .body("httpCode", equalTo(HttpURLConnection.HTTP_OK))
                    .body("errorCode", nullValue())
                    .body("data.name", equalTo("1"));

        Mockito.verify(bayarbayarService, times(1)).getBankDetail(anyString());
    }

    @Test
    public void validatePaymentSuccess() throws Exception {
        doNothing().when(bayarbayarService).validatePayment(any(PaymentRequest.class));

        RestAssured
                .given()
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(objectMapper.writeValueAsString(PaymentWebRequest.builder()
                            .amountPaid(BigDecimal.valueOf(5000))
                            .totalPrice(BigDecimal.valueOf(5000))
                            .build()))
                .when()
                    .post("/api/bayarbayar")
                .then()
                    .body("httpCode", equalTo(HttpURLConnection.HTTP_OK))
                    .body("errorCode", nullValue())
                    .body("data", nullValue());

        Mockito.verify(bayarbayarService, times(1)).validatePayment(any(PaymentRequest.class));
    }
}