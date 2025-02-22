package com.example.quanlitntt_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LopDto {
    private String maLop;

    @NotNull(message = "Tên lớp không được để trống")
    private String tenLop;
}
