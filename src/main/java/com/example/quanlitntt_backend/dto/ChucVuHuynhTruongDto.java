package com.example.quanlitntt_backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChucVuHuynhTruongDto {
    private String maHT;
    private List<String> listMaHT;
    private String tenThanh;
    private String ho;
    private String ten;
    private String namHoc;
    private String maChucVu;
    private String tenChucVu;
}
