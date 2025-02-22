package com.example.quanlitntt_backend.dto;

import com.example.quanlitntt_backend.entities.enums.VaiTro;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TaiKhoanDto {
    private String tenDangNhap;

    @NotNull(message = "Mật khẩu không được để trống")
    //mat khau co tu 6 ki tu tro len
    @Pattern(regexp = ".{6,}", message = "Mật khẩu phải có ít nhất 6 kí tự")
    private String matKhau;

    @NotNull(message = "Vai trò không được để trống")
    private VaiTro vaiTro;
}
