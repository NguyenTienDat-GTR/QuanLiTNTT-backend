package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.Nganh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NganhRepository extends JpaRepository<Nganh, String> {
}
