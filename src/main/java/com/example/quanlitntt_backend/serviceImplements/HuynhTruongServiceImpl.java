package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.dto.HuynhTruongDto;
import com.example.quanlitntt_backend.entities.HuynhTruong;
import com.example.quanlitntt_backend.entities.enums.CapSao;
import com.example.quanlitntt_backend.repositories.HuynhTruongRepository;
import com.example.quanlitntt_backend.repositories.TaiKhoanRepository;
import com.example.quanlitntt_backend.services.HuynhTruongService;
import com.example.quanlitntt_backend.utils.DateUtil;
import com.example.quanlitntt_backend.utils.GenerateMa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HuynhTruongServiceImpl implements HuynhTruongService {
    @Autowired
    private HuynhTruongRepository huynhTruongRepository;


    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    private final GenerateMa generateMa = new GenerateMa();


    @Override
    public HuynhTruong addHuynhTruong(HuynhTruongDto huynhTruongDTO) {
        HuynhTruong ht = new HuynhTruong();


        Date ngaySinhFormatted = DateUtil.convertToDateFormat(huynhTruongDTO.getNgaySinh());

        // Tạo mã HuynhTruong tự động
        String maHT = generateMa.generateMaHuynhTruong(
                ngaySinhFormatted,
                huynhTruongDTO.getNgayBonMang(),
                huynhTruongDTO.getSoDienThoai()
        );
        ht.setMaHT(maHT);

        setValueForHuynhTruong(huynhTruongDTO, ht, ngaySinhFormatted);
        ht.setHoatDong(true);

        return huynhTruongRepository.save(ht);
    }

    private void setValueForHuynhTruong(HuynhTruongDto huynhTruongDTO, HuynhTruong ht, Date ngaySinhFormatted) {
        ht.setTenThanh(huynhTruongDTO.getTenThanh());
        ht.setHo(huynhTruongDTO.getHo());
        ht.setTen(huynhTruongDTO.getTen());
        ht.setCapSao(huynhTruongDTO.getCapSao());
        ht.setSoDienThoai(huynhTruongDTO.getSoDienThoai());
        ht.setEmail(huynhTruongDTO.getEmail());
        ht.setGioiTinh(huynhTruongDTO.getGioiTinh());
        ht.setNgaySinh(ngaySinhFormatted);
        ht.setNgayBonMang(huynhTruongDTO.getNgayBonMang());
        ht.setHinhAnh(huynhTruongDTO.getHinhAnh());
    }

    @Override
    public HuynhTruong updateHuynhTruong(HuynhTruongDto huynhTruongDTO, String maHT) {

        if (huynhTruongRepository.existsById(maHT)) {
            HuynhTruong ht = huynhTruongRepository.findById(maHT).get();

            Date ngaySinhFormatted = DateUtil.convertToDateFormat(huynhTruongDTO.getNgaySinh());

            setValueForHuynhTruong(huynhTruongDTO, ht, ngaySinhFormatted);
            ht.setHoatDong(huynhTruongDTO.isHoatDong());

            return huynhTruongRepository.save(ht);
        }

        return null;
    }

    @Override
    public void deleteHuynhTruong(String maHT) {
        huynhTruongRepository.findById(maHT).ifPresent(huynhTruong -> {
            huynhTruong.setHoatDong(false);
            huynhTruongRepository.save(huynhTruong);
        });
    }

    @Override
    public Optional<HuynhTruong> getHuynhTruongByMa(String maHT) {
        Optional<HuynhTruong> huynhTruong = huynhTruongRepository.findById(maHT);

        if (!huynhTruong.isPresent()) {
            return Optional.empty();
        }

        return huynhTruong;

    }

    @Override
    public Page<HuynhTruong> getHuynhTruongByTen(String tenHT, Pageable pageable) {

        if (tenHT != null && !tenHT.isEmpty()) {
            return huynhTruongRepository.getHuynhTruongByTen(tenHT, pageable);
        }

        return Page.empty();
    }

    @Override
    public Page<HuynhTruong> getAllHuynhTruong(Pageable pageable, String sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return huynhTruongRepository.getAllHuynhTruong(pageable);
        }
        return huynhTruongRepository.getAllHuynhTruongSorted(sortBy, pageable);
    }

    @Override
    public Page<HuynhTruong> getHuynhTruongByCapSao(CapSao capSao, Pageable pageable) {

        if (capSao != null && !capSao.name().isEmpty()) {
            return huynhTruongRepository.getHuynhTruongByCapSao(capSao, pageable);
        } else
            return huynhTruongRepository.getAllHuynhTruong(pageable);
    }


    @Override
    public Optional<HuynhTruong> getHuynhTruongBySoDT(String soDT) {
        Optional<HuynhTruong> huynhTruong = Optional.of(new HuynhTruong());
        try {
            huynhTruong = huynhTruongRepository.getHuynhTruongBySoDT(soDT);
            return huynhTruong;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<HuynhTruong> getHuynhTruongByEmail(String email) {
        Optional<HuynhTruong> huynhTruong = Optional.of(new HuynhTruong());
        try {
            huynhTruong = huynhTruongRepository.getHuynhTruongByEmail(email);
            return huynhTruong;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<HuynhTruong> getHuynhTruongByNamHoc(String namHoc) {
        return null;
    }


}
