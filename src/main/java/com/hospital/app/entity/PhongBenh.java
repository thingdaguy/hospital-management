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

import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể Phòng bệnh — một phòng có nhiều {@link BenhNhan}.
 */
@Entity
@Table(name = "phong_benh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhongBenh {

    @Id
    @Column(name = "ma_phong", length = 20)
    private String maPhong;

    @Column(name = "loai_phong", nullable = false, length = 20)
    private String loaiPhong;

    @Column(name = "so_giuong_toi_da", nullable = false)
    private Integer soGiuongToiDa;

    @OneToMany(mappedBy = "phongBenh")
    private List<BenhNhan> benhNhans = new ArrayList<>();
}
