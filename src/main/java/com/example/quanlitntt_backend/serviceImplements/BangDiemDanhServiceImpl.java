package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.entities.BangDiem;
import com.example.quanlitntt_backend.entities.BangDiemDanh;
import com.example.quanlitntt_backend.entities.LopNamHoc;
import com.example.quanlitntt_backend.entities.ThieuNhi;
import com.example.quanlitntt_backend.entities.compositeKey.LopNamHocKey;
import com.example.quanlitntt_backend.repositories.BangDiemDanhRepository;
import com.example.quanlitntt_backend.repositories.LopNamHocRepository;
import com.example.quanlitntt_backend.repositories.ThieuNhiRepository;
import com.example.quanlitntt_backend.services.BangDiemDanhService;
import com.example.quanlitntt_backend.utils.GenerateMa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BangDiemDanhServiceImpl implements BangDiemDanhService {

    @Autowired
    private BangDiemDanhRepository bangDiemDanhRepository;

    private final GenerateMa generateMa = new GenerateMa();

    @Autowired
    private ThieuNhiRepository thieuNhiRepository;

    @Autowired
    private LopNamHocRepository lopNamHocRepository;

    @Override
    public void taoBangDiemDanhLop(String maTN, String maLop, String namHoc) {
        Optional<ThieuNhi> optionalThieuNhi = thieuNhiRepository.findById(maTN);
        if (optionalThieuNhi.isEmpty()) {
            throw new RuntimeException("Không tìm thấy Thiếu Nhi có mã: " + maTN);
        }
        ThieuNhi thieuNhi = optionalThieuNhi.get();

        Optional<LopNamHoc> optionalLopNamHoc = lopNamHocRepository.findById(new LopNamHocKey(maLop, namHoc));

        LopNamHoc lopNamHoc = optionalLopNamHoc.get();

        String maBDD = generateMa.generateMaBangDiemDanh(maTN, maLop, namHoc);

        if (bangDiemDanhRepository.existsById(maBDD)) {
            throw new RuntimeException("Thiếu nhi này đã có bảng điểm danh");
        }

        BangDiemDanh bangDiemDanh = new BangDiemDanh();
        bangDiemDanh.setMaBangDiemDanh(maBDD);
        bangDiemDanh.setThieuNhi(thieuNhi);
        bangDiemDanh.setLopNamHoc(lopNamHoc);

        bangDiemDanhRepository.save(bangDiemDanh);
    }

    @Override
    public void xoaBangDiemDanh(String maBDD) {
        bangDiemDanhRepository.deleteById(maBDD);
    }

    @Override
    public Optional<BangDiemDanh> layBangDiemDanh(String maBDD) {
        return bangDiemDanhRepository.findById(maBDD);
    }
}
