package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.dto.ChucVuDto;
import com.example.quanlitntt_backend.entities.ChucVu;
import com.example.quanlitntt_backend.repositories.ChucVuRepository;
import com.example.quanlitntt_backend.services.ChucVuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChucVuServiceImpl implements ChucVuService {
    @Autowired
    private ChucVuRepository chucVuRepository;

    @Override
    public List<ChucVu> getAllChucVu() {

        return chucVuRepository.findAll();
    }

    @Override
    public Optional<ChucVu> getChucVuByTen(String tenChucVu) {
        Optional<ChucVu> chucVu = chucVuRepository.findByTenChucVu(tenChucVu);

        if (chucVu.isPresent()) {
            return chucVu;
        }

        return Optional.empty();
    }

    @Override
    public ChucVu addChucVu(ChucVuDto chucVuDto) {

        ChucVu chucVu = new ChucVu();


        chucVu.setTenChucVu(chucVuDto.getTenChucVu());

        return chucVuRepository.save(chucVu);
    }

    @Override
    public ChucVu updateChucVu(ChucVuDto chucVuDto) {
        return null;
    }

    @Override
    public void deleteChucVu(String maChucVu) {
        chucVuRepository.deleteById(maChucVu);

    }
}
