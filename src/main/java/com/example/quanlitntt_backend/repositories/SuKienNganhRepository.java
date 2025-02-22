package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.SuKienNganh;
import com.example.quanlitntt_backend.entities.compositeKey.SuKienNganhKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuKienNganhRepository extends JpaRepository<SuKienNganh, SuKienNganhKey> {
}
