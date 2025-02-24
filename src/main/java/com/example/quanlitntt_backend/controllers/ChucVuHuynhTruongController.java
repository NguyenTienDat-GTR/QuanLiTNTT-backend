package com.example.quanlitntt_backend.controllers;

import com.example.quanlitntt_backend.dto.ChucVuHuynhTruongDto;
import com.example.quanlitntt_backend.serviceImplements.ChucVuHuynhTruongServiceImpl;
import com.example.quanlitntt_backend.serviceImplements.ChucVuServiceImpl;
import com.example.quanlitntt_backend.serviceImplements.HuynhTruongServiceImpl;
import com.example.quanlitntt_backend.serviceImplements.NamHocServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

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
            for (String maHT : chucVuHuynhTruongDto.getListMaHT()) {
                if (huynhTruongService.getHuynhTruongByMa(maHT).isEmpty()) {
                    return ResponseEntity.badRequest().body("KHông tìm thấy Huynh truưởng với mã " + maHT);
                }
            }
            if (chucVuService.getChucVuByMa(chucVuHuynhTruongDto.getMaChucVu()).isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy chức vụ với mã " + chucVuHuynhTruongDto.getMaChucVu());
            }
            if (namHocService.getNamHocById(chucVuHuynhTruongDto.getNamHoc()).isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy năm học " + chucVuHuynhTruongDto.getNamHoc());
            }

            chucVuHuynhTruongService.addChucVuHuynhTruong(chucVuHuynhTruongDto);
            return ResponseEntity.status(201).body("Thêm chức vụ huynh trưởng thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi thêm chức vụ huynh trưởng" + e.getMessage());
        }
    }

}
