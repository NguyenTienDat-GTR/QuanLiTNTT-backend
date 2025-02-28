package com.example.quanlitntt_backend.controllers;

import com.example.quanlitntt_backend.entities.TaiKhoan;
import com.example.quanlitntt_backend.serviceImplements.AuthServiceImpl;
import com.example.quanlitntt_backend.serviceImplements.TaiKhoanServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private TaiKhoanServiceImpl taiKhoanService;

    @PostMapping("/dangnhap")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String tenDangNhap = request.get("tenDangNhap");
        String matKhau = request.get("matKhau");

        Optional<TaiKhoan> tk = taiKhoanService.getTaiKhoan(tenDangNhap);

        if(tk.isPresent()){
            if (!tk.get().isHoatDong()){
               return ResponseEntity.status(HttpStatus.LOCKED).body("Tài khoản đã bị khóa");
            }
        };

        String token = authService.DangNhap(tenDangNhap, matKhau);

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/dangxuat")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(Map.of("message", authService.DangXuat(token)));
    }
}
