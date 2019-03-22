package com.axell.tukutiket.acara;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
import java.util.Date;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.NONE)
@DirtiesContext
public class AcaraRestControllerTest {

    @Value("${local.server.port}")
    private int serverPort;

    @MockBean
    private AcaraService acaraService;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = serverPort;
    }

    @Test
    public void findAllOngoingEventsSuccess() {
        Mockito.when(acaraService.findOngoingEvents())
                .thenReturn(Collections.singletonList(
                        EventResponse.builder()
                                .id("1")
                                .name("1")
                                .venue("1")
                                .date(new Date())
                                .status(EventStatus.TO_BE_HELD)
                                .ticketPrice(BigDecimal.valueOf(1000))
                                .build()));

        RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .get("/api/acara")
                .then()
                    .body("httpCode", equalTo(HttpURLConnection.HTTP_OK))
                    .body("errorCode", nullValue())
                    .body("data[0].id", equalTo("1"));

        verify(acaraService, times(1)).findOngoingEvents();
    }

    @Test
    public void getEventDetailSuccess() {
        Mockito.when(acaraService.getEventDetail(anyString()))
                .thenReturn(EventDetailResponse.builder()
                        .id("1")
                        .name("1")
                        .venue("1")
                        .date(new Date())
                        .status(EventStatus.TO_BE_HELD)
                        .ticketPrice(BigDecimal.valueOf(1000))
                        .build());

        RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .get("/api/acara/123")
                .then()
                    .body("httpCode", equalTo(HttpURLConnection.HTTP_OK))
                    .body("errorCode", nullValue())
                    .body("data.name", equalTo("1"));

        verify(acaraService, times(1)).getEventDetail(anyString());
    }
}