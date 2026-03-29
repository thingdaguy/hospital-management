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
 * Khóa ghép chi tiết đơn thuốc (ma_don, ma_thuoc).
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChiTietDonThuocId implements Serializable {

    @Column(name = "ma_don", length = 20)
    private String maDon;

    @Column(name = "ma_thuoc", length = 20)
    private String maThuoc;
}
