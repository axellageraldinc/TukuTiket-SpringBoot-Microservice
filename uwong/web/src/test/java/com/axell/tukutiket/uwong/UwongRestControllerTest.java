package com.axell.tukutiket.uwong;

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

import java.net.HttpURLConnection;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.NONE)
@DirtiesContext
public class UwongRestControllerTest {

    @Value("${local.server.port}")
    private int serverPort;

    @MockBean
    private UwongService uwongService;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = serverPort;
    }

    @Test
    public void registerSuccess() throws Exception {
        Mockito.when(uwongService.register(any(RegisterUserRequest.class)))
                .thenReturn(RegisterUserResponse.builder()
                        .id("1")
                        .build());

        RestAssured
                .given()
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(objectMapper.writeValueAsString(RegisterUserWebRequest.builder()
                            .name("1")
                            .email("1@1.com")
                            .password("12345678")
                            .build()))
                .when()
                    .post("/api/uwong")
                .then()
                    .body("httpCode", equalTo(HttpURLConnection.HTTP_OK))
                    .body("errorCode", nullValue())
                    .body("data.id", equalTo("1"));

        Mockito.verify(uwongService, Mockito.times(1)).register(any(RegisterUserRequest.class));
    }

    @Test
    public void getUserDetailSuccess() {
        Mockito.when(uwongService.getUserDetail(anyString()))
                .thenReturn(UserDetailResponse.builder()
                        .name("1")
                        .email("1@1.com")
                        .build());

        RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .get("/api/uwong/123")
                .then()
                    .body("httpCode", equalTo(HttpURLConnection.HTTP_OK))
                    .body("errorCode", nullValue())
                    .body("data.email", equalTo("1@1.com"));
    }
}