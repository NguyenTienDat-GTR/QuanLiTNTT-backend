package com.example.quanlitntt_backend.dto;

import com.example.quanlitntt_backend.entities.ChucVuHuynhTruong;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChucVuDto {
    private String maChucVu;

    @NotNull(message = "Tên chức vụ không được để trống")
    private String tenChucVu;

    private List<ChucVuHuynhTruong> chucVuHuynhTruongList = new ArrayList<>();
}
