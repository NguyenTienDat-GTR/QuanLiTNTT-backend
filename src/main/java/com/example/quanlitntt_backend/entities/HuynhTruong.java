package com.example.quanlitntt_backend.entities;

import com.example.quanlitntt_backend.entities.enums.CapSao;
import com.example.quanlitntt_backend.entities.enums.GioiTinh;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "huynhTruong")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HuynhTruong {
    @Id
    @Column(name = "maHT")
    private String maHT;

    @OneToOne
    @JoinColumn(name = "taiKhoanId", referencedColumnName = "tenDangNhap", unique = true, nullable = true)
    private TaiKhoan taiKhoan;

    @Column(name = "tenThanh")
    @NotNull(message = "Tên thánh không được để trống")
    private String tenThanh;

    @Column(name = "ho")
    @NotNull(message = "Họ không được để trống")
    private String ho;

    @Column(name = "ten")
    @NotNull(message = "Tên không được để trống")
    private String ten;

    @Column(name = "ngaySinh")
    @NotNull(message = "Ngày sinh không được để trống")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy",timezone = "Asia/Ho_Chi_Minh")
//    @JsonDeserialize(using = LocalDateDeserializer.class)
    private Date ngaySinh;

    @Column(name = "ngayBonMang")
    @NotNull(message = "Ngày bổn mạng không được để trống")
    private String ngayBonMang;

    @Column(name = "gioiTinh")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Giới tính không được để trống")
    private GioiTinh gioiTinh;

    @Column(name = "hinhAnh")
    @NotNull(message = "Hình ảnh không được để trống")
    private String hinhAnh;

    @Column(name = "soDienThoai", unique = true)
    @NotNull(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại không hợp lệ")
    private String soDienThoai;

    @Column(name = "email",unique = true)
    @NotNull(message = "Email không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email không hợp lệ")
    private String email;

    @Column(name = "capSao")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Cấp sao không được để trống")
    private CapSao capSao;

    @Column(name = "hoatDong", nullable = false, columnDefinition = "boolean default true")
    @NotNull(message = "Trạng thái không được để trống")
    private boolean hoatDong = true;

    @OneToMany(mappedBy = "huynhTruong", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChucVuHuynhTruong> chucVuHuynhTruongList = new ArrayList<>();
}
