package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.entities.BangDiem;
import com.example.quanlitntt_backend.entities.LopNamHoc;
import com.example.quanlitntt_backend.entities.ThieuNhi;
import com.example.quanlitntt_backend.entities.compositeKey.LopNamHocKey;
import com.example.quanlitntt_backend.repositories.BangDiemRepository;
import com.example.quanlitntt_backend.repositories.LopNamHocRepository;
import com.example.quanlitntt_backend.repositories.ThieuNhiRepository;
import com.example.quanlitntt_backend.services.BangDiemService;
import com.example.quanlitntt_backend.utils.GenerateMa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    public BangDiem taoBangDiem(String maTN, String maLop, String namHoc) {
        Optional<ThieuNhi> optionalThieuNhi = thieuNhiRepository.findById(maTN);
        if (optionalThieuNhi.isEmpty()) {
            throw new RuntimeException("Không tìm thấy Thiếu Nhi có mã: " + maTN);
        }
        ThieuNhi thieuNhi = optionalThieuNhi.get();

        Optional<LopNamHoc> optionalLopNamHoc = lopNamHocRepository.findById(new LopNamHocKey(maLop, namHoc));

        LopNamHoc lopNamHoc = optionalLopNamHoc.get();

        String maBD = generateMa.generateMaBangDiem(maTN, maLop, namHoc);

        BangDiem bangDiem = new BangDiem();
        bangDiem.setMaBangDiem(maBD);
        bangDiem.setThieuNhi(thieuNhi);
        bangDiem.setLopNamHoc(lopNamHoc);

        return bangDiemRepository.save(bangDiem);
    }
}
