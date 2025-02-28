package com.example.quanlitntt_backend.utils;

import com.example.quanlitntt_backend.dto.HuynhTruongDto;
import com.example.quanlitntt_backend.dto.ThieuNhiDto;
import com.example.quanlitntt_backend.entities.HuynhTruong;
import com.example.quanlitntt_backend.entities.ThieuNhi;
import com.example.quanlitntt_backend.repositories.ThieuNhiRepository;
import org.apache.poi.ss.usermodel.*;

import java.text.SimpleDateFormat;

public class ExcelUtil {

    private static GenerateMa generateMa = new GenerateMa();

    private static ThieuNhiRepository thieuNhiRepository;

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
        ThieuNhi tn = new ThieuNhi();

        generateMa.generateMaThieuNhi(dto.getNgaySinh(), dto.getTenThanh(), dto.getHo(), dto.getTen(), thieuNhiRepository::existsById);
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
        tn.setTaiKhoan(null);
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
            return "";
        }

        if (isCellDateFormatted(cell)) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(cell.getDateCellValue());
        } else {
            return cell.toString().trim(); // Nếu không phải ngày thì trả về chuỗi nguyên bản
        }
    }
}
