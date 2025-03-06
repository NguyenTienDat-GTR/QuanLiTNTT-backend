package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.*;
import com.example.quanlitntt_backend.entities.compositeKey.LopNamHocKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    // tìm huynh trưởng theo lớp và  năm học
    @Query(value = "SELECT ht.* FROM huynh_truong ht " +
                   "JOIN lop_nam_hoc_huynh_truong lnh_ht ON ht.maht = lnh_ht.maht " +
                   "JOIN lop_nam_hoc lnh ON lnh_ht.ma_lop = lnh.ma_lop AND lnh_ht.nam_hoc = lnh.nam_hoc " +
                   "JOIN nam_hoc nh ON lnh.nam_hoc = nh.nam_hoc " +
                   "JOIN lop l ON l.ma_lop = lnh.ma_lop " +
                   "WHERE ht.maht = :maHT AND nh.nam_hoc = :namHoc AND l.ma_lop = :maLop", nativeQuery = true)
    Optional<HuynhTruong> timHuynhTruongTheoLopNamHoc(@Param("maHT") String maHT, @Param("maLop") String maLop, @Param("namHoc") String namHoc);

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

    // tìm thiếu nhi trong năm học
    @Query(value = "SELECT tn.* FROM thieu_nhi tn " +
                   "JOIN lop_nam_hoc_thieu_nhi lnh_tn ON tn.matn = lnh_tn.matn " +
                   "JOIN lop_nam_hoc lnh ON lnh_tn.ma_lop = lnh.ma_lop AND lnh_tn.nam_hoc = lnh.nam_hoc " +
                   "JOIN nam_hoc nh ON lnh.nam_hoc = nh.nam_hoc " +
                   "WHERE tn.matn = :maTN AND nh.nam_hoc = :namHoc", nativeQuery = true)
    Optional<ThieuNhi> timThieuNhiTrongNamHoc(@Param("maTN") String maTN, @Param("namHoc") String namHoc);

    // tìm thiếu nhi theo lớp và  năm học
    @Query(value = "SELECT tn.* FROM thieu_nhi tn " +
                   "JOIN lop_nam_hoc_thieu_nhi lnh_tn ON tn.matn = lnh_tn.matn " +
                   "JOIN lop_nam_hoc lnh ON lnh_tn.ma_lop = lnh.ma_lop AND lnh_tn.nam_hoc = lnh.nam_hoc " +
                   "JOIN nam_hoc nh ON lnh.nam_hoc = nh.nam_hoc " +
                   "JOIN lop l ON l.ma_lop = lnh.ma_lop " +
                   "WHERE tn.matn = :maTN AND nh.nam_hoc = :namHoc AND l.ma_lop = :maLop", nativeQuery = true)
    Optional<ThieuNhi> timThieuNhiTheoLopNamHoc(@Param("maTN") String maTN, @Param("maLop") String maLop, @Param("namHoc") String namHoc);

    //xóa huynh trưởng ra khỏi lớp
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM lop_nam_hoc_huynh_truong " +
                   "WHERE maht = :maHT AND ma_lop = :maLop AND nam_hoc = :namHoc",
            nativeQuery = true)
    int xoaHuynhTruongKhoiLop(@Param("maHT") String maHT, @Param("maLop") String maLop, @Param("namHoc") String namHoc);

    // chuyển lớp cho thiếu nhi
    @Transactional
    @Modifying
    @Query(value = "UPDATE lop_nam_hoc_thieu_nhi " +
                   "SET ma_lop = :maLopMoi " +
                   "WHERE matn = :maTN " +
                   "AND ma_lop = :maLopCu " +
                   "AND nam_hoc = :namHoc",
            nativeQuery = true)
    int chuyenThieuNhiSangLopKhac(@Param("maTN") String maTN, @Param("maLopCu") String maLopCu,
                                  @Param("maLopMoi") String maLopMoi, @Param("namHoc") String namHoc);

    // lấy danh sách năm học của thiếu nhi
    @Query(value = "SELECT DISTINCT nam_hoc FROM lop_nam_hoc_thieu_nhi " +
                   "WHERE matn = :maThieuNhi " +
                   "ORDER BY nam_hoc DESC", nativeQuery = true)
    List<String> findDanhSachNamHocByMaThieuNhi(@Param("maThieuNhi") String maThieuNhi);

    //xóa thiếu nhi ra khỏi lớp
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM lop_nam_hoc_thieu_nhi " +
                   "WHERE matn = :maTN AND ma_lop = :maLop AND nam_hoc = :namHoc",
            nativeQuery = true)
    int xoaThieuNhiKhoiLop(@Param("maTN") String maTN, @Param("maLop") String maLop, @Param("namHoc") String namHoc);


    @Query(value = "SELECT * from huynh_truong ht " +
                   "JOIN lop_nam_hoc_huynh_truong lnh_ht.maht ON lnh.ht.maht = ht.maht " +
                   "JOIN lop_nam_hoc lnh ON lnh.ma_lop = lnh_ht.ma_lop AND lnh.nam_hoc = lnh_ht.nam_hoc " +
                   "JOIN nganh ng ON lnh.ma_nganh = ng.ma_nganh " +
                   "WHERE ht.maht = :maHT AND ng.ma_nganh = :maNganh AND lnh.nam_hoc = :namHoc", nativeQuery = true)
    Optional<HuynhTruong> layHTTheoNganhNamHoc(@Param("maHT") String maHT, @Param("maNganh") String maNganh, @Param("namHoc") String namHoc);

    //kiểm tra lớp có thuộc ngành trong năm học hay không
    @Query(value = "SELECT COUNT(*) FROM lop_nam_hoc lnh " +
                   "JOIN lop l ON lnh.ma_lop = l.ma_lop " +
                   "JOIN nam_hoc nh ON lnh.nam_hoc = nh.nam_hoc " +
                   "JOIN nganh ng ON lnh.ma_nganh = ng.ma_nganh " +
                   "WHERE l.ma_lop = :maLop AND nh.nam_hoc = :namHoc AND ng.ma_nganh = :maNganh",
            nativeQuery = true)
    int existsLopInNganhAndNamHoc(@Param("maLop") String maLop,
                                  @Param("maNganh") String maNganh,
                                  @Param("namHoc") String namHoc);


}
