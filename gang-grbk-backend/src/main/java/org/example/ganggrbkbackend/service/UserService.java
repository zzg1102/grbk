package org.example.ganggrbkbackend.service;

import org.example.ganggrbkbackend.common.utils.PageResult;
import org.example.ganggrbkbackend.domain.dto.UserLoginDTO;
import org.example.ganggrbkbackend.domain.dto.UserQueryDTO;
import org.example.ganggrbkbackend.domain.dto.UserSaveDTO;
import org.example.ganggrbkbackend.domain.vo.UserLoginVO;
import org.example.ganggrbkbackend.domain.vo.UserVO;

public interface UserService {

    UserLoginVO login(UserLoginDTO loginDTO);

    UserLoginVO register(UserSaveDTO registerDTO);

    void logout(String token);

    String refreshToken(String token);

    PageResult<UserVO> pageUsers(UserQueryDTO queryDTO);

    UserVO getUserById(Long id);

    Long saveUser(UserSaveDTO saveDTO);

    void updateUser(UserSaveDTO saveDTO);

    void deleteUser(Long id);

    void updateUserStatus(Long id, Integer status);

    void resetPassword(Long id);
}