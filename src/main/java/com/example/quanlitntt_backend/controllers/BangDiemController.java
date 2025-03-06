package com.example.quanlitntt_backend.controllers;

import com.example.quanlitntt_backend.dto.ThieuNhiDto;
import com.example.quanlitntt_backend.entities.compositeKey.LopNamHocKey;
import com.example.quanlitntt_backend.serviceImplements.*;
import com.example.quanlitntt_backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/bang-diem")
public class BangDiemController {

    @Autowired
    private BangDiemServiceImpl bangDiemService;

    @Autowired
    private LopNamHocServiceImpl lopNamHocService;

    @Autowired
    private ThieuNhiServiceImpl thieuNhiService;

    @Autowired
    private NganhServiceImpl nganhService;

    @Autowired
    private HuynhTruongServiceImpl huynhTruongService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated() AND !hasRole('THIEUNHI')")
    public ResponseEntity<?> createBangDiem(@RequestBody List<String> dsMaTN,
                                            @RequestParam String maLop,
                                            @RequestParam String namHoc,
                                            @RequestParam String maNganh,
                                            @RequestHeader("Authorization") String token) {
        try {
            // Loại bỏ "Bearer " từ token
            String jwtToken = token.substring(7);

            // Lấy role từ token
            String role = jwtUtil.extractRole(jwtToken);

            // Lấy username từ token
            String username = jwtUtil.extractUsername(jwtToken);

            LopNamHocKey key = new LopNamHocKey(maLop, namHoc);

            if (lopNamHocService.getLopNamHocById(key).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp " + maLop + " trong năm học " + namHoc);
            }

            if (nganhService.getNganhById(maNganh).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy ngành với mã " + maNganh);
            }

            if (!lopNamHocService.kiemTraLopThuocNganhNamHoc(maLop, maNganh, namHoc)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lớp " + maLop + " không thuộc ngành " + maNganh);
            }

            if (("TRUONGNGANH".equals(role) || "XUDOANTRUONG".equals(role) || "HUYNHTRUONG".equals(role))
                && lopNamHocService.timHTTheoLopNamHoc(username, maLop, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ được tạo bảng điểm thuộc lớp mình quản lí");
            }

            if ("THUKYNGANH".equals(role)
                && lopNamHocService.layHTTheoNganhNamHoc(username, maNganh, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ được tạo bảng điểm thuộc ngành mình quản lí");
            }

            List<String> thanhCong = new ArrayList<>();
            List<String> thatBai = new ArrayList<>();

            for (String maTN : dsMaTN) {
                try {
                    // Kiểm tra xem Thiếu Nhi có tồn tại không
                    Optional<ThieuNhiDto> optionalThieuNhi = thieuNhiService.getThieuNhiByMa(maTN);
                    if (optionalThieuNhi.isEmpty()) {
                        thatBai.add("Không tìm thấy Thiếu Nhi có mã: " + maTN);
                        continue;
                    }

                    // Kiểm tra xem Thiếu Nhi đã có trong lớp chưa
                    if (lopNamHocService.timTNTheoLopNamHoc(maTN, maLop, namHoc).isEmpty()) {
                        thatBai.add("Thiếu Nhi " + maTN + " không có trong lớp " + maLop);
                        continue;
                    }

                    bangDiemService.taoBangDiem(maTN, maLop, namHoc);
                    thanhCong.add("Tạo bảng điểm thành công cho: " + maTN);
                } catch (RuntimeException e) {
                    thatBai.add("Lỗi với Thiếu Nhi " + maTN + ": " + e.getMessage());
                }

            }
            Map<String, Object> response = new HashMap<>();
            response.put("success", thanhCong);
            response.put("failed", thatBai);

            if (thanhCong.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tạo bảng điểm: " + e.getMessage());
        }
    }


}
