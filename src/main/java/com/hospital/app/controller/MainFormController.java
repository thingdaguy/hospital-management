package com.hospital.app.controller;

import com.hospital.app.dto.BenhNhanRowDTO;
import com.hospital.app.service.BenhNhanService;
import com.hospital.app.view.MainForm;

import java.util.List;

/**
 * Controller cho MainForm — nối View với Service (MVC).
 */
public class MainFormController {

    private final MainForm view;
    private final BenhNhanService benhNhanService = new BenhNhanService();

    public MainFormController(MainForm view) {
        this.view = view;
        wireEvents();
    }

    /** Gắn sự kiện nút và tải dữ liệu ban đầu. */
    private void wireEvents() {
        view.getBtnRefresh().addActionListener(e -> loadPatients());
        view.getBtnSearch().addActionListener(e -> searchPatients());
        loadPatients();
    }

    /** Tải lại toàn bộ danh sách bệnh nhân. */
    public void loadPatients() {
        view.getSearchField().setText("");
        List<BenhNhanRowDTO> rows = benhNhanService.listAllForTable();
        view.populatePatientTable(rows);
    }

    /** Tìm theo tên (gọi JPQL có điều kiện). */
    public void searchPatients() {
        String kw = view.getSearchField().getText();
        List<BenhNhanRowDTO> rows = benhNhanService.searchByName(kw);
        view.populatePatientTable(rows);
    }
}
