package com.example.quanlitntt_backend.controllers;

import com.example.quanlitntt_backend.dto.HuynhTruongDto;
import com.example.quanlitntt_backend.entities.HuynhTruong;
import com.example.quanlitntt_backend.entities.enums.CapSao;
import com.example.quanlitntt_backend.serviceImplements.HuynhTruongServiceImpl;
import com.example.quanlitntt_backend.serviceImplements.TaiKhoanServiceImpl;
import com.example.quanlitntt_backend.utils.CloudinaryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@RequestMapping("/api/huynhtruong")
@RestController
public class HuynhTruongController {
    @Autowired
    private HuynhTruongServiceImpl huynhTruongService;

    @Autowired
    private TaiKhoanServiceImpl taiKhoanService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<?> addHuynhTruong(@RequestBody @Valid HuynhTruongDto huynhTruongDTO) {
        try {

            if (huynhTruongService.getHuynhTruongBySoDT(huynhTruongDTO.getSoDienThoai()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Số điện thoại đã tồn tại");
            }

            if (huynhTruongService.getHuynhTruongByEmail(huynhTruongDTO.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Email đã tồn tại");
            }

            HuynhTruong newHuynhTruong = huynhTruongService.addHuynhTruong(huynhTruongDTO);
            huynhTruongService.generateAndUploadQRCode(newHuynhTruong.getMaHT());
            return ResponseEntity.status(HttpStatus.CREATED).body(newHuynhTruong);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi thêm HuynhTruong: " + e.getMessage());
        }
    }

    @PutMapping("/update/{maHT}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<?> updateHuynhTruong(@RequestBody @Valid HuynhTruongDto huynhTruongDTO, @PathVariable String maHT) {
        try {
            if (huynhTruongService.getHuynhTruongByMa(maHT).isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy HuynhTruong với mã: " + maHT);
            }

            HuynhTruong updatedHuynhTruong = huynhTruongService.updateHuynhTruong(huynhTruongDTO, maHT);
            return ResponseEntity.status(HttpStatus.OK).body(updatedHuynhTruong);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật HuynhTruong: " + e.getMessage());
        }
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<Page<HuynhTruong>> getAllHuynhTruong(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size,
                                                               @RequestParam(defaultValue = "") String sortBy) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<HuynhTruong> result = huynhTruongService.getAllHuynhTruong(pageRequest, sortBy);
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
            }
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Page.empty());
        }
    }

    @PutMapping("/delete/{maHT}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG')")
    public ResponseEntity<?> deleteHuynhTruong(@PathVariable String maHT) {
        try {
            if (huynhTruongService.getHuynhTruongByMa(maHT).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy HuynhTruong với mã: " + maHT);
            }

            huynhTruongService.deleteHuynhTruong(maHT);

            if (taiKhoanService.getTaiKhoan(maHT).isPresent()) {
                taiKhoanService.deleteTaiKhoan(maHT);
            }

            return ResponseEntity.status(HttpStatus.OK).body("Vô hiệu hóa Huynh Truong thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi vô hiệu hóa HuynhTruong: " + e.getMessage());
        }
    }

    @GetMapping("/getByTen")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<Page<HuynhTruong>> getHuynhTruongByTen(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "") String tenHT) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<HuynhTruong> result = huynhTruongService.getHuynhTruongByTen(tenHT, pageRequest);
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
            }
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Page.empty());
        }
    }

    @GetMapping("/getByCapSao")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<Page<HuynhTruong>> getHuynhTruongByCapSao(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "All") String capSao) {

        try {
            PageRequest pageRequest = PageRequest.of(page, size);

            // Nếu giá trị capSao là "All", gọi hàm lấy tất cả huynh trưởng
            if ("All".equalsIgnoreCase(capSao)) {
                return ResponseEntity.ok(huynhTruongService.getAllHuynhTruong(pageRequest, ""));
            } else {

                // Chuyển đổi capSao từ String thành Enum
                CapSao capSaoEnum;
                try {
                    capSaoEnum = Arrays.stream(CapSao.values())
                            .filter(e -> e.name().equalsIgnoreCase(capSao.trim()))
                            .findFirst()
                            .orElse(null);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Page.empty());
                }

                // Gọi service lấy danh sách theo cấp sao
                Page<HuynhTruong> result = huynhTruongService.getHuynhTruongByCapSao(capSaoEnum, pageRequest);

                return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Page.empty());
        }
    }

    @PostMapping("/addFromExcel")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<?> addHuynhTruongFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            CompletableFuture<Void> future = huynhTruongService.addHuynhTruongFromExcel(file);
            future.join(); // Đợi cho đến khi tất cả dữ liệu được xử lý xong
            return ResponseEntity.status(HttpStatus.CREATED).body("Thêm Huynh Trưởng từ file Excel thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi thêm Huynh Trưởng từ file Excel: " + e.getMessage());
        }
    }


    @GetMapping("/getByMa/{maHT}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<?> getHuynhTruongByMa(@PathVariable("maHT") String maHT) {
        try {
            return huynhTruongService.getHuynhTruongByMa(maHT)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy thông tin Huynh Trưởng: " + e.getMessage());
        }
    }

    @GetMapping("/getBySoDT")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<?> getHuynhTruongBySoDT(@RequestParam String soDT) {
        try {
            return huynhTruongService.getHuynhTruongBySoDT(soDT)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy thông tin Huynh Trưởng: " + e.getMessage());
        }
    }

    @PutMapping("/active/{maHT}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG')")
    public ResponseEntity<?> activeHuynhTruong(@PathVariable String maHT) {
        try {
            huynhTruongService.activeHuynhTruong(maHT);
            return ResponseEntity.status(HttpStatus.OK).body("Kích hoạt Huynh Trưởng thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi kích hoạt Huynh Trưởng: " + e.getMessage());
        }
    }

    @PostMapping("/upload-avatar")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<?> uploadAvatarInDirectory(@RequestParam("folderPath") String folderPath) {
        try {

            // Decode URL nếu cần thiết
            String decodedFolderPath = URLDecoder.decode(folderPath, StandardCharsets.UTF_8);

            CompletableFuture<List<Map<String, String>>> uploadFuture = huynhTruongService.uploadAvatarInDirectory(decodedFolderPath, "avatar_HT");
            List<Map<String, String>> errors = uploadFuture.join();

            if (errors.isEmpty()) {
                return ResponseEntity.ok("Upload avatar thành công.");
            } else {
                return ResponseEntity.ok(errors);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi upload ảnh: " + e.getMessage());
        }
    }

    // Upload avatar từ việc chọn 1 ảnh cho 1 huynh trưởng
    @PostMapping("/upload-avatar/{maHT}")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file, @PathVariable String maHT) {
        try {
            CompletableFuture<Map<String, String>> uploadFuture = huynhTruongService.uploadAvatar(file, maHT, "avatar_HT");
            return ResponseEntity.ok(uploadFuture.join());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi upload ảnh: " + e.getMessage());
        }
    }

    // Generate and upload QR code
    @CrossOrigin(origins = "*")
    @PostMapping("/create-qrcode")
    @PreAuthorize("hasAnyRole('ADMIN','XUDOANTRUONG','THUKY')")
    public ResponseEntity<?> generateAndUploadQRCode(@RequestBody List<String> dsMaHT) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try {
            for (String maHT : dsMaHT) {
                if (huynhTruongService.getHuynhTruongByMa(maHT).isEmpty()) {
                    errors.add("Không tìm thấy HuynhTruong với mã: " + maHT);
                    continue;
                }

                CompletableFuture<Void> future = huynhTruongService.generateAndUploadQRCode(maHT)
                        .exceptionally(ex -> {
                            errors.add(ex.getMessage());
                            return null;
                        });

                futures.add(future);
            }

            // Chờ tất cả các tác vụ hoàn thành
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            if (errors.isEmpty()) {
                return ResponseEntity.ok("Tạo và upload QR code thành công");
            } else {
                return ResponseEntity.ok(errors);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tạo và upload QR code: " + e.getMessage());
        }
    }

}
