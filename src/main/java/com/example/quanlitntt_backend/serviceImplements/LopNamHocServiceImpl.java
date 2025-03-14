package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.dto.ThieuNhiDto;
import com.example.quanlitntt_backend.entities.*;
import com.example.quanlitntt_backend.entities.compositeKey.LopNamHocKey;
import com.example.quanlitntt_backend.entities.enums.GioiTinh;
import com.example.quanlitntt_backend.entities.enums.TrangThaiHocVu;
import com.example.quanlitntt_backend.entities.enums.VaiTro;
import com.example.quanlitntt_backend.repositories.*;
import com.example.quanlitntt_backend.services.LopNamHocService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@Service
public class LopNamHocServiceImpl implements LopNamHocService {

    @Autowired
    private LopNamHocRepository lopNamHocRepository;

    @Autowired
    private LopRepository lopRepository;

    @Autowired
    private NamHocRepository namHocRepository;

    @Autowired
    private NganhRepository nganhRepository;

    @Autowired
    private HuynhTruongRepository huynhTruongRepository;

    @Autowired
    private ThieuNhiRepository thieuNhiRepository;

    @Autowired
    private Executor asyncExecutor; // Dùng Executor để chạy đa luồng

    @Autowired
    private BangDiemServiceImpl bangDiemService;

    private final TaiKhoanServiceImpl taiKhoanService;

    public LopNamHocServiceImpl(TaiKhoanServiceImpl taiKhoanService) {
        this.taiKhoanService = taiKhoanService;
    }

    @Override
    public void addLopNamNganh(List<String> maLop, String namHoc) {
        if (maLop.isEmpty() || namHoc.isEmpty()) {
            throw new RuntimeException("Năm học, mã lớp không được để trống");
        }

        NamHoc namHocEntity = namHocRepository.findById(namHoc)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy năm học " + namHoc));

        // Tạo Map để ánh xạ mã ngành với đối tượng ngành để tránh truy vấn nhiều lần
        Map<String, Nganh> nganhMap = nganhRepository.findAll().stream()
                .collect(Collectors.toMap(nganh -> nganh.getMaNganh().name(), nganh -> nganh));

        // Chạy các tác vụ thêm lớp song song
        List<CompletableFuture<Void>> futures = maLop.stream()
                .map(ma -> processLopAsync(ma, namHocEntity, nganhMap))
                .collect(Collectors.toList());

        // Đợi tất cả các tác vụ hoàn thành
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    @Async("asyncExecutor")
    public CompletableFuture<Void> processLopAsync(String ma, NamHoc namHocEntity, Map<String, Nganh> nganhMap) {
        return CompletableFuture.runAsync(() -> {
            Lop lop = lopRepository.findById(ma)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với mã " + ma));

            LopNamHocKey key = new LopNamHocKey(ma, namHocEntity.getNamHoc());

            if (lopNamHocRepository.existsById(key)) {
                throw new RuntimeException("Đã tồn tại lớp với mã " + ma + " trong năm học " + namHocEntity.getNamHoc());
            }

            LopNamHoc lopNamHoc = new LopNamHoc();
            lopNamHoc.setMaLop_NamHoc(key);
//            lopNamHoc.setNamHoc(namHocEntity);
//            lopNamHoc.setLop(lop);

            // Xác định ngành dựa vào mã lớp
            String maNganh = getMaNganhFromMaLop(ma);
            if (nganhMap.containsKey(maNganh)) {
                lopNamHoc.setNganh(nganhMap.get(maNganh));
            } else {
                throw new RuntimeException("Không tìm thấy ngành với mã " + maNganh);
            }

            lopNamHocRepository.save(lopNamHoc);
        }, asyncExecutor);
    }

    private String getMaNganhFromMaLop(String ma) {
        if (ma.startsWith("DCN")) return "CC";
        if (ma.startsWith("RL")) return "AN";
        if (ma.startsWith("TS")) return "TN";
        if (ma.startsWith("BD") || ma.startsWith("KT")) return "NS";
        throw new RuntimeException("Mã lớp không hợp lệ: " + ma);
    }

    @Override
    public Optional<LopNamHoc> getLopNamHocById(LopNamHocKey maLopNamHoc) {
        return lopNamHocRepository.findById(maLopNamHoc);
    }

    @Override
    public void addThieuNhiVaoLop(String maTN, String maLop, String namHoc) {
        Optional<ThieuNhi> thieuNhiOpt = thieuNhiRepository.findById(maTN);
        if (thieuNhiOpt.isEmpty())
            throw new RuntimeException("Không tìm thấy thiếu nhi với mã " + maTN);

        //kiểm tra thiếu nhi đã có trong lớp này chưa
        ThieuNhi thieuNhi = thieuNhiOpt.get();

        LopNamHocKey key = new LopNamHocKey(maLop, namHoc);

        Optional<LopNamHoc> lopNamHoc = lopNamHocRepository.findById(key);

        if (lopNamHoc.isEmpty())
            throw new RuntimeException("Không tìm thấy lớp " + maLop + "trong năm học " + namHoc);

        if (lopNamHocRepository.timThieuNhiTheoLopNamHoc(maTN, maLop, namHoc).isPresent()) {
            throw new RuntimeException("Thiếu nhi với mã " + maTN + " đã có trong lớp này");
        }

        if (lopNamHocRepository.timThieuNhiTrongNamHoc(maTN, namHoc).isPresent())
            throw new RuntimeException("Thiếu nhi với mã " + maTN + " đã ở lớp khác trong năm học này");

        lopNamHoc.get().getDanhSachThieuNhi().add(thieuNhi);

        lopNamHocRepository.save(lopNamHoc.get());
        bangDiemService.taoBangDiem(maTN, maLop, namHoc);

        if (taiKhoanService.getTaiKhoan(maTN).isEmpty())
            taiKhoanService.taoTaiKhoan(maTN, VaiTro.THIEUNHI);

    }

    @Override
    public void addHuynhTruongVaoLop(String maHT, LopNamHoc lopNamHoc) {
        Optional<HuynhTruong> huynhTruongOpt = huynhTruongRepository.findById(maHT);
        if (huynhTruongOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy huynh trưởng với mã: " + maHT);
        }

        // Kiểm tra huynh trưởng đã có trong lớp này chưa
        HuynhTruong huynhTruong = huynhTruongOpt.get();
        if (lopNamHocRepository.timHuynhTruongTheoLopNamHoc(maHT, lopNamHoc.getMaLop_NamHoc().getMaLop(), lopNamHoc.getMaLop_NamHoc().getNamHoc()).isPresent()) {
            throw new RuntimeException("Huynh trưởng với mã " + maHT + " đã có trong lớp này");
        }

        // Kiểm tra huynh trưởng có đang ở lớp khác trong năm học này không
        boolean daCoLopKhac = lopNamHocRepository.timHuynhTruongTrongNamHoc(maHT, lopNamHoc.getMaLop_NamHoc().getNamHoc()).isPresent();
        if (daCoLopKhac) {
            throw new RuntimeException("Huynh trưởng với mã " + maHT + " đã thuộc lớp khác trong năm học này");
        }

        // Thêm huynh trưởng vào lớp
        lopNamHoc.getDanhSachHuynhTruong().add(huynhTruong);
        lopNamHocRepository.save(lopNamHoc);
    }

    @Override
    public List<HuynhTruong> layHuynhTruongCuaLop(String maLop, String namHoc) {
        return lopNamHocRepository.layHuynhTruongCuaLopNamHoc(maLop, namHoc);
    }

    @Override
    public List<Lop> layLopTheoNganhVaNam(String maNganh, String namHoc) {
        return lopNamHocRepository.layLopTheoNganhVaNamHoc(maNganh, namHoc);
    }

    @Override
    public Optional<HuynhTruong> timHTTheoLopNamHoc(String maHT, String maLop, String namHoc) {
        return lopNamHocRepository.timHuynhTruongTheoLopNamHoc(maHT, maLop, namHoc);
    }

    //xóa huynh trưởng khỏi lớp theo maHt, maLop, namHoc
    @Override
    public boolean xoaHuynhTruongKhoiLop(String maHT, String maLop, String namHoc) {

        return lopNamHocRepository.xoaHuynhTruongKhoiLop(maHT, maLop, namHoc) > 0;

    }

    @Override
    public Optional<ThieuNhi> timTNTheoLopNamHoc(String maTN, String maLop, String namHoc) {
        return lopNamHocRepository.timThieuNhiTheoLopNamHoc(maTN, maLop, namHoc);
    }

    //chuyển thiếu nhi sang lớp khác
    @Override
    public boolean chuyenThieuNhiSangLopKhac(String maTN, String maLopCu, String maLopMoi, String namHoc) {
        return lopNamHocRepository.chuyenThieuNhiSangLopKhac(maTN, maLopCu, maLopMoi, namHoc) > 0;
    }

    @Override
    public List<String> getDanhSachNamHocCuaThieuNhi(String maThieuNhi) {
        return lopNamHocRepository.findDanhSachNamHocByMaThieuNhi(maThieuNhi);
    }

    @Override
    public boolean xoaThieuNhiKhoiLop(String maTN, String maLop, String namHoc) {
        return lopNamHocRepository.xoaThieuNhiKhoiLop(maTN, maLop, namHoc) > 0;
    }

    @Override
    public Optional<HuynhTruong> layHTTheoNganhNamHoc(String maHT, String maNganh, String namHoc) {
        return lopNamHocRepository.layHTTheoNganhNamHoc(maHT, maNganh, namHoc);
    }

    @Override
    public boolean kiemTraLopThuocNganhNamHoc(String maLop, String maNganh, String namHoc) {
        return lopNamHocRepository.existsLopInNganhAndNamHoc(maLop, maNganh, namHoc) > 0;
    }

    @Override
    public boolean kiemTraThieuNhiThuocNganhNamHoc(String maTN, String namHoc, String maNganh) {
        return lopNamHocRepository.kiemTraThieuNhiThuocNganh(maTN, namHoc, maNganh) > 0;
    }

    @Override
    public Page<ThieuNhiDto> layDSThieuNHiByLopAndNamHoc(String maLop, String namHoc, Pageable pageable) {
        Page<Object[]> result = lopNamHocRepository.layDSThieuNhiByLopAndNamHoc(maLop, namHoc, pageable);

        ExecutorService executorService = Executors.newFixedThreadPool(10); // 10 luồng song song

        List<Future<ThieuNhiDto>> futures = result.stream().map(objects -> executorService.submit(() -> {
            ThieuNhiDto thieuNhiDto = new ThieuNhiDto();
            thieuNhiDto.setMaTN((String) objects[0]);
            thieuNhiDto.setTenThanh((String) objects[1]);
            thieuNhiDto.setHo((String) objects[2]);
            thieuNhiDto.setTen((String) objects[3]);
            thieuNhiDto.setNgaySinh((Date) objects[4]);
            thieuNhiDto.setGioiTinh(GioiTinh.valueOf((String) objects[5]));
            thieuNhiDto.setNgayRuaToi((Date) objects[6]);
            thieuNhiDto.setNoiRuaToi((String) objects[7]);
            thieuNhiDto.setNgayRuocLe((Date) objects[8]);
            thieuNhiDto.setNoiRuocLe((String) objects[9]);
            thieuNhiDto.setNgayThemSuc((Date) objects[10]);
            thieuNhiDto.setNoiThemSuc((String) objects[11]);
            thieuNhiDto.setNgayBaoDong((Date) objects[12]);
            thieuNhiDto.setNoiBaoDong((String) objects[13]);
            thieuNhiDto.setHoTenCha((String) objects[14]);
            thieuNhiDto.setHoTenMe((String) objects[15]);
            thieuNhiDto.setSoDienThoaiCha((String) objects[16]);
            thieuNhiDto.setSoDienThoaiMe((String) objects[17]);
            thieuNhiDto.setSoDienThoaiCaNhan((String) objects[18]);
            thieuNhiDto.setTrangThai(TrangThaiHocVu.valueOf((String) objects[19]));
            return thieuNhiDto;
        })).toList();

        List<ThieuNhiDto> thieuNhiDtos = futures.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        executorService.shutdown();
        return new PageImpl<>(thieuNhiDtos, pageable, result.getTotalElements());
    }


    @Override
    public ByteArrayInputStream exportDSThieuNhiLopToFileExcel(String maLop, String namHoc) {
        List<Object[]> thieuNhiList = lopNamHocRepository.layDSThieuNhiByLopAndNamHocToExport(maLop, namHoc);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("DanhSachThieuNhi");

            // Tạo style cho header (tô đậm và có border)
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Thêm border cho header
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Tạo style cho dữ liệu (có border)
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            String[] headers = {"STT", "Mã TN", "Tên Thánh", "Họ", "Tên", "Ngày Sinh", "Giới Tính", "Ngày Rửa Tội", "Nơi Rửa Tội",
                    "Ngày Rước Lễ", "Nơi Rước Lễ", "Ngày Thêm Sức", "Nơi Thêm Sức", "Ngày Bao Đồng", "Nơi Bao Đồng",
                    "Họ Tên Cha", "Họ Tên Mẹ", "SĐT Cha", "SĐT Mẹ", "SĐT Cá Nhân", "Trạng Thái"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            ExecutorService executorService = Executors.newFixedThreadPool(10);
            List<Future<?>> futures = new ArrayList<>();

            int rowIdx = 1;
            for (Object[] obj : thieuNhiList) {
                final int currentRowIdx = rowIdx;
                futures.add(executorService.submit(() -> {
                    Row row = sheet.createRow(currentRowIdx);

                    // Ghi số thứ tự (STT)
                    Cell sttCell = row.createCell(0);
                    sttCell.setCellValue(currentRowIdx);
                    sttCell.setCellStyle(dataStyle);

                    // Ghi dữ liệu từ obj vào từng cột
                    for (int i = 0; i < obj.length; i++) {
                        Cell cell = row.createCell(i + 1); // Dịch cột qua phải 1 đơn vị
                        if (obj[i] != null) {
                            if (obj[i] instanceof java.sql.Timestamp) {  // Nếu là kiểu Timestamp
                                Date date = new Date(((java.sql.Timestamp) obj[i]).getTime());
                                cell.setCellValue(dateFormat.format(date));
                            } else if (obj[i] instanceof java.util.Date) {  // Nếu là kiểu Date
                                cell.setCellValue(dateFormat.format((Date) obj[i]));
                            } else {
                                cell.setCellValue(obj[i].toString());
                            }
                        }
                        cell.setCellStyle(dataStyle); // Áp dụng style có border cho ô dữ liệu
                    }
                }));
                rowIdx++;
            }

            for (Future<?> future : futures) {
                future.get();
            }

            executorService.shutdown();

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

}
