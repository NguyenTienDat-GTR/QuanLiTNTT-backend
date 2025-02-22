package com.example.quanlitntt_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SuKienDto {
    private int maSuKien;

    @NotNull(message = "Tên sự kiện không được để trống")
    private String tenSuKien;

    @NotNull(message = "Ngày tổ chức không được để trống")
    @FutureOrPresent(message = "Ngày tổ chức phải ở hiện tại hoặc tương lai")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy",timezone = "Asia/Ho_Chi_Minh")
//    @JsonDeserialize(using = LocalDateDeserializer.class)
    private Date ngayToChuc;
}
