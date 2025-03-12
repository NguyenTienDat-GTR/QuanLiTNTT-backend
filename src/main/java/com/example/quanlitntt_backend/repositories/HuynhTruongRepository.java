package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.HuynhTruong;
import com.example.quanlitntt_backend.entities.enums.CapSao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Repository
public interface HuynhTruongRepository extends JpaRepository<HuynhTruong, String> {

    /*
     * Lấy tất cả huynh trưởng trong 1 năm học
     * Phân trang
     * @ Param String namHoc
     */
    @Query(value = """
                SELECT ht.*\s
                FROM huynh_truong ht
                JOIN lop_nam_hoc lnh ON ht.maht = lnh.math
                JOIN nam_hoc nh ON lnh.nam_hoc = nh.nam_hoc
                WHERE nh.nam_hoc = :namHoc
            """, nativeQuery = true)
    Page<HuynhTruong> getHuynhTruongOfNamHoc(String namHoc, Pageable pageable);

    /*
     * Tìm huynh trưởng theo số điện thoại
     * @Param String soDT
     */
    @Query(value = """
                SELECT ht.*\s
                FROM huynh_truong ht
                where ht.so_dien_thoai = : soDT
            """, nativeQuery = true)
    Optional<HuynhTruong> getHuynhTruongBySoDT(String soDT);

    /*
     * Tìm huynh trưởng theo email
     * @Param String email
     */
    @Query(value = """
                SELECT ht.*\s
                FROM huynh_truong ht
                where ht.email = :email
            """, nativeQuery = true)
    Optional<HuynhTruong> getHuynhTruongByEmail(String email);

    /*
     * Lấy tất cả HuynhTruong
     * Phân trang
     */
    @Query(value = "SELECT * FROM huynh_truong", nativeQuery = true)
    Page<HuynhTruong> getAllHuynhTruong(Pageable pageable);

    /*
     * Lấy tất cả HuynhTruong có sắp xêp theo tên hoặc theo cấp sao
     * Phân trang
     * @Param String sortBy
     * @Param Pageable
     */
    @Query(value = "SELECT * FROM huynh_truong ORDER BY " +
                   "CASE WHEN :sortBy = 'ten' THEN ten END ASC, " +
                   "CASE WHEN :sortBy = 'capSao' THEN cap_sao END ASC",
            nativeQuery = true)
    Page<HuynhTruong> getAllHuynhTruongSorted(@RequestParam("sortBy") String sortBy, Pageable pageable);

    /*
     * Tìm tất cả huynh trưởng theo họ, tên
     * Phân trang
     * @Param String ten
     * @Param Pageable
     */
    @Query(value = "SELECT * FROM huynh_truong WHERE ten LIKE %:ten% OR ho LIKE %:ten%",
            countQuery = "SELECT count(*) FROM huynh_truong WHERE ten LIKE %:ten% OR ho LIKE %:ten%",
            nativeQuery = true)
    Page<HuynhTruong> getHuynhTruongByTen(String ten, Pageable pageable);

    /*
     * Tìm tất cả huynh trưởng theo cấp sao
     * Phân trang
     * @Param CapSao capSao
     * @Param Pageable pageable
     */
    @Query("SELECT h FROM HuynhTruong h WHERE h.capSao = :capSao ORDER BY h.capSao DESC")
    Page<HuynhTruong> getHuynhTruongByCapSao(@Param("capSao") CapSao capSao, Pageable pageable);


}
