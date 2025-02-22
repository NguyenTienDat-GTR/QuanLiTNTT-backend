package com.example.quanlitntt_backend.dto;

import com.example.quanlitntt_backend.entities.ChuyenCan;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BangDiemDanhDto {
    private String maBangDiemDanh;

    @Min(value = 0, message = "Số ngày không phép phải lớn hơn hoặc bằng 0")
    private int soNgayKhongPhep;

    private List<ChuyenCan> chuyenCans;
}
