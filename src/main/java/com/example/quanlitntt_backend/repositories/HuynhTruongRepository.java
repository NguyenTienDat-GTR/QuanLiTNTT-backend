package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.HuynhTruong;
import com.example.quanlitntt_backend.entities.enums.CapSao;
import jakarta.persistence.NamedNativeQueries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Repository
public interface HuynhTruongRepository extends JpaRepository<HuynhTruong, String> {

    @Query(value = """
                SELECT ht.* 
                FROM huynh_truong ht
                JOIN lop_nam_hoc lnh ON ht.maht = lnh.math
                JOIN nam_hoc nh ON lnh.nam_hoc = nh.nam_hoc
                WHERE nh.nam_hoc = :namHoc
            """, nativeQuery = true)
        // lay danh sach huynh truong theo nam hoc
    List<HuynhTruong> getHuynhTruongOfNamHoc(String namHoc);

    @Query(value = """
                SELECT ht.* 
                FROM huynh_truong ht
                where ht.so_dien_thoai = :soDT
            """, nativeQuery = true)
        // tim huynh truong theo so dien thoai
    Optional<HuynhTruong> getHuynhTruongBySoDT(String soDT);

    @Query(value = """
                SELECT ht.* 
                FROM huynh_truong ht
                where ht.email = :email
            """, nativeQuery = true)
        // tim huynh truong theo email
    Optional<HuynhTruong> getHuynhTruongByEmail(String email);

    // Lấy tất cả HuynhTruong có phân trang
    @Query(value = "SELECT * FROM huynh_truong", nativeQuery = true)
    Page<HuynhTruong> getAllHuynhTruong(Pageable pageable);

    @Query(value = "SELECT * FROM huynh_truong ORDER BY " +
                   "CASE WHEN :sortBy = 'ten' THEN ten END ASC, " +
                   "CASE WHEN :sortBy = 'capSao' THEN cap_sao END ASC",
            nativeQuery = true)
    Page<HuynhTruong> getAllHuynhTruongSorted(@RequestParam("sortBy") String sortBy, Pageable pageable);

    @Query(value = "SELECT * FROM huynh_truong WHERE ten LIKE %:ten%",
            countQuery = "SELECT count(*) FROM huynh_truong WHERE ten LIKE %:ten%",
            nativeQuery = true)
    Page<HuynhTruong> getHuynhTruongByTen(String ten, Pageable pageable);

    @Query("SELECT h FROM HuynhTruong h WHERE h.capSao = :capSao ORDER BY h.capSao DESC")
    Page<HuynhTruong> getHuynhTruongByCapSao(@Param("capSao") CapSao capSao, Pageable pageable);


}
