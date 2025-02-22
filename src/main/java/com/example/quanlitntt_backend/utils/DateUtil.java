package com.example.quanlitntt_backend.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil{
    public static Date convertToDateFormat(Date inputDate) {
        if (inputDate == null) {
            return null;
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            // Chuyển đổi Date thành String theo format đầu vào
            String formattedDateStr = outputFormat.format(inputDate);

            // Chuyển lại từ String về Date với format "dd/MM/yyyy"
            return outputFormat.parse(formattedDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}

