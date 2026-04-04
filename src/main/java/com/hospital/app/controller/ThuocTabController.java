package com.hospital.app.controller;

import com.hospital.app.dto.ThuocRowDTO;
import com.hospital.app.entity.Thuoc;
import com.hospital.app.service.ThuocService;
import com.hospital.app.view.MainForm;
import com.hospital.app.view.ThuocDialog;

import javax.swing.*;
import java.util.List;

public class ThuocTabController {

    private final MainForm view;
    private final ThuocService thuocService;

    public ThuocTabController(MainForm view, ThuocService thuocService) {
        this.view = view;
        this.thuocService = thuocService;
        wireEvents();
        loadData();
    }

    private void wireEvents() {
        view.getBtnSearchTH().addActionListener(e -> search());
        view.getBtnRefreshTH().addActionListener(e -> loadData());
        view.getBtnAddTH().addActionListener(e -> add());
        view.getBtnEditTH().addActionListener(e -> edit());
        view.getBtnDeleteTH().addActionListener(e -> delete());
    }

    private void loadData() {
        List<ThuocRowDTO> rows = thuocService.listAllForTable();
        view.populateMedicineTable(rows);
    }

    private void search() {
        String kw = view.getSearchFieldTH().getText();
        List<ThuocRowDTO> rows = thuocService.searchByName(kw);
        view.populateMedicineTable(rows);
    }

    private void add() {
        ThuocDialog dlg = new ThuocDialog(view, "Thêm Thuốc Mới", null);
        dlg.setVisible(true);

        if (dlg.isOk()) {
            try {
                thuocService.save(dlg.getThuoc());
                loadData();
                JOptionPane.showMessageDialog(view, "Đã thêm thuốc thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Lỗi khi thêm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void edit() {
        String id = view.getSelectedMedicineId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn thuốc để sửa.");
            return;
        }

        Thuoc t = thuocService.findById(id);
        if (t == null) return;

        ThuocDialog dlg = new ThuocDialog(view, "Sửa Thông Tin Thuốc", t);
        dlg.setVisible(true);

        if (dlg.isOk()) {
            try {
                thuocService.update(dlg.getThuoc());
                loadData();
                JOptionPane.showMessageDialog(view, "Đã cập nhật thông tin thuốc!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Lỗi khi sửa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void delete() {
        String id = view.getSelectedMedicineId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn thuốc để xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc muốn xóa thuốc này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                thuocService.delete(id);
                loadData();
                JOptionPane.showMessageDialog(view, "Đã xóa thuốc thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Không thể xóa thuốc: " + ex.getMessage(), "Lỗi ràng buộc", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
