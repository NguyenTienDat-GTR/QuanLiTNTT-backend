package com.example.quanlitntt_backend.entities.enums;

public enum PhieuThuong {
    A_PLUS, A, B, C;

    @Override
    public String toString() {
        return this == A_PLUS ? "A+" : name();
    }
}
