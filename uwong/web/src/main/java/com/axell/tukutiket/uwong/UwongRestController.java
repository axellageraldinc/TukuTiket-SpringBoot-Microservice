package com.axell.tukutiket.uwong;

import com.axell.microservices.common.webmodel.WebResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/uwong")
public class UwongRestController {
    @Autowired
    private UwongService uwongService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<RegisterUserWebResponse> register(@RequestBody RegisterUserWebRequest registerUserWebRequest) {
        RegisterUserRequest registerUserRequest = toUserRequest(registerUserWebRequest);
        return WebResponse.OK(
                toUserWebResponse(uwongService.register(registerUserRequest))
        );
    }

    private RegisterUserRequest toUserRequest(RegisterUserWebRequest registerUserWebRequest) {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        BeanUtils.copyProperties(registerUserWebRequest, registerUserRequest);
        return registerUserRequest;
    }

    private RegisterUserWebResponse toUserWebResponse(RegisterUserResponse registerUserResponse) {
        RegisterUserWebResponse registerUserWebResponse = new RegisterUserWebResponse();
        BeanUtils.copyProperties(registerUserResponse, registerUserWebResponse);
        return registerUserWebResponse;
    }

    @GetMapping(
            value = "/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserDetailWebResponse> getUserDetail(@PathVariable(value = "userId") String userId) {
        UserDetailResponse userDetailResponse = uwongService.getUserDetail(userId);
        return WebResponse.OK(
                toUserDetailWebResponse(userDetailResponse)
        );
    }

    private UserDetailWebResponse toUserDetailWebResponse(UserDetailResponse userDetailResponse) {
        UserDetailWebResponse userDetailWebResponse = new UserDetailWebResponse();
        BeanUtils.copyProperties(userDetailResponse, userDetailWebResponse);
        return userDetailWebResponse;
    }
}
