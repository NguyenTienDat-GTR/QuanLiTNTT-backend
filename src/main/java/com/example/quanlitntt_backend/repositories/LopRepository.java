package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.Lop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LopRepository extends JpaRepository<Lop, String> {
}
