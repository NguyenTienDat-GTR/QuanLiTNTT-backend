package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.dto.TaiKhoanDto;
import com.example.quanlitntt_backend.entities.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, String> {

    @Query(value = "SELECT tk.ten_dang_nhap, tk.vai_tro, tk.hoat_dong, ht.ten_thanh, ht.ho, ht.ten " +
                   "        FROM tai_khoan tk " +
                   "        JOIN huynh_truong ht ON tk.ten_dang_nhap = ht.maht " +
                   "        WHERE tk.vai_tro != 'THIEUNHI' " +
                   "        ORDER BY ht.ten ASC"
            , nativeQuery = true)
    Page<Object[]> getAllTaiKhoanHT(Pageable pageable);

    @Query(value = "SELECT tk.ten_dang_nhap, tk.vai_tro, tk.hoat_dong, tn.ten_thanh, tn.ho, tn.ten " +
                   "        FROM tai_khoan tk " +
                   "        JOIN thieu_nhi tn ON tk.ten_dang_nhap = tn.matn " +
                   "        WHERE tk.vai_tro = 'THIEUNHI' " +
                   "        ORDER BY tn.ten ASC ",
            nativeQuery = true)
    Page<Object[]> getAllTaiKhoanTN(Pageable pageable);

}
