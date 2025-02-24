package com.example.quanlitntt_backend.controllers;

import com.example.quanlitntt_backend.dto.LopDto;
import com.example.quanlitntt_backend.entities.Lop;
import com.example.quanlitntt_backend.serviceImplements.LopServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lop")
public class LopController {

    @Autowired
    private LopServiceImpl lopService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<?> addLop(@RequestBody List<LopDto> listLopDto) {
        try {
            List<String> errors = new ArrayList<>();
            List<String> successMessages = new ArrayList<>();

            for (LopDto lopDto : listLopDto) {
                if (lopDto.getMaLop().isEmpty() || lopDto.getTenLop().isEmpty()) {
                    errors.add("Mã lớp và tên lớp không được trống (" + lopDto.getMaLop() + ")");
                    continue;
                }

                Optional<Lop> lop = lopService.getLopByMaLop(lopDto.getMaLop());
                if (lop.isPresent()) {
                    errors.add("Đã tồn tại mã lớp " + lopDto.getMaLop());
                    continue;
                }

                lopService.addLop(lopDto);
                successMessages.add("Tạo lớp " + lopDto.getTenLop() + " thành công");
            }

            if (!errors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(successMessages);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi thêm lớp: " + e.getMessage());
        }
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<Page<Lop>> getAllLop(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            Page<Lop> lop = lopService.getAllLop(pageable);

            if (lop.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(lop);
            }

            return ResponseEntity.status(HttpStatus.OK).body(lop);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Page.empty());
        }
    }

    @GetMapping("/getByMa/{maLop}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<?> getLopByMa(@PathVariable String maLop) {
        try {
            Optional<Lop> lop = lopService.getLopByMaLop(maLop);

            if (lop.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không tìm thấy lớp có mã " + maLop);
            }

            return ResponseEntity.status(HttpStatus.OK).body(lop);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tim lớp" + maLop + "." + e.getMessage());
        }


    }

    // API xóa lớp theo mã
    @DeleteMapping("/delete/{maLop}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<?> deleteLop(@PathVariable String maLop) {
        try {

            Optional<Lop> lop = lopService.getLopByMaLop(maLop);

            if (lop.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không tìm thấy lớp có mã " + maLop);
            }

            lopService.deleteLop(maLop);
            return ResponseEntity.ok("Xóa lớp có mã " + maLop + " thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
