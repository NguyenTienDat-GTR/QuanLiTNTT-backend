package com.example.quanlitntt_backend.entities;

import com.example.quanlitntt_backend.entities.enums.KetQuaHocTap;
import com.example.quanlitntt_backend.entities.enums.PhieuThuong;
import com.example.quanlitntt_backend.entities.enums.XepLoai;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "BangDiem")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BangDiem {
    @Id
    @Column(name = "maBangDiem")
    private String maBangDiem;

    @Column(name = "diemKT_HKI")
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemKT_HKI;

    @Column(name = "diemThiGL_HKI")
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemThiGL_HKI;

    @Column(name = "diemThiTN_HKI")
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemThiTN_HKI;

    @Column(name = "diemTB_HKI")
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemTB_HKI;

    @Column(name = "phieuThuong")
    @Enumerated(EnumType.STRING)
    private PhieuThuong phieuThuong;

    @Column(name = "diemKT_HKII")
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemKT_HKII;

    @Column(name = "diemThiGL_HKII")
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemThiGL_HKII;

    @Column(name = "diemThiTN_HKII")
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemThiTN_HKII;

    @Column(name = "diemTB_HKII")
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemTB_HKII;

    @Column(name = "diemTBCN")
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không lớn hơn 10")
    private double diemTBCN;

    @Column(name = "xepLoai")
    @Enumerated(EnumType.STRING)
    private XepLoai xepLoai;

    @Column(name = "ketQua")
    @Enumerated(EnumType.STRING)
    private KetQuaHocTap ketQua;

    @OneToOne
    @JoinColumn(name = "maTN", nullable = false, unique = true)
    private ThieuNhi thieuNhi;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "maLop", referencedColumnName = "maLop"),
            @JoinColumn(name = "namHoc", referencedColumnName = "namHoc")
    })
    private LopNamHoc lopNamHoc;

}
