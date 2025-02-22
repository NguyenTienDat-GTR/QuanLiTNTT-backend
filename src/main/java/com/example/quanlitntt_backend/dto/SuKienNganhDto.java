package com.example.quanlitntt_backend.dto;

import com.example.quanlitntt_backend.entities.enums.MaNganh;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SuKienNganhDto {
    private MaNganh maNganh;
    private String tenNganh;
    private String namHoc;
    private int maSuKien;
    private String tenSuKien;
    private String maTN;
    private String maBangDiemDanh;

}
