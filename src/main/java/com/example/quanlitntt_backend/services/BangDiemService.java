package com.example.quanlitntt_backend.services;

import com.example.quanlitntt_backend.dto.BangDiemDto;
import com.example.quanlitntt_backend.dto.BangDiemNamHocDto;
import com.example.quanlitntt_backend.dto.ThieuNhiBangDiemDto;
import com.example.quanlitntt_backend.entities.BangDiem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BangDiemService {
    public void taoBangDiem(String maTN, String maLop, String namHoc);

    public void xoaBangDiem(String maTN, String maLop, String namHoc);

    public boolean capNhatBangDiem(BangDiemDto bangDiemDto);

    public void xepLoaiAPlus(String maLop, String namHoc);

    public Optional<BangDiem> layBangDiemTheoMa(String maBD);

    public Page<ThieuNhiBangDiemDto> layBangDiemCuaThieuNhiTrongLop(String maLop, String namHoc, Pageable pageable);

    public List<BangDiemNamHocDto> layBangDiemCuaThieuNhi(String maTN);
}
