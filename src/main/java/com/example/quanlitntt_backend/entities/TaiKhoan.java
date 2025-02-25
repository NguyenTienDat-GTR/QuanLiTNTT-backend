package com.example.quanlitntt_backend.entities;

import com.example.quanlitntt_backend.entities.enums.VaiTro;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "TaiKhoan")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TaiKhoan {

    @Id
    @Column(name = "tenDangNhap")
    private String tenDangNhap;

    @OneToOne(mappedBy = "taiKhoan", cascade = CascadeType.ALL, optional = true)
    private HuynhTruong huynhTruong;


    @OneToOne(mappedBy = "taiKhoan", cascade = CascadeType.ALL, optional = true)
    @JsonIgnore
    private ThieuNhi thieuNhi;


    @Column(name = "matKhau")
    @NotNull(message = "Mật khẩu không được để trống")
    //mat khau co tu 6 ki tu tro len
    @Pattern(regexp = ".{6,}", message = "Mật khẩu phải có ít nhất 6 kí tự")
    private String matKhau;

    @Column(name = "VaiTro")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Vai trò không được để trống")
    private VaiTro vaiTro;
}
