package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.ThieuNhi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThieuNhiRepository extends JpaRepository<ThieuNhi, String> {
}
