# Sử dụng image base là JDK 17
FROM openjdk:17-jdk-slim

# Đặt thư mục làm việc
WORKDIR /src

# Copy file JAR vào container
COPY target/QuanLiTNTT_backend-0.0.1-SNAPSHOT.jar app.jar

# Cấu hình cổng chạy ứng dụng
EXPOSE 8000

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
