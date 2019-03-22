package com.axell.tukutiket.uwong;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UwongServiceImplTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private UwongRepository uwongRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UwongServiceImpl uwongService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void registerSuccess() {
        when(bCryptPasswordEncoder.encode(anyString()))
                .thenReturn("encoded password");
        when(uwongRepository.save(any(User.class)))
                .thenReturn(User.builder()
                        .id("1")
                        .build());

        RegisterUserResponse registerUserResponse = uwongService.register(RegisterUserRequest.builder()
                .name("1")
                .email("1@1.com")
                .password("12345678")
                .build());

        assertThat(registerUserResponse, notNullValue());
        assertThat(registerUserResponse.getId(), equalTo("1"));

        InOrder inOrder = Mockito.inOrder(bCryptPasswordEncoder, uwongRepository);
        inOrder.verify(bCryptPasswordEncoder, times(1)).encode(anyString());
        inOrder.verify(uwongRepository, times(1)).save(any(User.class));
    }

    @Test
    public void getUserDetailSuccess() {
        when(uwongRepository.findById(anyString()))
                .thenReturn(Optional.of(User.builder()
                        .id("1")
                        .email("1")
                        .name("1")
                        .password("1")
                        .build()));

        UserDetailResponse userDetailResponse = uwongService.getUserDetail("123");

        assertThat(userDetailResponse, notNullValue());
        assertThat(userDetailResponse.getName(), equalTo("1"));

        verify(uwongRepository, times(1)).findById(anyString());
    }

    @Test(expected = RuntimeException.class)
    public void getUserDetailFailedUserNotFound() {
        when(uwongRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        uwongService.getUserDetail("123");

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(ErrorCode.USER_NOT_FOUND.toString());

        verify(uwongRepository, times(1)).findById(anyString());
    }
}