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
 * Khóa ghép (ma_y_ta, ma_ca) cho bảng trung gian y tá — ca trực.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class YTaCaTrucId implements Serializable {

    @Column(name = "ma_y_ta", length = 20)
    private String maYTa;

    @Column(name = "ma_ca", length = 20)
    private String maCa;
}
