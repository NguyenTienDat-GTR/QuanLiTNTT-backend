package com.example.quanlitntt_backend.serviceImplements;

import com.example.quanlitntt_backend.dto.ThieuNhiDto;
import com.example.quanlitntt_backend.entities.HuynhTruong;
import com.example.quanlitntt_backend.entities.ThieuNhi;
import com.example.quanlitntt_backend.entities.enums.TrangThaiHocVu;
import com.example.quanlitntt_backend.entities.enums.VaiTro;
import com.example.quanlitntt_backend.repositories.ThieuNhiRepository;
import com.example.quanlitntt_backend.services.ThieuNhiService;
import com.example.quanlitntt_backend.utils.DateUtil;
import com.example.quanlitntt_backend.utils.GenerateMa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ThieuNhiServiceImpl implements ThieuNhiService {

    @Autowired
    private ThieuNhiRepository thieuNhiRepository;

    private final GenerateMa generateMa = new GenerateMa();
    private final TaiKhoanServiceImpl taiKhoanService;

    public ThieuNhiServiceImpl(TaiKhoanServiceImpl taiKhoanService) {
        this.taiKhoanService = taiKhoanService;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^0\\d{9}$");
    }


    private void setValueForThieuNhi(ThieuNhiDto thieuNhiDto, ThieuNhi tn) {
        Date ngayHienTai = new Date(); // Lấy ngày hiện tại

        Date ngaySinh = DateUtil.convertToDateFormat(thieuNhiDto.getNgaySinh());
        Date ngayRuaToi = DateUtil.convertToDateFormat(thieuNhiDto.getNgayRuaToi());
        Date ngayRuocLe = DateUtil.convertToDateFormat(thieuNhiDto.getNgayRuocLe());
        Date ngayThemSuc = DateUtil.convertToDateFormat(thieuNhiDto.getNgayThemSuc());
        Date ngayBaoDong = DateUtil.convertToDateFormat(thieuNhiDto.getNgayBaoDong());

        // Kiểm tra ngày không được lớn hơn ngày hiện tại
        if ((ngaySinh != null && ngaySinh.after(ngayHienTai)) ||
            (ngayRuaToi != null && ngayRuaToi.after(ngayHienTai)) ||
            (ngayRuocLe != null && ngayRuocLe.after(ngayHienTai)) ||
            (ngayThemSuc != null && ngayThemSuc.after(ngayHienTai)) ||
            (ngayBaoDong != null && ngayBaoDong.after(ngayHienTai))) {
            throw new IllegalArgumentException("Ngày không được lớn hơn ngày hiện tại!");
        }

        // Kiểm tra thứ tự các ngày
        if ((ngayRuaToi != null && ngaySinh != null && ngaySinh.after(ngayRuaToi)) ||
            (ngayRuocLe != null && ngaySinh != null && ngaySinh.after(ngayRuocLe)) ||
            (ngayThemSuc != null && ngaySinh != null && ngaySinh.after(ngayThemSuc)) ||
            (ngayBaoDong != null && ngaySinh != null && ngaySinh.after(ngayBaoDong))) {
            throw new IllegalArgumentException("Ngày sinh phải nhỏ hơn tất cả các ngày khác!");
        }

        if ((ngayRuaToi != null && ngayRuocLe != null && ngayRuaToi.after(ngayRuocLe)) ||
            (ngayRuaToi != null && ngayThemSuc != null && ngayRuaToi.after(ngayThemSuc)) ||
            (ngayRuaToi != null && ngayBaoDong != null && ngayRuaToi.after(ngayBaoDong))) {
            throw new IllegalArgumentException("Ngày Rửa Tội phải nhỏ hơn ngày Rước Lễ, Thêm Sức và Bao Đồng!");
        }

        if ((ngayRuocLe != null && ngayThemSuc != null && ngayRuocLe.after(ngayThemSuc)) ||
            (ngayRuocLe != null && ngayBaoDong != null && ngayRuocLe.after(ngayBaoDong))) {
            throw new IllegalArgumentException("Ngày Rước Lễ phải nhỏ hơn ngày Thêm Sức và Bao Đồng!");
        }

        if (ngayThemSuc != null && ngayBaoDong != null && ngayThemSuc.after(ngayBaoDong)) {
            throw new IllegalArgumentException("Ngày Thêm Sức phải nhỏ hơn ngày Bao Đồng!");
        }


        // Kiểm tra số điện thoại hợp lệ
        if (!(thieuNhiDto.getSoDienThoaiCha() == null) && !isValidPhoneNumber(thieuNhiDto.getSoDienThoaiCha())) {
            throw new IllegalArgumentException("Số điện thoại cha không hợp lệ! Phải có 10 số và bắt đầu bằng 0.");
        }
        if (!(thieuNhiDto.getSoDienThoaiMe() == null) && !isValidPhoneNumber(thieuNhiDto.getSoDienThoaiMe())) {
            throw new IllegalArgumentException("Số điện thoại mẹ không hợp lệ! Phải có 10 số và bắt đầu bằng 0.");
        }
        if (!(thieuNhiDto.getSoDienThoaiCaNhan() == null) && !isValidPhoneNumber(thieuNhiDto.getSoDienThoaiCaNhan())) {
            throw new IllegalArgumentException("Số điện thoại cá nhân không hợp lệ! Phải có 10 số và bắt đầu bằng 0.");
        }

        // Gán giá trị cho đối tượng ThieuNhi nếu các điều kiện hợp lệ
        tn.setTenThanh(thieuNhiDto.getTenThanh());
        tn.setHo(thieuNhiDto.getHo());
        tn.setTen(thieuNhiDto.getTen());
        tn.setGioiTinh(thieuNhiDto.getGioiTinh());
        tn.setNgaySinh(ngaySinh);
        tn.setNgayRuaToi(ngayRuaToi);
        tn.setNoiRuaToi(thieuNhiDto.getNoiRuaToi());
        tn.setNgayRuocLe(ngayRuocLe);
        tn.setNoiRuocLe(thieuNhiDto.getNoiRuocLe());
        tn.setNgayThemSuc(ngayThemSuc);
        tn.setNoiThemSuc(thieuNhiDto.getNoiThemSuc());
        tn.setNgayBaoDong(ngayBaoDong);
        tn.setNoiBaoDong(thieuNhiDto.getNoiBaoDong());
        tn.setHoTenCha(thieuNhiDto.getHoTenCha());
        tn.setSoDienThoaiCha(thieuNhiDto.getSoDienThoaiCha());
        tn.setHoTenMe(thieuNhiDto.getHoTenMe());
        tn.setSoDienThoaiMe(thieuNhiDto.getSoDienThoaiMe());
        tn.setSoDienThoaiCaNhan(thieuNhiDto.getSoDienThoaiCaNhan());
        tn.setTrinhDo(thieuNhiDto.getTrinhDo());
        tn.setTaiKhoan(null);
    }


    @Override
    public ThieuNhi addThieuNhi(ThieuNhiDto thieuNhiDto) {
        ThieuNhi tn = new ThieuNhi();

        Date ngaySinh = DateUtil.convertToDateFormat(thieuNhiDto.getNgaySinh());

        String maTN = generateMa.generateMaThieuNhi(ngaySinh, thieuNhiDto.getTenThanh(), thieuNhiDto.getHo(), thieuNhiDto.getTen(), thieuNhiRepository::existsById);

        tn.setMaTN(maTN);
        setValueForThieuNhi(thieuNhiDto, tn);
        tn.setTrangThai(TrangThaiHocVu.DANGHOC);

        taiKhoanService.taoTaiKhoan(tn.getMaTN(), VaiTro.THIEUNHI);

        return thieuNhiRepository.save(tn);
    }

    @Override
    public Page<ThieuNhiDto> getAllThieuNhis(Pageable pageable) {

        return thieuNhiRepository.getAllThieuNhi(pageable);
    }

    @Override
    public Optional<ThieuNhiDto> getThieuNhiByMa(String maTN) {
        Optional<ThieuNhiDto> thieuNhi = thieuNhiRepository.getThieuNhiByMa(maTN);
        if (!thieuNhiRepository.existsById(maTN)) {

            return Optional.empty();

        }
        return thieuNhi;
    }

    @Override
    public Page<ThieuNhiDto> getThieuNhiBySdtChaMe(String sdt, Pageable pageable) {
        String queryString = "%" + sdt + "%";

        Page<ThieuNhiDto> result = thieuNhiRepository.getThieuNhiBySdtChaMe(queryString, pageable);

        if (result.isEmpty()) {
            return Page.empty();
        }
        return result;
    }

    @Override
    public void updateThieuNhi(ThieuNhiDto thieuNhiDto) {


        if (!thieuNhiRepository.existsById(thieuNhiDto.getMaTN())) {
            throw new RuntimeException("Không tìm thấy Thiếu nhi với mã " + thieuNhiDto.getMaTN());
        }
        ThieuNhi thieuNhi = thieuNhiRepository.findById(thieuNhiDto.getMaTN()).get();
        setValueForThieuNhi(thieuNhiDto, thieuNhi);

        thieuNhiRepository.save(thieuNhi);
    }

    @Override
    public void deleteThieuNhi(String maTN) {
        Optional<ThieuNhi> tn = thieuNhiRepository.findById(maTN);

        if (!tn.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy thiếu nhi với mã " + maTN);
        } else if (tn.get().getTrangThai().equals(TrangThaiHocVu.NGHIHOC)) {
            throw new IllegalArgumentException("Thiếu nhi đã ở trạng thái nghỉ học");
        }

        tn.get().setTrangThai(TrangThaiHocVu.NGHIHOC);
        thieuNhiRepository.save(tn.get());
    }

    @Override
    public void activeThieuNhi(String maTN) {
        Optional<ThieuNhi> tn = thieuNhiRepository.findById(maTN);

        if (!tn.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy thiếu nhi với mã " + maTN);
        } else if (!tn.get().getTrangThai().equals(TrangThaiHocVu.NGHIHOC)) {
            throw new IllegalArgumentException("Thiếu nhi đã ở trạng thái Đang Học hoặc Đã Hoàn Thành");
        }

        tn.get().setTrangThai(TrangThaiHocVu.DANGHOC);
        thieuNhiRepository.save(tn.get());
    }
}
