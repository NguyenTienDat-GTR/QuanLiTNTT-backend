package com.example.quanlitntt_backend.entities;

import com.example.quanlitntt_backend.entities.enums.GioiTinh;
import com.example.quanlitntt_backend.entities.enums.TrangThaiHocVu;
import com.example.quanlitntt_backend.entities.enums.TrinhDo;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "thieuNhi")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ThieuNhi {
    @Id
    @Column(name = "maTN")
    private String maTN;

    @OneToOne
    @JoinColumn(name = "matn", referencedColumnName = "tenDangNhap", nullable = true)
    private TaiKhoan taiKhoan;

    @Column(name = "tenThanh")
    private String tenThanh;

    @Column(name = "ho")
    @NotNull(message = "Họ không được để trống")
    private String ho;

    @Column(name = "ten")
    @NotNull(message = "Tên không được để trống")
    private String ten;

    @Column(name = "ngaySinh")
    @NotNull(message = "Ngày sinh không được để trống")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date ngaySinh;

    @Column(name = "gioiTinh")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Giới tính không được để trống")
    private GioiTinh gioiTinh;

    @Column(name = "ngayRuaToi")
    @Past(message = "Ngày rửa tội phải nhỏ hơn ngày hiện tại")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date ngayRuaToi;

    @Column(name = "noiRuaToi")
    private String noiRuaToi;

    @Column(name = "ngayRuocLe")
    @PastOrPresent(message = "Ngày rước lễ phải nhỏ hơn hoặc bằng ngày hiện tại")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date ngayRuocLe;

    @Column(name = "noiRuocLe")
    private String noiRuocLe;

    @Column(name = "ngayThemSuc")
    @PastOrPresent(message = "Ngày thêm sức phải nhỏ hơn hoặc bằng ngày hiện tại")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date ngayThemSuc;

    @Column(name = "noiThemSuc")
    private String noiThemSuc;

    @Column(name = "ngayBaoDong")
    @PastOrPresent(message = "Ngày bao đồng phải nhỏ hơn hoặc bằng ngày hiện tại")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date ngayBaoDong;

    @Column(name = "noiBaoDong")
    private String noiBaoDong;

    @Column(name = "hoTenCha")
    private String hoTenCha;

    @Column(name = "hoTenMe")
    private String hoTenMe;

    @Column(name = "soDienThoaiCha")
    @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại không hợp lệ")
    private String soDienThoaiCha;

    @Column(name = "soDienThoaiMe")
    @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại không hợp lệ")
    private String soDienThoaiMe;

    @Column(name = "soDienThoaiCaNhan")
    @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại không hợp lệ")
    private String soDienThoaiCaNhan;

    @Column(name = "trangThai")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Trạng tha không được để trống")
    private TrangThaiHocVu trangThai;

    @OneToOne(mappedBy = "thieuNhi", cascade = CascadeType.ALL, orphanRemoval = true)
    private BangDiem bangDiem;

}
