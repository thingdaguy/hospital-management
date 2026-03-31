package com.hospital.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO chứa thông tin thống kê tổng quan.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThongKeDTO {
    private long totalPatients;
    private long totalDoctors;
    
    // Danh sách Top (tên, giá trị)
    private List<Object[]> topExpensivePatients; 
    private List<Object[]> topDoctorsByPatients;
}
