package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.Lop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LopRepository extends JpaRepository<Lop, String> {

    @Query(value = "select * from lop",
            countQuery = "Select count(*) from lop",
            nativeQuery = true)
    Page<Lop> getAllLop(Pageable pageable);
}
