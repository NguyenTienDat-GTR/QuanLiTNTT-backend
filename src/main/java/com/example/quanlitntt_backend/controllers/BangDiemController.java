package com.example.quanlitntt_backend.controllers;

import com.example.quanlitntt_backend.dto.BangDiemDto;
import com.example.quanlitntt_backend.dto.ThieuNhiDto;
import com.example.quanlitntt_backend.entities.BangDiem;
import com.example.quanlitntt_backend.entities.compositeKey.LopNamHocKey;
import com.example.quanlitntt_backend.serviceImplements.*;
import com.example.quanlitntt_backend.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

    @PutMapping("/update")
    @PreAuthorize("isAuthenticated() AND !hasRole('THIEUNHI')")
    public ResponseEntity<?> capNhatBangDiem(@RequestBody @Valid List<BangDiemDto> dsBangDiem,
                                             @RequestParam String maLop,
                                             @RequestParam String namHoc,
                                             @RequestParam String maNganh,
                                             @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            String role = jwtUtil.extractRole(jwtToken);
            String username = jwtUtil.extractUsername(jwtToken);
            LopNamHocKey key = new LopNamHocKey(maLop, namHoc);

            if (lopNamHocService.getLopNamHocById(key).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy lớp " + maLop + " trong năm học " + namHoc));
            }

            if (nganhService.getNganhById(maNganh).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy ngành với mã " + maNganh));
            }

            if (!lopNamHocService.kiemTraLopThuocNganhNamHoc(maLop, maNganh, namHoc)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Lớp " + maLop + " không thuộc ngành " + maNganh));
            }

            if (("TRUONGNGANH".equals(role) || "XUDOANTRUONG".equals(role) || "HUYNHTRUONG".equals(role))
                && lopNamHocService.timHTTheoLopNamHoc(username, maLop, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Chỉ được cập nhật bảng điểm thuộc lớp mình quản lý"));
            }

            if ("THUKYNGANH".equals(role) && lopNamHocService.layHTTheoNganhNamHoc(username, maNganh, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Chỉ được cập nhật bảng điểm thuộc ngành mình quản lý"));
            }

            List<String> thanhCong = new ArrayList<>();
            List<String> thatBai = new ArrayList<>();

            for (BangDiemDto dto : dsBangDiem) {
                try {
                    Optional<BangDiem> bangDiemOpt = bangDiemService.layBangDiemTheoMa(dto.getMaBangDiem());

                    if (bangDiemOpt.isEmpty()) {
                        thatBai.add("Không tìm thấy bảng điểm có mã " + dto.getMaBangDiem());
                        continue;
                    }

                    BangDiem bangDiem = bangDiemOpt.get();

                    if (!kiemTraDiemHopLe(bangDiem)) {
                        thatBai.add("Điểm của bảng điểm " + dto.getMaBangDiem() + " không hợp lệ! Giá trị phải từ 0 đến 10.");
                        continue;
                    }

                    bangDiemService.capNhatBangDiem(dto);
                    thanhCong.add("Cập nhật bảng điểm " + dto.getMaBangDiem() + " thành công");

                } catch (Exception e) {
                    thatBai.add("Lỗi khi cập nhật bảng điểm " + dto.getMaBangDiem() + ": " + e.getMessage());
                }
            }

            bangDiemService.xepLoaiAPlus(maLop, namHoc);

            Map<String, Object> response = new HashMap<>();
            response.put("success", thanhCong);
            response.put("failed", thatBai);

            if (thanhCong.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi khi cập nhật bảng điểm: " + e.getMessage()));
        }
    }

    // Hàm kiểm tra điểm hợp lệ
    private boolean kiemTraDiemHopLe(BangDiem bangDiem) {
        return (bangDiem.getDiemKT_HKI() == null || (bangDiem.getDiemKT_HKI() >= 0 && bangDiem.getDiemKT_HKI() <= 10)) &&
               (bangDiem.getDiemKT_HKII() == null || (bangDiem.getDiemKT_HKII() >= 0 && bangDiem.getDiemKT_HKII() <= 10)) &&
               (bangDiem.getDiemThiGL_HKI() == null || (bangDiem.getDiemThiGL_HKI() >= 0 && bangDiem.getDiemThiGL_HKI() <= 10)) &&
               (bangDiem.getDiemThiGL_HKII() == null || (bangDiem.getDiemThiGL_HKII() >= 0 && bangDiem.getDiemThiGL_HKII() <= 10)) &&
               (bangDiem.getDiemThiTN_HKI() == null || (bangDiem.getDiemThiTN_HKI() >= 0 && bangDiem.getDiemThiTN_HKI() <= 10)) &&
               (bangDiem.getDiemThiTN_HKII() == null || (bangDiem.getDiemThiTN_HKII() >= 0 && bangDiem.getDiemThiTN_HKII() <= 10));
    }

    // lấy bảng điểm của tất cả thiếu nhi trong lớp
    @GetMapping("/get-bangDiem-thieuNhi-lop")
    @PreAuthorize("isAuthenticated() AND !hasRole('THIEUNHI')")
    public ResponseEntity<?> layBangDiemCuaThieuNhiTrongLop(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam String maLop,
                                                            @RequestParam String namHoc,
                                                            @RequestHeader("Authorization") String token) {
        try {
            System.out.println("maLop: " + maLop);
            System.out.println("namHoc: " + namHoc);

            String jwtToken = token.substring(7);
            String role = jwtUtil.extractRole(jwtToken);
            String username = jwtUtil.extractUsername(jwtToken);
            LopNamHocKey key = new LopNamHocKey(maLop, namHoc);

            if (lopNamHocService.getLopNamHocById(key).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy lớp " + maLop + " trong năm học " + namHoc);
            }

            if (!("ADMIN".equals(role) ||
                  "XUDOANTRUONG".equals(role) ||
                  "THUKY".equals(role)) &&
                lopNamHocService.timHTTheoLopNamHoc(username, maLop, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Bạn chỉ có quyền xem bảng điểm của lớp mình quản lí");
            }

            PageRequest pageRequest = PageRequest.of(page, size);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(bangDiemService.layBangDiemCuaThieuNhiTrongLop(maLop, namHoc, pageRequest));


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy bảng điểm. " + e.getMessage());
        }
    }

}
