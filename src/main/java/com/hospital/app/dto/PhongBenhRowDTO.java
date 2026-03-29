package com.hospital.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO hiển thị một dòng thông tin phòng bệnh trên bảng (MainForm).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhongBenhRowDTO {
    private String maPhong;
    private String loaiPhong;
    private Integer soGiuongToiDa;
}
