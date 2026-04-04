package com.hospital.app.service;

import com.hospital.app.dao.ThuocDAO;
import com.hospital.app.entity.Thuoc;

import java.util.List;

/**
 * Service cho nghiệp vụ thuốc.
 * Hiện tại chỉ dùng để tra cứu danh mục thuốc cho đơn thuốc & hóa đơn.
 */
public class ThuocService {

    private final ThuocDAO thuocDAO = new ThuocDAO();

    /**
     * Tìm thông tin thuốc theo mã.
     */
    public Thuoc findById(String id) {
        return thuocDAO.findById(id);
    }
    
    /**
     * Lấy toàn bộ danh mục thuốc.
     */
    public List<Thuoc> findAll() {
        return thuocDAO.findAll();
    }
}
