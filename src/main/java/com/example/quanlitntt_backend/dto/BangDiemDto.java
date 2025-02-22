package com.example.quanlitntt_backend.dto;

import com.example.quanlitntt_backend.entities.enums.KetQuaHocTap;
import com.example.quanlitntt_backend.entities.enums.PhieuThuong;
import com.example.quanlitntt_backend.entities.enums.XepLoai;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BangDiemDto {
    private String maBangDiem;
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemKT_HKI;

    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemThiGL_HKI;

    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemThiTN_HKI;

    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemTB_HKI;

    private PhieuThuong phieuThuong;

    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemKT_HKII;

    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemThiGL_HKII;

    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemThiTN_HKII;

    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemTB_HKII;

    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemTBCN;

    private XepLoai xepLoai;

    private KetQuaHocTap ketQua;
}
