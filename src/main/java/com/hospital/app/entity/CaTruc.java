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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể Ca trực — liên kết N-N với bác sĩ và y tá.
 */
@Entity
@Table(name = "ca_truc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaTruc {

    @Id
    @Column(name = "ma_ca", length = 20)
    private String maCa;

    @Column(name = "ten_ca", nullable = false, length = 50)
    private String tenCa;

    @Column(name = "thoi_gian_bat_dau", nullable = false)
    private LocalTime thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc", nullable = false)
    private LocalTime thoiGianKetThuc;

    @OneToMany(mappedBy = "caTruc")
    private List<BacSiCaTruc> bacSiCaTrucs = new ArrayList<>();

    @OneToMany(mappedBy = "caTruc")
    private List<YTaCaTruc> yTaCaTrucs = new ArrayList<>();
}
