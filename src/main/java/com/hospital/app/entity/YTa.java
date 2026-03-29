package com.hospital.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể Y tá — thuộc một {@link Khoa}.
 */
@Entity
@Table(name = "y_ta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YTa {

    @Id
    @Column(name = "ma_y_ta", length = 20)
    private String maYTa;

    @Column(name = "ten_y_ta", nullable = false, length = 200)
    private String tenYTa;

    @Column(name = "nam_kinh_nghiem", nullable = false)
    private Integer namKinhNghiem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khoa", nullable = false)
    private Khoa khoa;

    @OneToMany(mappedBy = "yTa")
    private List<YTaCaTruc> caTrucAssignments = new ArrayList<>();
}
