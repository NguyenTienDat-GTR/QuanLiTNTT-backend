package com.example.quanlitntt_backend.entities.compositeKey;

import com.example.quanlitntt_backend.entities.enums.MaNganh;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SuKienNganhKey implements Serializable {
    @Column(name = "maNganh")
    @Enumerated(EnumType.STRING)
    private MaNganh maNganh;

    @Column(name = "namHoc")
    private String namHoc;

    @Column(name = "maSuKien")
    private int maSuKien;
}
