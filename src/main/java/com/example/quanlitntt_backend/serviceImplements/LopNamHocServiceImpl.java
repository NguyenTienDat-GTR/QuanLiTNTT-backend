package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.entities.*;
import com.example.quanlitntt_backend.entities.compositeKey.LopNamHocKey;
import com.example.quanlitntt_backend.entities.enums.VaiTro;
import com.example.quanlitntt_backend.repositories.*;
import com.example.quanlitntt_backend.services.LopNamHocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
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

}
