package com.example.quanlitntt_backend.entities;

import com.example.quanlitntt_backend.entities.compositeKey.ChucVuHuynhTruongKey;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ChucVu_HuynhTruong", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"maHT", "namHoc"})
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
//    @JoinColumn(name = "maHT")
    private HuynhTruong huynhTruong;

    @ManyToOne
    @MapsId("namHoc")
//    @JoinColumn(name = "namHoc")
    private NamHoc namHoc;

    @ManyToOne
    @MapsId("maChucVu")
//    @JoinColumn(name = "maChucVu")
    private ChucVu chucVu;
}

