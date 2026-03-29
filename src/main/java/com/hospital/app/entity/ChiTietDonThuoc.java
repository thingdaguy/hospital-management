package com.hospital.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Chi tiết đơn thuốc — số lượng từng thuốc trong đơn.
 */
@Entity
@Table(name = "chi_tiet_don_thuoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDonThuoc {

    @EmbeddedId
    private ChiTietDonThuocId id;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @MapsId("maDon")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_don", nullable = false)
    private DonThuoc donThuoc;

    @MapsId("maThuoc")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_thuoc", nullable = false)
    private Thuoc thuoc;
}
