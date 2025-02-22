package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.dto.NamHocDto;
import com.example.quanlitntt_backend.entities.NamHoc;
import com.example.quanlitntt_backend.repositories.NamHocRepository;
import com.example.quanlitntt_backend.services.NamHocService;
import com.example.quanlitntt_backend.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NamHocServiceImpl implements NamHocService {
    @Autowired
    private NamHocRepository namHocRepository;

    @Override
    public NamHoc addNamHoc(NamHocDto namHocDto) {

        NamHoc namHoc = new NamHoc();

        namHoc.setNamHoc(namHocDto.getNamHoc());

        Date ngayBatDauFormatted = DateUtil.convertToDateFormat(namHocDto.getNgayBatDau());

        namHoc.setNgayBatDau(ngayBatDauFormatted);

        //tao ngay ket thuc = ngay bat dau cong 1 nam
        namHoc.setNgayKetThuc(DateUtil.addYearToDate(ngayBatDauFormatted, 1));

        return namHocRepository.save(namHoc);
    }

    @Override
    public NamHoc updateNamHoc(NamHocDto namHocDto) {

//        Optional<NamHoc> namHoc = namHocRepository.findById(namHocDto.getNamHoc());
//
//        if (namHoc.isPresent()) {
//            namHoc.get().setNgayBatDau(namHocDto.getNgayBatDau());
//            namHoc.get().setNgayKetThuc(namHocDto.getNgayKetThuc());
//            return namHocRepository.save(namHoc.get());
//        }

        return null;
    }

    @Override
    public List<NamHoc> getAllNamHoc() {
        return namHocRepository.findAll();
    }

    @Override
    public Optional<NamHoc> getNamHocById(String namHoc) {

        Optional<NamHoc> namHocOptional = namHocRepository.findById(namHoc);

        if (namHocOptional.isPresent()) {
            return namHocOptional;
        }

        return Optional.empty();
    }
}
