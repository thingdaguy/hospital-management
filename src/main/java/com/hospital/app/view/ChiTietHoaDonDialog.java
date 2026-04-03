package com.hospital.app.view;

import com.hospital.app.dto.ChiTietDichVuDTO;
import com.hospital.app.dto.ChiTietHoaDonDTO;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

public class ChiTietHoaDonDialog extends JDialog {

    private final DefaultTableModel tableModel;
    private final JTable table;

    public ChiTietHoaDonDialog(JFrame parent, String maHoaDon, ChiTietHoaDonDTO dto) {
        super(parent, "Chi tiết Hóa Đơn - " + maHoaDon, true);
        setSize(500, 400);
        setLocationRelativeTo(parent);

        JPanel pnlTop = new JPanel(new GridLayout(3, 1, 5, 5));
        pnlTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblThuoc = new JLabel(String.format("Tổng tiền thuốc: %,.0f VNĐ", dto.getTongTienThuoc()));
        JLabel lblDichVu = new JLabel(String.format("Tổng tiền dịch vụ: %,.0f VNĐ", dto.getTongTienDichVu()));
        JLabel lblTong = new JLabel(String.format("Tổng tiền hóa đơn: %,.0f VNĐ", dto.getTongTienHoaDon()));
        lblTong.setFont(lblTong.getFont().deriveFont(Font.BOLD));

        pnlTop.add(lblThuoc);
        pnlTop.add(lblDichVu);
        pnlTop.add(lblTong);

        String[] cols = {"Tên Dịch Vụ", "Thành Tiền"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);

        populateTable(dto.getDanhSachDichVu());

        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBorder(BorderFactory.createTitledBorder("Danh sách Dịch Vụ"));
        pnlCenter.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBottom.add(btnClose);

        setLayout(new BorderLayout());
        add(pnlTop, BorderLayout.NORTH);
        add(pnlCenter, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void populateTable(List<ChiTietDichVuDTO> dtoList) {
        if (dtoList != null) {
            for (ChiTietDichVuDTO d : dtoList) {
                String amount = d.getDonGia() != null ? String.format("%,.0f VNĐ", d.getDonGia()) : "0 VNĐ";
                tableModel.addRow(new Object[]{d.getTenDichVu(), amount});
            }
        }
    }
}
