package com.example.quanlitntt_backend.utils;

import jakarta.validation.constraints.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

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

    public String generateMaThieuNhi(@NotNull(message = "Ngày sinh không được để trống") Date ngaySinh,
                                     @NotNull(message = "Tên thánh không được để trống") String tenThanh,
                                     @NotNull(message = "Họ không được để trống") String ho,
                                     @NotNull(message = "Tên không được để trống") String ten,
                                     Function<String, Boolean> checkExists) { // Truyền vào một hàm kiểm tra mã tồn tại
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String ngaySinhStr = sdf.format(ngaySinh);
            String rawData = tenThanh + ho + ten + ngaySinhStr;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            Random random = new Random();
            String maSo;
            int attempt = 0;

            do {
                // Băm dữ liệu với attempt để tránh trùng lặp
                String attemptData = rawData + attempt;
                byte[] hash = digest.digest(attemptData.getBytes(StandardCharsets.UTF_8));

                // Chuyển đổi sang chuỗi hex
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    hexString.append(String.format("%02x", b));
                }

                // Lấy 6 ký tự số từ chuỗi hex
                List<Character> numbers = new ArrayList<>();
                for (char c : hexString.toString().toCharArray()) {
                    if (Character.isDigit(c)) {
                        numbers.add(c);
                    }
                }

                // Nếu không đủ số, thêm số ngẫu nhiên
                while (numbers.size() < 6) {
                    numbers.add((char) ('0' + random.nextInt(10)));
                }

                // Trộn ngẫu nhiên danh sách số
                Collections.shuffle(numbers, random);

                // Lấy 6 ký tự đầu tiên
                StringBuilder maSoBuilder = new StringBuilder("TN");
                for (int i = 0; i < 6; i++) {
                    maSoBuilder.append(numbers.get(i));
                }

                maSo = maSoBuilder.toString();
                attempt++;
            } while (checkExists.apply(maSo)); // Kiểm tra mã có tồn tại không, nếu có thì thử lại với `attempt` khác

            return maSo;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Lỗi tạo mã: " + e.getMessage());
        }
    }

}
