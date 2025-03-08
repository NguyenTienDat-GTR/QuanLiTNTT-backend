package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.dto.BangDiemDto;
import com.example.quanlitntt_backend.entities.BangDiem;
import com.example.quanlitntt_backend.entities.LopNamHoc;
import com.example.quanlitntt_backend.entities.ThieuNhi;
import com.example.quanlitntt_backend.entities.compositeKey.LopNamHocKey;
import com.example.quanlitntt_backend.entities.enums.KetQuaHocTap;
import com.example.quanlitntt_backend.entities.enums.PhieuThuong;
import com.example.quanlitntt_backend.entities.enums.XepLoai;
import com.example.quanlitntt_backend.repositories.BangDiemRepository;
import com.example.quanlitntt_backend.repositories.LopNamHocRepository;
import com.example.quanlitntt_backend.repositories.ThieuNhiRepository;
import com.example.quanlitntt_backend.services.BangDiemService;
import com.example.quanlitntt_backend.utils.GenerateMa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BangDiemServiceImpl implements BangDiemService {
    @Autowired
    private BangDiemRepository bangDiemRepository;

    @Autowired
    private ThieuNhiRepository thieuNhiRepository;

    @Autowired
    private LopNamHocRepository lopNamHocRepository;

    private final GenerateMa generateMa = new GenerateMa();

    @Override
    public void taoBangDiem(String maTN, String maLop, String namHoc) {
        Optional<ThieuNhi> optionalThieuNhi = thieuNhiRepository.findById(maTN);
        if (optionalThieuNhi.isEmpty()) {
            throw new RuntimeException("Không tìm thấy Thiếu Nhi có mã: " + maTN);
        }
        ThieuNhi thieuNhi = optionalThieuNhi.get();

        Optional<LopNamHoc> optionalLopNamHoc = lopNamHocRepository.findById(new LopNamHocKey(maLop, namHoc));

        LopNamHoc lopNamHoc = optionalLopNamHoc.get();

        String maBD = generateMa.generateMaBangDiem(maTN, maLop, namHoc);

        if (bangDiemRepository.existsById(maBD)) {
            throw new RuntimeException("Thiếu nhi này đã có bảng điểm");
        }

        BangDiem bangDiem = new BangDiem();
        bangDiem.setMaBangDiem(maBD);
        bangDiem.setThieuNhi(thieuNhi);
        bangDiem.setLopNamHoc(lopNamHoc);

        bangDiemRepository.save(bangDiem);
    }

    @Override
    public void xoaBangDiem(String maTN, String maLop, String namHoc) {
        bangDiemRepository.xoaBangDiem(maTN, maLop, namHoc);
    }

    @Override
    public void xepLoaiAPlus(String maLop, String namHoc) {
        // Lấy danh sách bảng điểm của lớp trong năm học
        List<BangDiem> bangDiems = bangDiemRepository.findByLopAndNamHoc(maLop, namHoc);

        // Lấy số lượng thiếu nhi của lớp
        int soLuongTN = lopNamHocRepository.laySoLuongTNCuaLop(maLop, namHoc);

        if (bangDiems.isEmpty()) {
            return; // Không có dữ liệu
        }

        // Sắp xếp danh sách theo điểm TB HKI giảm dần
        List<BangDiem> topStudents = bangDiems.stream()
                .filter(bd -> bd.getDiemTB_HKI() != null) // Lọc bỏ học sinh chưa có điểm
                .sorted((bd1, bd2) -> Double.compare(bd2.getDiemTB_HKI(), bd1.getDiemTB_HKI())) // Sắp xếp giảm dần
                .limit(soLuongTN < 50 ? 1 : 2) // Chọn 1 hoặc 2 học sinh
                .collect(Collectors.toList());

        // Đặt xếp loại A+ cho những học sinh được chọn
        topStudents.forEach(bd -> bd.setPhieuThuong(PhieuThuong.A_PLUS));

        // Cập nhật vào cơ sở dữ liệu
        bangDiemRepository.saveAll(topStudents);
    }

    @Override
    public Optional<BangDiem> layBangDiemTheoMa(String maBD) {
        return bangDiemRepository.findById(maBD);
    }

    private BangDiem tinhDiem(BangDiem bangDiem) {

        Double diemTBHKI = null;
        Double diemTBHKII = null;
        Double diemTBNam = null;

        if (bangDiem.getDiemKT_HKI() == null &&
            bangDiem.getDiemThiGL_HKI() != null &&
            bangDiem.getDiemThiTN_HKI() != null) {

            diemTBHKI = (bangDiem.getDiemThiGL_HKI() * 2 + bangDiem.getDiemThiTN_HKI()) / 3;

        }
        if (bangDiem.getDiemKT_HKI() != null &&
            bangDiem.getDiemThiGL_HKI() != null &&
            bangDiem.getDiemThiTN_HKI() != null) {
            diemTBHKI = (((bangDiem.getDiemThiGL_HKI() * 2 + bangDiem.getDiemThiTN_HKI()) / 3) * 2 + bangDiem.getDiemKT_HKI()) / 3;
        }

        if (bangDiem.getDiemKT_HKII() == null &&
            bangDiem.getDiemThiGL_HKII() != null &&
            bangDiem.getDiemThiTN_HKII() != null) {

            diemTBHKII = (bangDiem.getDiemThiGL_HKII() * 2 + bangDiem.getDiemThiTN_HKII()) / 3;

        }

        if (bangDiem.getDiemKT_HKII() != null &&
            bangDiem.getDiemThiGL_HKII() != null &&
            bangDiem.getDiemThiTN_HKII() != null) {
            diemTBHKII = (((bangDiem.getDiemThiGL_HKII() * 2 + bangDiem.getDiemThiTN_HKII()) / 3) * 2 + bangDiem.getDiemKT_HKII()) / 3;
        }

        if (diemTBHKI != null && diemTBHKII != null)
            diemTBNam = (diemTBHKI + diemTBHKII) / 2;

        // Làm tròn điểm đến 2 chữ số thập phân
        diemTBHKI = diemTBHKI != null ? Math.round(diemTBHKI * 100.0) / 100.0 : null;
        diemTBHKII = diemTBHKII != null ? Math.round(diemTBHKII * 100.0) / 100.0 : null;
        diemTBNam = diemTBNam != null ? Math.round(diemTBNam * 100.0) / 100.0 : null;

        bangDiem.setDiemTB_HKI(diemTBHKI);
        bangDiem.setDiemTB_HKII(diemTBHKII);
        bangDiem.setDiemTBCN(diemTBNam);


        if (bangDiem.getDiemTB_HKI() == null)
            bangDiem.setPhieuThuong(null);
        else if (bangDiem.getDiemTB_HKI() >= 8)
            bangDiem.setPhieuThuong(PhieuThuong.A);
        else if (bangDiem.getDiemTB_HKI() >= 5)
            bangDiem.setPhieuThuong(PhieuThuong.B);
        else bangDiem.setPhieuThuong(PhieuThuong.C);

        if (bangDiem.getDiemTBCN() == null) {
            bangDiem.setXepLoai(null);
            bangDiem.setKetQua(null);
        } else if (bangDiem.getDiemTBCN() < 5) {
            bangDiem.setKetQua(KetQuaHocTap.OLai);
            bangDiem.setXepLoai(XepLoai.Yeu);
        } else {
            bangDiem.setKetQua(KetQuaHocTap.LenLop);
            if (bangDiem.getDiemTBCN() <= 6.5)
                bangDiem.setXepLoai(XepLoai.TrungBinh);
            if (bangDiem.getDiemTBCN() < 8)
                bangDiem.setXepLoai(XepLoai.Kha);
            else bangDiem.setXepLoai(XepLoai.Gioi);
        }

        return bangDiem;
    }

    @Override
    @Transactional
    public boolean capNhatBangDiem(BangDiemDto bangDiemDto) {
        Optional<BangDiem> bangDiemOptional = bangDiemRepository.findById(bangDiemDto.getMaBangDiem());

        if (bangDiemOptional.isEmpty()) {
            throw new RuntimeException("Không tìm thấy bảng điểm " + bangDiemDto.getMaBangDiem());
        }

        BangDiem bangDiem = bangDiemOptional.get();

        // Cập nhật các giá trị, đảm bảo null cho `diemKT_HKI` và `diemKT_HKII`
        bangDiem.setDiemKT_HKI(bangDiemDto.getDiemKT_HKI() != null && bangDiemDto.getDiemKT_HKI() >= 0 ? bangDiemDto.getDiemKT_HKI() : null);
        bangDiem.setDiemKT_HKII(bangDiemDto.getDiemKT_HKII() != null && bangDiemDto.getDiemKT_HKII() >= 0 ? bangDiemDto.getDiemKT_HKII() : null);

        // Các điểm khác mặc định là 0 nếu null
        bangDiem.setDiemThiGL_HKI(bangDiemDto.getDiemThiGL_HKI() >= 0 ? bangDiemDto.getDiemThiGL_HKI() : null);
        bangDiem.setDiemThiTN_HKI(bangDiemDto.getDiemThiTN_HKI() >= 0 ? bangDiemDto.getDiemThiTN_HKI() : null);

        bangDiem.setDiemThiGL_HKII(bangDiemDto.getDiemThiGL_HKII() >= 0 ? bangDiemDto.getDiemThiGL_HKII() : null);
        bangDiem.setDiemThiTN_HKII(bangDiemDto.getDiemThiTN_HKII() >= 0 ? bangDiemDto.getDiemThiTN_HKII() : null);

        BangDiem bangDiemDaTinh = tinhDiem(bangDiem);

        bangDiem.setPhieuThuong(bangDiemDaTinh.getPhieuThuong());
        bangDiem.setDiemTB_HKI(bangDiemDaTinh.getDiemTB_HKI());
        bangDiem.setDiemTB_HKII(bangDiemDaTinh.getDiemTB_HKII());
        bangDiem.setDiemTBCN(bangDiemDaTinh.getDiemTBCN());

        // Cập nhật thông tin khác
        bangDiem.setPhieuThuong(bangDiemDaTinh.getPhieuThuong());
        bangDiem.setXepLoai(bangDiemDaTinh.getXepLoai());
        bangDiem.setKetQua(bangDiemDaTinh.getKetQua());

        // Lưu vào cơ sở dữ liệu
        bangDiemRepository.save(bangDiem);

        return true;
    }

}
