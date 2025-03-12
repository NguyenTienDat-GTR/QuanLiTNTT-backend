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

    /*
     * Kiểm tra xem Huynh trưởng đã có chức vụ trong năm học hay chưa
     * @Param String maHT
     * @Param String namHoc
     */
    @Query("SELECT COUNT(c) > 0 FROM ChucVuHuynhTruong c WHERE c.huynhTruong.maHT = ?1 AND c.namHoc.namHoc = ?2")
    boolean existsByHuynhTruongAndNamHoc(String maHT, String namHoc);

    /*
     * Kiểm tra chức vụ có ồn tại trong năm học không
     * @Param String maChucVu
     * @Param String namHoc
     */
    @Query("SELECT COUNT(c) > 0 FROM ChucVuHuynhTruong c WHERE c.chucVu.maChucVu = ?1 AND c.namHoc.namHoc = ?2")
    boolean existsByChucVuAndNamHoc(String maChucVu, String namHoc);

    /*
     * Xóa chức vụ của huynh trưởng theo chức vụ trong năm học
     * @Param String maChucVu
     * @Param String namHoc
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ChucVuHuynhTruong c WHERE c.chucVu.maChucVu = ?1 AND c.namHoc.namHoc = ?2")
    void deleteByChucVuAndNamHoc(String maChucVu, String namHoc);

    /*
     * Xóa chức vụ của Huynh Trưởng theo huynh trưởng và năm học
     * @Param String maHT
     * @Param String namHoc
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ChucVuHuynhTruong c WHERE c.huynhTruong.maHT = ?1 AND c.namHoc.namHoc = ?2")
    void deleteByHuynhTruongAndNamHoc(String maHT, String namHoc);

    /*
     * Lấy tất cả chức vụ có trong năm học
     * Phân trang
     * @Param String namHoc
     */
    @Query(value = "SELECT ht.maht, ht.ten_thanh, ht.ho, ht.ten, cv.ten_chuc_vu, nh.nam_hoc , cvht.ma_chuc_vu \n" +
                   "FROM chuc_vu_huynh_truong cvht " +
                   "JOIN chuc_vu cv ON cvht.ma_chuc_vu = cv.ma_chuc_vu " +
                   "JOIN huynh_truong ht ON cvht.maht = ht.maht " +
                   "JOIN nam_hoc nh ON cvht.nam_hoc = nh.nam_hoc " +
                   "WHERE cvht.nam_hoc = :namHoc",

            countQuery = " SELECT COUNT(*) " +
                         "    FROM chuc_vu_huynh_truong cvht " +
                         "    JOIN huynh_truong ht ON cvht.maht = ht.maht " +
                         "    JOIN nam_hoc nh ON cvht.nam_hoc = nh.nam_hoc " +
                         "    WHERE cvht.nam_hoc = :namHoc",
            nativeQuery = true)
    Page<ChucVuHuynhTruongDto> getAllChucVuInNamHoc(Pageable pageable, @Param("namHoc") String namHoc);

    /*
     * Lấy tất cả chức vụ của 1 huynh trưởng
     * Phân trang
     * @Param String maHT
     */
    @Query(value = "SELECT ht.maht, ht.ten_thanh, ht.ho, ht.ten, cv.ten_chuc_vu, nh.nam_hoc, cvht.ma_chuc_vu " +
                   "FROM chuc_vu_huynh_truong cvht " +
                   "JOIN chuc_vu cv ON cvht.ma_chuc_vu = cv.ma_chuc_vu " +
                   "JOIN huynh_truong ht ON cvht.maht = ht.maht " +
                   "JOIN nam_hoc nh ON cvht.nam_hoc = nh.nam_hoc " +
                   "WHERE cvht.maht = :maHT " +
                   "ORDER BY ht.maht, nh.nam_hoc, cv.ten_chuc_vu;",

            countQuery = "    SELECT COUNT(DISTINCT ht.maht)  " +
                         "    FROM chuc_vu_huynh_truong cvht " +
                         "    JOIN chuc_vu cv ON cvht.ma_chuc_vu = cv.ma_chuc_vu " +
                         "    JOIN huynh_truong ht ON cvht.maht = ht.maht " +
                         "    JOIN nam_hoc nh ON cvht.nam_hoc = nh.nam_hoc " +
                         "    WHERE cvht.maht = :maHT",

            nativeQuery = true)
    Page<ChucVuHuynhTruongDto> getAllChucVuByHT(Pageable pageable, @Param("maHT") String maHT);

    /*
     * Lấy tất cả Huynh trưửng từng có chức vụ cần lấy
     * @Param String maChucVu
     */
    @Query(value = "SELECT ht.maht, ht.ten_thanh, ht.ho, ht.ten, cv.ten_chuc_vu, nh.nam_hoc, cvht.ma_chuc_vu " +
                   "FROM chuc_vu_huynh_truong cvht " +
                   "JOIN chuc_vu cv ON cvht.ma_chuc_vu = cv.ma_chuc_vu " +
                   "JOIN huynh_truong ht ON cvht.maht = ht.maht " +
                   "JOIN nam_hoc nh ON cvht.nam_hoc = nh.nam_hoc " +
                   "WHERE cvht.ma_chuc_vu = :maChucVu " +
                   "ORDER BY ht.maht, nh.nam_hoc, cv.ten_chuc_vu;",

            countQuery = "    SELECT COUNT(DISTINCT cvht.ma_chuc_vu)  " +
                         "    FROM chuc_vu_huynh_truong cvht " +
                         "    JOIN chuc_vu cv ON cvht.ma_chuc_vu = cv.ma_chuc_vu " +
                         "    JOIN huynh_truong ht ON cvht.maht = ht.maht " +
                         "    JOIN nam_hoc nh ON cvht.nam_hoc = nh.nam_hoc " +
                         "    WHERE cvht.ma_chuc_vu = :maChucVu",

            nativeQuery = true)
    Page<ChucVuHuynhTruongDto> getAllChucVuByChucVu(Pageable pageable, @Param("maChucVu") String maChucVu);

}
