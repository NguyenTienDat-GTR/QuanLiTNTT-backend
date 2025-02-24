package com.example.quanlitntt_backend.entities;

import com.example.quanlitntt_backend.entities.compositeKey.ChucVuHuynhTruongKey;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chuc_vu_huynh_truong", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"maht", "nam_hoc"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChucVuHuynhTruong {

    @EmbeddedId
    private ChucVuHuynhTruongKey maCV_HT;

    @ManyToOne
    @MapsId("maHT")
//    @JoinColumn(name = "maht")
    private HuynhTruong huynhTruong;

    @ManyToOne
    @MapsId("namHoc")
//    @JoinColumn(name = "nam_hoc")
    private NamHoc namHoc;

    @ManyToOne
    @MapsId("maChucVu")
//    @JoinColumn(name = "ma_chuc_vu")
    private ChucVu chucVu;
}

