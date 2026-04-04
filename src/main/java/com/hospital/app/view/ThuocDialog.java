package com.hospital.app.view;

import com.hospital.app.entity.Thuoc;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class ThuocDialog extends JDialog {

    private JTextField txtMaThuoc;
    private JTextField txtTenThuoc;
    private JTextField txtThanhPhan;
    private JTextField txtGiaBan;

    private JButton btnSave;
    private JButton btnCancel;

    private boolean isOk = false;
    private final Thuoc thuoc;

    public ThuocDialog(Frame parent, String title, Thuoc thuoc) {
        super(parent, title, true);
        this.thuoc = thuoc != null ? thuoc : new Thuoc();
        initComponents();
        fillData(thuoc != null);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel pnlInput = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlInput.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtMaThuoc = new JTextField(20);
        txtTenThuoc = new JTextField(20);
        txtThanhPhan = new JTextField(20);
        txtGiaBan = new JTextField(20);

        pnlInput.add(new JLabel("Mã thuốc:"));
        pnlInput.add(txtMaThuoc);
        pnlInput.add(new JLabel("Tên thuốc:"));
        pnlInput.add(txtTenThuoc);
        pnlInput.add(new JLabel("Thành phần:"));
        pnlInput.add(txtThanhPhan);
        pnlInput.add(new JLabel("Giá bán (VNĐ):"));
        pnlInput.add(txtGiaBan);

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
            txtMaThuoc.setText(thuoc.getMaThuoc());
            txtMaThuoc.setEditable(false);
            txtTenThuoc.setText(thuoc.getTenThuoc());
            txtThanhPhan.setText(thuoc.getThanhPhan());
            if (thuoc.getGiaBan() != null) {
                txtGiaBan.setText(thuoc.getGiaBan().toString());
            }
        }
    }

    private boolean validateData() {
        if (txtMaThuoc.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã thuốc không được để trống!");
            return false;
        }
        if (txtTenThuoc.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên thuốc không được để trống!");
            return false;
        }
        if (txtThanhPhan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Thành phần không được để trống!");
            return false;
        }
        try {
            BigDecimal gia = new BigDecimal(txtGiaBan.getText().trim());
            if (gia.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Giá bán phải lớn hơn hoặc bằng 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá bán phải là số hợp lệ!");
            return false;
        }
        return true;
    }

    private void saveData() {
        thuoc.setMaThuoc(txtMaThuoc.getText().trim());
        thuoc.setTenThuoc(txtTenThuoc.getText().trim());
        thuoc.setThanhPhan(txtThanhPhan.getText().trim());
        thuoc.setGiaBan(new BigDecimal(txtGiaBan.getText().trim()));
    }

    public boolean isOk() {
        return isOk;
    }

    public Thuoc getThuoc() {
        return thuoc;
    }
}
