package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.dto.NganhDto;
import com.example.quanlitntt_backend.entities.Nganh;
import com.example.quanlitntt_backend.entities.enums.MaNganh;
import com.example.quanlitntt_backend.repositories.NganhRepository;
import com.example.quanlitntt_backend.services.NganhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NganhServiceImpl implements NganhService {

    @Autowired
    private NganhRepository nganhRepository;

    @Override
    public Nganh addNganh(NganhDto nganhDto) {

        Nganh nganh = new Nganh();

        // Chuyển đổi String thành Enum
        try {
            nganh.setMaNganh(MaNganh.valueOf(nganhDto.getMaNganh()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Mã ngành không hợp lệ: " + nganhDto.getMaNganh());
        }
        nganh.setTenNganh(nganhDto.getTenNganh());

        return nganhRepository.save(nganh);
    }

    @Override
    public Nganh updateNganh(NganhDto nganhDto) {

        MaNganh maNganhEnum;
        try {
            maNganhEnum = MaNganh.valueOf(nganhDto.getMaNganh());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Mã ngành không hợp lệ: " + nganhDto.getMaNganh());
        }

        if (!nganhRepository.existsById(maNganhEnum)) {
            throw new RuntimeException("Không tìm thấy ngành với mã " + nganhDto.getMaNganh());
        }

        Optional<Nganh> nganh = nganhRepository.findById(maNganhEnum);
        if (nganh.isPresent()) {
            nganh.get().setTenNganh(nganhDto.getTenNganh());
            return nganhRepository.save(nganh.get());
        } else {
            throw new RuntimeException("Không tìm thấy ngành với mã " + nganhDto.getMaNganh());
        }
    }

    @Override
    public List<Nganh> getAllNganh() {

        return nganhRepository.findAll();
    }

    @Override
    public Optional<Nganh> getNganhById(String maNganh) {

        try {
            MaNganh maNganhEnum = MaNganh.valueOf(maNganh);
            return nganhRepository.findById(maNganhEnum);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteNganh(String maNganh) {
        try {
            MaNganh maNganhEnum = MaNganh.valueOf(maNganh);
            Optional<Nganh> nganh = nganhRepository.findById(maNganhEnum);
            if (nganh.isPresent()) {
                nganhRepository.delete(nganh.get());
            } else {
                throw new RuntimeException("Không tìm thấy ngành với mã " + maNganh);
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Mã ngành không hợp lệ: " + maNganh);
        }
    }
}
