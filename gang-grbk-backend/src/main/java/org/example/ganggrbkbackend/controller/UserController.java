package org.example.ganggrbkbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ganggrbkbackend.common.utils.PageResult;
import org.example.ganggrbkbackend.common.utils.Result;
import org.example.ganggrbkbackend.domain.dto.UserQueryDTO;
import org.example.ganggrbkbackend.domain.dto.UserSaveDTO;
import org.example.ganggrbkbackend.domain.vo.UserVO;
import org.example.ganggrbkbackend.service.UserService;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户列表")
    @PostMapping("/page")
    public Result<PageResult<UserVO>> pageUsers(@Valid @RequestBody UserQueryDTO queryDTO) {
        PageResult<UserVO> pageResult = userService.pageUsers(queryDTO);
        return Result.ok(pageResult);
    }

    @Operation(summary = "根据ID获取用户详情")
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        UserVO user = userService.getUserById(id);
        return Result.ok(user);
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<Long> saveUser(@Valid @RequestBody UserSaveDTO saveDTO) {
        Long id = userService.saveUser(saveDTO);
        return Result.ok(id);
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @Valid @RequestBody UserSaveDTO saveDTO) {
        saveDTO.setId(id);
        userService.updateUser(saveDTO);
        return Result.ok();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.ok();
    }

    @Operation(summary = "禁用/启用用户")
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.ok();
    }

    @Operation(summary = "重置用户密码")
    @PutMapping("/{id}/password/reset")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.ok();
    }
}