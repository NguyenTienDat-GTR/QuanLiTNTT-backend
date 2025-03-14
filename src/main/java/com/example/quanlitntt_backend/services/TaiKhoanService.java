package com.example.quanlitntt_backend.services;

import com.example.quanlitntt_backend.dto.TaiKhoanDto;
import com.example.quanlitntt_backend.dto.ThongTinTaiKhoanDto;
import com.example.quanlitntt_backend.entities.TaiKhoan;
import com.example.quanlitntt_backend.entities.enums.VaiTro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TaiKhoanService {
    public TaiKhoan taoTaiKhoan(String tenDangNhap, VaiTro vaiTro);

    public Optional<TaiKhoan> getTaiKhoan(String tenDangNhap);

    public TaiKhoan updateTaiKhoan(TaiKhoanDto taiKhoanDto, String tenDangNhap);

    public void deleteTaiKhoan(String tenDangNhap);

    public TaiKhoan changePassword(String tenDangNhap, String matKhauCu, String matKhauMoi);

    public TaiKhoan changeRole(String tenDangNhap, String role);

    public Page<ThongTinTaiKhoanDto> getAllTaiKhoanHT(Pageable pageable);

    public Page<ThongTinTaiKhoanDto> getAllTaiKhoanTN(Pageable pageable);
}
