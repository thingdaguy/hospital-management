package com.hospital.app.entity;

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
 * Chi tiết dịch vụ theo lượt — (N)-(N) giữa lượt điều trị và dịch vụ.
 */
@Entity
@Table(name = "chi_tiet_dich_vu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDichVu {

    @EmbeddedId
    private ChiTietDichVuId id;

    @MapsId("maLuot")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_luot", nullable = false)
    private LuotDieuTri luotDieuTri;

    @MapsId("maDichVu")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_dich_vu", nullable = false)
    private DichVu dichVu;
}
