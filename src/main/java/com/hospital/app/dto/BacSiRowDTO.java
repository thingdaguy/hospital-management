package com.hospital.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO hiển thị một dòng thông tin bác sĩ trên bảng (MainForm) - tách khỏi Entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BacSiRowDTO {
    private String maBacSi;
    private String tenBacSi;
    private LocalDate ngayVaoLam;
    private String chuyenMon;
    private String tenKhoa;

}
