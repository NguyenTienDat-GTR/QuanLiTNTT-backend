package com.example.quanlitntt_backend.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "SuKien")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SuKien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maSuKien")
    private int maSuKien;

    @Column(name = "tenSuKien")
    @NotNull(message = "Tên sự kiện không được để trống")
    private String tenSuKien;

    @Column(name = "ngayToChuc")
    @NotNull(message = "Ngày tổ chức không được để trống")
    @FutureOrPresent(message = "Ngày tổ chức phải ở hiện tại hoặc tương lai")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy",timezone = "Asia/Ho_Chi_Minh")
//    @JsonDeserialize(using = LocalDateDeserializer.class)
    private Date ngayToChuc;
}
