package com.example.quanlitntt_backend.dto;

import com.example.quanlitntt_backend.entities.enums.TrangThaiDiemDanh;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChuyenCanDto {
    private int maChuyenCan;

    @PastOrPresent(message = "Ngày điểm danh phải là ngày trong quá khứ hoặc hiện tại")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy",timezone = "Asia/Ho_Chi_Minh")
//    @JsonDeserialize(using = LocalDateDeserializer.class)
    private Date ngayDiemDanh;

    private TrangThaiDiemDanh trangThai;

    private String liDoVang;

    private String maBangDiemDanh;
}
