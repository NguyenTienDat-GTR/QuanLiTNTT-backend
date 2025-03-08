package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.BangDiem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BangDiemRepository extends JpaRepository<BangDiem, String> {

    @Modifying
    @Transactional
    @Query(value = "DELETE from bang_diem bd " +
                   "WHERE bd.matn = :maTN AND bd.ma_lop = :maLop AND bd.nam_hoc = :namHoc "
            , nativeQuery = true)
    int xoaBangDiem(@Param("maTN") String maTN, @Param("maLop") String maLop, @Param("namHoc") String namHoc);

    @Query(value = "SELECT * from bang_diem bd " +
                   "WHERE ma_lop = :maLop AND nam_hoc = :namHoc", nativeQuery = true)
    List<BangDiem> findByLopAndNamHoc(@Param("maLop") String maLop, @Param("namHoc") String namHoc);
}
