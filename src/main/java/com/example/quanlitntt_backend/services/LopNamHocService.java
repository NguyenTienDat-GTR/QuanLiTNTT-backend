package com.example.quanlitntt_backend.services;

import com.example.quanlitntt_backend.dto.LopNamHocDto;
import com.example.quanlitntt_backend.entities.HuynhTruong;
import com.example.quanlitntt_backend.entities.Lop;
import com.example.quanlitntt_backend.entities.LopNamHoc;
import com.example.quanlitntt_backend.entities.ThieuNhi;
import com.example.quanlitntt_backend.entities.compositeKey.LopNamHocKey;

import java.util.List;
import java.util.Optional;

public interface LopNamHocService {

    public void addLopNamNganh(List<String> maLop, String namHoc);

    public Optional<LopNamHoc> getLopNamHocById(LopNamHocKey maLopNamHoc);

    public void addThieuNhiVaoLop(String maTN, String maLop, String namHoc);

    public void addHuynhTruongVaoLop(String maHT, LopNamHoc lopNamHoc);

    public List<HuynhTruong> layHuynhTruongCuaLop(String maLop, String namHoc);

    public List<Lop> layLopTheoNganhVaNam(String maNganh, String namHoc);

    public Optional<HuynhTruong> timHTTheoLopNamHoc(String maHt, String maLop, String namHoc);

    public boolean xoaHuynhTruongKhoiLop(String maHT, String maLop, String namHoc);

    public Optional<ThieuNhi> timTNTheoLopNamHoc(String maTN, String maLop, String namHoc);

    public boolean chuyenThieuNhiSangLopKhac(String maTN, String maLopCu, String maLopMoi, String namHoc);

    public List<String> getDanhSachNamHocCuaThieuNhi(String maThieuNhi);

    public boolean xoaThieuNhiKhoiLop(String maTN, String maLop, String namHoc);

    public Optional<HuynhTruong> layHTTheoNganhNamHoc(String maHT, String maNganh, String namHoc);

}
