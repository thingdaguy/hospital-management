package com.hospital.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tai khoan dang nhap (LOGIN) — dung cho dang nhap theo vai tro (ADMIN).
 */
@Entity
@Table(name = "tai_khoan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoan {

    @Id
    @Column(name = "ma_tai_khoan", length = 20)
    private String maTaiKhoan;

    @Column(name = "ten_dang_nhap", nullable = false, length = 50, unique = true)
    private String tenDangNhap;

    @Column(name = "mat_khau_hash", nullable = false, length = 255)
    private String matKhauHash;

    @Column(name = "ho_ten", length = 200)
    private String hoTen;

    @Column(name = "vai_tro", nullable = false, length = 30)
    private String vaiTro;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;
}

