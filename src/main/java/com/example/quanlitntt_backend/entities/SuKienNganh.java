package com.example.quanlitntt_backend.entities;

import com.example.quanlitntt_backend.entities.compositeKey.SuKienNganhKey;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SuKien_Nganh", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"maNganh", "namHoc", "maSuKien"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuKienNganh {
    @EmbeddedId
    private SuKienNganhKey maSK_Nganh;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maNganh")
    private Nganh nganh;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("namHoc")
    private NamHoc namHoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maSuKien")
    private SuKien suKien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maTN")
    protected ThieuNhi thieuNhi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maBangDiemDanh")
    protected BangDiemDanh bangDiemDanh;
}
