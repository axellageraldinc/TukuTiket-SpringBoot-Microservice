package com.axell.tukutiket.uwong;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UwongServiceImpl implements UwongService {

    @Autowired
    private UwongRepository uwongRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public RegisterUserResponse register(RegisterUserRequest registerUserRequest) {
        User newUser = toUser(registerUserRequest);
        User registeredUser = uwongRepository.save(newUser);
        return toUserResponse(registeredUser);
    }

    private User toUser(RegisterUserRequest registerUserRequest) {
        User user = new User();
        BeanUtils.copyProperties(registerUserRequest, user);
        user.setId(UUID.randomUUID().toString());
        user.setPassword(bCryptPasswordEncoder.encode(registerUserRequest.getPassword()));
        return user;
    }

    private RegisterUserResponse toUserResponse(User user) {
        RegisterUserResponse registerUserResponse = new RegisterUserResponse();
        BeanUtils.copyProperties(user, registerUserResponse);
        return registerUserResponse;
    }

    @Override
    public UserDetailResponse getUserDetail(String userId) {
        Optional<User> optionalUser = uwongRepository.findById(userId);
        if (!optionalUser.isPresent())
            throw new RuntimeException(ErrorCode.USER_NOT_FOUND.toString());

        User user = optionalUser.get();
        return toUserDetailResponse(user);
    }

    private UserDetailResponse toUserDetailResponse(User user) {
        UserDetailResponse userDetailResponse = new UserDetailResponse();
        BeanUtils.copyProperties(user, userDetailResponse);
        return userDetailResponse;
    }
}
