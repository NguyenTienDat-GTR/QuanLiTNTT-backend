package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.dto.TaiKhoanDto;
import com.example.quanlitntt_backend.entities.TaiKhoan;
import com.example.quanlitntt_backend.entities.enums.VaiTro;
import com.example.quanlitntt_backend.repositories.TaiKhoanRepository;
import com.example.quanlitntt_backend.services.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaiKhoanServiceImpl implements TaiKhoanService {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public TaiKhoan taoTaiKhoan(String tenDangNhap, VaiTro vaiTro) {
        TaiKhoan tk = new TaiKhoan();

        tk.setTenDangNhap(tenDangNhap);

        if (vaiTro.equals(VaiTro.THIEUNHI)) {
            tk.setMatKhau(hashPassword(tenDangNhap));
            tk.setVaiTro(vaiTro);
        }

        tk.setMatKhau(hashPassword("tnttgxbung2013"));
        tk.setVaiTro(vaiTro);
        tk.setHoatDong(true);

        return taiKhoanRepository.save(tk);

    }

    @Override
    public Optional<TaiKhoan> getTaiKhoan(String tenDangNhap) {
        return taiKhoanRepository.findById(tenDangNhap);
    }

    @Override
    public TaiKhoan updateTaiKhoan(TaiKhoanDto taiKhoanDto, String tenDangNhap) {
        return null;
    }

    @Override
    public void deleteTaiKhoan(String tenDangNhap) {

        taiKhoanRepository.findById(tenDangNhap).ifPresent(taiKhoan -> {
            taiKhoan.setHoatDong(false);
            taiKhoanRepository.save(taiKhoan);
        });
    }

    @Override
    public TaiKhoan changePassword(String tenDangNhap, String matKhauCu, String matKhauMoi) {
        if (taiKhoanRepository.existsById(tenDangNhap)) {
            TaiKhoan tk = taiKhoanRepository.findById(tenDangNhap).get();
            if (passwordEncoder.matches(matKhauCu, tk.getMatKhau())) {
                tk.setMatKhau(hashPassword(matKhauMoi));
                return taiKhoanRepository.save(tk);
            }
        }
        return null;
    }

    @Override
    public TaiKhoan changeRole(String tenDangNhap, String role) {

        if (taiKhoanRepository.existsById(tenDangNhap)) {
            TaiKhoan tk = taiKhoanRepository.findById(tenDangNhap).get();
            tk.setVaiTro(VaiTro.valueOf(role));
            return taiKhoanRepository.save(tk);
        }
        return null;
    }
}
