package com.hospital.app.service;

import com.hospital.app.dao.KhoaDAO;
import com.hospital.app.entity.Khoa;

import java.util.List;

/**
 * Lớp dịch vụ (Service) quản lý thông tin các Khoa trong bệnh viện.
 */
public class KhoaService {

    private final KhoaDAO khoaDAO = new KhoaDAO();

    /**
     * Lấy toàn bộ danh mục các Khoa từ Cơ sở dữ liệu.
     * Thường dùng để đổ dữ liệu vào các ô chọn (ComboBox) trên giao diện quản lý Bác sĩ.
     */
    public List<Khoa> findAll() {
        return khoaDAO.findAll();
    }
}
