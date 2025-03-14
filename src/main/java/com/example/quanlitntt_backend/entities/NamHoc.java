package com.example.quanlitntt_backend.entities;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.Date;

import java.util.List;

@Entity
@Table(name = "NamHoc")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NamHoc {
    @Getter
    @Id
    @Column(name = "namHoc")
    private String namHoc;

    @Getter
    @Column(name = "ngayBatDau")
    @NotNull(message = "Ngày bắt đầu không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu phải là ngày hiện tại hoặc trong tương lai")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date ngayBatDau;

    @Getter
    @Column(name = "ngayKetThuc")
    @NotNull(message = "Ngày kết thúc không được để trống")
    @FutureOrPresent(message = "Ngày kết thúc phải là ngày hiện tại hoặc trong tương lai")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date ngayKetThuc;

    @OneToMany(mappedBy = "namHoc", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<ChucVuHuynhTruong> chucVuHuynhTruongList = new ArrayList<>();

    @Column(name = "namHienTai", columnDefinition = "BOOLEAN DEFAULT true")
    private boolean namHienTai;

}
