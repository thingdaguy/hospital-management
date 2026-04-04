package com.hospital.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO trung tâm chứa toàn bộ dữ liệu thống kê, phục vụ cả 3 chế độ xem:
 * - Tổng quan hệ thống (không lọc ngày)
 * - Theo khoảng thời gian (từ ngày → đến ngày)
 * - Theo ngày đơn lẻ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThongKeDTO {

    /** Doanh thu của riêng ngày đang chọn */
    private BigDecimal revenueDay;

    /** Doanh thu từ ngày 1 đầu tháng đến ngày đang chọn */
    private BigDecimal revenueMonth;

    /** Top 5 bệnh nhân chi nhiều nhất (từ ngày 1 -> ngày chọn) */
    private List<Object[]> topPatientsMonth;

    /** Top 5 bác sĩ có lượt tiếp nhận nhiều nhất (từ ngày 1 -> ngày chọn) */
    private List<Object[]> topDoctorsMonth;

    /** Danh sách bệnh nhân đang điều trị (snapshot tại ngày chọn) */
    private List<Object[]> patientsInTreatment;

    /** Trạng thái có dữ liệu hay không */
    @Builder.Default
    private boolean hasData = true;
}
