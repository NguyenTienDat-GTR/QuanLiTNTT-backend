package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.dto.ChucVuHuynhTruongDto;
import com.example.quanlitntt_backend.entities.ChucVu;
import com.example.quanlitntt_backend.entities.ChucVuHuynhTruong;
import com.example.quanlitntt_backend.entities.HuynhTruong;
import com.example.quanlitntt_backend.entities.NamHoc;
import com.example.quanlitntt_backend.entities.compositeKey.ChucVuHuynhTruongKey;
import com.example.quanlitntt_backend.repositories.ChucVuHuynhTruongRepository;
import com.example.quanlitntt_backend.repositories.ChucVuRepository;
import com.example.quanlitntt_backend.repositories.HuynhTruongRepository;
import com.example.quanlitntt_backend.repositories.NamHocRepository;
import com.example.quanlitntt_backend.services.ChucVuHuynhTruongService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChucVuHuynhTruongServiceImpl implements ChucVuHuynhTruongService {

    @Autowired
    private HuynhTruongRepository huynhTruongRepository;

    @Autowired
    private NamHocRepository namHocRepository;

    @Autowired
    private ChucVuRepository chucVuRepository;

    @Autowired
    private ChucVuHuynhTruongRepository chucVuHuynhTruongRepository;

    @Override
    public void addChucVuHuynhTruong(ChucVuHuynhTruongDto chucVuHuynhTruongDto) {

        Optional<HuynhTruong> huynhTruong = huynhTruongRepository.findById(chucVuHuynhTruongDto.getMaHT());
        Optional<ChucVu> chucVu = chucVuRepository.findById(chucVuHuynhTruongDto.getMaChucVu());
        Optional<NamHoc> namHoc = namHocRepository.findById(chucVuHuynhTruongDto.getNamHoc());

        if (huynhTruong.isPresent() && chucVu.isPresent() && namHoc.isPresent()) {
            ChucVuHuynhTruong entity = new ChucVuHuynhTruong(
                    new ChucVuHuynhTruongKey(chucVuHuynhTruongDto.getMaHT(), chucVuHuynhTruongDto.getNamHoc(), chucVuHuynhTruongDto.getMaChucVu()),
                    huynhTruong.get(),
                    namHoc.get(),
                    chucVu.get()
            );
            chucVuHuynhTruongRepository.save(entity);
        }

    }

    @Override
    public boolean existsByHuynhTruongAndNamHoc(String maHT, String namHoc) {
        return chucVuHuynhTruongRepository.existsByHuynhTruongAndNamHoc(maHT, namHoc);
    }

    @Override
    public boolean existsByChucVuAndNamHoc(String maChucVu, String namHoc) {
        return chucVuHuynhTruongRepository.existsByChucVuAndNamHoc(maChucVu, namHoc);
    }

    @Override
    public void deleteChucVuHuynhTruongByChucVu(String maChucVu, String namHoc) {
        Optional<ChucVu> chucVu = chucVuRepository.findById(maChucVu);
        if (chucVu.isEmpty()) {
            throw new RuntimeException("Không tìm thấy chức vụ với mã: " + maChucVu);
        }

        Optional<NamHoc> namHocById = namHocRepository.findById(namHoc);
        if (namHoc.isEmpty()) {
            throw new RuntimeException("Không tìm thấy năm học: " + namHoc);
        }

        // Xóa tất cả chức vụ của huynh trưởng trong năm học này
        chucVuHuynhTruongRepository.deleteByChucVuAndNamHoc(chucVu.get().getMaChucVu(), namHocById.get().getNamHoc());
    }

    @Override
    public void deleteChucVuHuynhTruongByHuynhTruong(String maHT, String namHoc) {
        Optional<HuynhTruong> ht = huynhTruongRepository.findById(maHT);
        if (ht.isEmpty()) {
            throw new RuntimeException("Không tìm thấy huynh trưởng với mã: " + maHT);
        }

        Optional<NamHoc> namHocById = namHocRepository.findById(namHoc);
        if (namHoc.isEmpty()) {
            throw new RuntimeException("Không tìm thấy năm học: " + namHoc);
        }


        // Xóa tất cả chức vụ của huynh trưởng trong năm học này
        chucVuHuynhTruongRepository.deleteByHuynhTruongAndNamHoc(ht.get().getMaHT(), namHocById.get().getNamHoc());
    }

    @Override
    public Page<ChucVuHuynhTruongDto> getAllChucVuInNamHoc(Pageable pageable, String namHoc) {

        if (namHoc != null && !namHoc.isEmpty()) {
            return chucVuHuynhTruongRepository.getAllChucVuInNamHoc(pageable, namHoc);
        }

        return Page.empty();
    }

    @Override
    public Page<ChucVuHuynhTruongDto> getAllChucVuByMaHT(Pageable pageable, String maHT) {

        if (maHT == null || maHT.isEmpty()) {
            throw new IllegalArgumentException("Mã Huynh Trưởng không được để trống");
        }

        boolean exists = huynhTruongRepository.existsById(maHT);
        if (!exists) {
            throw new EntityNotFoundException("Không tìm thấy Huynh Trưởng với mã: " + maHT);
        }

        return chucVuHuynhTruongRepository.getAllChucVuByHT(pageable, maHT);
    }

    @Override
    public Page<ChucVuHuynhTruongDto> getAllChucVuByChucVu(Pageable pageable, String maChucVu) {
        if (maChucVu == null || maChucVu.isEmpty()) {
            throw new IllegalArgumentException("Mã chức vụ không được để trống");
        }

        boolean exists = chucVuRepository.existsById(maChucVu);
        if (!exists) {
            throw new EntityNotFoundException("Không tìm thấy chức với mã: " + maChucVu);
        }

        return chucVuHuynhTruongRepository.getAllChucVuByChucVu(pageable, maChucVu);
    }

}
