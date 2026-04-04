package com.hospital.app.service;

import com.hospital.app.dao.ThongKeDAO;
import com.hospital.app.dto.ThongKeDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service thống kê rút gọn — chỉ cung cấp một phương thức tổng hợp duy nhất
 * phục vụ Dashboard theo yêu cầu của người dùng.
 */
public class ThongKeService {

    private final ThongKeDAO dao = new ThongKeDAO();

    /**
     * Tổng hợp dữ liệu thống kê cho Dashboard dựa trên ngày người dùng chọn.
     * Cung cấp các chỉ số: Doanh thu ngày, Doanh thu lũy kế tháng (Month-to-date), Top Bệnh nhân, Top Bác sĩ.
     */
    public ThongKeDTO getSimplifiedDashboardData(LocalDate selectedDate) {
        if (selectedDate == null) return null;

        // 1. Xác định ngày bắt đầu của tháng dựa trên ngày được chọn
        LocalDate startOfMonth = selectedDate.withDayOfMonth(1);
        
        // 2. Truy xuất doanh thu phát sinh duy nhất trong ngày đang chọn
        BigDecimal revDay = dao.getRevenueOnDate(selectedDate);
        
        // 3. Truy xuất doanh thu lũy kế từ ngày 1 đến ngày được chọn (Doanh thu tháng tính đến hiện tại)
        BigDecimal revMonth = dao.getRevenueInRange(startOfMonth, selectedDate);
        
        // 4. Lấy danh sách Top 5 bệnh nhân chi trả nhiều nhất trong khoảng thời gian lũy kế tháng
        List<Object[]> topBN = dao.getTopExpensivePatientsInRange(startOfMonth, selectedDate, 5);
        // 5. Lấy danh sách Top 5 bác sĩ có lượt tiếp nhận bệnh nhân nhiều nhất trong tháng
        List<Object[]> topBS = dao.getTopDoctorsByEncountersInRange(startOfMonth, selectedDate, 5);
        
        // 6. Lấy danh sách các bệnh nhân đang trong quá trình điều trị (Nằm viện) tại ngày được chọn
        List<Object[]> inTreatment = dao.getInTreatmentOnDate(selectedDate);
        
        // Kiểm tra xem hệ thống có dữ liệu để hiển thị hay không (tránh hiển thị bảng trống trơn)
        boolean hasData = revMonth.compareTo(BigDecimal.ZERO) != 0 || !inTreatment.isEmpty();

        // Đóng gói dữ liệu vào DTO để tầng Controller đẩy lên giao diện OverviewPanel
        return ThongKeDTO.builder()
                .revenueDay(revDay)
                .revenueMonth(revMonth)
                .topPatientsMonth(topBN)
                .topDoctorsMonth(topBS)
                .patientsInTreatment(inTreatment)
                .hasData(hasData)
                .build();
    }
}
