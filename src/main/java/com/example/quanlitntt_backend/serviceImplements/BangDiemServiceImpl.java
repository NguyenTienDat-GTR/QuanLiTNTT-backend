package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.dto.BangDiemDto;
import com.example.quanlitntt_backend.dto.BangDiemNamHocDto;
import com.example.quanlitntt_backend.dto.ThieuNhiBangDiemDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Override
    public Page<ThieuNhiBangDiemDto> layBangDiemCuaThieuNhiTrongLop(String maLop, String namHoc, Pageable pageable) {
        Page<Object[]> resultPage = bangDiemRepository.layBangDiemCuaThieuNhiTrongLop(maLop, namHoc, pageable);

        List<ThieuNhiBangDiemDto> dtos = resultPage.getContent().stream().map(row -> {
            ThieuNhiBangDiemDto dto = new ThieuNhiBangDiemDto();

            dto.setMaTN((String) row[0]);
            dto.setTenThanh((String) row[1]);
            dto.setHo((String) row[2]);
            dto.setTen((String) row[3]);
            dto.setMaBangDiem((String) row[4]);
            dto.setDiemKT_HKI((Double) row[5]);
            dto.setDiemThiGL_HKI((Double) row[6]);
            dto.setDiemThiTN_HKI((Double) row[7]);
            dto.setDiemTB_HKI((Double) row[8]);
            dto.setPhieuThuong(row[9] != null ? PhieuThuong.valueOf((String) row[9]) : null);
            dto.setDiemKT_HKII((Double) row[10]);
            dto.setDiemThiGL_HKII((Double) row[11]);
            dto.setDiemThiTN_HKII((Double) row[12]);
            dto.setDiemTB_HKII((Double) row[13]);
            dto.setDiemTBCN((Double) row[14]);
            dto.setXepLoai(row[15] != null ? XepLoai.valueOf((String) row[15]) : null);
            dto.setKetQua(row[16] != null ? KetQuaHocTap.valueOf((String) row[16]) : null);

            return dto;
        }).toList();

        return new PageImpl<>(dtos, pageable, resultPage.getTotalElements());
    }

    @Override
    public List<BangDiemNamHocDto> layBangDiemCuaThieuNhi(String maTN) {
        return convertToBangDiemNamHocDto(bangDiemRepository.layBangDiemCuaThieuNhi(maTN));
    }

    public List<BangDiemNamHocDto> convertToBangDiemNamHocDto(List<Object[]> results) {
        List<BangDiemNamHocDto> bangDiemDtos = new ArrayList<>();

        for (Object[] row : results) {
            BangDiemNamHocDto bangDiemDto = new BangDiemNamHocDto();

            bangDiemDto.setMaBangDiem((String) row[0]);
            bangDiemDto.setNamHoc((String) row[1]);
            bangDiemDto.setDiemKT_HKI((Double) row[2]);
            bangDiemDto.setDiemThiGL_HKI((Double) row[3]);
            bangDiemDto.setDiemThiTN_HKI((Double) row[4]);
            bangDiemDto.setDiemTB_HKI((Double) row[5]);
            bangDiemDto.setPhieuThuong(PhieuThuong.valueOf((String) row[6]));
            bangDiemDto.setDiemKT_HKII((Double) row[7]);
            bangDiemDto.setDiemThiGL_HKII((Double) row[8]);
            bangDiemDto.setDiemThiTN_HKII((Double) row[9]);
            bangDiemDto.setDiemTB_HKII((Double) row[10]);
            bangDiemDto.setDiemTBCN((Double) row[11]);
            bangDiemDto.setXepLoai(XepLoai.valueOf((String) row[12]));
            bangDiemDto.setKetQua(KetQuaHocTap.valueOf((String) row[13]));

            bangDiemDtos.add(bangDiemDto);
        }
        return bangDiemDtos;
    }


    private BangDiem tinhDiem(BangDiem bangDiem) {
        Double diemTBHKI = tinhDiemTBHK(bangDiem.getDiemKT_HKI(), bangDiem.getDiemThiGL_HKI(), bangDiem.getDiemThiTN_HKI());
        Double diemTBHKII = tinhDiemTBHK(bangDiem.getDiemKT_HKII(), bangDiem.getDiemThiGL_HKII(), bangDiem.getDiemThiTN_HKII());
        Double diemTBNam = tinhDiemTBNam(diemTBHKI, diemTBHKII);

        bangDiem.setDiemTB_HKI(diemTBHKI);
        bangDiem.setDiemTB_HKII(diemTBHKII);
        bangDiem.setDiemTBCN(diemTBNam);

        bangDiem.setPhieuThuong(tinhPhieuThuong(diemTBHKI));
        tinhKetQuaVaXepLoai(bangDiem);

        return bangDiem;
    }

    private Double tinhDiemTBHK(Double diemKT, Double diemThiGL, Double diemThiTN) {
        if (diemThiGL == null || diemThiTN == null) return null;

        double diemTBHK = (diemThiGL * 2 + diemThiTN) / 3;

        if (diemKT != null) {
            diemTBHK = (diemTBHK * 2 + diemKT) / 3;
        }

        // Làm tròn đến 2 chữ số thập phân
        return Math.round(diemTBHK * 100.0) / 100.0;
    }

    private Double tinhDiemTBNam(Double diemTBHKI, Double diemTBHKII) {
        if (diemTBHKI == null || diemTBHKII == null) return null;

        double diemTBNam = (diemTBHKI + diemTBHKII) / 2;
        return Math.round(diemTBNam * 100.0) / 100.0;
    }

    private PhieuThuong tinhPhieuThuong(Double diemTBHKI) {
        if (diemTBHKI == null) return null;

        if (diemTBHKI >= 8) return PhieuThuong.A;
        if (diemTBHKI >= 5) return PhieuThuong.B;
        return PhieuThuong.C;
    }

    private void tinhKetQuaVaXepLoai(BangDiem bangDiem) {
        Double diemTBCN = bangDiem.getDiemTBCN();

        if (diemTBCN == null) {
            bangDiem.setXepLoai(null);
            bangDiem.setKetQua(null);
            return;
        }

        if (diemTBCN < 5) {
            bangDiem.setKetQua(KetQuaHocTap.OLai);
            bangDiem.setXepLoai(XepLoai.Yeu);
        } else {
            bangDiem.setKetQua(KetQuaHocTap.LenLop);

            if (diemTBCN <= 6.5) bangDiem.setXepLoai(XepLoai.TrungBinh);
            else if (diemTBCN < 8) bangDiem.setXepLoai(XepLoai.Kha);
            else bangDiem.setXepLoai(XepLoai.Gioi);
        }
    }


    @Override
    @Transactional
    public boolean capNhatBangDiem(BangDiemDto bangDiemDto) {
        Optional<BangDiem> bangDiemOptional = bangDiemRepository.findById(bangDiemDto.getMaBangDiem());

        if (bangDiemOptional.isEmpty()) {
            throw new RuntimeException("Không tìm thấy bảng điểm " + bangDiemDto.getMaBangDiem());
        }

        BangDiem bangDiem = bangDiemOptional.get();

        //điểm học kì I
        bangDiem.setDiemKT_HKI(bangDiemDto.getDiemKT_HKI() != null && bangDiemDto.getDiemKT_HKI() >= 0 ? bangDiemDto.getDiemKT_HKI() : null);
        bangDiem.setDiemThiGL_HKI(bangDiemDto.getDiemThiGL_HKI() >= 0 ? bangDiemDto.getDiemThiGL_HKI() : null);
        bangDiem.setDiemThiTN_HKI(bangDiemDto.getDiemThiTN_HKI() >= 0 ? bangDiemDto.getDiemThiTN_HKI() : null);

        if (bangDiem.getDiemTB_HKI() == null) {
            throw new RuntimeException("Phải có điểm trung bình học kì I mới được phép nhập điểm học kì II");
        }

        // điểm học kì II
        bangDiem.setDiemKT_HKII(bangDiemDto.getDiemKT_HKII() != null && bangDiemDto.getDiemKT_HKII() >= 0 ? bangDiemDto.getDiemKT_HKII() : null);
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
