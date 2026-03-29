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
 * Bảng trung gian Y tá (N)-(N) Ca trực.
 */
@Entity
@Table(name = "y_ta_ca_truc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YTaCaTruc {

    @EmbeddedId
    private YTaCaTrucId id;

    @MapsId("maYTa")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_y_ta", nullable = false)
    private YTa yTa;

    @MapsId("maCa")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_ca", nullable = false)
    private CaTruc caTruc;
}
