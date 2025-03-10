package com.example.quanlitntt_backend.controllers;

import com.example.quanlitntt_backend.dto.NamHocDto;
import com.example.quanlitntt_backend.serviceImplements.NamHocServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RequestMapping("/api/nam-hoc")
@RestController
public class NamHocController {

    @Autowired
    private NamHocServiceImpl namHocService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    @Transactional
    public ResponseEntity<?> addNamHoc(@RequestBody @Valid NamHocDto namHoc) {

        try {
            if (namHocService.getNamHocById(namHoc.getNamHoc()).isPresent()) {
                return ResponseEntity.status(400).body("Năm học đã tồn tại");
            }

            if (namHoc.getNgayBatDau() == null) {
                return ResponseEntity.status(400).body("Ngày bắt đầu không được để trống");
            }

            if (namHoc.getNgayBatDau().before(new Date())) {
                return ResponseEntity.status(400).body("Ngày bắt đầu phải là ngày hiện tại hoặc trong tương lai");
            }


            return ResponseEntity.status(201).body(namHocService.addNamHoc(namHoc));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi thêm năm học: " + e.getMessage());
        }
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY','HUYNHTRUONG','TRUONGNGANH','THUKYNGANH')")
    public ResponseEntity<?> getAllNamHoc() {
        try {
            return ResponseEntity.status(200).body(namHocService.getAllNamHoc());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi lấy danh sách năm học: " + e.getMessage());
        }
    }
}
