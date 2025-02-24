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
import org.springframework.beans.factory.annotation.Autowired;
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
        for (String maHT : chucVuHuynhTruongDto.getListMaHT()) {
            Optional<HuynhTruong> huynhTruong = huynhTruongRepository.findById(maHT);
            Optional<ChucVu> chucVu = chucVuRepository.findById(chucVuHuynhTruongDto.getMaChucVu());
            Optional<NamHoc> namHoc = namHocRepository.findById(chucVuHuynhTruongDto.getNamHoc());

            if (huynhTruong.isPresent() && chucVu.isPresent() && namHoc.isPresent()) {
                ChucVuHuynhTruong entity = new ChucVuHuynhTruong(
                        new ChucVuHuynhTruongKey(maHT, chucVuHuynhTruongDto.getNamHoc(), chucVuHuynhTruongDto.getMaChucVu()),
                        huynhTruong.get(),
                        namHoc.get(),
                        chucVu.get()
                );
                chucVuHuynhTruongRepository.save(entity);
            }
        }
    }
}
