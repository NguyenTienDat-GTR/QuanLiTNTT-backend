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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maTN")
    protected ThieuNhi thieuNhi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maBangDiemDanh")
    protected BangDiemDanh bangDiemDanh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maBangDiem")
    protected BangDiem bangDiem;

}
