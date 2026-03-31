package com.hospital.app.view;

import com.hospital.app.entity.CaTruc;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class CaTrucDialog extends JDialog {


    private JTextField txtMaCa;
    private JTextField txtTenCa;
    private JTextField txtBatDau;
    private JTextField txtKetThuc;

    private JButton btnSave;
    private JButton btnCancel;

    private boolean isOk = false;
    private final CaTruc caTruc;

    public CaTrucDialog(Frame parent, String title, CaTruc caTruc) {
        super(parent, title, true);
        this.caTruc = caTruc != null ? caTruc : new CaTruc();
        initComponents();
        fillData(caTruc != null);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel pnlInput = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlInput.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtMaCa = new JTextField(20);
        txtTenCa = new JTextField(20);
        txtBatDau = new JTextField(20);
        txtKetThuc = new JTextField(20);

        pnlInput.add(new JLabel("Mã ca:"));
        pnlInput.add(txtMaCa);

        pnlInput.add(new JLabel("Tên ca:"));
        pnlInput.add(txtTenCa);

        pnlInput.add(new JLabel("Bắt đầu (HH:mm):"));
        pnlInput.add(txtBatDau);

        pnlInput.add(new JLabel("Kết thúc (HH:mm):"));
        pnlInput.add(txtKetThuc);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu");
        btnCancel = new JButton("Hủy");

        pnlButtons.add(btnSave);
        pnlButtons.add(btnCancel);

        btnCancel.addActionListener(e -> dispose());

        btnSave.addActionListener(e -> {
            if (validateData()) {
                saveData();
                isOk = true;
                dispose();
            }
        });

        add(pnlInput, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private void fillData(boolean isEditMode) {
        if (isEditMode) {
            txtMaCa.setText(caTruc.getMaCa());
            txtMaCa.setEditable(false);

            txtTenCa.setText(caTruc.getTenCa());

            if (caTruc.getThoiGianBatDau() != null) {
                txtBatDau.setText(caTruc.getThoiGianBatDau().toString());
            }

            if (caTruc.getThoiGianKetThuc() != null) {
                txtKetThuc.setText(caTruc.getThoiGianKetThuc().toString());
            }
        }
    }

    private boolean validateData() {
        if (txtMaCa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã ca không được để trống!");
            return false;
        }

        if (txtTenCa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên ca không được để trống!");
            return false;
        }

        try {
            LocalTime start = LocalTime.parse(txtBatDau.getText().trim());
            LocalTime end = LocalTime.parse(txtKetThuc.getText().trim());

            if (!start.isBefore(end)) {
                JOptionPane.showMessageDialog(this, "Giờ bắt đầu phải nhỏ hơn giờ kết thúc!");
                return false;
            }

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Thời gian phải đúng định dạng HH:mm (ví dụ 08:00)");
            return false;
        }

        return true;
    }

    private void saveData() {
        caTruc.setMaCa(txtMaCa.getText().trim());
        caTruc.setTenCa(txtTenCa.getText().trim());
        caTruc.setThoiGianBatDau(LocalTime.parse(txtBatDau.getText().trim()));
        caTruc.setThoiGianKetThuc(LocalTime.parse(txtKetThuc.getText().trim()));
    }

    public boolean isOk() {
        return isOk;
    }

    public CaTruc getCaTruc() {
        return caTruc;
    }


}
