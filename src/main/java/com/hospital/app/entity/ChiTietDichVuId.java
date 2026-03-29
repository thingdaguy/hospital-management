package com.hospital.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Khóa ghép chi tiết dịch vụ (ma_luot, ma_dich_vu).
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChiTietDichVuId implements Serializable {

    @Column(name = "ma_luot", length = 20)
    private String maLuot;

    @Column(name = "ma_dich_vu", length = 20)
    private String maDichVu;
}
