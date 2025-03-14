package com.example.quanlitntt_backend.repositories;

import com.example.quanlitntt_backend.entities.NamHoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NamHocRepository extends JpaRepository<NamHoc, String> {

    /*
     * cập nhật trường năm hiê tại = false của tất cả năm học trước khi thêm năm học mới
     */
    @Modifying
    @Query(value = "UPDATE nam_hoc SET nam_hoc.nam_hien_tai = false "
//                   + "WHERE nam_hoc.nam_hien_tai = true"
            , nativeQuery = true)
    int suaNamHocThanhQuaKhu();

}
