package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.dto.ThieuNhiDto;
import com.example.quanlitntt_backend.entities.HuynhTruong;
import com.example.quanlitntt_backend.entities.ThieuNhi;
import com.example.quanlitntt_backend.entities.enums.GioiTinh;
import com.example.quanlitntt_backend.entities.enums.TrangThaiHocVu;
import com.example.quanlitntt_backend.entities.enums.VaiTro;
import com.example.quanlitntt_backend.repositories.ThieuNhiRepository;
import com.example.quanlitntt_backend.services.ThieuNhiService;
import com.example.quanlitntt_backend.utils.*;
import com.example.quanlitntt_backend.utils.DateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Service
public class ThieuNhiServiceImpl implements ThieuNhiService {

    @Autowired
    private ThieuNhiRepository thieuNhiRepository;

    private final GenerateMa generateMa = new GenerateMa();
    private final TaiKhoanServiceImpl taiKhoanService;

    private final QRCodeUtil qrCodeUtil = new QRCodeUtil();

    @Autowired
    private WasabiService wasabiService;

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
        tn.setTaiKhoan(null);
    }


    @Override
    public ThieuNhi addThieuNhi(ThieuNhiDto thieuNhiDto) {
        ThieuNhi tn = new ThieuNhi();

        Date ngaySinh = DateUtil.convertToDateFormat(thieuNhiDto.getNgaySinh());

        String maTN = generateMa.generateMaThieuNhi(ngaySinh, thieuNhiDto.getTenThanh(), thieuNhiDto.getHo(), thieuNhiDto.getTen());

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

        taiKhoanService.getTaiKhoan(maTN).ifPresent(taiKhoan -> {
            taiKhoan.setHoatDong(false);
        });


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

        taiKhoanService.getTaiKhoan(maTN).ifPresent(taiKhoan -> {
            taiKhoan.setHoatDong(true);
        });
    }

    private String getStringCellValue(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return (cell != null && cell.getCellType() == CellType.STRING) ? cell.getStringCellValue().trim() : null;
    }

    private <T extends Enum<T>> T parseEnum(Class<T> enumType, String value) {
        return (value != null) ? Enum.valueOf(enumType, value.trim().toUpperCase()) : null;
    }

    private Date parseDate(SimpleDateFormat dateFormat, String dateStr) throws ParseException {
        return (dateStr != null) ? dateFormat.parse(dateStr) : null;
    }

    @Override
    @Async
    public CompletableFuture<List<String>> addThieuNhiFromFileExcel(MultipartFile file) {
        return CompletableFuture.supplyAsync(() -> {
            List<ThieuNhiDto> danhSachThieuNhi = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề
                    try {
                        ThieuNhiDto thieuNhiDto = new ThieuNhiDto();

                        String tenThanh = getStringCellValue(row, 0);
                        String ho = getStringCellValue(row, 1);
                        String ten = getStringCellValue(row, 2);
                        String ngaySinhStr = ExcelUtil.getDateCellValue(row, 4);

                        // Kiểm tra các trường bắt buộc
                        if (tenThanh == null || ho == null || ten == null || ngaySinhStr == null) {
                            throw new RuntimeException("Dữ liệu thiếu tại dòng " + (row.getRowNum() + 1));
                        }

                        thieuNhiDto.setTenThanh(tenThanh);
                        thieuNhiDto.setHo(ho);
                        thieuNhiDto.setTen(ten);
                        thieuNhiDto.setNgaySinh(dateFormat.parse(ngaySinhStr));

                        // Các trường không bắt buộc, nếu rỗng thì set null
                        thieuNhiDto.setGioiTinh(parseEnum(GioiTinh.class, getStringCellValue(row, 3)));
                        thieuNhiDto.setNgayRuaToi(parseDate(dateFormat, ExcelUtil.getDateCellValue(row, 5)));
                        thieuNhiDto.setNoiRuaToi(getStringCellValue(row, 6));
                        thieuNhiDto.setHoTenCha(getStringCellValue(row, 7));
                        thieuNhiDto.setHoTenMe(getStringCellValue(row, 8));
                        thieuNhiDto.setSoDienThoaiCaNhan(getStringCellValue(row, 9));
                        thieuNhiDto.setSoDienThoaiCha(getStringCellValue(row, 10));
                        thieuNhiDto.setSoDienThoaiMe(getStringCellValue(row, 11));
                        thieuNhiDto.setNgayRuocLe(parseDate(dateFormat, ExcelUtil.getDateCellValue(row, 12)));
                        thieuNhiDto.setNoiRuocLe(getStringCellValue(row, 13));
                        thieuNhiDto.setNgayThemSuc(parseDate(dateFormat, ExcelUtil.getDateCellValue(row, 14)));
                        thieuNhiDto.setNoiThemSuc(getStringCellValue(row, 15));
                        thieuNhiDto.setNgayBaoDong(parseDate(dateFormat, ExcelUtil.getDateCellValue(row, 16)));
                        thieuNhiDto.setNoiBaoDong(getStringCellValue(row, 17));

                        danhSachThieuNhi.add(thieuNhiDto);
                    } catch (Exception e) {
                        throw new RuntimeException("Lỗi xử lý dòng " + (row.getRowNum() + 1) + ": " + e.getMessage());
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
            }

            return danhSachThieuNhi;
        }).thenComposeAsync(this::saveBatchAndReturnMaAsync);
    }

    @Async
    @Transactional
    public CompletableFuture<List<String>> saveBatchAndReturnMaAsync(List<ThieuNhiDto> danhSachThieuNhi) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> danhSachMaTN = new ArrayList<>();
            int batchSize = 100;

            for (int i = 0; i < danhSachThieuNhi.size(); i += batchSize) {
                List<ThieuNhiDto> batch = danhSachThieuNhi.subList(i, Math.min(i + batchSize, danhSachThieuNhi.size()));

                List<ThieuNhi> entities = batch.stream()
                        .map(ExcelUtil::convertFileThieuNhiToEntity)
                        .toList();

                List<ThieuNhi> savedEntities = thieuNhiRepository.saveAll(entities);
                danhSachMaTN.addAll(savedEntities.stream().map(ThieuNhi::getMaTN).toList());
            }

            return danhSachMaTN;
        });
    }

    @Override
    public CompletableFuture<Void> generateAndUploadQRCode(String maTN) throws Exception {
        return CompletableFuture.runAsync(() -> {
            try {
                // Lấy thông tin Thieu nhi từ CSDL
                ThieuNhi thieuNhi = thieuNhiRepository.findById(maTN).orElseThrow(() ->
                        new RuntimeException("Không tìm thấy Thiếu nhi với mã: " + maTN));

                // Tạo nội dung QR code dưới dạng JSON
                String qrContent = String.format(
                        "{\"maHT\":\"%s\", \"tenThanh\":\"%s\", \"ho\":\"%s\", \"ten\":\"%s\"}",
                        thieuNhi.getMaTN(),
                        thieuNhi.getTenThanh(),
                        thieuNhi.getHo(),
                        thieuNhi.getTen()
                );

                // Tạo mã QR
                byte[] qrImage = qrCodeUtil.generateQRCodeImage(qrContent, 300, 300);

                // Upload ảnh QR lên Wasabi
                String qr_url = wasabiService.checkAndReplaceFile(maTN, qrImage, "qr_TN/");
                if (qr_url == null) {
                    throw new RuntimeException("Lỗi khi upload QR code cho mã " + maTN);
                }

                // Lưu URL vào CSDL
                String presignedUrl = wasabiService.generatePreSignedUrl(qr_url);
                thieuNhi.setQr_code(presignedUrl);
                thieuNhiRepository.save(thieuNhi);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi tạo QR code cho mã " + maTN + ": " + e.getMessage(), e);
            }
        });
    }

}
