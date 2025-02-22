package com.example.quanlitntt_backend.dto;

import com.example.quanlitntt_backend.entities.ChucVuHuynhTruong;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NamHocDto {
    private String namHoc;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu phải là ngày hiện tại hoặc trong tương lai")
    private LocalDate ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @FutureOrPresent(message = "Ngày kết thúc phải là ngày hiện tại hoặc trong tương lai")
    private LocalDate ngayKetThuc;

    private List<ChucVuHuynhTruong> chucVuHuynhTruongList;
}
