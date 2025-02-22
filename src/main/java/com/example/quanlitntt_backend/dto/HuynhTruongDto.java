package com.example.quanlitntt_backend.dto;

import com.example.quanlitntt_backend.entities.enums.CapSao;
import com.example.quanlitntt_backend.entities.enums.GioiTinh;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HuynhTruongDto {
    private String maHT;

    @NotNull(message = "Tên thánh không được để trống")
    private String tenThanh;

    @NotNull(message = "Họ không được để trống")
    private String ho;

    @NotNull(message = "Tên không được để trống")
    private String ten;

    @NotNull(message = "Ngày sinh không được để trống")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy",timezone = "Asia/Ho_Chi_Minh")
//    @JsonDeserialize(using = LocalDateDeserializer.class)
    private Date ngaySinh;

    @NotNull(message = "Ngày bổn mạng không được để trống")
    private String ngayBonMang;

    @NotNull(message = "Giới tính không được để trống")
    private GioiTinh gioiTinh;

    @NotNull(message = "Hình ảnh không được để trống")
    private String hinhAnh;

    @NotNull(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại không hợp lệ")
    private String soDienThoai;

    @NotNull(message = "Email không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email không hợp lệ")
    private String email;

    @NotNull(message = "Cấp sao không được để trống")
    private CapSao capSao;

    @NotNull(message = "Trạng thái không được để trống")
    private boolean hoatDong = true;

    private String tenDangNhap;

    private List<String> danhSachChucVu;
}
