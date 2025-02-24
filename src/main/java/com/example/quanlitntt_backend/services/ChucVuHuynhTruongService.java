package com.example.quanlitntt_backend.services;

import com.example.quanlitntt_backend.dto.ChucVuHuynhTruongDto;
import com.example.quanlitntt_backend.entities.ChucVuHuynhTruong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChucVuHuynhTruongService {

    public void addChucVuHuynhTruong(ChucVuHuynhTruongDto chucVuHuynhTruongDto);

    public boolean existsByHuynhTruongAndNamHoc(String maHT, String namHoc);

    public boolean existsByChucVuAndNamHoc(String maChucVu, String namHoc);

    public void deleteChucVuHuynhTruongByChucVu(String maChucVu, String namHoc);

    public void deleteChucVuHuynhTruongByHuynhTruong(String maHT, String namHoc);

    public Page<ChucVuHuynhTruongDto> getAllChucVuInNamHoc(Pageable pageable, String namHoc);

    public Page<ChucVuHuynhTruongDto> getAllChucVuByMaHT(Pageable pageable, String maHT);

    public Page<ChucVuHuynhTruongDto> getAllChucVuByChucVu(Pageable pageable, String maChucVu);

}
