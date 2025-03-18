package com.example.quanlitntt_backend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

@Service
public class WasabiService {

    private S3Client s3Client; // Thay đổi từ final thành biến thường để cập nhật khi gặp lỗi 307
    private final S3Presigner presigner;
    private final AwsBasicCredentials credentials;
    private final String region;

    @Value("${WASABI_BUCKET_NAME}")
    private String bucketName;

    public WasabiService(
            @Value("${WASABI_ACCESS_KEY}") String accessKey,
            @Value("${WASABI_SECRET_KEY}") String secretKey,
            @Value("${WASABI_REGION}") String region,
            @Value("${WASABI_ENDPOINT_URL}") String endpointUrl) {

        this.region = region;
        this.credentials = AwsBasicCredentials.create(accessKey, secretKey);

        s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .httpClient(ApacheHttpClient.builder().build()) // Sử dụng ApacheHttpClient
                .endpointOverride(URI.create(endpointUrl))
                .forcePathStyle(true) // Sử dụng Path-style Access
                .build();

        presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(endpointUrl))
                .build();
    }

    // Upload file lên Wasabi
    public String uploadFile(String fileName, byte[] fileData) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .acl("public-read") // Công khai file
//                .contentType(Files.probeContentType(Paths.get(fileName))) // Định dạng file
                .contentType("image/jpeg")
                .build();


        try {
            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(fileData));
            return fileName;
        } catch (S3Exception e) {
            if (e.statusCode() == 307) {
                // Bắt lỗi 307 và trích xuất URL tạm thời
                String temporaryEndpoint = e.awsErrorDetails().sdkHttpResponse().headers().get("Location").get(0);
                s3Client = S3Client.builder()
                        .region(Region.of(region))
                        .credentialsProvider(StaticCredentialsProvider.create(credentials))
                        .httpClient(ApacheHttpClient.builder().build())
                        .endpointOverride(URI.create(temporaryEndpoint))
                        .forcePathStyle(true)
                        .build();

                try {
                    // Thử upload lại với URL tạm thời
                    s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(fileData));
                    return fileName;
                } catch (S3Exception retryException) {
                    throw new RuntimeException("Lỗi khi upload file sau khi chuyển hướng: " + retryException.awsErrorDetails().errorMessage());
                }
            }
            throw new RuntimeException("Lỗi khi upload file: " + e.awsErrorDetails().errorMessage());
        }
    }

    // Tạo URL truy cập tạm thời (Pre-signed URL) dành cho ảnh
    public String generatePreSignedUrl(String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(12))  // Thời gian URL có hiệu lực (12 giờ)
                .getObjectRequest(getObjectRequest)
                .build();

        URL presignedUrl = presigner.presignGetObject(presignRequest).url();
        return presignedUrl.toString();
    }

    // Kiểm tra và thay thế file đã tồn tại trên Wasabi
    public String checkAndReplaceFile(String maHT, byte[] fileData) throws IOException {
        String folder = "avatar_HT/";
        String fileName = folder + maHT;

        // Kiểm tra xem file có tồn tại không
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.headObject(headObjectRequest);

            // Nếu không ném ra lỗi thì file đã tồn tại -> Tiến hành xóa file cũ
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (NoSuchKeyException e) {
            // File không tồn tại, không cần xóa
        } catch (S3Exception e) {
            throw new RuntimeException("Lỗi khi kiểm tra file trên Wasabi: " + e.awsErrorDetails().errorMessage());
        }

        // Upload file mới lên
        return uploadFile(fileName, fileData);
    }

}
