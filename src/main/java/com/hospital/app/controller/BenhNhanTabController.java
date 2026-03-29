package com.hospital.app.controller;

import com.hospital.app.dto.BenhNhanRowDTO;
import com.hospital.app.entity.BacSi;
import com.hospital.app.entity.BenhNhan;
import com.hospital.app.entity.PhongBenh;
import com.hospital.app.service.BacSiService;
import com.hospital.app.service.BenhNhanService;
import com.hospital.app.service.HoaDonService;
import com.hospital.app.service.PhongBenhService;
import com.hospital.app.view.BenhNhanDialog;
import com.hospital.app.view.HoaDonDialog;
import com.hospital.app.view.MainForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class BenhNhanTabController {
    private final MainForm view;
    private final BenhNhanService benhNhanService;
    private final BacSiService bacSiService;
    private final PhongBenhService phongBenhService;
    private final HoaDonService hoaDonService;

    public BenhNhanTabController(MainForm view, BenhNhanService benhNhanService, 
                                 BacSiService bacSiService, PhongBenhService phongBenhService,
                                 HoaDonService hoaDonService) {
        this.view = view;
        this.benhNhanService = benhNhanService;
        this.bacSiService = bacSiService;
        this.phongBenhService = phongBenhService;
        this.hoaDonService = hoaDonService;
        wireEvents();
    }

    private void wireEvents() {
        view.getBtnRefreshBN().addActionListener(e -> loadPatients());
        view.getBtnSearchBN().addActionListener(e -> searchPatients());
        view.getBtnAddBN().addActionListener(e -> addPatient());
        view.getBtnEditBN().addActionListener(e -> editPatient());
        view.getBtnDeleteBN().addActionListener(e -> deletePatient());
        view.getBtnInvoiceBN().addActionListener(e -> showPatientInvoices());
        loadPatients();
    }

    private void loadPatients() {
        view.getSearchFieldBN().setText("");
        List<BenhNhanRowDTO> rows = benhNhanService.listAllForTable();
        view.populatePatientTable(rows);
    }

    private void searchPatients() {
        String kw = view.getSearchFieldBN().getText();
        List<BenhNhanRowDTO> rows = benhNhanService.searchByName(kw);
        view.populatePatientTable(rows);
    }

    private void addPatient() {
        try {
            List<BacSi> bacSis = bacSiService.findAll();
            List<PhongBenh> phongBenhs = phongBenhService.findAll();
            
            Map<String, Long> roomOccupancy = new HashMap<>();
            for (PhongBenh pb : phongBenhs) {
                roomOccupancy.put(pb.getMaPhong(), benhNhanService.countByPhong(pb.getMaPhong()));
            }
            
            BenhNhanDialog dlg = new BenhNhanDialog(view, "Thêm Mới Bệnh Nhân", bacSis, phongBenhs, roomOccupancy, null);
            dlg.setVisible(true);
            if (dlg.isOk()) {
                benhNhanService.save(dlg.getBenhNhan());
                loadPatients();
                JOptionPane.showMessageDialog(view, "Thêm mới thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi thêm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editPatient() {
        String id = view.getSelectedPatientId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một bệnh nhân để sửa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            BenhNhan bn = benhNhanService.findById(id);
            if (bn == null) return;
            List<BacSi> bacSis = bacSiService.findAll();
            List<PhongBenh> phongBenhs = phongBenhService.findAll();
            
            Map<String, Long> roomOccupancy = new HashMap<>();
            for (PhongBenh pb : phongBenhs) {
                roomOccupancy.put(pb.getMaPhong(), benhNhanService.countByPhong(pb.getMaPhong()));
            }
            
            BenhNhanDialog dlg = new BenhNhanDialog(view, "Sửa Bệnh Nhân", bacSis, phongBenhs, roomOccupancy, bn);
            dlg.setVisible(true);

            if (dlg.isOk()) {
                benhNhanService.update(dlg.getBenhNhan());
                loadPatients();
                JOptionPane.showMessageDialog(view, "Cập nhật thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePatient() {
        String id = view.getSelectedPatientId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một bệnh nhân để xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(view, "Xóa bệnh nhân " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                benhNhanService.delete(id);
                loadPatients();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Lỗi khi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showPatientInvoices() {
        String id = view.getSelectedPatientId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một bệnh nhân để xem hóa đơn.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            var hoaDons = hoaDonService.listByBenhNhan(id);
            HoaDonDialog dlg = new HoaDonDialog(view, "Hóa Đơn Của Bệnh Nhân: " + id, hoaDons);
            dlg.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tải hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
