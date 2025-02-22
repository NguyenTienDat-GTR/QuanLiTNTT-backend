package com.example.quanlitntt_backend.services;

import com.example.quanlitntt_backend.entities.TaiKhoan;
import com.example.quanlitntt_backend.entities.TaiKhoanDetail;
import com.example.quanlitntt_backend.repositories.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TaiKhoanDetailService implements UserDetailsService {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TaiKhoan taiKhoan = taiKhoanRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản"));
        return new TaiKhoanDetail(taiKhoan);
    }
}
