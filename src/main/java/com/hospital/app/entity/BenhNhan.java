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
 * Thực thể Bệnh nhân — một bác sĩ tiếp nhận, một phòng, (1-1) bảo hiểm.
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bac_si", nullable = false)
    private BacSi bacSiTiepNhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phong", nullable = false)
    private PhongBenh phongBenh;

    @OneToOne(mappedBy = "benhNhan", fetch = FetchType.LAZY)
    private BaoHiem baoHiem;

    @OneToMany(mappedBy = "benhNhan")
    private List<LuotDieuTri> luotDieuTris = new ArrayList<>();

    @OneToMany(mappedBy = "benhNhan")
    private List<HoaDon> hoaDons = new ArrayList<>();
}
