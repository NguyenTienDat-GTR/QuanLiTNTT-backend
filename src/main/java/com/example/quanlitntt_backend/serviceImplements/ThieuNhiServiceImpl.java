package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.dto.ThieuNhiDto;
import com.example.quanlitntt_backend.entities.HuynhTruong;
import com.example.quanlitntt_backend.entities.ThieuNhi;
import com.example.quanlitntt_backend.entities.enums.GioiTinh;
import com.example.quanlitntt_backend.entities.enums.TrangThaiHocVu;
import com.example.quanlitntt_backend.entities.enums.TrinhDo;
import com.example.quanlitntt_backend.entities.enums.VaiTro;
import com.example.quanlitntt_backend.repositories.ThieuNhiRepository;
import com.example.quanlitntt_backend.services.ThieuNhiService;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.example.quanlitntt_backend.utils.ExcelUtil.getDateCellValue;

@Service
public class ThieuNhiServiceImpl implements ThieuNhiService {

    @Autowired
    private ThieuNhiRepository thieuNhiRepository;

    private final GenerateMa generateMa = new GenerateMa();
    private final TaiKhoanServiceImpl taiKhoanService;

    public ThieuNhiServiceImpl(TaiKhoanServiceImpl taiKhoanService) {
        this.taiKhoanService = taiKhoanService;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^0\\d{9}$");
    }


    private void setValueForThieuNhi(ThieuNhiDto thieuNhiDto, ThieuNhi tn) {
        Date ngayHienTai = new Date(); // Lấy ngày hiện tại

        Date ngaySinh = DateUtil.convertToDateFormat(thieuNhiDto.getNgaySinh());
        Date ngayRuaToi = DateUtil.convertToDateFormat(thieuNhiDto.getNgayRuaToi());
        Date ngayRuocLe = DateUtil.convertToDateFormat(thieuNhiDto.getNgayRuocLe());
        Date ngayThemSuc = DateUtil.convertToDateFormat(thieuNhiDto.getNgayThemSuc());
        Date ngayBaoDong = DateUtil.convertToDateFormat(thieuNhiDto.getNgayBaoDong());

        // Kiểm tra ngày không được lớn hơn ngày hiện tại
        if ((ngaySinh != null && ngaySinh.after(ngayHienTai)) ||
            (ngayRuaToi != null && ngayRuaToi.after(ngayHienTai)) ||
            (ngayRuocLe != null && ngayRuocLe.after(ngayHienTai)) ||
            (ngayThemSuc != null && ngayThemSuc.after(ngayHienTai)) ||
            (ngayBaoDong != null && ngayBaoDong.after(ngayHienTai))) {
            throw new IllegalArgumentException("Ngày không được lớn hơn ngày hiện tại!");
        }

        // Kiểm tra thứ tự các ngày
        if ((ngayRuaToi != null && ngaySinh != null && ngaySinh.after(ngayRuaToi)) ||
            (ngayRuocLe != null && ngaySinh != null && ngaySinh.after(ngayRuocLe)) ||
            (ngayThemSuc != null && ngaySinh != null && ngaySinh.after(ngayThemSuc)) ||
            (ngayBaoDong != null && ngaySinh != null && ngaySinh.after(ngayBaoDong))) {
            throw new IllegalArgumentException("Ngày sinh phải nhỏ hơn tất cả các ngày khác!");
        }

        if ((ngayRuaToi != null && ngayRuocLe != null && ngayRuaToi.after(ngayRuocLe)) ||
            (ngayRuaToi != null && ngayThemSuc != null && ngayRuaToi.after(ngayThemSuc)) ||
            (ngayRuaToi != null && ngayBaoDong != null && ngayRuaToi.after(ngayBaoDong))) {
            throw new IllegalArgumentException("Ngày Rửa Tội phải nhỏ hơn ngày Rước Lễ, Thêm Sức và Bao Đồng!");
        }

        if ((ngayRuocLe != null && ngayThemSuc != null && ngayRuocLe.after(ngayThemSuc)) ||
            (ngayRuocLe != null && ngayBaoDong != null && ngayRuocLe.after(ngayBaoDong))) {
            throw new IllegalArgumentException("Ngày Rước Lễ phải nhỏ hơn ngày Thêm Sức và Bao Đồng!");
        }

        if (ngayThemSuc != null && ngayBaoDong != null && ngayThemSuc.after(ngayBaoDong)) {
            throw new IllegalArgumentException("Ngày Thêm Sức phải nhỏ hơn ngày Bao Đồng!");
        }


        // Kiểm tra số điện thoại hợp lệ
        if (!(thieuNhiDto.getSoDienThoaiCha() == null) && !isValidPhoneNumber(thieuNhiDto.getSoDienThoaiCha())) {
            throw new IllegalArgumentException("Số điện thoại cha không hợp lệ! Phải có 10 số và bắt đầu bằng 0.");
        }
        if (!(thieuNhiDto.getSoDienThoaiMe() == null) && !isValidPhoneNumber(thieuNhiDto.getSoDienThoaiMe())) {
            throw new IllegalArgumentException("Số điện thoại mẹ không hợp lệ! Phải có 10 số và bắt đầu bằng 0.");
        }
        if (!(thieuNhiDto.getSoDienThoaiCaNhan() == null) && !isValidPhoneNumber(thieuNhiDto.getSoDienThoaiCaNhan())) {
            throw new IllegalArgumentException("Số điện thoại cá nhân không hợp lệ! Phải có 10 số và bắt đầu bằng 0.");
        }

        // Gán giá trị cho đối tượng ThieuNhi nếu các điều kiện hợp lệ
        tn.setTenThanh(thieuNhiDto.getTenThanh());
        tn.setHo(thieuNhiDto.getHo());
        tn.setTen(thieuNhiDto.getTen());
        tn.setGioiTinh(thieuNhiDto.getGioiTinh());
        tn.setNgaySinh(ngaySinh);
        tn.setNgayRuaToi(ngayRuaToi);
        tn.setNoiRuaToi(thieuNhiDto.getNoiRuaToi());
        tn.setNgayRuocLe(ngayRuocLe);
        tn.setNoiRuocLe(thieuNhiDto.getNoiRuocLe());
        tn.setNgayThemSuc(ngayThemSuc);
        tn.setNoiThemSuc(thieuNhiDto.getNoiThemSuc());
        tn.setNgayBaoDong(ngayBaoDong);
        tn.setNoiBaoDong(thieuNhiDto.getNoiBaoDong());
        tn.setHoTenCha(thieuNhiDto.getHoTenCha());
        tn.setSoDienThoaiCha(thieuNhiDto.getSoDienThoaiCha());
        tn.setHoTenMe(thieuNhiDto.getHoTenMe());
        tn.setSoDienThoaiMe(thieuNhiDto.getSoDienThoaiMe());
        tn.setSoDienThoaiCaNhan(thieuNhiDto.getSoDienThoaiCaNhan());
        tn.setTrinhDo(thieuNhiDto.getTrinhDo());
        tn.setTaiKhoan(null);
    }


    @Override
    public ThieuNhi addThieuNhi(ThieuNhiDto thieuNhiDto) {
        ThieuNhi tn = new ThieuNhi();

        Date ngaySinh = DateUtil.convertToDateFormat(thieuNhiDto.getNgaySinh());

        String maTN = generateMa.generateMaThieuNhi(ngaySinh, thieuNhiDto.getTenThanh(), thieuNhiDto.getHo(), thieuNhiDto.getTen(), thieuNhiRepository::existsById);

        tn.setMaTN(maTN);
        setValueForThieuNhi(thieuNhiDto, tn);
        tn.setTrangThai(TrangThaiHocVu.DANGHOC);

        taiKhoanService.taoTaiKhoan(tn.getMaTN(), VaiTro.THIEUNHI);

        return thieuNhiRepository.save(tn);
    }

    @Override
    public Page<ThieuNhiDto> getAllThieuNhis(Pageable pageable) {

        return thieuNhiRepository.getAllThieuNhi(pageable);
    }

    @Override
    public Optional<ThieuNhiDto> getThieuNhiByMa(String maTN) {
        Optional<ThieuNhiDto> thieuNhi = thieuNhiRepository.getThieuNhiByMa(maTN);
        if (!thieuNhiRepository.existsById(maTN)) {

            return Optional.empty();

        }
        return thieuNhi;
    }

    @Override
    public Page<ThieuNhiDto> getThieuNhiBySdtChaMe(String sdt, Pageable pageable) {
        String queryString = "%" + sdt + "%";

        Page<ThieuNhiDto> result = thieuNhiRepository.getThieuNhiBySdtChaMe(queryString, pageable);

        if (result.isEmpty()) {
            return Page.empty();
        }
        return result;
    }

    @Override
    public void updateThieuNhi(ThieuNhiDto thieuNhiDto) {


        if (!thieuNhiRepository.existsById(thieuNhiDto.getMaTN())) {
            throw new RuntimeException("Không tìm thấy Thiếu nhi với mã " + thieuNhiDto.getMaTN());
        }
        ThieuNhi thieuNhi = thieuNhiRepository.findById(thieuNhiDto.getMaTN()).get();
        setValueForThieuNhi(thieuNhiDto, thieuNhi);

        thieuNhiRepository.save(thieuNhi);
    }

    @Override
    public void deleteThieuNhi(String maTN) {
        Optional<ThieuNhi> tn = thieuNhiRepository.findById(maTN);

        if (!tn.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy thiếu nhi với mã " + maTN);
        } else if (tn.get().getTrangThai().equals(TrangThaiHocVu.NGHIHOC)) {
            throw new IllegalArgumentException("Thiếu nhi đã ở trạng thái nghỉ học");
        }

        tn.get().setTrangThai(TrangThaiHocVu.NGHIHOC);
        thieuNhiRepository.save(tn.get());
    }

    @Override
    public void activeThieuNhi(String maTN) {
        Optional<ThieuNhi> tn = thieuNhiRepository.findById(maTN);

        if (!tn.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy thiếu nhi với mã " + maTN);
        } else if (!tn.get().getTrangThai().equals(TrangThaiHocVu.NGHIHOC)) {
            throw new IllegalArgumentException("Thiếu nhi đã ở trạng thái Đang Học hoặc Đã Hoàn Thành");
        }

        tn.get().setTrangThai(TrangThaiHocVu.DANGHOC);
        thieuNhiRepository.save(tn.get());
    }

    private String getStringCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return "";
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    @Override
    public void addThieuNhiFromFileExcel(MultipartFile file) {
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0); // lấy sheet đầu tiên

            List<ThieuNhiDto> danhSachThieuNhi = new ArrayList<>();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; //bỏ qua dòng tiêu ề

                ThieuNhiDto thieuNhiDto = new ThieuNhiDto();

                thieuNhiDto.setTenThanh(getStringCellValue(row, 0).trim());
                thieuNhiDto.setHo(getStringCellValue(row, 1).trim());
                thieuNhiDto.setTen(getStringCellValue(row, 2).trim());

                // Xử lý giới tính
                String gioiTinhStr = getStringCellValue(row, 3).trim().toUpperCase();
                try {
                    thieuNhiDto.setGioiTinh(GioiTinh.valueOf(gioiTinhStr));
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Giới tính không hợp lệ tại dòng " + row.getRowNum());
                }

                // Xử lý ngày sinh
                String ngaySinhStr = getDateCellValue(row, 4).trim();
                if (ngaySinhStr.isEmpty()) {
                    throw new IllegalArgumentException("Ngày sinh không được để trống tại dòng " + row.getRowNum());
                }
                thieuNhiDto.setNgaySinh(dateFormat.parse(ngaySinhStr));

                String ngayRuaToi = getDateCellValue(row, 5).trim();
                thieuNhiDto.setNgayRuaToi((dateFormat.parse(ngayRuaToi)));

                thieuNhiDto.setNoiRuaToi(getStringCellValue(row, 6).trim());

                thieuNhiDto.setHoTenCha(getStringCellValue(row, 7).trim());
                thieuNhiDto.setHoTenMe(getStringCellValue(row, 8).trim());
                thieuNhiDto.setSoDienThoaiCaNhan((getStringCellValue(row, 9).trim()));
                thieuNhiDto.setSoDienThoaiCha((getStringCellValue(row, 10).trim()));
                thieuNhiDto.setSoDienThoaiMe((getStringCellValue(row, 11).trim()));

                String ngayRuocLe = getDateCellValue(row, 12).trim();
                thieuNhiDto.setNgayRuocLe((dateFormat.parse(ngayRuocLe)));

                thieuNhiDto.setNoiRuocLe(getStringCellValue(row, 13).trim());

                String ngayThemSuc = getDateCellValue(row, 14).trim();
                thieuNhiDto.setNgayThemSuc(dateFormat.parse(ngayThemSuc));

                thieuNhiDto.setNoiThemSuc(getStringCellValue(row, 15).trim());

                String ngayBaoDong = getDateCellValue(row, 16).trim();
                thieuNhiDto.setNgayBaoDong(dateFormat.parse(ngayBaoDong));

                thieuNhiDto.setNoiBaoDong(getStringCellValue(row, 17));

                String trinhDo = getStringCellValue(row, 18).trim().toUpperCase();
                try {
                    thieuNhiDto.setTrinhDo(TrinhDo.valueOf(trinhDo));
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Trình độ không hợp lệ tại dòng " + row.getRowNum());
                }

                String trangThai = getStringCellValue(row, 19).trim().toUpperCase();
                try {
                    thieuNhiDto.setTrangThai(TrangThaiHocVu.valueOf(trangThai));
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Trạng thái không hợp lệ tại dòng " + row.getRowNum());
                }

                danhSachThieuNhi.add(thieuNhiDto);
            }

            danhSachThieuNhi.forEach(thieunhi -> {
                thieuNhiRepository.save(ExcelUtil.convertFileThieuNhiToEntity(thieunhi));
            });

            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi đọc file excel " + e.getMessage());
        }
    }
}
