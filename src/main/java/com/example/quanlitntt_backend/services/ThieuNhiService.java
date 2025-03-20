package com.example.quanlitntt_backend.services;

import com.example.quanlitntt_backend.dto.ThieuNhiDto;
import com.example.quanlitntt_backend.entities.ThieuNhi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ThieuNhiService {
    public ThieuNhi addThieuNhi(ThieuNhiDto thieuNhiDto);

    public Page<ThieuNhiDto> getAllThieuNhis(Pageable pageable);

    public Optional<ThieuNhiDto> getThieuNhiByMa(String maTN);

    public Page<ThieuNhiDto> getThieuNhiBySdtChaMe(String sdt, Pageable pageable);

    public void updateThieuNhi(ThieuNhiDto thieuNhiDto);

    public void deleteThieuNhi(String maTN);

    public void activeThieuNhi(String maTN);

    public CompletableFuture<List<String>> addThieuNhiFromFileExcel(MultipartFile file);

    public CompletableFuture<Void> generateAndUploadQRCode(String maTN) throws Exception;
}
