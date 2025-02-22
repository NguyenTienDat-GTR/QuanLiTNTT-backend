package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.SuKien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuKienRepository extends JpaRepository<SuKien, Integer> {
}
