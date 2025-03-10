package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.dto.ThieuNhiBangDiemDto;
import com.example.quanlitntt_backend.entities.BangDiem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    //Lấy danh sách thiếu nhi kèm bảng điểm
    @Query(value = "SELECT  t.matn, t.ten_thanh, t.ho, t.ten, bd.ma_bang_diem, bd.diemkt_hki, bd.diem_thigl_hki, "
                   + "bd.diem_thitn_hki, bd.diemtb_hki, bd.phieu_thuong, bd.diemkt_hkii, "
                   + "bd.diem_thigl_hkii, bd.diem_thitn_hkii, bd.diemtb_hkii, bd.diemtbcn, "
                   + "bd.xep_loai, bd.ket_qua "
                   + "FROM bang_diem bd "
                   + "JOIN thieu_nhi t ON bd.matn = t.matn "
                   + "JOIN lop_nam_hoc lnh ON bd.ma_lop = lnh.ma_lop AND bd.nam_hoc = lnh.nam_hoc "
                   + "WHERE bd.ma_lop = :maLop AND bd.nam_hoc = :namHoc "
                   + "ORDER BY t.ten ASC",
            countQuery = "SELECT COUNT(*) FROM bang_diem bd "
                         + "JOIN thieu_nhi t ON bd.matn = t.matn "
                         + "JOIN lop_nam_hoc lnh ON bd.ma_lop = lnh.ma_lop AND bd.nam_hoc = lnh.nam_hoc "
                         + "WHERE bd.ma_lop = :maLop AND bd.nam_hoc = :namHoc",
            nativeQuery = true)
    Page<Object[]> layBangDiemCuaThieuNhiTrongLop(
            @Param("maLop") String maLop,
            @Param("namHoc") String namHoc,
            Pageable pageable);

}
