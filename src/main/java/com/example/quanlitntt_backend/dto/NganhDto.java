package com.example.quanlitntt_backend.dto;

import com.example.quanlitntt_backend.entities.enums.MaNganh;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NganhDto {
    private String maNganh;

    @NotNull(message = "Tên ngành không được để trống")
    private String tenNganh;
}
