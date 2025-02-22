package com.example.quanlitntt_backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "BangDiemDanh")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BangDiemDanh {
    @Id
    private String maBangDiemDanh;

    @Column(name = "soNgayKhongPhep")
    @Min(value = 0, message = "Số ngày không phép phải lớn hơn hoặc bằng 0")
    private int soNgayKHongPhep;

    @OneToMany(mappedBy = "bangDiemDanh", fetch = FetchType.LAZY)
    private List<ChuyenCan> chuyenCans;
}
