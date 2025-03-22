package com.example.quanlitntt_backend.services;

import com.example.quanlitntt_backend.entities.BangDiemDanh;

import java.util.Optional;

public interface BangDiemDanhService {
    public void taoBangDiemDanhLop(String maTN, String maLop, String namHoc);

    public void xoaBangDiemDanh(String maBDD);

    public Optional<BangDiemDanh> layBangDiemDanh(String maBDD);
}
