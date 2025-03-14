package com.example.quanlitntt_backend.services;

import com.example.quanlitntt_backend.dto.NamHocDto;
import com.example.quanlitntt_backend.entities.NamHoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NamHocService {
    public NamHoc addNamHoc(NamHocDto namHocDto);

    public NamHoc updateNamHoc(NamHocDto namHocDto);

    public List<NamHoc> getAllNamHoc();

    public Optional<NamHoc> getNamHocById(String namHoc);

    public boolean kiemTraNamHocHienTai(String namHoc);
}
