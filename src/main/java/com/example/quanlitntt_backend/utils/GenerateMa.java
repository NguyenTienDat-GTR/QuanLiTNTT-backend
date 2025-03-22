package com.example.quanlitntt_backend.utils;

import com.example.quanlitntt_backend.repositories.ThieuNhiRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class GenerateMa {

    private static final SecureRandom random = new SecureRandom();
    private static ThieuNhiRepository thieuNhiRepository;

    @Autowired
    public void setThieuNhiRepository(ThieuNhiRepository repository) {
        thieuNhiRepository = repository;
    }

    public String generateMaHuynhTruong(@NotNull Date ngaySinh, String ngayBonMang, String soDienThoai) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String ngaySinhStr = sdf.format(ngaySinh);

        String[] ngaySinhParts = ngaySinhStr.split("/");
        String namSinh = ngaySinhParts[2].substring(2);
        String ngaySinhNum = ngaySinhParts[0];

        String[] ngayBonMangParts = ngayBonMang.split("/");
        String ngayBonMangNum = ngayBonMangParts[0];
        String thangBonMangNum = ngayBonMangParts[1];

        String soDienThoaiNum = soDienThoai.substring(soDienThoai.length() - 2);

        String rawNumber = namSinh + ngaySinhNum + ngayBonMangNum + thangBonMangNum + soDienThoaiNum;

        List<Character> chars = new ArrayList<>();
        for (char c : rawNumber.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars, random);

        StringBuilder maSo = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            maSo.append(chars.get(i));
        }

        return "HT" + maSo.toString();
    }

    public String generateMaThieuNhi(@NotNull Date ngaySinh, @NotNull String tenThanh, @NotNull String ho, @NotNull String ten) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String ngaySinhStr = sdf.format(ngaySinh);
            String rawData = tenThanh + ho + ten + ngaySinhStr;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            Random random = new Random();
            String maSo;
            int attempt = 0;

            do {
                String attemptData = rawData + attempt;
                byte[] hash = digest.digest(attemptData.getBytes(StandardCharsets.UTF_8));

                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    hexString.append(String.format("%02x", b));
                }

                List<Character> numbers = new ArrayList<>();
                for (char c : hexString.toString().toCharArray()) {
                    if (Character.isDigit(c)) {
                        numbers.add(c);
                    }
                }

                while (numbers.size() < 6) {
                    numbers.add((char) ('0' + random.nextInt(10)));
                }

                Collections.shuffle(numbers, random);

                StringBuilder maSoBuilder = new StringBuilder("TN");
                for (int i = 0; i < 6; i++) {
                    maSoBuilder.append(numbers.get(i));
                }

                maSo = maSoBuilder.toString();
                attempt++;
            } while (thieuNhiRepository.existsById(maSo));

            return maSo;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Lỗi tạo mã: " + e.getMessage());
        }
    }

    public String generateMaBangDiem(@NotNull String maTN, @NotNull String maLop, @NotNull String namHoc) {
        // Lấy 6 ký tự cuối của mã Thiếu Nhi
        String maTNCut = maTN.substring(Math.max(maTN.length() - 6, 0));

        // Lấy 4 ký tự mã năm học (chỉ lấy 2 số cuối của từng năm)
        String namHocFormatted = namHoc.substring(2, 4) + namHoc.substring(7, 9);

        // Ghép chuỗi theo định dạng BD + maTNCut + maLop + namHocFormatted
        return "BD" + maTNCut + maLop + namHocFormatted;
    }

    public String generateMaBangDiemDanh(@NotNull String maTN, @NotNull String maLop, @NotNull String namHoc) {
        // Lấy 6 ký tự cuối của mã Thiếu Nhi
        String maTNCut = maTN.substring(Math.max(maTN.length() - 6, 0));

        // Lấy 4 ký tự mã năm học (chỉ lấy 2 số cuối của từng năm)
        String namHocFormatted = namHoc.substring(2, 4) + namHoc.substring(7, 9);

        // Ghép chuỗi theo định dạng BD + maTNCut + maLop + namHocFormatted
        return "DD" + maTNCut + maLop + namHocFormatted;
    }

    public String generateMaChuyenCan(@NotNull Date ngayDiemDanh, @NotNull String maTN) {
        // Lấy 6 ký tự cuối của mã Thiếu Nhi
        String maTNCut = maTN.substring(Math.max(maTN.length() - 6, 0));

        // Lấy ngày và giờ điểm danh
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String ngayDiemDanhStr = sdf.format(ngayDiemDanh);

        String gioDiemDanhStr = new SimpleDateFormat("HHmm").format(ngayDiemDanh);
        ngayDiemDanhStr = ngayDiemDanhStr + gioDiemDanhStr;

        // Ghép chuỗi theo định dạng CC + maBangDiemDanhCut
        return "CC" + maTNCut + ngayDiemDanhStr + gioDiemDanhStr;
    }

}