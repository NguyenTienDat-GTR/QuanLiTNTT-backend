package com.example.quanlitntt_backend.utils;

import jakarta.validation.constraints.NotNull;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GenerateMa {

    private static final SecureRandom random = new SecureRandom();

    public String generateMaHuynhTruong(@NotNull(message = "Ngày sinh không được để trống") Date ngaySinh, String ngayBonMang, String soDienThoai) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String ngaySinhStr = sdf.format(ngaySinh);

        // Lấy 2 số cuối của năm sinh và ngày
        String[] ngaySinhParts = ngaySinhStr.split("/");
        String namSinh = ngaySinhParts[2].substring(2); // 2 số cuối của năm sinh
        String ngaySinhNum = ngaySinhParts[0]; // Ngày sinh (dd)

        // Lấy ngày & tháng từ ngày bổn mạng
        String[] ngayBonMangParts = ngayBonMang.split("/");
        String ngayBonMangNum = ngayBonMangParts[0]; // Ngày bổn mạng (dd)
        String thangBonMangNum = ngayBonMangParts[1]; // Tháng bổn mạng (MM)

        // Lấy 2 số cuối của số điện thoại
        String soDienThoaiNum = soDienThoai.substring(soDienThoai.length() - 2);

        // Tạo chuỗi gốc
        String rawNumber = namSinh + ngaySinhNum + ngayBonMangNum + thangBonMangNum + soDienThoaiNum;

        // Băm và trộn ngẫu nhiên
        List<Character> chars = new ArrayList<>();
        for (char c : rawNumber.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars, random);

        // Chỉ lấy 6 ký tự đầu tiên
        StringBuilder maSo = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            maSo.append(chars.get(i));
        }

        return "HT" + maSo.toString();
    }
}
