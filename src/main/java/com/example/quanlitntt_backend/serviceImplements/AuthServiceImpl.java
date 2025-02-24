package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.entities.TaiKhoan;
import com.example.quanlitntt_backend.entities.TaiKhoanDetail;
import com.example.quanlitntt_backend.services.AuthService;
import com.example.quanlitntt_backend.services.TaiKhoanDetailService;
import com.example.quanlitntt_backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;


@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private TaiKhoanDetailService taiKhoanDetailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String DangNhap(String tenDangNhap, String matKhau) {
        UserDetails userDetails = taiKhoanDetailService.loadUserByUsername(tenDangNhap);


        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(matKhau, userDetails.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác");
        }


        // Ép kiểu UserDetails về TaiKhoanDetail để lấy thông tin tài khoản đầy đủ
        TaiKhoanDetail taiKhoanDetail = (TaiKhoanDetail) userDetails;
        TaiKhoan taiKhoan = taiKhoanDetail.getTaiKhoan();

        return jwtUtil.generateToken(taiKhoan.getTenDangNhap(), taiKhoan.getVaiTro().name());
    }

    @Override
    public String DangXuat(String token) {
        // Token không lưu trên server nên chỉ cần client xóa token là xong.
        return "Đăng xuất thành công";
    }
}
