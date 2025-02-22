package com.example.quanlitntt_backend.services;

public interface AuthService {
    String DangNhap(String tenDangNhap, String matKhau);
    String DangXuat(String token);
}
