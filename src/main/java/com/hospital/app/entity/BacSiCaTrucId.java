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
 * Khóa ghép (ma_bac_si, ma_ca) cho bảng trung gian bác sĩ — ca trực.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BacSiCaTrucId implements Serializable {

    @Column(name = "ma_bac_si", length = 20)
    private String maBacSi;

    @Column(name = "ma_ca", length = 20)
    private String maCa;
}
