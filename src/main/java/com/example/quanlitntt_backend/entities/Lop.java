package com.example.quanlitntt_backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "Lop")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Lop {

    @Id
    @Column(name = "MaLop")
    private String maLop;

    @Column(name = "TenLop")
    @NotNull(message = "Tên lớp không được để trống")
    private String tenLop;


}
