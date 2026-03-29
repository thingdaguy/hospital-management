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
 * Thực thể Bảo hiểm y tế — (1-1) với {@link BenhNhan}.
 */
@Entity
@Table(name = "bao_hiem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaoHiem {

    @Id
    @Column(name = "ma_the", length = 20)
    private String maThe;

    @Column(name = "ngay_het_han", nullable = false)
    private LocalDate ngayHetHan;

    @Column(name = "muc_huong", nullable = false, precision = 5, scale = 2)
    private BigDecimal mucHuong;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_benh_nhan", nullable = false, unique = true)
    private BenhNhan benhNhan;
}
