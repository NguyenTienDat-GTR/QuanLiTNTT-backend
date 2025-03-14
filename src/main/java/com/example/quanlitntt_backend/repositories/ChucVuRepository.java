package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.ChucVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChucVuRepository extends JpaRepository<ChucVu, String> {

    /*
     * Tìm kiếm chức vụ theo tên ( tìm kiếm tương đối)
     * @Param String tenChucVu
     */
    @Query(value = "SELECT * FROM chuc_vu WHERE ten_chuc_vu LIKE %?1%", nativeQuery = true)
    public Optional<ChucVu> findByTenChucVu(String tenChucVu);
}
