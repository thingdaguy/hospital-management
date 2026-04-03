package com.hospital.app.view;

import com.hospital.app.dto.HoaDonRowDTO;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HoaDonDialog extends JDialog {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DefaultTableModel tableModel;
    private final JTable table;

    public HoaDonDialog(JFrame parent, String title, List<HoaDonRowDTO> hoaDons, Consumer<String> onInvoiceDoubleClicked) {
        super(parent, title, true);
        setSize(600, 400);
        setLocationRelativeTo(parent);

        String[] cols = {"Mã Hóa Đơn", "Ngày Lập", "Tổng Tiền", "Trạng Thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);

        if (onInvoiceDoubleClicked != null) {
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                        String maHoaDon = tableModel.getValueAt(table.getSelectedRow(), 0).toString();
                        onInvoiceDoubleClicked.accept(maHoaDon);
                    }
                }
            });
        }

        populateTable(hoaDons);

        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        pnlCenter.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBottom.add(btnClose);

        setLayout(new BorderLayout());
        add(pnlCenter, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void populateTable(List<HoaDonRowDTO> rows) {
        tableModel.setRowCount(0);
        if (rows != null) {
            for (HoaDonRowDTO r : rows) {
                String date = r.getNgayLap() != null ? r.getNgayLap().format(DATE_FMT) : "";
                String total = r.getTongTien() != null ? String.format("%,.0f VNĐ", r.getTongTien()) : "0 VNĐ";
                tableModel.addRow(new Object[]{
                        r.getMaHoaDon(), date, total, r.getTrangThai()
                });
            }
        }
    }
}
