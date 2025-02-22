package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.ChucVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChucVuRepository extends JpaRepository<ChucVu, String> {

    //lay chuc vu theo ten ( tim kiem tuong doi), nativeQuery = true de su dung cau lenh sql
    @Query(value = "SELECT * FROM chuc_vu WHERE ten_chuc_vu LIKE %?1%", nativeQuery = true)
    public Optional<ChucVu> findByTenChucVu(String tenChucVu);
}
