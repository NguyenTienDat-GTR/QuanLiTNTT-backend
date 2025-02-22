package com.example.quanlitntt_backend.entities;

import com.example.quanlitntt_backend.entities.enums.TrangThaiDiemDanh;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "ChuyenCan")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChuyenCan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaChuyenCan")
    private int maChuyenCan;

    @Column(name = "ngayDiemDanh")
    @PastOrPresent(message = "Ngày điểm danh phải là ngày trong quá khứ hoặc hiện tại")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy",timezone = "Asia/Ho_Chi_Minh")
//    @JsonDeserialize(using = LocalDateDeserializer.class)
    private Date ngayDiemDanh;

    @Column(name = "trangThai")
    @Enumerated(EnumType.STRING)
    private TrangThaiDiemDanh trangThai;

    @Column(name = "liDoVang")
    private String liDoVang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maBangDiemDanh")
    protected BangDiemDanh bangDiemDanh;

}
