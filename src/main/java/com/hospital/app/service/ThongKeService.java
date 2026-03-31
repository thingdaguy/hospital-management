package com.hospital.app.service;

import com.hospital.app.dao.ThongKeDAO;
import com.hospital.app.dto.ThongKeDTO;
import java.util.List;

/**
 * Service xử lý thống kê.
 */
public class ThongKeService {

    private final ThongKeDAO thongKeDAO = new ThongKeDAO();

    /**
     * Lấy dữ liệu tổng quan cho Dashboard.
     */
    public ThongKeDTO getOverviewData() {
        long totalPatients = thongKeDAO.getTotalPatients();
        long totalDoctors = thongKeDAO.getTotalDoctors();
        List<Object[]> topPatients = thongKeDAO.getTopExpensivePatients(5);
        List<Object[]> topDoctors = thongKeDAO.getTopDoctorsByPatients(5);

        return ThongKeDTO.builder()
                .totalPatients(totalPatients)
                .totalDoctors(totalDoctors)
                .topExpensivePatients(topPatients)
                .topDoctorsByPatients(topDoctors)
                .build();
    }
}
