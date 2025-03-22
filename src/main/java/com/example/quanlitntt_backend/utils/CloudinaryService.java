package com.example.quanlitntt_backend.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(@Value("${CLOUDINARY_URL}") String cloudinaryUrl) {
        this.cloudinary = new Cloudinary(cloudinaryUrl);
    }

    // Upload file lên Cloudinary
    public String uploadFile(String fileName, byte[] fileData, String folderName) throws IOException {
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "public_id", fileName,               // Chỉ truyền tên file, không ghép folder
                "folder", folderName,                // Thêm tham số folder để Cloudinary tự tạo hoặc chọn folder
                "overwrite", true
        );


        Map<String, Object> uploadResult = cloudinary.uploader().upload(fileData, uploadParams);

        return (String) uploadResult.get("secure_url");
    }

    // Kiểm tra và thay thế file nếu tồn tại
    public String checkAndReplaceFile(String ma, byte[] fileData, String folderName) throws IOException {
        String fileName = folderName + "/" + ma;

        try {
            cloudinary.uploader().destroy(fileName, ObjectUtils.emptyMap());
        } catch (Exception ignored) {
            // Nếu không tồn tại thì bỏ qua lỗi
        }

        // Upload file mới lên
        return uploadFile(ma, fileData, folderName);
    }
}
