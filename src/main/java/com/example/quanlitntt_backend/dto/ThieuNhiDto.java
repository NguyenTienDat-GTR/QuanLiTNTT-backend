package com.example.quanlitntt_backend.dto;

import com.example.quanlitntt_backend.entities.enums.GioiTinh;
import com.example.quanlitntt_backend.entities.enums.TrinhDo;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ThieuNhiDto {
    private String maTN;

    private String tenDangNhap;

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

    @NotNull(message = "Giới tính không được để trống")
    private GioiTinh gioiTinh;

    @Past(message = "Ngày rửa tội phải nhỏ hơn ngày hiện tại")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy",timezone = "Asia/Ho_Chi_Minh")
//    @JsonDeserialize(using = LocalDateDeserializer.class)
    private Date ngayRuaToi;

    private String noiRuaToi;

    @PastOrPresent(message = "Ngày rước lễ phải nhỏ hơn hoặc bằng ngày hiện tại")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy",timezone = "Asia/Ho_Chi_Minh")
//    @JsonDeserialize(using = LocalDateDeserializer.class)
    private Date ngayRuocLe;

    private String noiRuocLe;

    @PastOrPresent(message = "Ngày thêm sức phải nhỏ hơn hoặc bằng ngày hiện tại")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy",timezone = "Asia/Ho_Chi_Minh")
//    @JsonDeserialize(using = LocalDateDeserializer.class)
    private Date ngayThemSuc;

    private String noiThemSuc;

    @PastOrPresent(message = "Ngày bao đồng phải nhỏ hơn hoặc bằng ngày hiện tại")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy",timezone = "Asia/Ho_Chi_Minh")
//    @JsonDeserialize(using = LocalDateDeserializer.class)
    private Date ngayBaoDong;

    private String noiBaoDong;

    private String hoTenCha;

    private String hoTenMe;

    @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại không hợp lệ")
    private String soDienThoaiCha;

    @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại không hợp lệ")
    private String soDienThoaiMe;

    @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại không hợp lệ")
    private String soDienThoaiCaNhan;

    @NotNull(message = "Trình độ không được để trống")
    private TrinhDo trinhDo;
}
