package com.hospital.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO hiển thị một dòng bệnh nhân trên bảng (MainForm) — tách khỏi entity để View không phụ thuộc JPA.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BenhNhanRowDTO {

    private String maBenhNhan;
    private String tenBenhNhan;
    private LocalDate ngaySinh;
    private String soDienThoai;
    /** Số thẻ BHYT (null nếu bệnh nhân không có bảo hiểm) */
    private String soTheBHYT;
    private String tenBacSiTiepNhan;
    private String maPhong;
    private String loaiPhong;
}
