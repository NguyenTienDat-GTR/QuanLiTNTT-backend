package com.example.quanlitntt_backend.entities.compositeKey;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChucVuHuynhTruongKey implements Serializable {

    @Column(name = "maht")
    private String maHT;

    @Column(name = "nam_hoc")
    private String namHoc;

    @Column(name = "ma_chuc_vu")
    private String maChucVu;
}
