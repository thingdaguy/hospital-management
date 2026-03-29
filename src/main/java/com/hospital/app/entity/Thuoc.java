package com.hospital.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể Thuốc.
 */
@Entity
@Table(name = "thuoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Thuoc {

    @Id
    @Column(name = "ma_thuoc", length = 20)
    private String maThuoc;

    @Column(name = "ten_thuoc", nullable = false, length = 200)
    private String tenThuoc;

    @Column(name = "thanh_phan", nullable = false, length = 500)
    private String thanhPhan;

    @Column(name = "gia_ban", nullable = false, precision = 14, scale = 2)
    private BigDecimal giaBan;

    @OneToMany(mappedBy = "thuoc")
    private List<ChiTietDonThuoc> chiTietDonThuocs = new ArrayList<>();
}
