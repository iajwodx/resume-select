package com.resume.controller;

import com.resume.dto.Result;
import com.resume.entity.User;
import com.resume.mapper.UserMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Login and session management controller.
 */
@RestController
@RequestMapping("/api")//这个是类路径
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    /**
     * Login: validate username/password, store in session.
     */
    @PostMapping("/login")//这个的完整路径是 /api/login，方法是POST
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpSession session) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return Result.error(400, "用户名和密码不能为空");
        }

        User user = userMapper.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            return Result.error(401, "用户名或密码错误");
        }

        // Store user info in session
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        return Result.success(Map.of(
                "username", user.getUsername(),
                "role", user.getRole()
        ));
    }

    /**
     * Get current session info.
     */
    @GetMapping("/session")
    public Result<Map<String, Object>> session(HttpSession session) {
        Object username = session.getAttribute("username");
        Object role = session.getAttribute("role");

        if (username == null) {
            return Result.error(401, "未登录");
        }

        return Result.success(Map.of(
                "username", username.toString(),
                "role", role.toString()
        ));
    }

    /**
     * Logout: invalidate session.
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.success(null);
    }

    /**
     * Register: create a new user (always as 'user' role).
     * Admin accounts are pre-created in the database.
     * account: 1-10 chars, password: 6-12 chars.
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        // Validate input
        if (username == null || username.isBlank()) {
            return Result.error(400, "账号不能为空");
        }
        if (username.length() > 10) {
            return Result.error(400, "账号长度不能超过10个字符");
        }
        if (password == null || password.length() < 6 || password.length() > 12) {
            return Result.error(400, "密码长度需要6-12位");
        }

        // Check if username already exists
        if (userMapper.findByUsername(username) != null) {
            return Result.error(400, "该账号已被注册");
        }

        // Always register as 'user' role
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole("user");

        userMapper.insert(user);

        return Result.success(Map.of(
                "username", user.getUsername(),
                "role", user.getRole()
        ));
    }
}
