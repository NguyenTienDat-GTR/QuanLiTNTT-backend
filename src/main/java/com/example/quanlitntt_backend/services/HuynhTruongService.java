package com.example.quanlitntt_backend.services;

import com.example.quanlitntt_backend.dto.HuynhTruongDto;
import com.example.quanlitntt_backend.entities.HuynhTruong;
import com.example.quanlitntt_backend.entities.enums.CapSao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public interface HuynhTruongService {
    public HuynhTruong addHuynhTruong(HuynhTruongDto huynhTruongDTO);

    public HuynhTruong updateHuynhTruong(HuynhTruongDto huynhTruongDTO, String maHT);

    public void deleteHuynhTruong(String maHT);

    public Optional<HuynhTruong> getHuynhTruongByMa(String maHT);

    public Page<HuynhTruong> getHuynhTruongByTen(String tenHT, Pageable pageable);

    Page<HuynhTruong> getHuynhTruongByCapSao(CapSao capSao, Pageable pageable);

    public Optional<HuynhTruong> getHuynhTruongBySoDT(String soDT);

    public Optional<HuynhTruong> getHuynhTruongByEmail(String email);

    public List<HuynhTruong> getHuynhTruongByNamHoc(String namHoc);

    Page<HuynhTruong> getAllHuynhTruong(Pageable pageable, String sortBy);

}
