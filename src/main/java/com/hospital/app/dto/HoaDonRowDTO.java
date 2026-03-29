package com.hospital.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonRowDTO {
    private String maHoaDon;
    private LocalDate ngayLap;
    private BigDecimal tongTien;
    private String trangThai;
}
