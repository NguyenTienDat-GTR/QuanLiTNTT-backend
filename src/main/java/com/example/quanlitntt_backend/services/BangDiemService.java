package com.example.quanlitntt_backend.services;

import com.example.quanlitntt_backend.entities.BangDiem;

public interface BangDiemService {
    public BangDiem taoBangDiem(String maTN, String maLop, String namHoc);
}
