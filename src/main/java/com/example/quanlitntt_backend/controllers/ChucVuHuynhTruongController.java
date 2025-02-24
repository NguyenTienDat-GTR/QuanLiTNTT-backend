package com.example.quanlitntt_backend.controllers;

import com.example.quanlitntt_backend.dto.ChucVuHuynhTruongDto;
import com.example.quanlitntt_backend.entities.ChucVu;
import com.example.quanlitntt_backend.entities.ChucVuHuynhTruong;
import com.example.quanlitntt_backend.serviceImplements.ChucVuHuynhTruongServiceImpl;
import com.example.quanlitntt_backend.serviceImplements.ChucVuServiceImpl;
import com.example.quanlitntt_backend.serviceImplements.HuynhTruongServiceImpl;
import com.example.quanlitntt_backend.serviceImplements.NamHocServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/chuc-vu-huynh-truong")
public class ChucVuHuynhTruongController {

    @Autowired
    private ChucVuHuynhTruongServiceImpl chucVuHuynhTruongService;

    @Autowired
    private HuynhTruongServiceImpl huynhTruongService;

    @Autowired
    private ChucVuServiceImpl chucVuService;

    @Autowired
    private NamHocServiceImpl namHocService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'XUDOANTRUONG')")
    public ResponseEntity<?> addChucVuHuynhTruong(@RequestBody ChucVuHuynhTruongDto chucVuHuynhTruongDto) {
        try {

            if (huynhTruongService.getHuynhTruongByMa(chucVuHuynhTruongDto.getMaHT()).isEmpty()) {
                return ResponseEntity.badRequest().body("KHông tìm thấy Huynh trưởng với mã " + chucVuHuynhTruongDto.getMaHT());
            }

            if (chucVuService.getChucVuByMa(chucVuHuynhTruongDto.getMaChucVu()).isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy chức vụ với mã " + chucVuHuynhTruongDto.getMaChucVu());
            }

            Optional<ChucVu> chucVu = chucVuService.getChucVuByMa(chucVuHuynhTruongDto.getMaChucVu());

            if (namHocService.getNamHocById(chucVuHuynhTruongDto.getNamHoc()).isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy năm học " + chucVuHuynhTruongDto.getNamHoc());
            }

            if (chucVuHuynhTruongService.existsByHuynhTruongAndNamHoc(chucVuHuynhTruongDto.getMaHT(), chucVuHuynhTruongDto.getNamHoc())) {
                return ResponseEntity.badRequest().body("Huynh trưởng với mã " + chucVuHuynhTruongDto.getMaHT() + " đã có chức vụ trong năm học " + chucVuHuynhTruongDto.getNamHoc());
            }

            if (!chucVuHuynhTruongService.existsByChucVuAndNamHoc(chucVuHuynhTruongDto.getMaChucVu(), chucVuHuynhTruongDto.getNamHoc())) {
                return ResponseEntity.badRequest().body("Chức vụ " + chucVu.get().getTenChucVu() + " không có trong năm học " + chucVuHuynhTruongDto.getNamHoc());
            }

            chucVuHuynhTruongService.addChucVuHuynhTruong(chucVuHuynhTruongDto);
            return ResponseEntity.status(201).body("Thêm chức vụ huynh trưởng thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi thêm chức vụ huynh trưởng" + e.getMessage());
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG')")
    public ResponseEntity<?> deteleteChucVuHuynhTruong(@RequestBody ChucVuHuynhTruongDto chucVuHuynhTruongDto) {
        try {

            if (huynhTruongService.getHuynhTruongByMa(chucVuHuynhTruongDto.getMaHT()).isEmpty()) {
                return ResponseEntity.badRequest().body("KHông tìm thấy Huynh trưởng với mã " + chucVuHuynhTruongDto.getMaHT());
            }

            if (namHocService.getNamHocById(chucVuHuynhTruongDto.getNamHoc()).isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy năm học " + chucVuHuynhTruongDto.getNamHoc());
            }

            if (chucVuService.getChucVuByMa(chucVuHuynhTruongDto.getMaChucVu()).isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy chức vụ với mã " + chucVuHuynhTruongDto.getMaChucVu());
            }

            Optional<ChucVu> chucVu = chucVuService.getChucVuByMa(chucVuHuynhTruongDto.getMaChucVu());

            //Xóa chức vụ muốn cập nhật với huynh trưởng trong năm học hiện có
            chucVuHuynhTruongService.deleteChucVuHuynhTruongByChucVu(chucVuHuynhTruongDto.getMaChucVu(), chucVuHuynhTruongDto.getNamHoc());

            // xóa chức vụ hiện ta của huynh trưởng ần cập nhật
            chucVuHuynhTruongService.deleteChucVuHuynhTruongByHuynhTruong(chucVuHuynhTruongDto.getMaHT(), chucVuHuynhTruongDto.getNamHoc());

            // thêm chức vụ mới với huynh trưởng cần cập nhât
            chucVuHuynhTruongService.addChucVuHuynhTruong(chucVuHuynhTruongDto);

            return ResponseEntity.status(201).body("Cập nhật chức vụ huynh trưởng thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cập nhật khi xóa chức vụ huynh trưởng" + e.getMessage());
        }
    }

    @GetMapping("/getAll/{namHoc}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY','TRUONGNGANH','THUKYNGANH','HUYNHTRUONG','THIEUNHI')")
    public ResponseEntity<Page<ChucVuHuynhTruongDto>> getAllChucVuInNamHoc(@PathVariable String namHoc,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size) {
        try {

            PageRequest pageRequest = PageRequest.of(page, size);

            Page<ChucVuHuynhTruongDto> result = chucVuHuynhTruongService.getAllChucVuInNamHoc(pageRequest, namHoc);

            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
            }

            return ResponseEntity.status(HttpStatus.OK).body(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Page.empty());
        }
    }

    @GetMapping("/getByHT/{maHT}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY','TRUONGNGANH','THUKYNGANH','HUYNHTRUONG')")
    public ResponseEntity<Page<ChucVuHuynhTruongDto>> getAllChucVuByMaHT(@PathVariable String maHT,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChucVuHuynhTruongDto> result = chucVuHuynhTruongService.getAllChucVuByMaHT(pageable, maHT);

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Page.empty());
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getByChucVu/{maChucVu}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY','TRUONGNGANH','THUKYNGANH','HUYNHTRUONG')")
    public ResponseEntity<Page<ChucVuHuynhTruongDto>> getAllChucVuByChucVu(@PathVariable String maChucVu,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChucVuHuynhTruongDto> result = chucVuHuynhTruongService.getAllChucVuByChucVu(pageable, maChucVu);

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Page.empty());
        }

        return ResponseEntity.ok(result);
    }


}
