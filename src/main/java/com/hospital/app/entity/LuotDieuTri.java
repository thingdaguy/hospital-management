package com.hospital.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
 * Thực thể Lượt điều trị — thuộc một bác sĩ và một bệnh nhân.
 */
@Entity
@Table(name = "luot_dieu_tri")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LuotDieuTri {

    @Id
    @Column(name = "ma_luot", length = 20)
    private String maLuot;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Column(name = "trang_thai", nullable = false, length = 50)
    private String trangThai;

    @Column(name = "ket_qua", length = 500)
    private String ketQua;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bac_si", nullable = false)
    private BacSi bacSiDieuTri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_benh_nhan", nullable = false)
    private BenhNhan benhNhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phong")
    private PhongBenh phongBenh;

    @OneToOne(mappedBy = "luotDieuTri", fetch = FetchType.LAZY)
    private HoaDon hoaDon;

    @OneToMany(mappedBy = "luotDieuTri")
    private List<ChiTietDichVu> chiTietDichVus = new ArrayList<>();

    @OneToMany(mappedBy = "luotDieuTri")
    private List<DonThuoc> donThuocs = new ArrayList<>();
}
