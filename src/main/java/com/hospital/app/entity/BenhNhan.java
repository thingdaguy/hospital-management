package com.hospital.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể Bệnh nhân — (1-1) bảo hiểm, có lịch sử nhiều Lượt điều trị.
 */
@Entity
@Table(name = "benh_nhan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BenhNhan {

    @Id
    @Column(name = "ma_benh_nhan", length = 20)
    private String maBenhNhan;

    @Column(name = "ten_benh_nhan", nullable = false, length = 200)
    private String tenBenhNhan;

    @Column(name = "ngay_sinh", nullable = false)
    private LocalDate ngaySinh;

    @Column(name = "so_dien_thoai", length = 30)
    private String soDienThoai;

    @OneToMany(mappedBy = "benhNhan")
    private List<LuotDieuTri> luotDieuTris = new ArrayList<>();

    @OneToOne(mappedBy = "benhNhan")
    private BaoHiem baoHiem;
}
