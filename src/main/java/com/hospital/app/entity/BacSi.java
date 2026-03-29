package com.hospital.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể Bác sĩ — thuộc một {@link Khoa}, tiếp nhận nhiều {@link BenhNhan}.
 */
@Entity
@Table(name = "bac_si")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BacSi {

    @Id
    @Column(name = "ma_bac_si", length = 20)
    private String maBacSi;

    @Column(name = "ten_bac_si", nullable = false, length = 200)
    private String tenBacSi;

    @Column(name = "ngay_vao_lam", nullable = false)
    private LocalDate ngayVaoLam;

    @Column(name = "chuyen_mon", nullable = false, length = 200)
    private String chuyenMon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khoa", nullable = false)
    private Khoa khoa;

    @OneToMany(mappedBy = "bacSiTiepNhan")
    private List<BenhNhan> benhNhans = new ArrayList<>();

    @OneToMany(mappedBy = "bacSiDieuTri")
    private List<LuotDieuTri> luotDieuTris = new ArrayList<>();

    @OneToMany(mappedBy = "bacSi")
    private List<BacSiCaTruc> caTrucAssignments = new ArrayList<>();
}
