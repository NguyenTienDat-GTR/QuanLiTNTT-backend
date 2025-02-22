package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.LopNamHoc;
import com.example.quanlitntt_backend.entities.compositeKey.LopNamHocKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LopNamHocRepository extends JpaRepository<LopNamHoc, LopNamHocKey> {
}
