package com.hospital.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LuotDieuTriRowDTO {
    private String maLuot;
    private String tenBenhNhan;
    private String tenBacSi;
    private String tenKhoa;
    private String maPhong;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private String trangThai;
}
