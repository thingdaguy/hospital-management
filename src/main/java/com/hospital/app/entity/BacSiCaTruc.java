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
 * Bảng trung gian Bác sĩ (N)-(N) Ca trực.
 */
@Entity
@Table(name = "bac_si_ca_truc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BacSiCaTruc {

    @EmbeddedId
    private BacSiCaTrucId id;

    @MapsId("maBacSi")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bac_si", nullable = false)
    private BacSi bacSi;

    @MapsId("maCa")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_ca", nullable = false)
    private CaTruc caTruc;
}
