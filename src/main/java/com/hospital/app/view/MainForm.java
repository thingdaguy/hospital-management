package com.hospital.app.view;

import com.hospital.app.dto.BacSiRowDTO;
import com.hospital.app.dto.BenhNhanRowDTO;
import com.hospital.app.dto.PhongBenhRowDTO;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * MainForm — form chính chứa các tabs: Bệnh Nhân, Bác Sĩ, Phòng Bệnh.
 */
public class MainForm extends JFrame {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Controls for Bệnh Nhân
    private final JTextField searchFieldBN = new JTextField(15);
    private final JButton btnSearchBN = new JButton("Tìm");
    private final JButton btnRefreshBN = new JButton("Tải lại");
    private final JButton btnAddBN = new JButton("Thêm");
    private final JButton btnEditBN = new JButton("Sửa");
    private final JButton btnDeleteBN = new JButton("Xóa");
    private final JButton btnInvoiceBN = new JButton("Hóa Đơn");
    private final DefaultTableModel tableModelBN;
    private final JTable tableBN;

    // Controls for Bác Sĩ
    private final JTextField searchFieldBS = new JTextField(15);
    private final JButton btnSearchBS = new JButton("Tìm");
    private final JButton btnRefreshBS = new JButton("Tải lại");
    private final JButton btnAddBS = new JButton("Thêm");
    private final JButton btnEditBS = new JButton("Sửa");
    private final JButton btnDeleteBS = new JButton("Xóa");
    private final DefaultTableModel tableModelBS;
    private final JTable tableBS;

    // Controls for Phòng Bệnh
    private final JTextField searchFieldPB = new JTextField(15);
    private final JButton btnSearchPB = new JButton("Tìm");
    private final JButton btnRefreshPB = new JButton("Tải lại");
    private final JButton btnAddPB = new JButton("Thêm");
    private final JButton btnEditPB = new JButton("Sửa");
    private final JButton btnDeletePB = new JButton("Xóa");
    private final DefaultTableModel tableModelPB;
    private final JTable tablePB;

    // Overview Panel
    private final OverviewPanel overviewPanel = new OverviewPanel();
    
    public MainForm() {
        super("Quản lý Bệnh viện — Màn hình chính");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // 0. Tab Tổng quan (Dashboard)
        tabbedPane.addTab("Tổng quan", overviewPanel);

        // 1. Tab Bệnh Nhân
        String[] colsBN = {
                "Mã BN", "Họ tên", "Ngày sinh", "Điện thoại",
                "Bác sĩ tiếp nhận", "Mã phòng", "Loại phòng"
        };
        tableModelBN = createTableModel(colsBN);
        tableBN = createTable(tableModelBN);
        JPanel pnlBenhNhan = createTabPanel(searchFieldBN, btnSearchBN, btnRefreshBN, btnAddBN, btnEditBN, btnDeleteBN, tableBN, "Tên bệnh nhân:", btnInvoiceBN);
        tabbedPane.addTab("Quản lý Bệnh Nhân", pnlBenhNhan);

        // 2. Tab Bác Sĩ
        String[] colsBS = {
                "Mã BS", "Họ tên", "Ngày vào làm", "Chuyên môn", "Tên khoa"
        };
        tableModelBS = createTableModel(colsBS);
        tableBS = createTable(tableModelBS);
        JPanel pnlBacSi = createTabPanel(searchFieldBS, btnSearchBS, btnRefreshBS, btnAddBS, btnEditBS, btnDeleteBS, tableBS, "Tên bác sĩ:");
        tabbedPane.addTab("Quản lý Bác Sĩ", pnlBacSi);

        // 3. Tab Phòng Bệnh
        String[] colsPB = {
                "Mã phòng", "Loại phòng", "Số giường tối đa"
        };
        tableModelPB = createTableModel(colsPB);
        tablePB = createTable(tableModelPB);
        JPanel pnlPhongBenh = createTabPanel(searchFieldPB, btnSearchPB, btnRefreshPB, btnAddPB, btnEditPB, btnDeletePB, tablePB, "Loại phòng/Mã:");
        tabbedPane.addTab("Quản lý Phòng Bệnh", pnlPhongBenh);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private DefaultTableModel createTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        return table;
    }

    private JPanel createTabPanel(JTextField txtSearch, JButton btnSearch, JButton btnRefresh,
                                  JButton btnAdd, JButton btnEdit, JButton btnDelete, JTable table, String searchLabel, JButton... extraButtons) {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        top.add(new JLabel(searchLabel));
        top.add(txtSearch);
        top.add(btnSearch);
        top.add(btnRefresh);
        top.add(btnAdd);
        top.add(btnEdit);
        top.add(btnDelete);
        if (extraButtons != null) {
            for (JButton btn : extraButtons) {
                if (btn != null) top.add(btn);
            }
        }

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(top, BorderLayout.NORTH);
        mainPanel.add(center, BorderLayout.CENTER);
        return mainPanel;
    }

    // --- POPULATE TABLES ---
    public void populatePatientTable(List<BenhNhanRowDTO> rows) {
        tableModelBN.setRowCount(0);
        for (BenhNhanRowDTO r : rows) {
            String dob = r.getNgaySinh() != null ? r.getNgaySinh().format(DATE_FMT) : "";
            String phone = r.getSoDienThoai() != null ? r.getSoDienThoai() : "";
            tableModelBN.addRow(new Object[] {
                    r.getMaBenhNhan(), r.getTenBenhNhan(), dob, phone,
                    r.getTenBacSiTiepNhan(), r.getMaPhong(), r.getLoaiPhong()
            });
        }
    }

    public void populateDoctorTable(List<BacSiRowDTO> rows) {
        tableModelBS.setRowCount(0);
        for (BacSiRowDTO r : rows) {
            String dob = r.getNgayVaoLam() != null ? r.getNgayVaoLam().format(DATE_FMT) : "";
            tableModelBS.addRow(new Object[] {
                    r.getMaBacSi(), r.getTenBacSi(), dob, r.getChuyenMon(), r.getTenKhoa()
            });
        }
    }

    public void populateRoomTable(List<PhongBenhRowDTO> rows) {
        tableModelPB.setRowCount(0);
        for (PhongBenhRowDTO r : rows) {
            tableModelPB.addRow(new Object[] {
                    r.getMaPhong(), r.getLoaiPhong(), r.getSoGiuongToiDa()
            });
        }
    }

    // --- GETTERS CHO ID ĐANG CHỌN (SELECTED ID) ---
    public String getSelectedPatientId() {
        int row = tableBN.getSelectedRow();
        if (row < 0) return null;
        return tableModelBN.getValueAt(row, 0).toString();
    }

    public String getSelectedDoctorId() {
        int row = tableBS.getSelectedRow();
        if (row < 0) return null;
        return tableModelBS.getValueAt(row, 0).toString();
    }

    public String getSelectedRoomId() {
        int row = tablePB.getSelectedRow();
        if (row < 0) return null;
        return tableModelPB.getValueAt(row, 0).toString();
    }

    // --- GETTERS CHO CONTROLS BỆNH NHÂN ---
    public JTextField getSearchFieldBN() { return searchFieldBN; }
    public JButton getBtnSearchBN() { return btnSearchBN; }
    public JButton getBtnRefreshBN() { return btnRefreshBN; }
    public JButton getBtnAddBN() { return btnAddBN; }
    public JButton getBtnEditBN() { return btnEditBN; }
    public JButton getBtnDeleteBN() { return btnDeleteBN; }
    public JButton getBtnInvoiceBN() { return btnInvoiceBN; }

    // --- GETTERS CHO CONTROLS BÁC SĨ ---
    public JTextField getSearchFieldBS() { return searchFieldBS; }
    public JButton getBtnSearchBS() { return btnSearchBS; }
    public JButton getBtnRefreshBS() { return btnRefreshBS; }
    public JButton getBtnAddBS() { return btnAddBS; }
    public JButton getBtnEditBS() { return btnEditBS; }
    public JButton getBtnDeleteBS() { return btnDeleteBS; }

    // --- GETTERS CHO CONTROLS PHÒNG BỆNH ---
    public JTextField getSearchFieldPB() { return searchFieldPB; }
    public JButton getBtnSearchPB() { return btnSearchPB; }
    public JButton getBtnRefreshPB() { return btnRefreshPB; }
    public JButton getBtnAddPB() { return btnAddPB; }
    public JButton getBtnEditPB() { return btnEditPB; }
    public JButton getBtnDeletePB() { return btnDeletePB; }
    
    // --- GETTERS CHO OVERVIEW ---
    public OverviewPanel getOverviewPanel() { return overviewPanel; }
}
