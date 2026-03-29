package com.hospital.app.view;

import com.hospital.app.entity.PhongBenh;

import javax.swing.*;
import java.awt.*;

public class PhongBenhDialog extends JDialog {

    private JTextField txtMaPhong;
    private JTextField txtLoaiPhong;
    private JTextField txtSoGiuongToiDa;

    private JButton btnSave;
    private JButton btnCancel;

    private boolean isOk = false;
    private final PhongBenh phongBenh;

    public PhongBenhDialog(Frame parent, String title, PhongBenh phongBenh) {
        super(parent, title, true);
        this.phongBenh = phongBenh != null ? phongBenh : new PhongBenh();
        initComponents();
        fillData(phongBenh != null);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel pnlInput = new JPanel(new GridLayout(3, 2, 10, 10));
        pnlInput.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtMaPhong = new JTextField(20);
        txtLoaiPhong = new JTextField(20);
        txtSoGiuongToiDa = new JTextField(20);

        pnlInput.add(new JLabel("Mã phòng:"));
        pnlInput.add(txtMaPhong);
        pnlInput.add(new JLabel("Loại phòng (VIP/Thường/...):"));
        pnlInput.add(txtLoaiPhong);
        pnlInput.add(new JLabel("Số giường tối đa:"));
        pnlInput.add(txtSoGiuongToiDa);

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
            txtMaPhong.setText(phongBenh.getMaPhong());
            txtMaPhong.setEditable(false);
            txtLoaiPhong.setText(phongBenh.getLoaiPhong());
            if (phongBenh.getSoGiuongToiDa() != null) {
                txtSoGiuongToiDa.setText(String.valueOf(phongBenh.getSoGiuongToiDa()));
            }
        }
    }

    private boolean validateData() {
        if (txtMaPhong.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã phòng không được để trống!");
            return false;
        }
        if (txtLoaiPhong.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Loại phòng không được để trống!");
            return false;
        }
        try {
            int giuong = Integer.parseInt(txtSoGiuongToiDa.getText().trim());
            if (giuong <= 0) {
                JOptionPane.showMessageDialog(this, "Số giường tối đa phải lớn hơn 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số giường tối đa phải là số nguyên!");
            return false;
        }
        return true;
    }

    private void saveData() {
        phongBenh.setMaPhong(txtMaPhong.getText().trim());
        phongBenh.setLoaiPhong(txtLoaiPhong.getText().trim());
        phongBenh.setSoGiuongToiDa(Integer.parseInt(txtSoGiuongToiDa.getText().trim()));
    }

    public boolean isOk() {
        return isOk;
    }

    public PhongBenh getPhongBenh() {
        return phongBenh;
    }
}
