package com.example.quanlitntt_backend.dto;

import com.example.quanlitntt_backend.entities.enums.MaNganh;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LopNamHocDto {
    private String maLop;
    private String tenLop;
    private String namHoc;
    private MaNganh maNganh;
    private String tenNganh;
    private String maHT;
    private String tenThanhHT;
    private String hoHt;
    private String tenHT;
    private String maTN;
    private String tenThanhTN;
    private String hoTN;
    private String tenTN;
    private String maBangDiemDanh;
    private String maBangDiem;

}
