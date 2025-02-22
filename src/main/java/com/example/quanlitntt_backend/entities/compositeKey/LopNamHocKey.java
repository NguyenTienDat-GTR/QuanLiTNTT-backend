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
public class LopNamHocKey implements Serializable {

    @Column(name = "maLop")
    private String maLop;

    @Column(name = "namHoc")
    private String namHoc;
}
