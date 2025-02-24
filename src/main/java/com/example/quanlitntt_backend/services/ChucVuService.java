package com.example.quanlitntt_backend.services;

import com.example.quanlitntt_backend.dto.ChucVuDto;
import com.example.quanlitntt_backend.entities.ChucVu;

import java.util.List;
import java.util.Optional;

public interface ChucVuService {
    public List<ChucVu> getAllChucVu();
    public Optional<ChucVu> getChucVuByTen(String tenChucVu);
    public Optional<ChucVu> getChucVuByMa(String maChucVu);
    public ChucVu addChucVu(ChucVuDto chucVuDto);
    public ChucVu updateChucVu(ChucVuDto chucVuDto);
    public void deleteChucVu(String maChucVu);
}
