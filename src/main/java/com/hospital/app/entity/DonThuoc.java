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
 * Thực thể Đơn thuốc — thuộc một {@link LuotDieuTri}.
 */
@Entity
@Table(name = "don_thuoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonThuoc {

    @Id
    @Column(name = "ma_don", length = 20)
    private String maDon;

    @Column(name = "ngay_ke", nullable = false)
    private LocalDate ngayKe;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_luot", nullable = false)
    private LuotDieuTri luotDieuTri;

    @OneToMany(mappedBy = "donThuoc")
    private List<ChiTietDonThuoc> chiTietDonThuocs = new ArrayList<>();
}
