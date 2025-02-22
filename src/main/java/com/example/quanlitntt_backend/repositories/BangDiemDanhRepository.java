package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.BangDiemDanh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BangDiemDanhRepository extends JpaRepository<BangDiemDanh,String> {
}
