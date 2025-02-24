package com.example.quanlitntt_backend.controllers;

import com.example.quanlitntt_backend.dto.NganhDto;
import com.example.quanlitntt_backend.entities.Nganh;
import com.example.quanlitntt_backend.serviceImplements.NganhServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/nganh")
public class NganhController {

    @Autowired
    private NganhServiceImpl nganhService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG')")
    public ResponseEntity<?> addNganh(@RequestBody NganhDto nganhDto) {
        try {
            if (nganhService.getNganhById(nganhDto.getMaNganh()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Đã tồn tại ngành với mã ngành " + nganhDto.getMaNganh());
            }
            nganhService.addNganh(nganhDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Thêm ngành thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi thêm ngành: " + e.getMessage());
        }
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY','HUYNHTRUONG','TRUONGNGANH','THUKYNGANH')")
    public ResponseEntity<List<Nganh>> getAllNganh() {
        List<Nganh> nganhList = nganhService.getAllNganh();
        return ResponseEntity.ok(nganhList);
    }

    @GetMapping("/getByMaNganh/{maNganh}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<?> getNganhById(@PathVariable String maNganh) {
        Optional<Nganh> nganh = nganhService.getNganhById(maNganh);
        if (nganh.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy ngành với mã ngành " + maNganh);
        }
        return ResponseEntity.status(HttpStatus.OK).body(nganh);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<?> updateNganh(@RequestBody NganhDto nganhDto) {
        try {
            if (nganhDto.getMaNganh().isEmpty() || nganhDto.getTenNganh().isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã ngành và tên ngành không được để trống");
            }

            Nganh updatedNganh = nganhService.updateNganh(nganhDto);
            return ResponseEntity.ok(updatedNganh);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{maNganh}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG')")
    public ResponseEntity<?> deleteNganh(@PathVariable String maNganh) {
        try {
            nganhService.deleteNganh(maNganh);
            return ResponseEntity.ok("Xóa ngành thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

