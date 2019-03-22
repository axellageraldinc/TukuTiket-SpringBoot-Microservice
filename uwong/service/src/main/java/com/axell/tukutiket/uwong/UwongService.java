package com.axell.tukutiket.uwong;

public interface UwongService {
    RegisterUserResponse register(RegisterUserRequest registerUserRequest);

    UserDetailResponse getUserDetail(String userId);
}
