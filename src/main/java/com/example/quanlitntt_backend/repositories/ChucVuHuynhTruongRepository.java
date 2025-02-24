package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.ChucVuHuynhTruong;
import com.example.quanlitntt_backend.entities.compositeKey.ChucVuHuynhTruongKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChucVuHuynhTruongRepository extends JpaRepository<ChucVuHuynhTruong, ChucVuHuynhTruongKey> {


}
