package com.example.quanlitntt_backend.controllers;

import com.example.quanlitntt_backend.dto.ChucVuDto;
import com.example.quanlitntt_backend.serviceImplements.ChucVuServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/chucvu")
@RestController
public class ChucVuController {
    @Autowired
    private ChucVuServiceImpl chucVuService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG')")
    public ResponseEntity<?> addChucVu(@RequestBody @Valid ChucVuDto chucVuDto) {
        try {

            if (chucVuDto.getTenChucVu().isEmpty()) {
                return ResponseEntity.status(HttpStatusCode.valueOf(400)).body("Tên chức vụ không được để trống");
            }

            if (chucVuService.getChucVuByTen(chucVuDto.getTenChucVu()).isPresent()) {
                return ResponseEntity.status(HttpStatusCode.valueOf(400)).body("Chức vụ đã tồn tại");
            }

            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(chucVuService.addChucVu(chucVuDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{maChucVu}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG')")
    public ResponseEntity<?> deleteChucVu(@PathVariable String maChucVu) {
        try {
            chucVuService.deleteChucVu(maChucVu);
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body("Xóa chức vụ thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(e.getMessage());
        }
    }

}
