package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.dto.TaiKhoanDto;
import com.example.quanlitntt_backend.dto.ThongTinTaiKhoanDto;
import com.example.quanlitntt_backend.entities.TaiKhoan;
import com.example.quanlitntt_backend.entities.enums.VaiTro;
import com.example.quanlitntt_backend.repositories.TaiKhoanRepository;
import com.example.quanlitntt_backend.services.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private Page<ThongTinTaiKhoanDto> convertObjectToThongTinTaiKhoanDto(Page<Object[]> objects, Pageable pageable) {
        // Tạo một Thread Pool để xử lý đa luồng
        ExecutorService executorService = Executors.newFixedThreadPool(10);  // 10 luồng

        try {
            // Dùng Stream API để chuyển đổi dữ liệu với CompletableFuture
            List<CompletableFuture<ThongTinTaiKhoanDto>> futures = objects.getContent().stream()
                    .map(obj -> CompletableFuture.supplyAsync(() -> {
                        ThongTinTaiKhoanDto dto = new ThongTinTaiKhoanDto();

                        dto.setTenDangNhap((String) obj[0]);
                        dto.setVaiTro(VaiTro.valueOf((String) obj[1]));
                        dto.setHoatDong((Boolean) obj[2]);
                        dto.setTenThanh((String) obj[3]);
                        dto.setHo((String) obj[4]);
                        dto.setTen((String) obj[5]);

                        return dto;
                    }, executorService))
                    .toList();

            // Thu thập kết quả từ các CompletableFuture
            List<ThongTinTaiKhoanDto> dtos = futures.stream()
                    .map(CompletableFuture::join)  // Chờ tất cả các nhiệm vụ hoàn thành
                    .toList();

            // Tạo trang mới từ danh sách đã chuyển đổi
            return new PageImpl<>(dtos, pageable, objects.getTotalElements());
        } finally {
            executorService.shutdown();  // Đảm bảo tắt ExecutorService sau khi sử dụng
        }
    }


    @Override
    public Page<ThongTinTaiKhoanDto> getAllTaiKhoanHT(Pageable pageable) {
        Page<Object[]> result = taiKhoanRepository.getAllTaiKhoanHT(pageable);
        return convertObjectToThongTinTaiKhoanDto(result, pageable);
    }

    @Override
    public Page<ThongTinTaiKhoanDto> getAllTaiKhoanTN(Pageable pageable) {
        Page<Object[]> result = taiKhoanRepository.getAllTaiKhoanTN(pageable);
        return convertObjectToThongTinTaiKhoanDto(result, pageable);
    }

}
