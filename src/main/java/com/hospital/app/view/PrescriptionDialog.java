package com.hospital.app.view;

import com.hospital.app.entity.Thuoc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrescriptionDialog extends JDialog {

    private final List<Thuoc> allThuoc;
    private JComboBox<Thuoc> comboThuoc;
    private JSpinner spinnerQuantity;
    private JButton btnAdd;
    private JButton btnRemove;
    private JTextArea txtGhiChu;

    private JTable tableItems;
    private DefaultTableModel tableModelItems;

    private JButton btnSave;
    private JButton btnCancel;

    private boolean isOk = false;
    private final Map<String, Integer> items = new HashMap<>();

    public PrescriptionDialog(Frame parent, String title, List<Thuoc> allThuoc) {
        this(parent, title, allThuoc, null, "");
    }

    public PrescriptionDialog(Frame parent, String title, List<Thuoc> allThuoc, 
                              Map<String, Integer> initialItems, String initialNote) {
        super(parent, title, true);
        this.allThuoc = allThuoc;
        if (initialItems != null) {
            this.items.putAll(initialItems);
        }
        initComponents();
        if (initialNote != null) {
            txtGhiChu.setText(initialNote);
        }
        refreshTable();
        setSize(600, 500);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // --- Top: Select Medicine ---
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Thêm thuốc vào đơn"));

        comboThuoc = new JComboBox<>(allThuoc.toArray(new Thuoc[0]));
        // Custom renderer for JComboBox to show medicine name
        comboThuoc.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Thuoc) {
                    setText(((Thuoc) value).getTenThuoc());
                }
                return this;
            }
        });

        spinnerQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        btnAdd = new JButton("Thêm");
        btnRemove = new JButton("Xóa dòng");

        pnlTop.add(new JLabel("Chọn thuốc:"));
        pnlTop.add(comboThuoc);
        pnlTop.add(new JLabel("Số lượng:"));
        pnlTop.add(spinnerQuantity);
        pnlTop.add(btnAdd);
        pnlTop.add(btnRemove);

        // --- Center: Table of items ---
        tableModelItems = new DefaultTableModel(new String[]{"Mã thuốc", "Tên thuốc", "Số lượng"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableItems = new JTable(tableModelItems);
        JScrollPane scrollItems = new JScrollPane(tableItems);

        // --- Bottom: Notes & Buttons ---
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlGhiChu = new JPanel(new BorderLayout());
        pnlGhiChu.add(new JLabel("Ghi chú:"), BorderLayout.NORTH);
        txtGhiChu = new JTextArea(3, 20);
        pnlGhiChu.add(new JScrollPane(txtGhiChu), BorderLayout.CENTER);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu đơn thuốc");
        btnCancel = new JButton("Hủy");
        pnlActions.add(btnSave);
        pnlActions.add(btnCancel);

        pnlBottom.add(pnlGhiChu, BorderLayout.CENTER);
        pnlBottom.add(pnlActions, BorderLayout.SOUTH);

        add(pnlTop, BorderLayout.NORTH);
        add(scrollItems, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);

        // --- Events ---
        btnAdd.addActionListener(e -> {
            Thuoc selected = (Thuoc) comboThuoc.getSelectedItem();
            if (selected == null) return;
            int qty = (int) spinnerQuantity.getValue();

            if (items.containsKey(selected.getMaThuoc())) {
                int oldQty = items.get(selected.getMaThuoc());
                items.put(selected.getMaThuoc(), oldQty + qty);
            } else {
                items.put(selected.getMaThuoc(), qty);
            }
            refreshTable();
        });

        btnRemove.addActionListener(e -> {
            int row = tableItems.getSelectedRow();
            if (row >= 0) {
                String maThuoc = tableModelItems.getValueAt(row, 0).toString();
                items.remove(maThuoc);
                refreshTable();
            }
        });

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> {
            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Đơn thuốc chưa có món nào!");
                return;
            }
            isOk = true;
            dispose();
        });
    }

    private void refreshTable() {
        tableModelItems.setRowCount(0);
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String ma = entry.getKey();
            Integer qty = entry.getValue();
            String ten = allThuoc.stream().filter(t -> t.getMaThuoc().equals(ma)).findFirst().map(Thuoc::getTenThuoc).orElse("?");
            tableModelItems.addRow(new Object[]{ma, ten, qty});
        }
    }

    public boolean isOk() {
        return isOk;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public String getGhiChu() {
        return txtGhiChu.getText().trim();
    }
}
