package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.dto.ThieuNhiDto;
import com.example.quanlitntt_backend.entities.ThieuNhi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThieuNhiRepository extends JpaRepository<ThieuNhi, String> {
    /*
     * lấy danh sách tất cả thiếu nhi
     * @Param Pageable
     */
    @Query("SELECT new com.example.quanlitntt_backend.dto.ThieuNhiDto( "
           + "t.maTN, t.tenThanh, t.ho, t.ten, t.ngaySinh, t.gioiTinh, t.ngayRuaToi, t.noiRuaToi, "
           + "t.ngayRuocLe, t.noiRuocLe, t.ngayThemSuc, t.noiThemSuc, t.ngayBaoDong, t.noiBaoDong, "
           + "t.hoTenCha, t.hoTenMe, t.soDienThoaiCha, t.soDienThoaiMe, t.soDienThoaiCaNhan, "
           + " t.trangThai) FROM ThieuNhi t" +
           " order by t.ten ASC ")
    Page<ThieuNhiDto> getAllThieuNhi(Pageable pageable);

    /*
     * lấy thiếu nhi theo mã
     * @Param String maTN
     */
    @Query("SELECT new com.example.quanlitntt_backend.dto.ThieuNhiDto( "
           + "t.maTN, t.tenThanh, t.ho, t.ten, t.ngaySinh, t.gioiTinh, t.ngayRuaToi, t.noiRuaToi, "
           + "t.ngayRuocLe, t.noiRuocLe, t.ngayThemSuc, t.noiThemSuc, t.ngayBaoDong, t.noiBaoDong, "
           + "t.hoTenCha, t.hoTenMe, t.soDienThoaiCha, t.soDienThoaiMe, t.soDienThoaiCaNhan, "
           + " t.trangThai) FROM ThieuNhi t WHERE t.maTN = ?1")
    Optional<ThieuNhiDto> getThieuNhiByMa(String maTN);

    /*
     * lấy thiếu nhi theo số điện thoại cha hoặc mẹ
     * @Param Pageable
     * @Param String soDT
     */
    @Query("SELECT new com.example.quanlitntt_backend.dto.ThieuNhiDto( "
           + "t.maTN, t.tenThanh, t.ho, t.ten, t.ngaySinh, t.gioiTinh, t.ngayRuaToi, t.noiRuaToi, "
           + "t.ngayRuocLe, t.noiRuocLe, t.ngayThemSuc, t.noiThemSuc, t.ngayBaoDong, t.noiBaoDong, "
           + "t.hoTenCha, t.hoTenMe, t.soDienThoaiCha, t.soDienThoaiMe, t.soDienThoaiCaNhan, "
           + " t.trangThai) FROM ThieuNhi t " +
           "WHERE t.soDienThoaiCha LIKE :soDT OR t.soDienThoaiMe LIKE :soDT")
    Page<ThieuNhiDto> getThieuNhiBySdtChaMe(@Param("soDT") String soDT, Pageable pageable);

}
