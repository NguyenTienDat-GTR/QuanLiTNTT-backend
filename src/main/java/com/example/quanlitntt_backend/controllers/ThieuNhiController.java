package com.example.quanlitntt_backend.controllers;

import com.example.quanlitntt_backend.dto.ThieuNhiDto;
import com.example.quanlitntt_backend.serviceImplements.ThieuNhiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/api/thieuNhi")
@RestController
public class ThieuNhiController {

    @Autowired
    private ThieuNhiServiceImpl thieuNhiService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY','TRUONGNGANH','THUKYNGANH','HUYNHTRUONG')")
    public ResponseEntity<?> addThieuNhi(@RequestBody ThieuNhiDto thieuNhiDto) {
        try {
            if (thieuNhiDto.getNgaySinh() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ngày sinh không được để trống.");
            }
            if (thieuNhiDto.getTenThanh().isEmpty() || thieuNhiDto.getHo().isEmpty() || thieuNhiDto.getTen().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên thánh, họ, tên không được trống");
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(thieuNhiService.addThieuNhi(thieuNhiDto));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi thêm thiếu nhi. " + e.getMessage());
        }
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY','TRUONGNGANH','THUKYNGANH')")
    public ResponseEntity<Page<ThieuNhiDto>> getAllThieuNhi(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size);

            Page<ThieuNhiDto> result = thieuNhiService.getAllThieuNhis(pageRequest);

            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
            }
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Page.empty());
        }
    }

    @GetMapping("/getByMa/{maTN}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY','TRUONGNGANH','THUKYNGANH')")
    public ResponseEntity<?> getThieuNhiByMa(@PathVariable String maTN) {
        try {
            if (maTN.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vui lòng nhập mã để tìm");
            }
            Optional<ThieuNhiDto> tn = thieuNhiService.getThieuNhiByMa(maTN);
            if (tn.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thiếu nhi với mã " + maTN);
            }
            return ResponseEntity.ok(tn);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tìm thiếu nhi. " + e.getMessage());
        }
    }

    @GetMapping("/getBySdtChaMe")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY','TRUONGNGANH','THUKYNGANH')")
    public ResponseEntity<Page<ThieuNhiDto>> getThieuNhiBySdtChaMe(@RequestParam String soDT,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        try {

            PageRequest pageRequest = PageRequest.of(page, size);

            Page<ThieuNhiDto> thieuNhi = thieuNhiService.getThieuNhiBySdtChaMe(soDT, pageRequest);

            if (thieuNhi.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Page.empty());
            }

            return ResponseEntity.status(HttpStatus.OK).body(thieuNhi);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Page.empty());
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY','TRUONGNGANH','THUKYNGANH','HUYNHTRUONG')")
    public ResponseEntity<?> updateThieuNhi(@RequestBody ThieuNhiDto thieuNhiDto) {
        try {
            if (thieuNhiDto.getMaTN().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vui lòng nhập mã để cập nhật");
            }
            thieuNhiService.updateThieuNhi(thieuNhiDto);
            return ResponseEntity.status(HttpStatus.OK).body("Cập nhật thông tin thiếu nhi thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật thông tin thiếu nhi. " + e.getMessage());
        }
    }

    @PutMapping("/disable/{maTN}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY','TRUONGNGANH','THUKYNGANH','HUYNHTRUONG')")
    public ResponseEntity<?> deleteThieuNhi(@PathVariable String maTN) {
        try {
            if (maTN.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vui lòng nhập mã để cập nhật");
            }
            thieuNhiService.deleteThieuNhi(maTN);
            return ResponseEntity.status(HttpStatus.OK).body("Chuyển trạng thái của thiếu nhi sang NGHỈ HỌC thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi chuyển trạng thái thiếu nhi. " + e.getMessage());
        }
    }

    @PutMapping("/enable/{maTN}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY','TRUONGNGANH','THUKYNGANH','HUYNHTRUONG')")
    public ResponseEntity<?> enableThieuNhi(@PathVariable String maTN) {
        try {
            if (maTN.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vui lòng nhập mã để cập nhật");
            }
            thieuNhiService.activeThieuNhi(maTN);
            return ResponseEntity.status(HttpStatus.OK).body("Chuyển trạng thái của thiếu nhi sang ĐANG HỌC thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi chuyển trạng thái thiếu nhi. " + e.getMessage());
        }
    }

//    @PostMapping("/addFromFileExcel")
//    @PreAuthorize("isAuthenticated()")

}
