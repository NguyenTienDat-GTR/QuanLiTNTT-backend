package com.example.quanlitntt_backend.services;

import com.example.quanlitntt_backend.dto.LopDto;
import com.example.quanlitntt_backend.entities.Lop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LopService {
    Lop addLop(LopDto lopDto);

    Optional<Lop> getLopByMaLop(String maLop);

    Page<Lop> getAllLop(Pageable pageable);

    Lop updateLop(LopDto lopDto);

    void deleteLop(String maLop);
}
