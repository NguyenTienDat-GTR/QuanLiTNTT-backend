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

    @Column(name = "maHT", length = 8)
    private String maHT;

    @Column(name = "namHoc", length = 9)
    private String namHoc;

    @Column(name = "maChucVu", length = 4)
    private String maChucVu;
}
