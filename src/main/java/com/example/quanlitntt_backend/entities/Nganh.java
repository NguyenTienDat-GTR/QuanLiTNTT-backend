package com.example.quanlitntt_backend.entities;

import com.example.quanlitntt_backend.entities.enums.MaNganh;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "Nganh")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Nganh {

    @Id
    @Column(name = "MaNganh")
    @Enumerated(EnumType.STRING)
    private MaNganh maNganh;

    @Column(name = "TenNganh")
    @NotNull(message = "Tên ngành không được để trống")
    private String tenNganh;
}
