package com.example.quanlitntt_backend.controllers;

import com.example.quanlitntt_backend.entities.HuynhTruong;
import com.example.quanlitntt_backend.entities.Lop;
import com.example.quanlitntt_backend.entities.LopNamHoc;
import com.example.quanlitntt_backend.entities.NamHoc;
import com.example.quanlitntt_backend.entities.compositeKey.LopNamHocKey;
import com.example.quanlitntt_backend.serviceImplements.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/thong-tin-lop")
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

    @PostMapping("add-huynhTruong-lop")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','TRUONGNGANH')")
    public ResponseEntity<?> addHuynhTruongVaoLop(@RequestParam String maLop,
                                                  @RequestParam String namHoc,
                                                  @RequestBody List<String> danhSachMaHT) {
        try {
            if (lopService.getLopByMaLop(maLop).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lớp với mã: " + maLop);
            }

            if (namHocService.getNamHocById(namHoc).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy năm học: " + namHoc);
            }

            LopNamHocKey key = new LopNamHocKey(maLop, namHoc);
            Optional<LopNamHoc> lopNamHocOpt = lopNamHocService.getLopNamHocById(key);

            if (lopNamHocOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy lớp " + maLop + " trong năm học " + namHoc);
            }

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

    @GetMapping("get-lop-nganh")
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

}


//    @PostMapping("add-thieuNhi-lop-all")
//    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")


