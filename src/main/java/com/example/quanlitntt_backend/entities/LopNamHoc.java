package com.example.quanlitntt_backend.entities;

import com.example.quanlitntt_backend.entities.compositeKey.LopNamHocKey;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @MapsId("maLop")
    private Lop lop;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("namHoc")
    private NamHoc namHoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNganh")
    protected Nganh nganh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maHT")
    protected HuynhTruong huynhTruong;

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
