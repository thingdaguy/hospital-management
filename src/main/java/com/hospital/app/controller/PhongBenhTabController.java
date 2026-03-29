package com.hospital.app.controller;

import com.hospital.app.dto.PhongBenhRowDTO;
import com.hospital.app.entity.PhongBenh;
import com.hospital.app.service.PhongBenhService;
import com.hospital.app.view.MainForm;
import com.hospital.app.view.PhongBenhDialog;

import java.util.List;
import javax.swing.JOptionPane;

public class PhongBenhTabController {
    private final MainForm view;
    private final PhongBenhService phongBenhService;

    public PhongBenhTabController(MainForm view, PhongBenhService phongBenhService) {
        this.view = view;
        this.phongBenhService = phongBenhService;
        wireEvents();
    }

    private void wireEvents() {
        view.getBtnRefreshPB().addActionListener(e -> loadRooms());
        view.getBtnSearchPB().addActionListener(e -> searchRooms());
        view.getBtnAddPB().addActionListener(e -> addRoom());
        view.getBtnEditPB().addActionListener(e -> editRoom());
        view.getBtnDeletePB().addActionListener(e -> deleteRoom());
        loadRooms();
    }

    private void loadRooms() {
        view.getSearchFieldPB().setText("");
        List<PhongBenhRowDTO> rows = phongBenhService.listAllForTable();
        view.populateRoomTable(rows);
    }

    private void searchRooms() {
        String kw = view.getSearchFieldPB().getText();
        List<PhongBenhRowDTO> rows = phongBenhService.searchByLoaiPhong(kw);
        view.populateRoomTable(rows);
    }

    private void addRoom() {
        try {
            PhongBenhDialog dlg = new PhongBenhDialog(view, "Thêm Phòng Bệnh", null);
            dlg.setVisible(true);
            if (dlg.isOk()) {
                phongBenhService.save(dlg.getPhongBenh());
                loadRooms();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi thêm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editRoom() {
        String id = view.getSelectedRoomId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một phòng bệnh để sửa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            PhongBenh pb = phongBenhService.findById(id);
            if (pb == null) return;
            PhongBenhDialog dlg = new PhongBenhDialog(view, "Sửa Phòng Bệnh", pb);
            dlg.setVisible(true);
            if (dlg.isOk()) {
                phongBenhService.update(dlg.getPhongBenh());
                loadRooms();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRoom() {
        String id = view.getSelectedRoomId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một phòng bệnh để xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(view, "Xóa phòng bệnh " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                phongBenhService.delete(id);
                loadRooms();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Lỗi khi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
