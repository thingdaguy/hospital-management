package com.hospital.app.view;

import com.hospital.app.dto.BenhNhanRowDTO;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * MainForm — form chính: danh sách bệnh nhân (SubForm khác có thể mở sau này).
 */
public class MainForm extends JFrame {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JTextField searchField = new JTextField(24);
    private final JButton btnSearch = new JButton("Tìm kiếm");
    private final JButton btnRefresh = new JButton("Tải lại");
    private final DefaultTableModel tableModel;
    private final JTable table;

    public MainForm() {
        super("Quản lý Bệnh viện — Danh sách bệnh nhân");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 520);
        setLocationRelativeTo(null);

        String[] cols = {
                "Mã BN", "Họ tên", "Ngày sinh", "Điện thoại",
                "Bác sĩ tiếp nhận", "Mã phòng", "Loại phòng"
        };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        top.add(new JLabel("Tên bệnh nhân:"));
        top.add(searchField);
        top.add(btnSearch);
        top.add(btnRefresh);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    /** Đổ dữ liệu DTO vào bảng. */
    public void populatePatientTable(List<BenhNhanRowDTO> rows) {
        tableModel.setRowCount(0);
        for (BenhNhanRowDTO r : rows) {
            String dob = r.getNgaySinh() != null ? r.getNgaySinh().format(DATE_FMT) : "";
            String phone = r.getSoDienThoai() != null ? r.getSoDienThoai() : "";
            tableModel.addRow(new Object[]{
                    r.getMaBenhNhan(),
                    r.getTenBenhNhan(),
                    dob,
                    phone,
                    r.getTenBacSiTiepNhan(),
                    r.getMaPhong(),
                    r.getLoaiPhong()
            });
        }
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JButton getBtnSearch() {
        return btnSearch;
    }

    public JButton getBtnRefresh() {
        return btnRefresh;
    }
}
