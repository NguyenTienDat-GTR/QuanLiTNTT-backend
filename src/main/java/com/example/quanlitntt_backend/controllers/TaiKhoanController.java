package com.example.quanlitntt_backend.controllers;

import com.example.quanlitntt_backend.dto.HuynhTruongDto;
import com.example.quanlitntt_backend.entities.enums.VaiTro;
import com.example.quanlitntt_backend.serviceImplements.TaiKhoanServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/taikhoan")
public class TaiKhoanController {

    @Autowired
    private TaiKhoanServiceImpl taiKhoanService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    //form data
    public ResponseEntity<?> taoTaiKhoan(@ModelAttribute HuynhTruongDto huynhTruongDTO, @ModelAttribute VaiTro vaiTro) {
        try {

            if (taiKhoanService.getTaiKhoan(huynhTruongDTO.getMaHT()).isPresent()) {
                return ResponseEntity.badRequest().body("Tài khoản đã tồn tại");
            }

            return ResponseEntity.ok(taiKhoanService.taoTaiKhoan(huynhTruongDTO.getMaHT(), vaiTro));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi tạo tài khoản: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{tenDangNhap}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<?> xoaTaiKhoan(@PathVariable String tenDangNhap) {
        try {
            taiKhoanService.deleteTaiKhoan(tenDangNhap);
            return ResponseEntity.status(HttpStatus.OK).body("Xóa tài khoản thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xóa tài khoản: " + e.getMessage());
        }
    }

    @PutMapping("/changepassword/{tenDangNhap}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY','HUYNHTRUONG','THIEUNHI','THUKYNGANH','TRUONGNGANH')")
    public ResponseEntity<?> doiMatKhau(
            @PathVariable String tenDangNhap,
            @RequestParam String matKhauCu,
            @RequestParam String matKhauMoi) {
        try {
            // Lấy username từ token
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

            // Kiểm tra xem tên đăng nhập từ token có khớp với PathVariable không
            if (!currentUsername.equals(tenDangNhap)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không thể đổi mật khẩu cho tài khoản khác");
            }

            // Gọi service đổi mật khẩu
            if (taiKhoanService.changePassword(tenDangNhap, matKhauCu, matKhauMoi) == null) {
                return ResponseEntity.badRequest().body("Mật khẩu cũ không đúng");
            }
            return ResponseEntity.ok("Đổi mật khẩu thành công");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi đổi mật khẩu: " + e.getMessage());
        }
    }

    @PutMapping("/changerole/{tenDangNhap}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG')")
    public ResponseEntity<?> doiVaiTro(@PathVariable String tenDangNhap, @RequestParam VaiTro vaiTro) {
        try {

            if (taiKhoanService.changeRole(tenDangNhap, String.valueOf(vaiTro)) == null) {
                return ResponseEntity.badRequest().body("Đổi vai trò không thành công");
            }

            return ResponseEntity.status(HttpStatus.OK).body("Đổi vai trò thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi đổi vai trò: " + e.getMessage());
        }
    }


}
