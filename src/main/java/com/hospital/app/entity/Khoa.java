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
 * Thực thể Khoa — một khoa có nhiều bác sĩ và y tá.
 */
@Entity
@Table(name = "khoa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Khoa {

    @Id
    @Column(name = "ma_khoa", length = 20)
    private String maKhoa;

    @Column(name = "ten_khoa", nullable = false, length = 200)
    private String tenKhoa;

    @Column(name = "so_dien_thoai", length = 30)
    private String soDienThoai;

    @OneToMany(mappedBy = "khoa")
    private List<BacSi> bacSis = new ArrayList<>();
}
