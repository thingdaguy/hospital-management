package com.hospital.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Thực thể Hóa đơn — thuộc một {@link LuotDieuTri}.
 */
@Entity
@Table(name = "hoa_don")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {

    @Id
    @Column(name = "ma_hoa_don", length = 20)
    private String maHoaDon;

    @Column(name = "ngay_lap", nullable = false)
    private LocalDate ngayLap;

    @Column(name = "tong_tien", nullable = false, precision = 14, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "trang_thai", nullable = false, length = 50)
    private String trangThai;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_luot", nullable = false, unique = true)
    private LuotDieuTri luotDieuTri;
}
