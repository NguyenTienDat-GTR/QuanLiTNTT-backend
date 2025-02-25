package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.dto.HuynhTruongDto;
import com.example.quanlitntt_backend.entities.HuynhTruong;
import com.example.quanlitntt_backend.entities.enums.CapSao;
import com.example.quanlitntt_backend.entities.enums.GioiTinh;
import com.example.quanlitntt_backend.repositories.HuynhTruongRepository;
import com.example.quanlitntt_backend.repositories.TaiKhoanRepository;
import com.example.quanlitntt_backend.services.HuynhTruongService;
import com.example.quanlitntt_backend.utils.DateUtil;
import com.example.quanlitntt_backend.utils.ExcelUtil;
import com.example.quanlitntt_backend.utils.GenerateMa;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.quanlitntt_backend.utils.ExcelUtil.getDateCellValue;

@Service
public class HuynhTruongServiceImpl implements HuynhTruongService {
    @Autowired
    private HuynhTruongRepository huynhTruongRepository;


    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    private final GenerateMa generateMa = new GenerateMa();


    @Override
    public HuynhTruong addHuynhTruong(HuynhTruongDto huynhTruongDTO) {
        HuynhTruong ht = new HuynhTruong();


        Date ngaySinhFormatted = DateUtil.convertToDateFormat(huynhTruongDTO.getNgaySinh());

        // Tạo mã HuynhTruong tự động
        String maHT = generateMa.generateMaHuynhTruong(
                ngaySinhFormatted,
                huynhTruongDTO.getNgayBonMang(),
                huynhTruongDTO.getSoDienThoai()
        );
        ht.setMaHT(maHT);

        setValueForHuynhTruong(huynhTruongDTO, ht, ngaySinhFormatted);
        ht.setHoatDong(true);

        return huynhTruongRepository.save(ht);
    }

    private void setValueForHuynhTruong(HuynhTruongDto huynhTruongDTO, HuynhTruong ht, Date ngaySinhFormatted) {
        ht.setTenThanh(huynhTruongDTO.getTenThanh());
        ht.setHo(huynhTruongDTO.getHo());
        ht.setTen(huynhTruongDTO.getTen());
        ht.setCapSao(huynhTruongDTO.getCapSao());
        ht.setSoDienThoai(huynhTruongDTO.getSoDienThoai());
        ht.setEmail(huynhTruongDTO.getEmail());
        ht.setGioiTinh(huynhTruongDTO.getGioiTinh());
        ht.setNgaySinh(ngaySinhFormatted);
        ht.setNgayBonMang(huynhTruongDTO.getNgayBonMang());
        ht.setHinhAnh(huynhTruongDTO.getHinhAnh());
    }

    @Override
    public HuynhTruong updateHuynhTruong(HuynhTruongDto huynhTruongDTO, String maHT) {

        if (huynhTruongRepository.existsById(maHT)) {
            HuynhTruong ht = huynhTruongRepository.findById(maHT).get();

            Date ngaySinhFormatted = DateUtil.convertToDateFormat(huynhTruongDTO.getNgaySinh());

            setValueForHuynhTruong(huynhTruongDTO, ht, ngaySinhFormatted);

            return huynhTruongRepository.save(ht);
        }

        return null;
    }

    @Override
    public void deleteHuynhTruong(String maHT) {
        Optional<HuynhTruong> ht = huynhTruongRepository.findById(maHT);

        if (!ht.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy Huynh Trưởng với mã " + maHT);
        } else if (!ht.get().isHoatDong()) {
            throw new IllegalArgumentException("Huynh Trưởng đã bị vô hiệu hóa");
        }

        ht.get().setHoatDong(false);
        huynhTruongRepository.save(ht.get());
    }

    @Override
    public Optional<HuynhTruong> getHuynhTruongByMa(String maHT) {
        Optional<HuynhTruong> huynhTruong = huynhTruongRepository.findById(maHT);

        if (!huynhTruong.isPresent()) {
            return Optional.empty();
        }

        return huynhTruong;

    }

    @Override
    public Page<HuynhTruong> getHuynhTruongByTen(String tenHT, Pageable pageable) {

        if (tenHT != null && !tenHT.isEmpty()) {
            return huynhTruongRepository.getHuynhTruongByTen(tenHT, pageable);
        }

        return Page.empty();
    }

    @Override
    public Page<HuynhTruong> getAllHuynhTruong(Pageable pageable, String sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return huynhTruongRepository.getAllHuynhTruong(pageable);
        }
        return huynhTruongRepository.getAllHuynhTruongSorted(sortBy, pageable);
    }

    // Hàm hỗ trợ lấy giá trị kiểu String từ ô Excel
    private String getStringCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return "";
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    @Override
    public void addHuynhTruongFromExcel(MultipartFile file) {
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            List<HuynhTruongDto> danhSachHuynhTruong = new ArrayList<>();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề

                HuynhTruongDto dto = new HuynhTruongDto();
//                dto.setMaHT(getStringCellValue(row, 0).trim());
                dto.setTenThanh(getStringCellValue(row, 0).trim());
                dto.setHo(getStringCellValue(row, 1).trim());
                dto.setTen(getStringCellValue(row, 2).trim());

                // Xử lý ngày sinh
                String ngaySinhStr = getDateCellValue(row, 3).trim();
                if (ngaySinhStr.isEmpty()) {
                    throw new IllegalArgumentException("Ngày sinh không được để trống tại dòng " + row.getRowNum());
                }
                dto.setNgaySinh(dateFormat.parse(ngaySinhStr));

                dto.setNgayBonMang(getStringCellValue(row, 4).trim());

                // Xử lý giới tính
                String gioiTinhStr = getStringCellValue(row, 5).trim().toUpperCase();
                try {
                    dto.setGioiTinh(GioiTinh.valueOf(gioiTinhStr));
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Giới tính không hợp lệ tại dòng " + row.getRowNum());
                }

                dto.setHinhAnh(getStringCellValue(row, 6).trim());
                dto.setSoDienThoai(getStringCellValue(row, 7).trim());
                dto.setEmail(getStringCellValue(row, 8).trim());

                // Xử lý cấp sao
                String capSaoStr = getStringCellValue(row, 9).trim().toUpperCase();
                try {
                    dto.setCapSao(CapSao.valueOf(capSaoStr));
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Cấp sao không hợp lệ tại dòng " + row.getRowNum());
                }

//                // Xử lý trạng thái hoạt động
//                Cell cell = row.getCell(11);
//                if (cell == null || cell.getCellType() != CellType.BOOLEAN) {
//                    throw new IllegalArgumentException("Trạng thái hoạt động không hợp lệ tại dòng " + row.getRowNum());
//                }
                dto.setHoatDong(true);

                danhSachHuynhTruong.add(dto);
            }

            // Lưu vào database
            danhSachHuynhTruong.forEach(dto -> {
                huynhTruongRepository.save(ExcelUtil.convertFileHuynhTruongToEntity(dto));
            });

            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    @Override
    public void activeHuynhTruong(String maHT) {

        Optional<HuynhTruong> ht = huynhTruongRepository.findById(maHT);

        if (!ht.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy Huynh Trưởng với mã " + maHT);
        } else if (ht.get().isHoatDong()) {
            throw new IllegalArgumentException("Huynh Trưởng đã hoạt động");
        }

        ht.get().setHoatDong(true);
        huynhTruongRepository.save(ht.get());
    }


    @Override
    public Page<HuynhTruong> getHuynhTruongByCapSao(CapSao capSao, Pageable pageable) {

        if (capSao != null && !capSao.name().isEmpty()) {
            return huynhTruongRepository.getHuynhTruongByCapSao(capSao, pageable);
        } else
            return huynhTruongRepository.getAllHuynhTruong(pageable);
    }


    @Override
    public Optional<HuynhTruong> getHuynhTruongBySoDT(String soDT) {
        Optional<HuynhTruong> huynhTruong = Optional.of(new HuynhTruong());
        try {
            huynhTruong = huynhTruongRepository.getHuynhTruongBySoDT(soDT);
            return huynhTruong;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<HuynhTruong> getHuynhTruongByEmail(String email) {
        Optional<HuynhTruong> huynhTruong = Optional.of(new HuynhTruong());
        try {
            huynhTruong = huynhTruongRepository.getHuynhTruongByEmail(email);
            return huynhTruong;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<HuynhTruong> getHuynhTruongByNamHoc(String namHoc) {
        return null;
    }


}
