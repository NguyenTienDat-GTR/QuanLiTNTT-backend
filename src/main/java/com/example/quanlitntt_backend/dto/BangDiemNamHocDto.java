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
public class BangDiemNamHocDto {

    private String maBangDiem;

    private String namHoc;

    private Double diemKT_HKI;

    private Double diemThiGL_HKI;

    private Double diemThiTN_HKI;

    private Double diemTB_HKI;

    private PhieuThuong phieuThuong;

    private Double diemKT_HKII;

    private Double diemThiGL_HKII;

    private Double diemThiTN_HKII;

    private Double diemTB_HKII;

    private Double diemTBCN;

    private XepLoai xepLoai;

    private KetQuaHocTap ketQua;
}
