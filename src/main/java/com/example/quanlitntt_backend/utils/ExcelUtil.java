package com.example.quanlitntt_backend.utils;

import com.example.quanlitntt_backend.dto.HuynhTruongDto;
import com.example.quanlitntt_backend.dto.ThieuNhiDto;
import com.example.quanlitntt_backend.entities.HuynhTruong;
import com.example.quanlitntt_backend.entities.ThieuNhi;
import com.example.quanlitntt_backend.repositories.ThieuNhiRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ExcelUtil {

    private static final GenerateMa generateMa = new GenerateMa();

    private static ThieuNhiRepository thieuNhiRepository;

    @Autowired
    public void setThieuNhiRepository(ThieuNhiRepository repository) {
        thieuNhiRepository = repository;
    }

    public static HuynhTruong convertFileHuynhTruongToEntity(HuynhTruongDto dto) {
        HuynhTruong entity = new HuynhTruong();

        String maHT = generateMa.generateMaHuynhTruong(
                dto.getNgaySinh(),
                dto.getNgayBonMang(),
                dto.getSoDienThoai()
        );
        entity.setMaHT(maHT);
        entity.setTenThanh(dto.getTenThanh());
        entity.setHo(dto.getHo());
        entity.setTen(dto.getTen());
        entity.setNgaySinh(dto.getNgaySinh());
        entity.setNgayBonMang(dto.getNgayBonMang());
        entity.setGioiTinh(dto.getGioiTinh());
        entity.setHinhAnh(dto.getHinhAnh());
        entity.setSoDienThoai(dto.getSoDienThoai());
        entity.setEmail(dto.getEmail());
        entity.setCapSao(dto.getCapSao());
        entity.setHoatDong(dto.isHoatDong());
        return entity;
    }

    public static ThieuNhi convertFileThieuNhiToEntity(ThieuNhiDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("ThieuNhiDto không được null");
        }

        ThieuNhi tn = new ThieuNhi();

        tn.setTenThanh(dto.getTenThanh());
        tn.setHo(dto.getHo());
        tn.setTen(dto.getTen());
        tn.setGioiTinh(dto.getGioiTinh());
        tn.setNgaySinh(dto.getNgaySinh());
        tn.setNgayRuaToi(dto.getNgayRuaToi());
        tn.setNoiRuaToi(dto.getNoiRuaToi());
        tn.setNgayRuocLe(dto.getNgayRuocLe());
        tn.setNoiRuocLe(dto.getNoiRuocLe());
        tn.setNgayThemSuc(dto.getNgayThemSuc());
        tn.setNoiThemSuc(dto.getNoiThemSuc());
        tn.setNgayBaoDong(dto.getNgayBaoDong());
        tn.setNoiBaoDong(dto.getNoiBaoDong());
        tn.setHoTenCha(dto.getHoTenCha());
        tn.setSoDienThoaiCha(dto.getSoDienThoaiCha());
        tn.setHoTenMe(dto.getHoTenMe());
        tn.setSoDienThoaiMe(dto.getSoDienThoaiMe());
        tn.setSoDienThoaiCaNhan(dto.getSoDienThoaiCaNhan());
        tn.setTrinhDo(dto.getTrinhDo());
        tn.setTrangThai(dto.getTrangThai());
        tn.setTaiKhoan(null);

        // Kiểm tra các giá trị quan trọng trước khi tạo mã thiếu nhi
        if (dto.getNgaySinh() == null || dto.getTenThanh() == null || dto.getHo() == null || dto.getTen() == null) {
            throw new RuntimeException("Thiếu dữ liệu bắt buộc để tạo mã thiếu nhi");
        }

        // Tạo mã thiếu nhi
        String maTN = generateMa.generateMaThieuNhi(
                dto.getNgaySinh(), dto.getTenThanh(), dto.getHo(), dto.getTen());
        tn.setMaTN(maTN);

        return tn;
    }


    public static boolean isCellDateFormatted(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return false; // Không phải kiểu số thì không phải ngày
        }

        CellStyle style = cell.getCellStyle();
        if (style == null) {
            return false;
        }

        DataFormat format = cell.getSheet().getWorkbook().createDataFormat();
        String formatString = style.getDataFormatString();

        // Kiểm tra nếu định dạng chuỗi có chứa các ký tự của ngày tháng
        if (formatString != null) {
            formatString = formatString.toLowerCase();
            return formatString.contains("d") || formatString.contains("m") || formatString.contains("y");
        }

        return false;
    }

    public static String getDateCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return null;
        }

        if (isCellDateFormatted(cell)) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(cell.getDateCellValue());
        } else if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }

        return null;
    }

}
