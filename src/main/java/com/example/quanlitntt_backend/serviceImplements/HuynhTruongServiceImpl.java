package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.dto.HuynhTruongDto;
import com.example.quanlitntt_backend.entities.HuynhTruong;
import com.example.quanlitntt_backend.entities.enums.CapSao;
import com.example.quanlitntt_backend.entities.enums.GioiTinh;
import com.example.quanlitntt_backend.repositories.HuynhTruongRepository;
import com.example.quanlitntt_backend.repositories.TaiKhoanRepository;
import com.example.quanlitntt_backend.services.HuynhTruongService;
import com.example.quanlitntt_backend.utils.DateUtil;
import com.example.quanlitntt_backend.utils.ExcelUtil;
import com.example.quanlitntt_backend.utils.GenerateMa;
import com.example.quanlitntt_backend.utils.WasabiService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class HuynhTruongServiceImpl implements HuynhTruongService {
    @Autowired
    private HuynhTruongRepository huynhTruongRepository;


    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private WasabiService wasabiService;

    private static final Logger logger = Logger.getLogger(HuynhTruongServiceImpl.class.getName());

    private final ExecutorService executor = Executors.newFixedThreadPool(10); // Tối ưu hóa bằng cách sử dụng 10 luồng


    private final GenerateMa generateMa = new GenerateMa();


    @Override
    public HuynhTruong addHuynhTruong(HuynhTruongDto huynhTruongDTO) {
        HuynhTruong ht = new HuynhTruong();


        Date ngaySinhFormatted = DateUtil.convertToDateFormat(huynhTruongDTO.getNgaySinh());

        // Tạo mã HuynhTruong tự động
        String maHT = generateMa.generateMaHuynhTruong(
                ngaySinhFormatted,
                huynhTruongDTO.getNgayBonMang(),
                huynhTruongDTO.getSoDienThoai()
        );
        ht.setMaHT(maHT);

        setValueForHuynhTruong(huynhTruongDTO, ht, ngaySinhFormatted);
        ht.setHoatDong(true);

        return huynhTruongRepository.save(ht);
    }

    private void setValueForHuynhTruong(HuynhTruongDto huynhTruongDTO, HuynhTruong ht, Date ngaySinhFormatted) {
        ht.setTenThanh(huynhTruongDTO.getTenThanh());
        ht.setHo(huynhTruongDTO.getHo());
        ht.setTen(huynhTruongDTO.getTen());
        ht.setCapSao(huynhTruongDTO.getCapSao());
        ht.setSoDienThoai(huynhTruongDTO.getSoDienThoai());
        ht.setEmail(huynhTruongDTO.getEmail());
        ht.setGioiTinh(huynhTruongDTO.getGioiTinh());
        ht.setNgaySinh(ngaySinhFormatted);
        ht.setNgayBonMang(huynhTruongDTO.getNgayBonMang());
        ht.setHinhAnh(huynhTruongDTO.getHinhAnh());
    }

    @Override
    public HuynhTruong updateHuynhTruong(HuynhTruongDto huynhTruongDTO, String maHT) {

        if (huynhTruongRepository.existsById(maHT)) {
            HuynhTruong ht = huynhTruongRepository.findById(maHT).get();

            Date ngaySinhFormatted = DateUtil.convertToDateFormat(huynhTruongDTO.getNgaySinh());

            setValueForHuynhTruong(huynhTruongDTO, ht, ngaySinhFormatted);

            return huynhTruongRepository.save(ht);
        }

        return null;
    }

    @Override
    public void deleteHuynhTruong(String maHT) {
        Optional<HuynhTruong> ht = huynhTruongRepository.findById(maHT);

        if (!ht.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy Huynh Trưởng với mã " + maHT);
        } else if (!ht.get().isHoatDong()) {
            throw new IllegalArgumentException("Huynh Trưởng đã bị vô hiệu hóa");
        }

        ht.get().setHoatDong(false);
        huynhTruongRepository.save(ht.get());
        taiKhoanRepository.findById(maHT).ifPresent(taiKhoan -> {
            taiKhoan.setHoatDong(false);
            taiKhoanRepository.save(taiKhoan);
        });
    }

    @Override
    public Optional<HuynhTruong> getHuynhTruongByMa(String maHT) {
        Optional<HuynhTruong> huynhTruong = huynhTruongRepository.findById(maHT);

        if (!huynhTruong.isPresent()) {
            return Optional.empty();
        }

        return huynhTruong;

    }

    @Override
    public Page<HuynhTruong> getHuynhTruongByTen(String tenHT, Pageable pageable) {

        if (tenHT != null && !tenHT.isEmpty()) {
            return huynhTruongRepository.getHuynhTruongByTen(tenHT, pageable);
        }

        return Page.empty();
    }

    @Override
    public Page<HuynhTruong> getAllHuynhTruong(Pageable pageable, String sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return huynhTruongRepository.getAllHuynhTruong(pageable);
        }
        return huynhTruongRepository.getAllHuynhTruongSorted(sortBy, pageable);
    }

    // Hàm hỗ trợ lấy giá trị kiểu String từ ô Excel
    private String getStringCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return "";
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    @Async("asyncExecutor")
    public CompletableFuture<Void> addHuynhTruongFromExcel(MultipartFile file) {
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            List<Row> rows = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề
                rows.add(row);
            }
            workbook.close();

            // Chia danh sách hàng thành các nhóm nhỏ để xử lý song song
            int batchSize = 100; // Số hàng mỗi nhóm
            List<List<Row>> partitions = IntStream.range(0, (rows.size() + batchSize - 1) / batchSize)
                    .mapToObj(i -> rows.subList(i * batchSize, Math.min(rows.size(), (i + 1) * batchSize)))
                    .toList();

            // Xử lý song song
            List<CompletableFuture<Void>> futures = partitions.stream()
                    .map(this::processBatch)
                    .toList();

            // Chờ tất cả tác vụ hoàn thành
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.failedFuture(new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage()));
        }
    }

    @Async("asyncExecutor")
    public CompletableFuture<Void> processBatch(List<Row> rows) {
        List<HuynhTruongDto> danhSachHuynhTruong = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        for (Row row : rows) {
            try {
                HuynhTruongDto dto = new HuynhTruongDto();
                dto.setTenThanh(getStringCellValue(row, 0).trim());
                dto.setHo(getStringCellValue(row, 1).trim());
                dto.setTen(getStringCellValue(row, 2).trim());

                String ngaySinhStr = ExcelUtil.getDateCellValue(row, 3).trim();
                if (ngaySinhStr.isEmpty()) {
                    throw new IllegalArgumentException("Ngày sinh không được để trống tại dòng " + row.getRowNum());
                }
                dto.setNgaySinh(dateFormat.parse(ngaySinhStr));

                dto.setNgayBonMang(getStringCellValue(row, 4).trim());

                String gioiTinhStr = getStringCellValue(row, 5).trim().toUpperCase();
                dto.setGioiTinh(GioiTinh.valueOf(gioiTinhStr));

                dto.setHinhAnh(getStringCellValue(row, 6).trim());
                dto.setSoDienThoai(getStringCellValue(row, 7).trim());
                dto.setEmail(getStringCellValue(row, 8).trim());

                String capSaoStr = getStringCellValue(row, 9).trim().toUpperCase();
                dto.setCapSao(CapSao.valueOf(capSaoStr));

                dto.setHoatDong(true);
                danhSachHuynhTruong.add(dto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Lưu danh sách vào database
        if (!danhSachHuynhTruong.isEmpty()) {
            huynhTruongRepository.saveAll(danhSachHuynhTruong.stream()
                    .map(ExcelUtil::convertFileHuynhTruongToEntity)
                    .collect(Collectors.toList()));
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void activeHuynhTruong(String maHT) {

        Optional<HuynhTruong> ht = huynhTruongRepository.findById(maHT);

        if (!ht.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy Huynh Trưởng với mã " + maHT);
        } else if (ht.get().isHoatDong()) {
            throw new IllegalArgumentException("Huynh Trưởng đã hoạt động");
        }

        ht.get().setHoatDong(true);
        huynhTruongRepository.save(ht.get());

        taiKhoanRepository.findById(maHT).ifPresent(taiKhoan -> {
            taiKhoan.setHoatDong(true);
            taiKhoanRepository.save(taiKhoan);
        });
    }

    @Override
    public Page<HuynhTruong> getHuynhTruongByCapSao(CapSao capSao, Pageable pageable) {

        if (capSao != null && !capSao.name().isEmpty()) {
            return huynhTruongRepository.getHuynhTruongByCapSao(capSao, pageable);
        } else
            return huynhTruongRepository.getAllHuynhTruong(pageable);
    }


    @Override
    public Optional<HuynhTruong> getHuynhTruongBySoDT(String soDT) {
        Optional<HuynhTruong> huynhTruong = Optional.of(new HuynhTruong());
        try {
            huynhTruong = huynhTruongRepository.getHuynhTruongBySoDT(soDT);
            return huynhTruong;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<HuynhTruong> getHuynhTruongByEmail(String email) {
        Optional<HuynhTruong> huynhTruong = Optional.of(new HuynhTruong());
        try {
            huynhTruong = huynhTruongRepository.getHuynhTruongByEmail(email);
            return huynhTruong;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<HuynhTruong> getHuynhTruongByNamHoc(String namHoc) {
        return null;
    }

    @Override
    public CompletableFuture<List<Map<String, String>>> uploadAvatarInDirectory(String directoryPath) {
        File folder = new File(directoryPath);
        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            logger.warning("Không tìm thấy file ảnh nào trong thư mục.");
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Thư mục trống hoặc không tồn tại.");
            return CompletableFuture.completedFuture(List.of(errorMap));
        }

        List<CompletableFuture<Map<String, String>>> futures = new ArrayList<>();

        for (File file : files) {
            if (file.isFile() && isImageFile(file)) {
                futures.add(CompletableFuture.supplyAsync(() -> processAndUploadImage(file), executor));
            }
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .filter(map -> !map.isEmpty()) // Lọc bỏ các map rỗng (thành công)
                        .collect(Collectors.toList())
                );
    }

    private Map<String, String> processAndUploadImage(File file) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            String maHT = file.getName().substring(0, file.getName().lastIndexOf("."));
            Optional<HuynhTruong> huynhTruongOptional = huynhTruongRepository.findById(maHT);

            if (huynhTruongOptional.isEmpty()) {
                resultMap.put("error", "Không tìm thấy HuynhTruong với mã: " + maHT);
                return resultMap;
            }

            byte[] jpegData = convertImageToJpeg(file);

            if (jpegData != null) {
                String fileName = "avatar_HT/" + maHT;

                // Thực hiện upload lên Wasabi
                wasabiService.uploadFile(fileName, jpegData);

                // Lưu URL vào database
                String presignedUrl = wasabiService.generatePreSignedUrl(fileName);
                HuynhTruong huynhTruong = huynhTruongOptional.get();
                huynhTruong.setHinhAnh(presignedUrl);
                huynhTruongRepository.save(huynhTruong);
            }
        } catch (IOException e) {
            resultMap.put("error", "Lỗi khi upload ảnh: " + file.getName() + " - " + e.getMessage());
        }
        return resultMap;
    }

    private boolean isImageFile(File file) {
        String[] imageExtensions = {"jpg", "jpeg", "png", "bmp", "gif"};
        String fileName = file.getName().toLowerCase();

        for (String extension : imageExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private byte[] convertImageToJpeg(File file) throws IOException {
        String fileExtension = getFileExtension(file.getName()).toLowerCase();

        // Các định dạng ảnh hợp lệ cần chuyển đổi
        List<String> validExtensions = Arrays.asList("jpg", "jpeg", "png", "bmp", "gif");

        if (!validExtensions.contains(fileExtension)) {
            throw new IOException("Định dạng ảnh không được hỗ trợ: " + fileExtension);
        }

        BufferedImage image = ImageIO.read(file);

        if (image == null) {
            throw new IOException("Không thể đọc được ảnh hoặc định dạng không được hỗ trợ: " + file.getName());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            // Lưu ảnh dưới định dạng JPEG
            ImageIO.write(image, "jpeg", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IOException("Lỗi khi chuyển đổi ảnh sang JPEG: " + e.getMessage());
        }
    }


    // Hàm hỗ trợ lấy phần mở rộng của file
    private String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            return ""; // Không tìm thấy phần mở rộng
        }
        return fileName.substring(lastIndexOfDot + 1);
    }

}
