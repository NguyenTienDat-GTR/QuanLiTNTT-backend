package com.example.quanlitntt_backend.entities;

import com.example.quanlitntt_backend.entities.compositeKey.LopNamHocKey;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Lop_NamHoc", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"maLop", "namHoc"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LopNamHoc {

    @EmbeddedId
    private LopNamHocKey maLop_NamHoc;

    @ManyToOne
//    @MapsId("namHoc")
//    @JoinColumn(name = "nam_hoc")
    private NamHoc namHoc;

    @ManyToOne
//    @MapsId("maLop")
//    @JoinColumn(name = "ma_lop")
    private Lop lop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNganh")
    protected Nganh nganh;

    @ManyToMany
    @JoinTable(
            name = "LopNamHoc_HuynhTruong",
            joinColumns = {
                    @JoinColumn(name = "maLop", referencedColumnName = "maLop"),
                    @JoinColumn(name = "namHoc", referencedColumnName = "namHoc")
            },
            inverseJoinColumns = @JoinColumn(name = "maHT")
    )
    private List<HuynhTruong> danhSachHuynhTruong = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "LopNamHoc_ThieuNhi",
            joinColumns = {
                    @JoinColumn(name = "maLop", referencedColumnName = "maLop"),
                    @JoinColumn(name = "namHoc", referencedColumnName = "namHoc")
            },
            inverseJoinColumns = @JoinColumn(name = "maTN")
    )
    private List<ThieuNhi> danhSachThieuNhi = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maBangDiemDanh")
    protected BangDiemDanh bangDiemDanh;

    @OneToMany(mappedBy = "lopNamHoc", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BangDiem> bangDiemList = new ArrayList<>();

}
