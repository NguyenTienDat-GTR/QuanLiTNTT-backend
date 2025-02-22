package com.example.quanlitntt_backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ChucVu")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChucVu {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "maChucVu")
    private String maChucVu;

    @Column(name = "tenChucVu")
    @NotNull(message = "Tên chức vụ không được để trống")
    private String tenChucVu;

    @OneToMany(mappedBy = "chucVu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChucVuHuynhTruong> chucVuHuynhTruongList = new ArrayList<>();
}
