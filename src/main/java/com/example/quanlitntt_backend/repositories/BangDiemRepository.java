package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.BangDiem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BangDiemRepository extends JpaRepository<BangDiem,String> {
}
