package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.dto.LopDto;
import com.example.quanlitntt_backend.entities.Lop;
import com.example.quanlitntt_backend.repositories.LopRepository;
import com.example.quanlitntt_backend.services.LopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LopServiceImpl implements LopService {

    @Autowired
    private LopRepository lopRepository;

    @Override
    public Lop addLop(LopDto lopDto) {

        if (lopDto.getMaLop().isEmpty() || lopDto.getTenLop().isEmpty()) {
            throw new RuntimeException("Mã lớp và tên lớp không được trống");
        }

        Optional<Lop> lop = lopRepository.findById(lopDto.getMaLop());


        if (lop.isPresent()) {
            throw new RuntimeException("Lớp với mã lớp " + lopDto.getMaLop() + "đã tồn tại");
        }

        Lop l = new Lop(lopDto.getMaLop(), lopDto.getTenLop());

        return lopRepository.save(l);

    }

    @Override
    public Optional<Lop> getLopByMaLop(String maLop) {
        return lopRepository.findById(maLop);
    }

    @Override
    public Page<Lop> getAllLop(Pageable pageable) {
        return lopRepository.findAll(pageable);
    }

    @Override
    public Lop updateLop(LopDto lopDto) {
        return null;
    }

    @Override
    public void deleteLop(String maLop) {
        Optional<Lop> lop = lopRepository.findById(maLop);
        if (lop.isEmpty()) {
            throw new RuntimeException("Lớp với mã lớp " + maLop + "không tồn tại");
        }
        lopRepository.delete(lop.get());

    }
}
