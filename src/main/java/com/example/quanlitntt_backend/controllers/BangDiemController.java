package com.example.quanlitntt_backend.controllers;

import com.example.quanlitntt_backend.dto.BangDiemDto;
import com.example.quanlitntt_backend.dto.ThieuNhiDto;
import com.example.quanlitntt_backend.entities.BangDiem;
import com.example.quanlitntt_backend.entities.LopNamHoc;
import com.example.quanlitntt_backend.entities.Nganh;
import com.example.quanlitntt_backend.entities.ThieuNhi;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

    @Autowired
    private NamHocServiceImpl namHocService;

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

            if (namHocService.getNamHocById(namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy năm học: " + namHoc);
            }

            if (!namHocService.kiemTraNamHocHienTai(namHoc))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Chỉ được tạo bảng điểm ở năm học hiện tại");

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

            // Lấy số nhân CPU để cấu hình số luồng tối ưu
            int soNhanCPU = Runtime.getRuntime().availableProcessors();
            int soLuongLuong = soNhanCPU * 2;

            // Tạo ThreadPool Executor
            ExecutorService executorService = Executors.newFixedThreadPool(soLuongLuong);

            List<CompletableFuture<Map.Entry<String, String>>> futures = dsMaTN.stream()
                    .map(maTN -> CompletableFuture.supplyAsync(() -> {
                        try {
                            Optional<ThieuNhiDto> optionalThieuNhi = thieuNhiService.getThieuNhiByMa(maTN);
                            if (optionalThieuNhi.isEmpty()) {
                                return Map.entry(maTN, "Không tìm thấy Thiếu Nhi có mã: " + maTN);
                            }

                            if (lopNamHocService.timTNTheoLopNamHoc(maTN, maLop, namHoc).isEmpty()) {
                                return Map.entry(maTN, "Thiếu Nhi " + maTN + " không có trong lớp " + maLop);
                            }

                            bangDiemService.taoBangDiem(maTN, maLop, namHoc);
                            return Map.entry(maTN, "success");

                        } catch (Exception e) {
                            return Map.entry(maTN, "Lỗi với Thiếu Nhi " + maTN + ": " + e.getMessage());
                        }
                    }, executorService))
                    .toList();

            // Chờ tất cả tác vụ hoàn thành
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            List<String> thanhCong = new ArrayList<>();
            List<String> thatBai = new ArrayList<>();

            for (CompletableFuture<Map.Entry<String, String>> future : futures) {
                Map.Entry<String, String> result = future.get();  // Lấy kết quả từ mỗi CompletableFuture
                if ("success".equals(result.getValue())) {
                    thanhCong.add("Tạo bảng điểm thành công cho: " + result.getKey());
                } else {
                    thatBai.add(result.getValue());
                }
            }

            // Tắt ExecutorService
            executorService.shutdown();

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

            if (namHocService.getNamHocById(namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy năm học: " + namHoc);
            }

            if (!namHocService.kiemTraNamHocHienTai(namHoc))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Chỉ được cập nhật bảng điểm ở năm học hiện tại");

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

            List<String> thanhCong = Collections.synchronizedList(new ArrayList<>());
            List<String> thatBai = Collections.synchronizedList(new ArrayList<>());

            // Lấy số nhân CPU để cấu hình số luồng tối ưu
            int soNhanCPU = Runtime.getRuntime().availableProcessors();
            int soLuongLuong = soNhanCPU * 2;

            // Tạo ThreadPool Executor
            ExecutorService executorService = Executors.newFixedThreadPool(soLuongLuong);

            // Danh sách các tác vụ bất đồng bộ
            List<CompletableFuture<Void>> futures = dsBangDiem.stream()
                    .map(dto -> CompletableFuture.runAsync(() -> {
                        try {
                            Optional<BangDiem> bangDiemOpt = bangDiemService.layBangDiemTheoMa(dto.getMaBangDiem());

                            if (bangDiemOpt.isEmpty()) {
                                thatBai.add("Không tìm thấy bảng điểm có mã " + dto.getMaBangDiem());
                                return;
                            }

                            BangDiem bangDiem = bangDiemOpt.get();

                            if (!kiemTraDiemHopLe(bangDiem)) {
                                thatBai.add("Điểm của bảng điểm " + dto.getMaBangDiem() + " không hợp lệ! Giá trị phải từ 0 đến 10.");
                                return;
                            }

                            bangDiemService.capNhatBangDiem(dto);
                            thanhCong.add("Cập nhật bảng điểm " + dto.getMaBangDiem() + " thành công");

                        } catch (Exception e) {
                            thatBai.add("Lỗi khi cập nhật bảng điểm " + dto.getMaBangDiem() + ": " + e.getMessage());
                        }
                    }, executorService)).toList();

            // Chờ tất cả các tác vụ hoàn thành
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // Gọi hàm xếp loại sau khi tất cả cập nhật đã hoàn tất
            bangDiemService.xepLoaiAPlus(maLop, namHoc);

            // Đóng ThreadPool
            executorService.shutdown();

            // Tạo phản hồi trả về
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
                                                            @RequestParam String maNganh,
                                                            @RequestHeader("Authorization") String token) {
        try {

            String jwtToken = token.substring(7);
            String role = jwtUtil.extractRole(jwtToken);
            String username = jwtUtil.extractUsername(jwtToken);
            LopNamHocKey key = new LopNamHocKey(maLop, namHoc);

            if (lopNamHocService.getLopNamHocById(key).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy lớp " + maLop + " trong năm học " + namHoc);
            }

            if (namHocService.getNamHocById(namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy năm học: " + namHoc);
            }

            PageRequest pageRequest = PageRequest.of(page, size);

            if (namHocService.kiemTraNamHocHienTai(namHoc)) {
                if (!("XUDOANTRUONG".equals(role) ||
                      "TRUONGNGANH".equals(role) ||
                      "HUYNHTRUONG".equals(role)) &&
                    lopNamHocService.timHTTheoLopNamHoc(username, maLop, namHoc).isEmpty()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Bạn chỉ có quyền xem bảng điểm của lớp mình quản lí");
                }

                return ResponseEntity.status(HttpStatus.OK)
                        .body(bangDiemService.layBangDiemCuaThieuNhiTrongLop(maLop, namHoc, pageRequest));
            }

            if ("THUKYNGANH".equals(role) && lopNamHocService.layHTTheoNganhNamHoc(username, maNganh, namHoc).isPresent()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(bangDiemService.layBangDiemCuaThieuNhiTrongLop(maLop, namHoc, pageRequest));
            } else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Chỉ được xem bảng điểm thuộc ngành mình quản lí");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy bảng điểm. " + e.getMessage());
        }
    }

    // lấy tất cả bảng điểm của 1 thiếu nhi
    @GetMapping("/getAll-bangDiem-thieuNhi")
    @PreAuthorize("hasAnyRole('ADMIN','THUKY','THUKYNGANH','THIEUNHI')")
    public ResponseEntity<?> layBangDiemCuaThieuNhi(@RequestParam(required = false) String maLop,
                                                    @RequestParam(required = false) String namHoc,
                                                    @RequestParam(required = false) String maTN,
                                                    @RequestParam(required = false) String maNganh,
                                                    @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            String role = jwtUtil.extractRole(jwtToken);
            String username = jwtUtil.extractUsername(jwtToken);

            if (namHocService.getNamHocById(namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy năm học: " + namHoc);
            }

            if ("THIEUNHI".equals(role)) {
                return ResponseEntity.ok(bangDiemService.layBangDiemCuaThieuNhi(username));
            }

            // Xử lý các truy vấn song song
            CompletableFuture<Optional<ThieuNhiDto>> thieuNhiFuture = CompletableFuture.supplyAsync(() ->
                    maTN != null ? thieuNhiService.getThieuNhiByMa(maTN) : Optional.empty()
            );

            CompletableFuture<Optional<LopNamHoc>> lopNamHocFuture = CompletableFuture.supplyAsync(() -> {
                if (maLop != null && namHoc != null) {
                    LopNamHocKey key = new LopNamHocKey(maLop, namHoc);
                    return lopNamHocService.getLopNamHocById(key);
                }
                return Optional.empty();
            });

            CompletableFuture<Optional<Nganh>> nganhFuture = CompletableFuture.supplyAsync(() ->
                    maNganh != null ? nganhService.getNganhById(maNganh) : Optional.empty()
            );

            CompletableFuture.allOf(thieuNhiFuture, lopNamHocFuture, nganhFuture).join();

            // Kiểm tra dữ liệu trả về từ các tác vụ song song
            if (maTN != null && thieuNhiFuture.get().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy thiếu nhi " + maTN);
            }

            if (maLop != null && namHoc != null && lopNamHocFuture.get().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy lớp " + maLop + " trong năm học " + namHoc);
            }

            if (maNganh != null && nganhFuture.get().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy ngành với mã " + maNganh);
            }

            // Kiểm tra mối liên hệ giữa maLop, maNganh và namHoc
            if (maLop != null && maNganh != null && namHoc != null &&
                !lopNamHocService.kiemTraLopThuocNganhNamHoc(maLop, maNganh, namHoc)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Lớp " + maLop + " không thuộc ngành " + maNganh);
            }

            if ("THUKYNGANH".equals(role)) {
                if (maNganh == null || namHoc == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Phải cung cấp mã ngành và năm học để truy vấn.");
                }

                if (lopNamHocService.layHTTheoNganhNamHoc(username, maNganh, namHoc).isEmpty()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Chỉ được xem bảng điểm của thiếu nhi thuộc ngành mình quản lý");
                }

                if (maTN != null && !lopNamHocService.kiemTraThieuNhiThuocNganhNamHoc(maTN, namHoc, maNganh)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Thiếu nhi " + maTN + " không thuộc ngành " + maNganh + " trong năm học " + namHoc);
                }
            }

            if (maTN == null && maLop == null && maNganh == null && namHoc == null) {
                return ResponseEntity.ok("Không có dữ liệu để xử lý.");
            }

            return ResponseEntity.ok(bangDiemService.layBangDiemCuaThieuNhi(maTN));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy bảng điểm. " + e.getMessage());
        }
    }



}
