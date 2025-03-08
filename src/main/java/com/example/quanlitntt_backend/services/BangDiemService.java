package com.example.quanlitntt_backend.services;

import com.example.quanlitntt_backend.dto.BangDiemDto;
import com.example.quanlitntt_backend.entities.BangDiem;

import java.util.Optional;

public interface BangDiemService {
    public void taoBangDiem(String maTN, String maLop, String namHoc);

    public void xoaBangDiem(String maTN, String maLop, String namHoc);

    public boolean capNhatBangDiem(BangDiemDto bangDiemDto);

    public void xepLoaiAPlus(String maLop, String namHoc);

    public Optional<BangDiem> layBangDiemTheoMa(String maBD);
}
