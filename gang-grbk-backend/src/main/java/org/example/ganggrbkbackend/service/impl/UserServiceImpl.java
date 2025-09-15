package org.example.ganggrbkbackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ganggrbkbackend.common.exception.BusinessException;
import org.example.ganggrbkbackend.common.exception.ResultCodeEnum;
import org.example.ganggrbkbackend.common.utils.JwtUtil;
import org.example.ganggrbkbackend.common.utils.PageResult;
import org.example.ganggrbkbackend.domain.dto.UserLoginDTO;
import org.example.ganggrbkbackend.domain.dto.UserQueryDTO;
import org.example.ganggrbkbackend.domain.dto.UserSaveDTO;
import org.example.ganggrbkbackend.domain.entity.User;
import org.example.ganggrbkbackend.domain.vo.UserLoginVO;
import org.example.ganggrbkbackend.domain.vo.UserVO;
import org.example.ganggrbkbackend.mapper.UserMapper;
import org.example.ganggrbkbackend.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LOGIN_USER_KEY_PREFIX = "login:user:";
    private static final String LOGOUT_TOKEN_KEY_PREFIX = "logout:token:";

    @Override
    public UserLoginVO login(UserLoginDTO loginDTO) {
        if (StrUtil.isBlank(loginDTO.getUsername()) || StrUtil.isBlank(loginDTO.getPassword())) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR);
        }

        // 查询用户
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用");
        }

        // 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 缓存用户信息到Redis
        String userKey = LOGIN_USER_KEY_PREFIX + user.getId();
        redisTemplate.opsForValue().set(userKey, user, 24, TimeUnit.HOURS);

        // 构造返回结果
        UserLoginVO loginVO = new UserLoginVO();
        BeanUtil.copyProperties(user, loginVO);
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setUserType(user.getUserType());

        log.info("用户登录成功: username={}, userId={}", user.getUsername(), user.getId());
        return loginVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLoginVO register(UserSaveDTO registerDTO) {
        if (StrUtil.isBlank(registerDTO.getUsername()) || StrUtil.isBlank(registerDTO.getPassword())) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR);
        }

        // 检查用户名是否已存在
        User existUser = userMapper.selectByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (StrUtil.isNotBlank(registerDTO.getEmail())) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, registerDTO.getEmail());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("邮箱已被注册");
            }
        }

        // 创建新用户
        User user = new User();
        BeanUtil.copyProperties(registerDTO, user);

        // 加密密码
        user.setPassword(BCrypt.hashpw(registerDTO.getPassword(), BCrypt.gensalt()));

        // 设置默认值
        user.setStatus(1); // 默认启用
        user.setUserType(0); // 默认为普通用户
        user.setAvatar("https://img.yzcdn.cn/vant/cat.jpeg"); // 默认头像

        // 保存用户
        userMapper.insert(user);

        // 生成JWT token并直接登录
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 缓存用户信息到Redis
        String userKey = LOGIN_USER_KEY_PREFIX + user.getId();
        redisTemplate.opsForValue().set(userKey, user, 24, TimeUnit.HOURS);

        // 构造返回结果
        UserLoginVO loginVO = new UserLoginVO();
        BeanUtil.copyProperties(user, loginVO);
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setUserType(user.getUserType());

        log.info("用户注册成功: username={}, userId={}", user.getUsername(), user.getId());
        return loginVO;
    }

    @Override
    public void logout(String token) {
        if (StrUtil.isBlank(token)) {
            return;
        }

        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId != null) {
                // 删除用户缓存
                String userKey = LOGIN_USER_KEY_PREFIX + userId;
                redisTemplate.delete(userKey);

                // 将token加入黑名单
                String tokenKey = LOGOUT_TOKEN_KEY_PREFIX + token;
                redisTemplate.opsForValue().set(tokenKey, "1", 24, TimeUnit.HOURS);

                log.info("用户退出登录成功: userId={}", userId);
            }
        } catch (Exception e) {
            log.error("用户退出登录失败: token={}, error={}", token, e.getMessage());
        }
    }

    @Override
    public String refreshToken(String token) {
        if (StrUtil.isBlank(token)) {
            throw new BusinessException("token不能为空");
        }

        // 检查token是否在黑名单中
        String tokenKey = LOGOUT_TOKEN_KEY_PREFIX + token;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey))) {
            throw new BusinessException("token已失效");
        }

        // 刷新token
        String newToken = jwtUtil.refreshToken(token);
        if (StrUtil.isBlank(newToken)) {
            throw new BusinessException("token刷新失败");
        }

        return newToken;
    }

    @Override
    public PageResult<UserVO> pageUsers(UserQueryDTO queryDTO) {
        Page<User> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(queryDTO.getUsername()), User::getUsername, queryDTO.getUsername())
                .like(StrUtil.isNotBlank(queryDTO.getNickname()), User::getNickname, queryDTO.getNickname())
                .like(StrUtil.isNotBlank(queryDTO.getEmail()), User::getEmail, queryDTO.getEmail())
                .eq(queryDTO.getStatus() != null, User::getStatus, queryDTO.getStatus())
                .orderByDesc(User::getCreateTime);

        IPage<User> pageResult = userMapper.selectPage(page, wrapper);

        List<UserVO> records = pageResult.getRecords().stream()
                .map(user -> {
                    UserVO vo = new UserVO();
                    BeanUtil.copyProperties(user, vo);
                    vo.setArticleCount(0); // TODO: 统计文章数量
                    vo.setCommentCount(0); // TODO: 统计评论数量
                    return vo;
                })
                .collect(Collectors.toList());

        return PageResult.of(records, pageResult.getTotal(), pageResult.getSize(), pageResult.getCurrent());
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserVO vo = new UserVO();
        BeanUtil.copyProperties(user, vo);
        vo.setArticleCount(0); // TODO: 统计文章数量
        vo.setCommentCount(0); // TODO: 统计评论数量
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveUser(UserSaveDTO saveDTO) {
        // 检查用户名是否重复
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, saveDTO.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否重复
        if (StrUtil.isNotBlank(saveDTO.getEmail())) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, saveDTO.getEmail());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("邮箱已被使用");
            }
        }

        User user = new User();
        BeanUtil.copyProperties(saveDTO, user);

        // 加密密码
        if (StrUtil.isNotBlank(saveDTO.getPassword())) {
            user.setPassword(BCrypt.hashpw(saveDTO.getPassword(), BCrypt.gensalt()));
        }

        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (user.getUserType() == null) {
            user.setUserType(0);
        }

        userMapper.insert(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserSaveDTO saveDTO) {
        if (saveDTO.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }

        User existUser = userMapper.selectById(saveDTO.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户名是否重复（排除自己）
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, saveDTO.getUsername())
                .ne(User::getId, saveDTO.getId());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否重复（排除自己）
        if (StrUtil.isNotBlank(saveDTO.getEmail())) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, saveDTO.getEmail())
                    .ne(User::getId, saveDTO.getId());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("邮箱已被使用");
            }
        }

        User user = new User();
        BeanUtil.copyProperties(saveDTO, user);

        // 如果有新密码，进行加密
        if (StrUtil.isNotBlank(saveDTO.getPassword())) {
            user.setPassword(BCrypt.hashpw(saveDTO.getPassword(), BCrypt.gensalt()));
        } else {
            user.setPassword(null); // 不更新密码
        }

        userMapper.updateById(user);

        // 清除用户缓存
        String userKey = LOGIN_USER_KEY_PREFIX + saveDTO.getId();
        redisTemplate.delete(userKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // TODO: 检查是否有关联数据（文章、评论等）

        userMapper.deleteById(id);

        // 清除用户缓存
        String userKey = LOGIN_USER_KEY_PREFIX + id;
        redisTemplate.delete(userKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setStatus(status);
        userMapper.updateById(updateUser);

        // 如果是禁用用户，清除缓存
        if (status == 0) {
            String userKey = LOGIN_USER_KEY_PREFIX + id;
            redisTemplate.delete(userKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 重置为默认密码：123456
        String defaultPassword = "123456";
        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setPassword(BCrypt.hashpw(defaultPassword, BCrypt.gensalt()));
        userMapper.updateById(updateUser);

        // 清除用户缓存，强制重新登录
        String userKey = LOGIN_USER_KEY_PREFIX + id;
        redisTemplate.delete(userKey);
    }
}