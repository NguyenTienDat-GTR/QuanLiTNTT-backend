package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.dto.ChucVuHuynhTruongDto;
import com.example.quanlitntt_backend.entities.ChucVuHuynhTruong;
import com.example.quanlitntt_backend.entities.compositeKey.ChucVuHuynhTruongKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ChucVuHuynhTruongRepository extends JpaRepository<ChucVuHuynhTruong, ChucVuHuynhTruongKey> {

    //kiem tra xem huynh truong da co chcu vu trong nam hoc chua
    @Query("SELECT COUNT(c) > 0 FROM ChucVuHuynhTruong c WHERE c.huynhTruong.maHT = ?1 AND c.namHoc.namHoc = ?2")
    boolean existsByHuynhTruongAndNamHoc(String maHT, String namHoc);

    @Query("SELECT COUNT(c) > 0 FROM ChucVuHuynhTruong c WHERE c.chucVu.maChucVu = ?1 AND c.namHoc.namHoc = ?2")
    boolean existsByChucVuAndNamHoc(String maChucVu, String namHoc);

    // Xóa chức vụ của huynh trưởng trong năm học
    @Modifying
    @Transactional
    @Query("DELETE FROM ChucVuHuynhTruong c WHERE c.chucVu.maChucVu = ?1 AND c.namHoc.namHoc = ?2")
    void deleteByChucVuAndNamHoc(String maChucVu, String namHoc);

    @Modifying
    @Transactional
    @Query("DELETE FROM ChucVuHuynhTruong c WHERE c.huynhTruong.maHT = ?1 AND c.namHoc.namHoc = ?2")
    void deleteByHuynhTruongAndNamHoc(String maHT, String namHoc);

    @Query(value = "SELECT ht.maht, ht.ten_thanh, ht.ho, ht.ten, cv.ten_chuc_vu, nh.nam_hoc , cvht.ma_chuc_vu " +
                   "FROM chuc_vu_huynh_truong cvht " +
                   "JOIN chuc_vu cv ON cvht.ma_chuc_vu = cv.ma_chuc_vu " +
                   "JOIN huynh_truong ht ON cvht.maht = ht.maht " +
                   "JOIN nam_hoc nh ON cvht.nam_hoc = nh.nam_hoc " +
                   "WHERE cvht.nam_hoc = :namHoc",

            countQuery = " SELECT COUNT(*) \n" +
                         "    FROM chuc_vu_huynh_truong cvht \n" +
                         "    JOIN huynh_truong ht ON cvht.maht = ht.maht \n" +
                         "    JOIN nam_hoc nh ON cvht.nam_hoc = nh.nam_hoc \n" +
                         "    WHERE cvht.nam_hoc = :namHoc",
            nativeQuery = true)
    Page<ChucVuHuynhTruongDto> getAllChucVuInNamHoc(Pageable pageable, @Param("namHoc") String namHoc);

    @Query(value = "SELECT ht.maht, ht.ten_thanh, ht.ho, ht.ten, cv.ten_chuc_vu, nh.nam_hoc, cvht.ma_chuc_vu\n" +
                   "FROM chuc_vu_huynh_truong cvht\n" +
                   "JOIN chuc_vu cv ON cvht.ma_chuc_vu = cv.ma_chuc_vu\n" +
                   "JOIN huynh_truong ht ON cvht.maht = ht.maht\n" +
                   "JOIN nam_hoc nh ON cvht.nam_hoc = nh.nam_hoc\n" +
                   "WHERE cvht.maht = :maHT\n" +
                   "ORDER BY ht.maht, nh.nam_hoc, cv.ten_chuc_vu;",

            countQuery = "    SELECT COUNT(DISTINCT ht.maht) \n" +
                         "    FROM chuc_vu_huynh_truong cvht\n" +
                         "    JOIN chuc_vu cv ON cvht.ma_chuc_vu = cv.ma_chuc_vu\n" +
                         "    JOIN huynh_truong ht ON cvht.maht = ht.maht\n" +
                         "    JOIN nam_hoc nh ON cvht.nam_hoc = nh.nam_hoc\n" +
                         "    WHERE cvht.maht = :maHT",

            nativeQuery = true)
    Page<ChucVuHuynhTruongDto> getAllChucVuByHT(Pageable pageable, @Param("maHT") String maHT);

    @Query(value = "SELECT ht.maht, ht.ten_thanh, ht.ho, ht.ten, cv.ten_chuc_vu, nh.nam_hoc, cvht.ma_chuc_vu\n" +
                   "FROM chuc_vu_huynh_truong cvht\n" +
                   "JOIN chuc_vu cv ON cvht.ma_chuc_vu = cv.ma_chuc_vu\n" +
                   "JOIN huynh_truong ht ON cvht.maht = ht.maht\n" +
                   "JOIN nam_hoc nh ON cvht.nam_hoc = nh.nam_hoc\n" +
                   "WHERE cvht.ma_chuc_vu = :maChucVu\n" +
                   "ORDER BY ht.maht, nh.nam_hoc, cv.ten_chuc_vu;",

            countQuery = "    SELECT COUNT(DISTINCT cvht.ma_chuc_vu) \n" +
                         "    FROM chuc_vu_huynh_truong cvht\n" +
                         "    JOIN chuc_vu cv ON cvht.ma_chuc_vu = cv.ma_chuc_vu\n" +
                         "    JOIN huynh_truong ht ON cvht.maht = ht.maht\n" +
                         "    JOIN nam_hoc nh ON cvht.nam_hoc = nh.nam_hoc\n" +
                         "    WHERE cvht.ma_chuc_vu = :maChucVu",

            nativeQuery = true)
    Page<ChucVuHuynhTruongDto> getAllChucVuByChucVu(Pageable pageable, @Param("maChucVu") String maChucVu);

}
