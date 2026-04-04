package com.hospital.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO đại diện cho một hàng dữ liệu Thuốc trên bảng (Table).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThuocRowDTO {
    private String maThuoc;
    private String tenThuoc;
    private String thanhPhan;
    private BigDecimal giaBan;
}
