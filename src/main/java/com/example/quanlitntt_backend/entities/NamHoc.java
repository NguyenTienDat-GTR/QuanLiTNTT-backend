package com.example.quanlitntt_backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private LocalDate ngayBatDau;

    @Getter
    @Column(name = "ngayKetThuc")
    @NotNull(message = "Ngày kết thúc không được để trống")
    @FutureOrPresent(message = "Ngày kết thúc phải là ngày hiện tại hoặc trong tương lai")
    private LocalDate ngayKetThuc;

    @OneToMany(mappedBy = "namHoc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChucVuHuynhTruong> chucVuHuynhTruongList = new ArrayList<>();

}
