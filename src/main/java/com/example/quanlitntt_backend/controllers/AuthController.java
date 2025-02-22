package com.example.quanlitntt_backend.controllers;

import com.example.quanlitntt_backend.serviceImplements.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthServiceImpl authService;

    @PostMapping("/dangnhap")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String tenDangNhap = request.get("tenDangNhap");
        String matKhau = request.get("matKhau");

        String token = authService.DangNhap(tenDangNhap, matKhau);

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/dangxuat")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(Map.of("message", authService.DangXuat(token)));
    }
}
