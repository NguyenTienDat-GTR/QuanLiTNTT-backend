package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.ChucVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChucVuRepository extends JpaRepository<ChucVu, String> {
}
