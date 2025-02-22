package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan,String> {
    Optional<TaiKhoan> findById(String tenDangNhap);
}
