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
 * Thực thể Dịch vụ y tế.
 */
@Entity
@Table(name = "dich_vu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DichVu {

    @Id
    @Column(name = "ma_dich_vu", length = 20)
    private String maDichVu;

    @Column(name = "ten_dich_vu", nullable = false, length = 200)
    private String tenDichVu;

    @Column(name = "don_gia", nullable = false, precision = 14, scale = 2)
    private BigDecimal donGia;

    @OneToMany(mappedBy = "dichVu")
    private List<ChiTietDichVu> chiTietDichVus = new ArrayList<>();
}
