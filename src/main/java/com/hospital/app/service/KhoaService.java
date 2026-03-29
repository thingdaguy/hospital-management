package com.hospital.app.service;

import com.hospital.app.dao.KhoaDAO;
import com.hospital.app.entity.Khoa;

import java.util.List;

/**
 * Service cho Khoa.
 */
public class KhoaService {

    private final KhoaDAO khoaDAO = new KhoaDAO();

    /**
     * Lấy tất cả Khoa để đổ vào UI/ComboBox.
     */
    public List<Khoa> findAll() {
        return khoaDAO.findAll();
    }
}
