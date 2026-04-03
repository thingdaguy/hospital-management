package com.hospital.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietHoaDonDTO {
    private BigDecimal tongTienThuoc;
    private BigDecimal tongTienDichVu;
    private BigDecimal tongTienHoaDon;
    private List<ChiTietDichVuDTO> danhSachDichVu;
}
