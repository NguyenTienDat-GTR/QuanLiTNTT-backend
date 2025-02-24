package com.example.quanlitntt_backend.services;

import com.example.quanlitntt_backend.dto.NganhDto;
import com.example.quanlitntt_backend.entities.Nganh;
import com.example.quanlitntt_backend.entities.enums.MaNganh;

import java.util.List;
import java.util.Optional;

public interface NganhService {

    Nganh addNganh(NganhDto nganhDto);

    Nganh updateNganh(NganhDto nganhDto);

    List<Nganh> getAllNganh();

    Optional<Nganh> getNganhById(String maNganh);

    void deleteNganh(String maNganh);
}
