package com.hospital.app.view;

import com.hospital.app.entity.BacSi;
import com.hospital.app.entity.Khoa;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BacSiDialog extends JDialog {

    private JTextField txtMaBacSi;
    private JTextField txtTenBacSi;
    private JTextField txtNgayVaoLam;
    private JTextField txtChuyenMon;
    private JComboBox<ComboItem> cbKhoa;

    private JButton btnSave;
    private JButton btnCancel;

    private boolean isOk = false;
    private final BacSi bacSi;

    public BacSiDialog(Frame parent, String title, List<Khoa> khoas, BacSi bacSi) {
        super(parent, title, true);
        this.bacSi = bacSi != null ? bacSi : new BacSi();
        initComponents(khoas);
        fillData(bacSi != null);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents(List<Khoa> khoas) {
        JPanel pnlInput = new JPanel(new GridLayout(5, 2, 10, 10));
        pnlInput.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtMaBacSi = new JTextField(20);
        txtTenBacSi = new JTextField(20);
        txtNgayVaoLam = new JTextField(20);
        txtChuyenMon = new JTextField(20);

        cbKhoa = new JComboBox<>();
        for (Khoa k : khoas) {
            cbKhoa.addItem(new ComboItem(k.getMaKhoa(), k.getMaKhoa() + " - " + k.getTenKhoa()));
        }

        pnlInput.add(new JLabel("Mã BS:"));
        pnlInput.add(txtMaBacSi);
        pnlInput.add(new JLabel("Họ Tên BS:"));
        pnlInput.add(txtTenBacSi);
        pnlInput.add(new JLabel("Ngày vào làm (dd/MM/yyyy):"));
        pnlInput.add(txtNgayVaoLam);
        pnlInput.add(new JLabel("Chuyên môn:"));
        pnlInput.add(txtChuyenMon);
        pnlInput.add(new JLabel("Khoa công tác:"));
        pnlInput.add(cbKhoa);

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
            txtMaBacSi.setText(bacSi.getMaBacSi());
            txtMaBacSi.setEditable(false);
            txtTenBacSi.setText(bacSi.getTenBacSi());
            if (bacSi.getNgayVaoLam() != null) {
                txtNgayVaoLam.setText(bacSi.getNgayVaoLam().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            txtChuyenMon.setText(bacSi.getChuyenMon());

            if (bacSi.getKhoa() != null) {
                setComboSelected(cbKhoa, bacSi.getKhoa().getMaKhoa());
            }
        }
    }

    private void setComboSelected(JComboBox<ComboItem> cb, String id) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            if (cb.getItemAt(i).getId().equals(id)) {
                cb.setSelectedIndex(i);
                break;
            }
        }
    }

    private boolean validateData() {
        if (txtMaBacSi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã bác sĩ không được để trống!");
            return false;
        }
        if (txtTenBacSi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên bác sĩ không được để trống!");
            return false;
        }
        try {
            LocalDate.parse(txtNgayVaoLam.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày vào làm không hợp lệ (định dạng dd/MM/yyyy)!");
            return false;
        }
        return true;
    }

    private void saveData() {
        bacSi.setMaBacSi(txtMaBacSi.getText().trim());
        bacSi.setTenBacSi(txtTenBacSi.getText().trim());
        bacSi.setNgayVaoLam(LocalDate.parse(txtNgayVaoLam.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        bacSi.setChuyenMon(txtChuyenMon.getText().trim());

        ComboItem selectedKhoa = (ComboItem) cbKhoa.getSelectedItem();
        if (selectedKhoa != null) {
            Khoa k = new Khoa();
            k.setMaKhoa(selectedKhoa.getId());
            bacSi.setKhoa(k);
        }
    }

    public boolean isOk() {
        return isOk;
    }

    public BacSi getBacSi() {
        return bacSi;
    }

    public static class ComboItem {
        private String id;
        private String label;

        public ComboItem(String id, String label) {
            this.id = id;
            this.label = label;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
