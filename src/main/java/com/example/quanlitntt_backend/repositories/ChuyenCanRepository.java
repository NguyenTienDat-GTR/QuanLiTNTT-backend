package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.ChuyenCan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChuyenCanRepository extends JpaRepository<ChuyenCan, Integer> {
}
