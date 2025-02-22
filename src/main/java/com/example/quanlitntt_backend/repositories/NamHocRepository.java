package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.NamHoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NamHocRepository extends JpaRepository<NamHoc, String> {
}
