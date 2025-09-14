package org.example.ganggrbkbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ganggrbkbackend.common.utils.Result;
import org.example.ganggrbkbackend.domain.dto.UserLoginDTO;
import org.example.ganggrbkbackend.domain.vo.UserLoginVO;
import org.example.ganggrbkbackend.service.UserService;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户认证", description = "用户登录、退出等认证相关接口")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        UserLoginVO loginVO = userService.login(loginDTO);
        return Result.ok(loginVO);
    }

    @Operation(summary = "用户退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        userService.logout(token);
        return Result.ok();
    }

    @Operation(summary = "刷新token")
    @PostMapping("/refresh")
    public Result<String> refreshToken(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        String newToken = userService.refreshToken(token);
        return Result.ok(newToken);
    }
}