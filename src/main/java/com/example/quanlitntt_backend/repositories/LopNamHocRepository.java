package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.HuynhTruong;
import com.example.quanlitntt_backend.entities.Lop;
import com.example.quanlitntt_backend.entities.LopNamHoc;
import com.example.quanlitntt_backend.entities.compositeKey.LopNamHocKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LopNamHocRepository extends JpaRepository<LopNamHoc, LopNamHocKey> {

    // tìm huynh trưởng trong năm học
    @Query(value = "SELECT ht.* FROM huynh_truong ht " +
                   "JOIN lop_nam_hoc_huynh_truong lnh_ht ON ht.maht = lnh_ht.maht " +
                   "JOIN lop_nam_hoc lnh ON lnh_ht.ma_lop = lnh.ma_lop AND lnh_ht.nam_hoc = lnh.nam_hoc " +
                   "JOIN nam_hoc nh ON lnh.nam_hoc = nh.nam_hoc " +
                   "WHERE ht.maht = :maHT AND nh.nam_hoc = :namHoc", nativeQuery = true)
    Optional<HuynhTruong> timHuynhTruongTrongNamHoc(@Param("maHT") String maHT, @Param("namHoc") String namHoc);

    //lấy tất cả huynh trưởng của 1 lớp trong năm học
    @Query(value = "SELECT ht.* FROM huynh_truong ht " +
                   "JOIN lop_nam_hoc_huynh_truong lnh_ht ON ht.maht = lnh_ht.maht " +
                   "JOIN lop_nam_hoc lnh ON lnh_ht.ma_lop = lnh.ma_lop AND lnh_ht.nam_hoc = lnh.nam_hoc " +
                   "JOIN lop l ON l.ma_lop = lnh.ma_lop " +
                   "JOIN nam_hoc nh ON lnh.nam_hoc = nh.nam_hoc " +
                   "WHERE l.ma_lop = :maLop AND nh.nam_hoc = :namHoc", nativeQuery = true)
    List<HuynhTruong> layHuynhTruongCuaLopNamHoc(@Param("maLop") String maLop, @Param("namHoc") String namHoc);

    // lấy tất cả lớp theo ngành và năm học
    @Query(value = "SELECT l.* FROM lop_nam_hoc lnh " +
                   "JOIN lop l ON l.ma_lop = lnh.ma_lop " +
                   "JOIN nganh ng ON ng.ma_nganh = lnh.ma_nganh " +
                   "JOIN nam_hoc nh ON lnh.nam_hoc = nh.nam_hoc " +
                   "WHERE ng.ma_nganh = :maNganh AND nh.nam_hoc = :namHoc", nativeQuery = true)
    List<Lop> layLopTheoNganhVaNamHoc(@Param("maNganh") String maNganh, @Param("namHoc") String namHoc);


}
