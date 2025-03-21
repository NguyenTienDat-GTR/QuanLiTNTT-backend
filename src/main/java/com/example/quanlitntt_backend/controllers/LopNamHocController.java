package com.example.quanlitntt_backend.controllers;

import com.example.quanlitntt_backend.dto.ThieuNhiDto;
import com.example.quanlitntt_backend.entities.*;
import com.example.quanlitntt_backend.entities.compositeKey.LopNamHocKey;
import com.example.quanlitntt_backend.serviceImplements.*;
import com.example.quanlitntt_backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/thong-tin-lop")
public class LopNamHocController {

    @Autowired
    private LopNamHocServiceImpl lopNamHocService;

    @Autowired
    private LopServiceImpl lopService;

    @Autowired
    private NamHocServiceImpl namHocService;

    @Autowired
    private NganhServiceImpl nganhService;

    @Autowired
    private ThieuNhiServiceImpl thieuNhiService;

    @Autowired
    private HuynhTruongServiceImpl huynhTruongService;

    @Autowired
    private BangDiemServiceImpl bangDiemService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WebClient webClient;

    @PostMapping("/add-lop-nam/{namHoc}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<?> addLopNamNganh(@RequestBody List<String> maLop,
                                            @PathVariable String namHoc) {
        try {
            if (maLop.isEmpty() || namHoc.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Năm học, mã lớp không được để trống");
            }

            Optional<NamHoc> nam = namHocService.getNamHocById(namHoc);
            if (nam.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy năm học " + namHoc);

            if (!nam.get().isNamHienTai())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chỉ được thêm lớp vào năm học hiện tại");

            for (String ma : maLop) {

                Optional<Lop> lop = lopService.getLopByMaLop(ma);
                if (lop.isEmpty())
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp với mã " + ma);

                LopNamHocKey key = new LopNamHocKey(ma, namHoc);

                Optional<LopNamHoc> lopNamHoc = lopNamHocService.getLopNamHocById(key);

                if (lopNamHoc.isPresent())
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Đã tồn tại lớp với mã" + ma + " trong năm học " + namHoc);
            }

            lopNamHocService.addLopNamNganh(maLop, nam.get().getNamHoc());

            return ResponseEntity.status(HttpStatus.CREATED).body("Đã thêm các lớp được chọn cho năm học " + nam.get().getNamHoc());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi thêm lớp vào ngành theo năm học. " + e.getMessage());
        }
    }

    @PostMapping("/add-huynhTruong-lop")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','TRUONGNGANH')")
    public ResponseEntity<?> addHuynhTruongVaoLop(@RequestParam String maLop,
                                                  @RequestParam String namHoc,
                                                  @RequestBody List<String> danhSachMaHT,
                                                  @RequestParam String maNganh,
                                                  @RequestHeader("Authorization") String token) {
        try {

            // Loại bỏ "Bearer " từ token
            String jwtToken = token.substring(7);

            //lấy role từ token
            String role = jwtUtil.extractRole(jwtToken);

            //Lấy username từ token
            String username = jwtUtil.extractUsername(jwtToken);

            if (lopService.getLopByMaLop(maLop).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp với mã: " + maLop);
            }

            if (namHocService.getNamHocById(namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy năm học: " + namHoc);
            }

            if (!namHocService.kiemTraNamHocHienTai(namHoc))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chỉ được thêm huynh trưởng cho năm học hiện tại");


            LopNamHocKey key = new LopNamHocKey(maLop, namHoc);

            if (lopNamHocService.getLopNamHocById(key).isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp " + maLop + " trong năm học " + namHoc);

            if (nganhService.getNganhById(maNganh).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy ngành với mã " + maNganh);
            }

            if (!lopNamHocService.kiemTraLopThuocNganhNamHoc(maLop, maNganh, namHoc)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lớp " + maLop + " không thuộc ngành " + maNganh);
            }

            if ("TRUONGNGANH".equals(role) && lopNamHocService.layHTTheoNganhNamHoc(username, maNganh, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ được thêm huynh trưởng vào lớp trong ngành");
            }


            Optional<LopNamHoc> lopNamHocOpt = lopNamHocService.getLopNamHocById(key);

            LopNamHoc lopNamHoc = lopNamHocOpt.get();

            List<String> addedHuynhTruongs = new ArrayList<>();
            List<String> failedHuynhTruongs = new ArrayList<>();

            for (String maHT : danhSachMaHT) {
                try {
                    lopNamHocService.addHuynhTruongVaoLop(maHT, lopNamHoc);
                    addedHuynhTruongs.add(maHT);
                } catch (RuntimeException e) {
                    failedHuynhTruongs.add(maHT + ": " + e.getMessage());
                }
            }

            if (!failedHuynhTruongs.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Một số huynh trưởng không được thêm: " + String.join(", ", failedHuynhTruongs));
            }

            return ResponseEntity.ok("Đã thêm huynh trưởng vào lớp thành công: " + String.join(", ", addedHuynhTruongs));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi thêm huynh trưởng vào lớp: " + e.getMessage());
        }
    }

    @GetMapping("/get-HuynhTruong-lop")
    @PreAuthorize("isAuthenticated() and !hasRole('THIEUNHI')")
    public ResponseEntity<?> layHuynhTruongCuaLop(@RequestParam String maLop,
                                                  @RequestParam String namHoc) {
        try {
            if (lopService.getLopByMaLop(maLop).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp với mã: " + maLop);
            }

            if (namHocService.getNamHocById(namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy năm học: " + namHoc);
            }

            if (!namHocService.kiemTraNamHocHienTai(namHoc))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chỉ được lấy huynh trưởng của năm học hiện tại");

            LopNamHocKey key = new LopNamHocKey(maLop, namHoc);

            if (lopNamHocService.getLopNamHocById(key).isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp " + maLop + " trong năm học " + namHoc);


            if (lopNamHocService.layHuynhTruongCuaLop(maLop, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Không tìm thấy huynh trưởng nào của lớp: " + maLop);
            }

            return ResponseEntity.status(HttpStatus.OK).body(lopNamHocService.layHuynhTruongCuaLop(maLop, namHoc));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy huynh trưởng của lớp: " + e.getMessage());
        }
    }

    @GetMapping("/get-lop-nganh")
    @PreAuthorize("isAuthenticated() and !hasRole('THIEUNHI')")
    public ResponseEntity<?> layLopTheoNganhVaNam(@RequestParam String maNganh,
                                                  @RequestParam String namHoc) {
        try {
            if (nganhService.getNganhById(maNganh).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy ngành với mã: " + maNganh);
            }

            if (namHocService.getNamHocById(namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy năm học: " + namHoc);
            }

            if (lopNamHocService.layLopTheoNganhVaNam(maNganh, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp nào. ");
            }

            return ResponseEntity.status(HttpStatus.OK).body(lopNamHocService.layLopTheoNganhVaNam(maNganh, namHoc));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy lớp của ngành: " + e.getMessage());
        }
    }

    @PostMapping("/add-thieuNhi-lop-fromFile")
    @PreAuthorize("isAuthenticated() AND !hasRole('THIEUNHI')")
    public ResponseEntity<?> addThieuNhiVaoLopFromFile(@RequestParam("file") MultipartFile file,
                                                       @RequestParam String maLop,
                                                       @RequestParam String namHoc,
                                                       @RequestParam String maNganh,
                                                       @RequestHeader("Authorization") String token) {

        try {

            // Loại bỏ "Bearer " từ token
            String jwtToken = token.substring(7);

            //lấy role từ token
            String role = jwtUtil.extractRole(jwtToken);

            //Lấy username từ token
            String username = jwtUtil.extractUsername(jwtToken);

            if (lopService.getLopByMaLop(maLop).isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp " + maLop);

            if (namHocService.getNamHocById(namHoc).isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy năm học " + namHoc);

            if (!namHocService.kiemTraNamHocHienTai(namHoc))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chỉ được thêm thiếu nhi cho năm học hiện tại");

            LopNamHocKey key = new LopNamHocKey(maLop, namHoc);

            if (lopNamHocService.getLopNamHocById(key).isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp " + maLop + " trong năm học " + namHoc);

            if (nganhService.getNganhById(maNganh).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy ngành với mã " + maNganh);
            }

            if (!lopNamHocService.kiemTraLopThuocNganhNamHoc(maLop, maNganh, namHoc)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lớp " + maLop + " không thuộc ngành " + maNganh);
            }

            if ("HUYNHTRUONG".equals(role) && lopNamHocService.timHTTheoLopNamHoc(username, maLop, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ được thêm Thiếu Nhi vào lớp mình quản lí");
            }

            if (("TRUONGNGANH".equals(role) || "THUKYNGANH".equals(role)) && lopNamHocService.layHTTheoNganhNamHoc(username, maNganh, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ được thêm thiếu nhi vào lớp trong ngành");
            }

            CompletableFuture<List<String>> future = thieuNhiService.addThieuNhiFromFileExcel(file);
            List<String> maThieuNhiList = future.get();


            List<String> addedThieuNhis = new ArrayList<>();
            List<String> failedThieuNhis = new ArrayList<>();

            for (String maTN : maThieuNhiList) {
                try {
                    lopNamHocService.addThieuNhiVaoLop(maTN, maLop, namHoc);
                    thieuNhiService.generateAndUploadQRCode(maTN);
                    addedThieuNhis.add(maTN);
                } catch (RuntimeException e) {
                    failedThieuNhis.add(maTN + ": " + e.getMessage());
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body("Đã thêm thiếu nhi vào lớp thành công: " + String.join(", ", addedThieuNhis));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi thêm thiếu nhi " + e.getMessage());
        }
    }

    // thêm thiếu nhi vào lớp bằng cách nhập tay
    @PostMapping("/add-thieuNhi-lop")
    @PreAuthorize("isAuthenticated() AND !hasRole('THIEUNHI')")
    public ResponseEntity<?> addThieuNhiVaoLop(@RequestBody List<ThieuNhiDto> thieuNhiDtos,
                                               @RequestParam String maLop,
                                               @RequestParam String namHoc,
                                               @RequestParam String maNganh,
                                               @RequestHeader("Authorization") String token) {
        try {

            // Loại bỏ "Bearer " từ token
            String jwtToken = token.substring(7);

            //lấy role từ token
            String role = jwtUtil.extractRole(jwtToken);

            //Lấy username từ token
            String username = jwtUtil.extractUsername(jwtToken);

            if (lopService.getLopByMaLop(maLop).isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp " + maLop);

            if (namHocService.getNamHocById(namHoc).isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy năm học " + namHoc);

            if (!namHocService.kiemTraNamHocHienTai(namHoc))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chỉ được thêm thiếu nhi cho năm học hiện tại");

            LopNamHocKey key = new LopNamHocKey(maLop, namHoc);

            if (lopNamHocService.getLopNamHocById(key).isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp " + maLop + " trong năm học " + namHoc);

            if (nganhService.getNganhById(maNganh).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy ngành với mã " + maNganh);
            }

            if (!lopNamHocService.kiemTraLopThuocNganhNamHoc(maLop, maNganh, namHoc)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lớp " + maLop + " không thuộc ngành " + maNganh);
            }

            if ("HUYNHTRUONG".equals(role) && lopNamHocService.timHTTheoLopNamHoc(username, maLop, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ được thêm Thiếu Nhi vào lớp mình quản lí");
            }

            if (("TRUONGNGANH".equals(role) || "THUKYNGANH".equals(role)) && lopNamHocService.layHTTheoNganhNamHoc(username, maNganh, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ được thêm thiếu nhi vào lớp trong ngành");
            }

            List<String> addedThieuNhis = new ArrayList<>();
            List<String> failedThieuNhis = new ArrayList<>();

            for (ThieuNhiDto dto : thieuNhiDtos) {
                try {
                    ThieuNhi tn = thieuNhiService.addThieuNhi(dto);
                    lopNamHocService.addThieuNhiVaoLop(tn.getMaTN(), maLop, namHoc);
                    thieuNhiService.generateAndUploadQRCode(tn.getMaTN());
                    addedThieuNhis.add(tn.getMaTN());
                } catch (RuntimeException e) {
                    failedThieuNhis.add("Lỗi " + e.getMessage());
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body("Đã thêm thiếu nhi vào lớp thành công: " + String.join(", ", addedThieuNhis));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi thêm thiếu nhi " + e.getMessage());
        }
    }

    // thêm thiếu nhi vào lớp theo mã thiếu nhi
    @PostMapping("/add-thieuNhi-lop-byMaTN")
    @PreAuthorize("isAuthenticated() AND !hasRole('THIEUNHI')")
    public ResponseEntity<?> addThieuNhiVaoLopByMaTN(@RequestBody List<String> dsMaTN,
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

            // Kiểm tra sự tồn tại của lớp, năm học, ngành
            if (lopService.getLopByMaLop(maLop).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp " + maLop);
            }

            if (namHocService.getNamHocById(namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy năm học " + namHoc);
            }

            if (!namHocService.kiemTraNamHocHienTai(namHoc))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chỉ được thêm thiếu nhi cho năm học hiện tại");

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

            // Kiểm tra quyền
            if ("HUYNHTRUONG".equals(role) && lopNamHocService.timHTTheoLopNamHoc(username, maLop, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ được thêm Thiếu Nhi vào lớp mình quản lí");
            }

            if (("TRUONGNGANH".equals(role) || "THUKYNGANH".equals(role))
                && lopNamHocService.layHTTheoNganhNamHoc(username, maNganh, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ được thêm thiếu nhi vào lớp trong ngành");
            }

            // Danh sách kết quả
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
                    if (lopNamHocService.timTNTheoLopNamHoc(maTN, maLop, namHoc).isPresent()) {
                        thatBai.add("Thiếu Nhi " + maTN + " đã có trong lớp " + maLop);
                        continue;
                    }

                    // Thêm vào lớp
                    lopNamHocService.addThieuNhiVaoLop(maTN, maLop, namHoc);

                    thanhCong.add("Thêm vào lớp thành công mã: " + maTN);
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi thêm Thiếu Nhi: " + e.getMessage());
        }
    }


    // xóa Ht khỏi lớp theo maHT, maLop, namHoc
    @DeleteMapping("/delete-HuynhTruong-lop")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','TRUONGNGANH')")
    public ResponseEntity<?> xoaHuynhTruongKhoiLop(@RequestBody List<String> danhSachMaHT,
                                                   @RequestParam String maLop,
                                                   @RequestParam String namHoc,
                                                   @RequestParam String maNganh,
                                                   @RequestHeader("Authorization") String token) {
        try {

            // Loại bỏ "Bearer " từ token
            String jwtToken = token.substring(7);

            //lấy role từ token
            String role = jwtUtil.extractRole(jwtToken);

            //Lấy username từ token
            String username = jwtUtil.extractUsername(jwtToken);

            // Kiểm tra lớp có tồn tại không
            if (lopService.getLopByMaLop(maLop).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy lớp với mã: " + maLop);
            }

            // Kiểm tra năm học có tồn tại không
            if (namHocService.getNamHocById(namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy năm học: " + namHoc);
            }

            if (!namHocService.kiemTraNamHocHienTai(namHoc))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                        body("Chỉ được xóa huynh trưởng ra khỏi lớp ở năm học hiện tại");

            LopNamHocKey key = new LopNamHocKey(maLop, namHoc);

            if (lopNamHocService.getLopNamHocById(key).isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp " + maLop + " trong năm học " + namHoc);

            if (nganhService.getNganhById(maNganh).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy ngành với mã " + maNganh);
            }

            if (!lopNamHocService.kiemTraLopThuocNganhNamHoc(maLop, maNganh, namHoc)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lớp " + maLop + " không thuộc ngành " + maNganh);
            }

            if ("TRUONGNGANH".equals(role) && lopNamHocService.layHTTheoNganhNamHoc(username, maNganh, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ được xóa huynh trưởng trong ngành");
            }

            // Danh sách kết quả
            List<String> thanhCong = new ArrayList<>();
            List<String> thatBai = new ArrayList<>();

            for (String maHT : danhSachMaHT) {
                try {
                    // Kiểm tra Huynh Trưởng có tồn tại không
                    if (huynhTruongService.getHuynhTruongByMa(maHT).isEmpty()) {
                        thatBai.add("Không tìm thấy Huynh Trưởng có mã: " + maHT);
                        continue;
                    }

                    // Kiểm tra Huynh Trưởng có trong lớp không
                    Optional<HuynhTruong> huynhTruong = lopNamHocService.timHTTheoLopNamHoc(maHT, maLop, namHoc);
                    if (huynhTruong.isEmpty()) {
                        thatBai.add("Huynh Trưởng mã " + maHT + " không thuộc lớp " + maLop + " năm học " + namHoc);
                        continue;
                    }

                    // Xóa Huynh Trưởng khỏi lớp
                    boolean ketQua = lopNamHocService.xoaHuynhTruongKhoiLop(maHT, maLop, namHoc);
                    if (ketQua) {
                        thanhCong.add("Đã xóa Huynh Trưởng mã " + maHT);
                    } else {
                        thatBai.add("Lỗi khi xóa Huynh Trưởng mã " + maHT);
                    }

                } catch (Exception e) {
                    thatBai.add("Lỗi khi xử lý Huynh Trưởng mã " + maHT + ": " + e.getMessage());
                }
            }

            // Kết quả trả về
            Map<String, Object> response = new HashMap<>();
            response.put("success", thanhCong);
            response.put("failed", thatBai);

            if (thanhCong.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa Huynh Trưởng khỏi lớp: " + e.getMessage());
        }
    }

    //chuyển TN sang lớp khác
    @PutMapping("/chuyen-thieu-nhi-lop")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<?> chuyenThieuNhiSangLopKhac(@RequestBody List<String> dsMaTN,
                                                       @RequestParam String maLopCu,
                                                       @RequestParam String maLopMoi,
                                                       @RequestParam String namHoc) {
        try {

            if (!namHocService.kiemTraNamHocHienTai(namHoc))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chỉ được chuyển lớp ở năm học hiện tại");

            // Kiểm tra lớp cũ và lớp mới có tồn tại không
            if (lopService.getLopByMaLop(maLopCu).isEmpty() || lopService.getLopByMaLop(maLopMoi).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp học");
            }

            List<String> thanhCong = new ArrayList<>();
            List<String> thatBai = new ArrayList<>();

            for (String maThieuNhi : dsMaTN) {
                try {
                    // Lấy danh sách năm học của Thiếu Nhi
                    List<String> danhSachNamHoc = lopNamHocService.getDanhSachNamHocCuaThieuNhi(maThieuNhi);

                    if (danhSachNamHoc.isEmpty()) {
                        thatBai.add("Thiếu Nhi mã " + maThieuNhi + " không có dữ liệu năm học.");
                        continue;
                    }

                    // Lấy năm học mới nhất của Thiếu Nhi
                    String namHocHienTai = danhSachNamHoc.get(0); // Vì danh sách đã sắp xếp giảm dần

                    // Kiểm tra nếu năm học mới nhất không trùng với namHoc yêu cầu
                    if (!namHocHienTai.equals(namHoc)) {
                        thatBai.add("Thiếu Nhi mã " + maThieuNhi + " thuộc năm học " + namHocHienTai +
                                    ", không thể chuyển sang lớp của năm học " + namHoc);
                        continue;
                    }

                    // Kiểm tra Thiếu Nhi có trong lớp cũ không
                    Optional<ThieuNhi> tn = lopNamHocService.timTNTheoLopNamHoc(maThieuNhi, maLopCu, namHoc);
                    if (tn.isEmpty()) {
                        thatBai.add("Thiếu Nhi mã " + maThieuNhi + " không thuộc lớp " + maLopCu + " năm học " + namHoc);
                        continue;
                    }

                    // Thực hiện chuyển lớp
                    boolean result = lopNamHocService.chuyenThieuNhiSangLopKhac(maThieuNhi, maLopCu, maLopMoi, namHoc);
                    if (result) {
                        thanhCong.add("Chuyển Thiếu Nhi mã " + maThieuNhi + " thành công.");
                        bangDiemService.xoaBangDiem(maThieuNhi, maLopCu, namHoc);
                        bangDiemService.taoBangDiem(maThieuNhi, maLopMoi, namHoc);
                    } else {
                        thatBai.add("Không thể chuyển Thiếu Nhi mã " + maThieuNhi);
                    }

                } catch (Exception e) {
                    thatBai.add("Lỗi khi xử lý Thiếu Nhi mã " + maThieuNhi + ": " + e.getMessage());
                }
            }

            // Tạo phản hồi trả về
            Map<String, Object> response = new HashMap<>();
            response.put("success", thanhCong);
            response.put("failed", thatBai);

            if (thanhCong.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.get("failed"));
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi chuyển Thiếu Nhi: " + e.getMessage());
        }
    }

    @DeleteMapping("delete-ThieuNhi-lop")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY','TRUONGNGANH','THUKYNGANH','HUYNHTRUONG')")
    public ResponseEntity<?> xoaThieuNhiKhoiLop(@RequestBody List<String> dsMaTN,
                                                @RequestParam String maLop,
                                                @RequestParam String namHoc,
                                                @RequestParam String maNganh,
                                                @RequestHeader("Authorization") String token) {
        try {

            // Loại bỏ "Bearer " từ token
            String jwtToken = token.substring(7);

            //lấy role từ token
            String role = jwtUtil.extractRole(jwtToken);

            //Lấy username từ token
            String username = jwtUtil.extractUsername(jwtToken);

            if (lopService.getLopByMaLop(maLop).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp với mã: " + maLop);
            }

            if (namHocService.getNamHocById(namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy năm học: " + namHoc);
            }

            if (!namHocService.kiemTraNamHocHienTai(namHoc))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Chỉ được xóa thiếu nhi ra khỏi lớp ở năm học hiện tại");

            LopNamHocKey key = new LopNamHocKey(maLop, namHoc);

            if (lopNamHocService.getLopNamHocById(key).isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp " + maLop + " trong năm học " + namHoc);


            if (nganhService.getNganhById(maNganh).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy ngành với mã " + maNganh);
            }

            if (!lopNamHocService.kiemTraLopThuocNganhNamHoc(maLop, maNganh, namHoc)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lớp " + maLop + " không thuộc ngành " + maNganh);
            }

            if ("HUYNHTRUONG".equals(role) && lopNamHocService.timHTTheoLopNamHoc(username, maLop, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ được xóa Thiếu Nhi thuộc lớp mình quản lí");
            }

            if (("TRUONGNGANH".equals(role) || "THUKYNGANH".equals(role)) && lopNamHocService.layHTTheoNganhNamHoc(username, maNganh, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ được xóa thiếu nhi thuộc lớp trong ngành");
            }

            List<String> thanhCong = new ArrayList<>();
            List<String> thatBai = new ArrayList<>();

            for (String maTN : dsMaTN) {
                try {

                    if (thieuNhiService.getThieuNhiByMa(maTN).isEmpty()) {
                        thatBai.add("Thiếu Nhi mã " + maTN + " không tồn tại");
                        continue;
                    }

                    Optional<ThieuNhi> tn = lopNamHocService.timTNTheoLopNamHoc(maTN, maLop, namHoc);
                    if (tn.isEmpty()) {
                        thatBai.add("Thiếu Nhi mã " + maTN + " không thuộc lớp " + maLop + " năm học " + namHoc);
                        continue;
                    }

                    boolean result = lopNamHocService.xoaThieuNhiKhoiLop(maTN, maLop, namHoc);

                    if (result) {
                        bangDiemService.xoaBangDiem(maTN, maLop, namHoc);
                        thanhCong.add("Xóa Thiếu Nhi mã " + maTN + " thành công.");
                    } else {
                        thatBai.add("Không thể xóa Thiếu Nhi mã " + maTN);
                    }
                } catch (RuntimeException e) {
                    thatBai.add("Lỗi khi xóa Thiếu Nhi mã " + maTN + ": " + e.getMessage());
                }
            }
            // Tạo phản hồi trả về
            Map<String, Object> response = new HashMap<>();
            response.put("success", thanhCong);
            response.put("failed", thatBai);

            if (thanhCong.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xóa thiếu nhi khỏi lớp " + e.getMessage());
        }
    }

    // lấy danh sách thiếu nhi theo lớp và năm học
    @GetMapping("/get-danhSachThieuNhi")
    @PreAuthorize("isAuthenticated() AND !hasRole('THIEUNHI')")
    public ResponseEntity<?> layDSThieuNHiByLopAndNamHoc(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam String maLop,
                                                         @RequestParam String namHoc,
                                                         @RequestParam(required = false) String maNganh,
                                                         @RequestHeader("Authorization") String token) {
        try {

            String jwtToken = token.substring(7);
            String role = jwtUtil.extractRole(jwtToken);
            String username = jwtUtil.extractUsername(jwtToken);

            if (lopService.getLopByMaLop(maLop).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp với mã: " + maLop);
            }

            if (namHocService.getNamHocById(namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy năm học: " + namHoc);
            }

            if (!namHocService.kiemTraNamHocHienTai(namHoc) && "HUYNHTRUONG".equals(role))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Chỉ được lấy danh sách thiếu nhi của lớp ở năm học hiện tại");

            LopNamHocKey key = new LopNamHocKey(maLop, namHoc);

            if (lopNamHocService.getLopNamHocById(key).isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp " + maLop + " trong năm học " + namHoc);


            if (maNganh != null) {
                if (nganhService.getNganhById(maNganh).isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy ngành với mã " + maNganh);
                }

                if (!lopNamHocService.kiemTraLopThuocNganhNamHoc(maLop, maNganh, namHoc)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lớp " + maLop + " không thuộc ngành " + maNganh);
                }

                if (("TRUONGNGANH".equals(role) || "THUKYNGANH".equals(role))
                    && lopNamHocService.layHTTheoNganhNamHoc(username, maNganh, namHoc).isEmpty()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ được lấy danh sách thiếu nhi thuộc lớp trong ngành");
                }
            }

            if ("HUYNHTRUONG".equals(role) && lopNamHocService.timHTTheoLopNamHoc(username, maLop, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ được lấy danh sách Thiếu Nhi thuộc lớp mình quản lí");
            }

            PageRequest pageRequest = PageRequest.of(page, size);

            return ResponseEntity.status(HttpStatus.OK).body(lopNamHocService.layDSThieuNHiByLopAndNamHoc(maLop, namHoc, pageRequest));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy danh sách thiếu nhi " + e.getMessage());
        }
    }

    @GetMapping("/export-danhSachThieuNhi")
    @PreAuthorize("isAuthenticated() AND !hasRole('THIEUNHI')")
    public ResponseEntity<?> exportDSThieuNhiLopToFileExcel(@RequestParam String maLop,
                                                            @RequestParam String namHoc,
                                                            @RequestParam(required = false) String maNganh,
                                                            @RequestHeader("Authorization") String token) {

        try {

            String jwtToken = token.substring(7);
            String role = jwtUtil.extractRole(jwtToken);
            String username = jwtUtil.extractUsername(jwtToken);

            if (lopService.getLopByMaLop(maLop).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp với mã: " + maLop);
            }

            if (namHocService.getNamHocById(namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy năm học: " + namHoc);
            }

            if (!namHocService.kiemTraNamHocHienTai(namHoc) && "HUYNHTRUONG".equals(role))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Chỉ được lấy danh sách thiếu nhi của lớp ở năm học hiện tại");

            LopNamHocKey key = new LopNamHocKey(maLop, namHoc);

            if (lopNamHocService.getLopNamHocById(key).isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp " + maLop + " trong năm học " + namHoc);


            if (maNganh != null) {
                if (nganhService.getNganhById(maNganh).isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy ngành với mã " + maNganh);
                }

                if (!lopNamHocService.kiemTraLopThuocNganhNamHoc(maLop, maNganh, namHoc)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lớp " + maLop + " không thuộc ngành " + maNganh);
                }

                if (("TRUONGNGANH".equals(role) || "THUKYNGANH".equals(role))
                    && lopNamHocService.layHTTheoNganhNamHoc(username, maNganh, namHoc).isEmpty()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ được lấy danh sách thiếu nhi thuộc lớp trong ngành");
                }
            }

            if ("HUYNHTRUONG".equals(role) && lopNamHocService.timHTTheoLopNamHoc(username, maLop, namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ được lấy danh sách Thiếu Nhi thuộc lớp mình quản lí");
            }


            ByteArrayInputStream in = lopNamHocService.exportDSThieuNhiLopToFileExcel(maLop, namHoc);

            if (in == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=ds_thieuNhi_" + maLop + "_" + namHoc + ".xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(in));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy danh sách thiếu nhi " + e.getMessage());
        }
    }

}




