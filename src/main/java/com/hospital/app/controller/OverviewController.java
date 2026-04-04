package com.hospital.app.controller;

import com.hospital.app.dto.ThongKeDTO;
import com.hospital.app.service.ThongKeService;
import com.hospital.app.view.OverviewPanel;

import java.time.LocalDate;

/**
 * Điều khiển Dashboard Thống kê rút gọn.
 */
/**
 * Bộ điều khiển (Controller) cho màn hình Tổng quan (Dashboard).
 * Kết nối giữa người dùng (thông qua OverviewPanel) và nghiệp vụ thống kê (ThongKeService).
 */
public class OverviewController {

    private final OverviewPanel view;
    private final ThongKeService service;

    /**
     * Khởi tạo OverviewController.
     * @param view Giao diện bảng điều khiển tổng quan.
     * @param service Dịch vụ xử lý logic thống kê.
     */
    public OverviewController(OverviewPanel view, ThongKeService service) {
        this.view = view;
        this.service = service;
        // Đăng ký các sự kiện người dùng
        initEvents();
        // Tự động tải dữ liệu cho ngày hôm nay ngay khi mở ứng dụng
        refreshData();
    }

    /**
     * Thiết lập các bộ lắng nghe sự kiện từ giao diện.
     */
    private void initEvents() {
        // Lắng nghe sự kiện nhấn nút "Xem thống kê"
        view.getBtnFilter().addActionListener(e -> refreshData());
    }

    /**
     * Làm mới dữ liệu hiển thị trên Dashboard.
     * Lấy ngày từ ô chọn ngày (DatePicker) và yêu cầu Service tổng hợp số liệu.
     */
    private void refreshData() {
        // 1. Lấy ngày người dùng đang chọn trên giao diện
        LocalDate selected = view.getSelectedDate();
        if (selected != null) {
            // 2. Gọi nghiệp vụ thống kê rút gọn cho ngày được chọn
            ThongKeDTO dto = service.getSimplifiedDashboardData(selected);
            // 3. Cập nhật các thẻ (Cards) và bảng dữ liệu hiển thị trên giao diện
            view.updateData(dto);
        }
    }
}
