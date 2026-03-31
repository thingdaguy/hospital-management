package com.hospital.app.controller;

import com.hospital.app.service.TaiKhoanService;
import com.hospital.app.service.ThongKeService;
import com.hospital.app.view.AdminDialog;
import com.hospital.app.view.MainForm;
import com.hospital.app.view.OverviewPanel;

/**
 * Controller cho tab Tổng quan.
 */
public class OverviewController {

    private final MainForm mainView;
    private final OverviewPanel view;
    private final ThongKeService thongKeService;
    private final TaiKhoanService taiKhoanService;

    public OverviewController(MainForm mainView, ThongKeService thongKeService, TaiKhoanService taiKhoanService) {
        this.mainView = mainView;
        this.view = mainView.getOverviewPanel();
        this.thongKeService = thongKeService;
        this.taiKhoanService = taiKhoanService;
        
        initEvents();
        refreshStatistics();
    }

    private void initEvents() {
        view.getBtnRefresh().addActionListener(e -> refreshStatistics());
        view.getBtnAddAdmin().addActionListener(e -> openAddAdminDialog());
    }

    public void refreshStatistics() {
        view.updateData(thongKeService.getOverviewData());
    }

    private void openAddAdminDialog() {
        AdminDialog dialog = new AdminDialog(mainView);
        new AdminController(dialog, taiKhoanService);
        dialog.setVisible(true);
    }
}
