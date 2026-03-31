package com.hospital.app.controller;

import com.hospital.app.entity.CaTruc;
import com.hospital.app.service.CaTrucService;
import com.hospital.app.view.CaTrucDialog;
import com.hospital.app.view.MainForm;

import javax.swing.*;
import java.util.List;

public class CaTrucTabController {

    private final MainForm view;
    private final CaTrucService service;

    public CaTrucTabController(MainForm view, CaTrucService service) {
        this.view = view;
        this.service = service;
        wireEvents();
        load();
    }

    private void wireEvents() {
        view.getBtnRefreshCT().addActionListener(e -> load());
        view.getBtnSearchCT().addActionListener(e -> search());
        view.getBtnAddCT().addActionListener(e -> add());
        view.getBtnEditCT().addActionListener(e -> edit());
        view.getBtnDeleteCT().addActionListener(e -> delete());

        load();
    }

    private void load() {
        view.getSearchFieldCT().setText("");
        var rows = service.listAllForTable();
        view.populateCaTrucTable(rows);
    }

    private void search() {
        String kw = view.getSearchFieldCT().getText();
        var rows = service.searchByName(kw);
        view.populateCaTrucTable(rows);
    }

    private void add() {
        CaTrucDialog dlg = new CaTrucDialog(view, "Thêm ca trực", null);
        dlg.setVisible(true);

        if (dlg.isOk()) {
            service.save(dlg.getCaTruc());
            load();
        }}

    private void edit() {
        String id = view.getSelectedCaTrucId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Chọn ca trực!");
            return;
        }

            CaTruc ct = service.findById(id);

            CaTrucDialog dlg = new CaTrucDialog(view, "Sửa ca trực", ct);
            dlg.setVisible(true);

            if (dlg.isOk()) {
                service.update(dlg.getCaTruc());
                load();
            }
    }

    private void delete() {
        String id = view.getSelectedCaTrucId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Chọn ca trực!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Xóa " + id + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            service.delete(id);
            load();
        }
    }
}