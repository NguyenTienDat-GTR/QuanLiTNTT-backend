package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.*;
import com.example.quanlitntt_backend.entities.compositeKey.LopNamHocKey;
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
import java.util.Optional;

@Repository
public interface LopNamHocRepository extends JpaRepository<LopNamHoc, LopNamHocKey> {

    /*
     * Tìm huynh trưởng theo năm học và mã huynh trưởng
     * @Param String maHT
     * @Param String namHoc
     */
    @Query(value = "SELECT ht.* FROM huynh_truong ht " +
                   "JOIN lop_nam_hoc_huynh_truong lnh_ht ON ht.maht = lnh_ht.maht " +
                   "JOIN lop_nam_hoc lnh ON lnh_ht.ma_lop = lnh.ma_lop AND lnh_ht.nam_hoc = lnh.nam_hoc " +
                   "JOIN nam_hoc nh ON lnh.nam_hoc = nh.nam_hoc " +
                   "WHERE ht.maht = :maHT AND nh.nam_hoc = :namHoc", nativeQuery = true)
    Optional<HuynhTruong> timHuynhTruongTrongNamHoc(@Param("maHT") String maHT, @Param("namHoc") String namHoc);

    /*
     * Tìm 1 huynh trưởng theo lớp, năm học và mã huynh trưởng
     * @Param String maHT
     * @Param String namHoc
     * @Param String maLop
     */
    @Query(value = "SELECT ht.* FROM huynh_truong ht " +
                   "JOIN lop_nam_hoc_huynh_truong lnh_ht ON ht.maht = lnh_ht.maht " +
                   "JOIN lop_nam_hoc lnh ON lnh_ht.ma_lop = lnh.ma_lop AND lnh_ht.nam_hoc = lnh.nam_hoc " +
                   "JOIN nam_hoc nh ON lnh.nam_hoc = nh.nam_hoc " +
                   "JOIN lop l ON l.ma_lop = lnh.ma_lop " +
                   "WHERE ht.maht = :maHT AND nh.nam_hoc = :namHoc AND l.ma_lop = :maLop", nativeQuery = true)
    Optional<HuynhTruong> timHuynhTruongTheoLopNamHoc(@Param("maHT") String maHT, @Param("maLop") String maLop, @Param("namHoc") String namHoc);

    /*
     * lấy danh sách huynh trưởng của 1 lớp trong năm học
     * @Param String namHoc
     * @Param String maLop
     */
    @Query(value = "SELECT ht.* FROM huynh_truong ht " +
                   "JOIN lop_nam_hoc_huynh_truong lnh_ht ON ht.maht = lnh_ht.maht " +
                   "JOIN lop_nam_hoc lnh ON lnh_ht.ma_lop = lnh.ma_lop AND lnh_ht.nam_hoc = lnh.nam_hoc " +
                   "JOIN lop l ON l.ma_lop = lnh.ma_lop " +
                   "JOIN nam_hoc nh ON lnh.nam_hoc = nh.nam_hoc " +
                   "WHERE l.ma_lop = :maLop AND nh.nam_hoc = :namHoc", nativeQuery = true)
    List<HuynhTruong> layHuynhTruongCuaLopNamHoc(@Param("maLop") String maLop, @Param("namHoc") String namHoc);

    /*
     * lấy danh sách lớp theo ngành và năm học
     * @Param String namHoc
     * @Param String maNganh
     */
    @Query(value = "SELECT l.* FROM lop_nam_hoc lnh " +
                   "JOIN lop l ON l.ma_lop = lnh.ma_lop " +
                   "JOIN nganh ng ON ng.ma_nganh = lnh.ma_nganh " +
                   "JOIN nam_hoc nh ON lnh.nam_hoc = nh.nam_hoc " +
                   "WHERE ng.ma_nganh = :maNganh AND nh.nam_hoc = :namHoc", nativeQuery = true)
    List<Lop> layLopTheoNganhVaNamHoc(@Param("maNganh") String maNganh, @Param("namHoc") String namHoc);

    /*
     * tìm thiếu nhi trong 1 năm học
     * @Param String namHoc
     * @Param String maTN
     */
    @Query(value = "SELECT tn.* FROM thieu_nhi tn " +
                   "JOIN lop_nam_hoc_thieu_nhi lnh_tn ON tn.matn = lnh_tn.matn " +
                   "JOIN lop_nam_hoc lnh ON lnh_tn.ma_lop = lnh.ma_lop AND lnh_tn.nam_hoc = lnh.nam_hoc " +
                   "JOIN nam_hoc nh ON lnh.nam_hoc = nh.nam_hoc " +
                   "WHERE tn.matn = :maTN AND nh.nam_hoc = :namHoc", nativeQuery = true)
    Optional<ThieuNhi> timThieuNhiTrongNamHoc(@Param("maTN") String maTN, @Param("namHoc") String namHoc);

    /*
     * tìm thiếu nhi theo lớp và  năm học
     * @Param String namHoc
     * @Param String maLop
     * @Param String maTN
     */
    @Query(value = "SELECT tn.* FROM thieu_nhi tn " +
                   "JOIN lop_nam_hoc_thieu_nhi lnh_tn ON tn.matn = lnh_tn.matn " +
                   "JOIN lop_nam_hoc lnh ON lnh_tn.ma_lop = lnh.ma_lop AND lnh_tn.nam_hoc = lnh.nam_hoc " +
                   "JOIN nam_hoc nh ON lnh.nam_hoc = nh.nam_hoc " +
                   "JOIN lop l ON l.ma_lop = lnh.ma_lop " +
                   "WHERE tn.matn = :maTN AND nh.nam_hoc = :namHoc AND l.ma_lop = :maLop", nativeQuery = true)
    Optional<ThieuNhi> timThieuNhiTheoLopNamHoc(@Param("maTN") String maTN, @Param("maLop") String maLop, @Param("namHoc") String namHoc);

    /*
     * xóa huynh trưởng ra khỏi 1 lớp trong năm học
     * @Param String namHoc
     * @Param String maLop
     * @Param String maHT
     */
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM lop_nam_hoc_huynh_truong " +
                   "WHERE maht = :maHT AND ma_lop = :maLop AND nam_hoc = :namHoc",
            nativeQuery = true)
    int xoaHuynhTruongKhoiLop(@Param("maHT") String maHT, @Param("maLop") String maLop, @Param("namHoc") String namHoc);

    /*
     * chuyển thiếu nhi sang lớp khác
     * @Param String namHoc
     * @Param String maLopCu
     * @Param String maLopMoi
     * @Param String maTN
     */
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

    /*
     * lấy danh sách năm học của thiếu nhi
     * @Param String maTN
     */
    @Query(value = "SELECT DISTINCT nam_hoc FROM lop_nam_hoc_thieu_nhi " +
                   "WHERE matn = :maThieuNhi " +
                   "ORDER BY nam_hoc DESC", nativeQuery = true)
    List<String> findDanhSachNamHocByMaThieuNhi(@Param("maThieuNhi") String maThieuNhi);

    /*
     * xóa thiếu nhi ra khỏi lớp
     * @Param String maTN
     * @Param String maLop
     * @Param String namHoc
     */
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM lop_nam_hoc_thieu_nhi " +
                   "WHERE matn = :maTN AND ma_lop = :maLop AND nam_hoc = :namHoc",
            nativeQuery = true)
    int xoaThieuNhiKhoiLop(@Param("maTN") String maTN, @Param("maLop") String maLop, @Param("namHoc") String namHoc);

    /*
     * kiểm tra huynh trưởng có thuộc 1 ngành trong năm học hay không
     * @Param String maHT
     * @Param String maNganh
     * @Param String namHoc
     */
    @Query(value = "SELECT * from huynh_truong ht " +
                   "JOIN lop_nam_hoc_huynh_truong lnh_ht.maht ON lnh.ht.maht = ht.maht " +
                   "JOIN lop_nam_hoc lnh ON lnh.ma_lop = lnh_ht.ma_lop AND lnh.nam_hoc = lnh_ht.nam_hoc " +
                   "JOIN nganh ng ON lnh.ma_nganh = ng.ma_nganh " +
                   "WHERE ht.maht = :maHT AND ng.ma_nganh = :maNganh AND lnh.nam_hoc = :namHoc", nativeQuery = true)
    Optional<HuynhTruong> layHTTheoNganhNamHoc(@Param("maHT") String maHT, @Param("maNganh") String maNganh, @Param("namHoc") String namHoc);

    /*
     * kiểm tra lớp có thuộc ngành trong năm học hay không
     * @Param String maLop
     * @Param String maNganh
     * @Param String namHoc
     */
    @Query(value = "SELECT COUNT(*) FROM lop_nam_hoc lnh " +
                   "JOIN lop l ON lnh.ma_lop = l.ma_lop " +
                   "JOIN nam_hoc nh ON lnh.nam_hoc = nh.nam_hoc " +
                   "JOIN nganh ng ON lnh.ma_nganh = ng.ma_nganh " +
                   "WHERE l.ma_lop = :maLop AND nh.nam_hoc = :namHoc AND ng.ma_nganh = :maNganh",
            nativeQuery = true)
    int existsLopInNganhAndNamHoc(@Param("maLop") String maLop,
                                  @Param("maNganh") String maNganh,
                                  @Param("namHoc") String namHoc);

    /*
     * lấy số lượng của thiếu nhi của 1 lớp trong 1 năm học
     * @Param String maLop
     * @Param String namHoc
     */
    @Query(value = "SELECT count(*) from lop_nam_hoc_thieu_nhi lnh_tn " +
                   "WHERE lnh_tn.nam_hoc = :namHoc AND lnh_tn.ma_lop = :maLop ", nativeQuery = true)
    int laySoLuongTNCuaLop(@Param("maLop") String maLop, @Param("namHoc") String namHoc);

    /*
     * kiểm tra thếu nhi có thuộc ngành trong nam học hay không
     * @Param String maTN
     * @Param String namHoc
     * @Param String maNganh
     */
    @Query(value = "SELECT count(*) from lop_nam_hoc_thieu_nhi lnh_tn " +
                   "JOIN lop_nam_hoc lnh ON lnh_tn.ma_lop = lnh.ma_lop AND lnh_tn.nam_hoc = lnh.nam_hoc " +
                   "JOIN nganh ng ON lnh.ma_nganh = ng.ma_nganh " +
                   "JOIN thieu_nhi tn ON tn.matn = lnh_tn.matn " +
                   "WHERE tn.matn = :maTN AND lnh.nam_hoc = :namHoc AND ng.ma_nganh = :maNganh",
            nativeQuery = true)
    int kiemTraThieuNhiThuocNganh(@Param("maTN") String maTN, @Param("namHoc") String namHoc, @Param("maNganh") String maNganh);

    /*
     * Lấy danh sách thiếu nhi cu 1 lớp trong 1 năm học
     * @Param String maLop
     * @Param String namHoc
     * @Param Pageable
     */
    @Query(value = "SELECT tn.matn, tn.ten_thanh, tn.ho, tn.ten, tn.ngay_sinh, tn.gioi_tinh, tn.ngay_rua_toi, tn.noi_rua_toi, " +
                   "tn.ngay_ruoc_le, tn.noi_ruoc_le, tn.ngay_them_suc, tn.noi_them_suc, tn.ngay_bao_dong, tn.noi_bao_dong, " +
                   "tn.ho_ten_cha, tn.ho_ten_me, tn.so_dien_thoai_cha, tn.so_dien_thoai_me, tn.so_dien_thoai_ca_nhan, tn.trang_thai " +
                   "FROM thieu_nhi tn " +
                   "JOIN lop_nam_hoc_thieu_nhi ltn ON tn.matn = ltn.matn " +
                   "JOIN lop_nam_hoc lnh ON ltn.ma_lop = lnh.ma_lop AND ltn.nam_hoc = lnh.nam_hoc " +
                   "WHERE lnh.ma_lop = :maLop AND lnh.nam_hoc = :namHoc " +
                   "order by ten ASC ",
            countQuery = "SELECT COUNT(tn.matn) FROM thieu_nhi tn " +
                         "JOIN lop_nam_hoc_thieu_nhi ltn ON tn.matn = ltn.matn " +
                         "JOIN lop_nam_hoc lnh ON ltn.ma_lop = lnh.ma_lop AND ltn.nam_hoc = lnh.nam_hoc " +
                         "WHERE lnh.ma_lop = :maLop AND lnh.nam_hoc = :namHoc",
            nativeQuery = true)
    Page<Object[]> layDSThieuNhiByLopAndNamHoc(@Param("maLop") String maLop,
                                               @Param("namHoc") String namHoc,
                                               Pageable pageable);

    /*
     * Lấy danh sách thiếu nhi của 1 lớp trong 1 năm học
     * @Param String maLop
     * @Param String namHoc
     */
    @Query(value = "SELECT tn.matn, tn.ten_thanh, tn.ho, tn.ten, tn.ngay_sinh, tn.gioi_tinh, tn.ngay_rua_toi, tn.noi_rua_toi, " +
                   "tn.ngay_ruoc_le, tn.noi_ruoc_le, tn.ngay_them_suc, tn.noi_them_suc, tn.ngay_bao_dong, tn.noi_bao_dong, " +
                   "tn.ho_ten_cha, tn.ho_ten_me, tn.so_dien_thoai_cha, tn.so_dien_thoai_me, tn.so_dien_thoai_ca_nhan, tn.trang_thai " +
                   "FROM thieu_nhi tn " +
                   "JOIN lop_nam_hoc_thieu_nhi ltn ON tn.matn = ltn.matn " +
                   "JOIN lop_nam_hoc lnh ON ltn.ma_lop = lnh.ma_lop AND ltn.nam_hoc = lnh.nam_hoc " +
                   "WHERE lnh.ma_lop = :maLop AND lnh.nam_hoc = :namHoc " +
                   "order by ten ASC ",
            nativeQuery = true)
    List<Object[]> layDSThieuNhiByLopAndNamHocToExport(@Param("maLop") String maLop, @Param("namHoc") String namHoc);
}
