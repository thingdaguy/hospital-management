package com.hospital.app.controller;

import com.hospital.app.dto.BacSiRowDTO;
import com.hospital.app.entity.BacSi;
import com.hospital.app.entity.Khoa;
import com.hospital.app.service.BacSiService;
import com.hospital.app.service.KhoaService;
import com.hospital.app.view.BacSiDialog;
import com.hospital.app.view.MainForm;

import java.util.List;
import javax.swing.JOptionPane;

public class BacSiTabController {
    private final MainForm view;
    private final BacSiService bacSiService;
    private final KhoaService khoaService;

    public BacSiTabController(MainForm view, BacSiService bacSiService, KhoaService khoaService) {
        this.view = view;
        this.bacSiService = bacSiService;
        this.khoaService = khoaService;
        wireEvents();
    }

    private void wireEvents() {
        view.getBtnRefreshBS().addActionListener(e -> loadDoctors());
        view.getBtnSearchBS().addActionListener(e -> searchDoctors());
        view.getBtnAddBS().addActionListener(e -> addDoctor());
        view.getBtnEditBS().addActionListener(e -> editDoctor());
        view.getBtnDeleteBS().addActionListener(e -> deleteDoctor());
        loadDoctors();
    }

    private void loadDoctors() {
        view.getSearchFieldBS().setText("");
        List<BacSiRowDTO> rows = bacSiService.listAllForTable();
        view.populateDoctorTable(rows);
    }

    private void searchDoctors() {
        String kw = view.getSearchFieldBS().getText();
        List<BacSiRowDTO> rows = bacSiService.searchByName(kw);
        view.populateDoctorTable(rows);
    }

    private void addDoctor() {
        try {
            List<Khoa> khoas = khoaService.findAll();
            BacSiDialog dlg = new BacSiDialog(view, "Thêm Mới Bác Sĩ", khoas, null);
            dlg.setVisible(true);
            if (dlg.isOk()) {
                bacSiService.save(dlg.getBacSi());
                loadDoctors();
                JOptionPane.showMessageDialog(view, "Thêm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi xử lý: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editDoctor() {
        String id = view.getSelectedDoctorId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một bác sĩ để sửa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            BacSi bs = bacSiService.findById(id);
            if (bs == null) return;
            List<Khoa> khoas = khoaService.findAll();
            BacSiDialog dlg = new BacSiDialog(view, "Sửa Bác Sĩ", khoas, bs);
            dlg.setVisible(true);
            if (dlg.isOk()) {
                bacSiService.update(dlg.getBacSi());
                loadDoctors();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDoctor() {
        String id = view.getSelectedDoctorId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một bác sĩ để xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(view, "Xóa bác sĩ " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bacSiService.delete(id);
                loadDoctors();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Lỗi khi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
