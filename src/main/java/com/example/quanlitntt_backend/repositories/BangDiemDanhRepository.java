package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.BangDiemDanh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BangDiemDanhRepository extends JpaRepository<BangDiemDanh,String> {
    /*
     * Xóa bảng điểm danh lớp
     * @Param String maTN
     * @Param String maLop
     * @Param String namHoc
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE from bang_diem_danh bdd " +
                   "WHERE bdd.matn = :maTN AND bdd.ma_lop = :maLop AND bdd.nam_hoc = :namHoc "
            , nativeQuery = true)
    int xoaBangDiemDanhLop(@Param("maTN") String maTN, @Param("maLop") String maLop, @Param("namHoc") String namHoc);
}
