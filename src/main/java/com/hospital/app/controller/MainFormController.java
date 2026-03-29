package com.hospital.app.controller;

import com.hospital.app.service.BacSiService;
import com.hospital.app.service.BenhNhanService;
import com.hospital.app.service.HoaDonService;
import com.hospital.app.service.KhoaService;
import com.hospital.app.service.PhongBenhService;
import com.hospital.app.view.MainForm;

public class MainFormController {

    private final MainForm view;

    // Services
    private final BenhNhanService benhNhanService = new BenhNhanService();
    private final BacSiService bacSiService = new BacSiService();
    private final PhongBenhService phongBenhService = new PhongBenhService();
    private final KhoaService khoaService = new KhoaService();
    private final HoaDonService hoaDonService = new HoaDonService();

    // Tab Controllers
    private BenhNhanTabController benhNhanTabController;
    private BacSiTabController bacSiTabController;
    private PhongBenhTabController phongBenhTabController;

    public MainFormController(MainForm view) {
        this.view = view;
        initControllers();
    }

    private void initControllers() {
        benhNhanTabController = new BenhNhanTabController(
                view, benhNhanService, bacSiService, phongBenhService, hoaDonService);
        bacSiTabController = new BacSiTabController(view, bacSiService, khoaService);
        phongBenhTabController = new PhongBenhTabController(view, phongBenhService);
    }
}
