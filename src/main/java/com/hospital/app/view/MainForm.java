package com.hospital.app.view;

import com.hospital.app.dto.BacSiRowDTO;
import com.hospital.app.dto.BenhNhanRowDTO;
import com.hospital.app.dto.PhongBenhRowDTO;
import com.hospital.app.dto.LuotDieuTriRowDTO;
import com.hospital.app.dto.ThuocRowDTO;



import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * MainForm : Header + Sidebar + Content Area.

 */
public class MainForm extends JFrame {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    //  Màu sắc theme
    private static final Color SIDEBAR_BG       = new Color(0x1A73C8);
    private static final Color SIDEBAR_HOVER    = new Color(0x1558A0);
    private static final Color SIDEBAR_SELECTED = new Color(0x0D3F7A);
    private static final Color HEADER_BG        = new Color(0xF0F6FF);
    private static final Color CONTENT_BG       = new Color(0xF4F7FB);
    private static final Color CARD_BG          = Color.WHITE;
    private static final Color TEXT_SIDEBAR     = Color.WHITE;
    private static final Color TEXT_PRIMARY     = new Color(0x1A1A2E);
    private static final Color ACCENT           = new Color(0x1A73C8);

    // Controls Bệnh Nhân
    private final JTextField searchFieldBN  = new JTextField(15);
    private final JButton btnSearchBN       = createActionButton(" Tìm", ACCENT);
    private final JButton btnRefreshBN      = createActionButton(" Tải lại", ACCENT);
    private final JButton btnAddBN          = createActionButton(" Thêm", ACCENT);
    private final JButton btnEditBN         = createActionButton(" Sửa", ACCENT);
    private final JButton btnDeleteBN       = createActionButton(" Xóa",ACCENT);
    private final DefaultTableModel tableModelBN;
    private final JTable tableBN;
    
    // Controls Lượt Điều Trị
    private final JTextField searchFieldLDT = new JTextField(15);
    private final JButton btnSearchLDT      = createActionButton(" Tìm", ACCENT);
    private final JButton btnRefreshLDT     = createActionButton(" Tải lại", ACCENT);
    private final JButton btnAddLDT         = createActionButton(" Tiếp nhận", ACCENT);
    private final JButton btnEditLDT        = createActionButton(" Sửa", ACCENT);
    private final JButton btnInvoiceLDT     = createActionButton(" Hóa Đơn", ACCENT);
    private final JButton btnPrescribeLDT   = createActionButton(" Kê đơn", ACCENT);
    private final JButton btnDischargeLDT   = createActionButton(" Xuất Viện", new Color(0x28A745));
    private final DefaultTableModel tableModelLDT;
    private final JTable tableLDT;

    // Controls Thuốc
    private final JTextField searchFieldTH  = new JTextField(15);
    private final JButton btnSearchTH       = createActionButton(" Tìm", ACCENT);
    private final JButton btnRefreshTH      = createActionButton(" Tải lại", ACCENT);
    private final JButton btnAddTH          = createActionButton(" Thêm", ACCENT);
    private final JButton btnEditTH         = createActionButton(" Sửa", ACCENT);
    private final JButton btnDeleteTH       = createActionButton(" Xóa", ACCENT);
    private final DefaultTableModel tableModelTH;
    private final JTable tableTH;

    // Controls Bác Sĩ
    private final JTextField searchFieldBS  = new JTextField(15);
    private final JButton btnSearchBS       = createActionButton(" Tìm", ACCENT);
    private final JButton btnRefreshBS      = createActionButton(" Tải lại", ACCENT);
    private final JButton btnAddBS          = createActionButton(" Thêm",ACCENT);
    private final JButton btnEditBS         = createActionButton(" Sửa", ACCENT);
    private final JButton btnDeleteBS       = createActionButton(" Xóa",ACCENT);
    private final DefaultTableModel tableModelBS;
    private final JTable tableBS;

    // Controls Phòng Bệnh
    private final JTextField searchFieldPB  = new JTextField(15);
    private final JButton btnSearchPB  = createActionButton("Tìm", ACCENT);
    private final JButton btnRefreshPB = createActionButton("Tải lại", ACCENT);
    private final JButton btnAddPB    = createActionButton("Thêm", ACCENT);
    private final JButton btnEditPB    = createActionButton("Sửa", ACCENT);
    private final JButton btnDeletePB  = createActionButton("Xóa", ACCENT);
    private final DefaultTableModel tableModelPB;
    private final JTable tablePB;

    //  Overview Panel
    private final OverviewPanel overviewPanel = new OverviewPanel();

    //  Layout
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentArea   = new JPanel(cardLayout);
    private JButton activeSidebarBtn   = null;

    private static final String TAB_OVERVIEW  = "Thống kê";
    private static final String TAB_PATIENT   = "Bệnh nhân";
    private static final String TAB_ENCOUNTER = "Lượt điều trị";
    private static final String TAB_DOCTOR    = "Bác sĩ";
    private static final String TAB_ROOM      = "Phòng nội trú";
    private static final String TAB_MEDICINE  = "Thuốc";

    public MainForm() {
        super("HealthSphere HMS — Quản lý Bệnh viện");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setMinimumSize(new Dimension(900, 550));
        setLocationRelativeTo(null);

        // Build tables
        tableModelBN = createTableModel(new String[]{
                "Mã BN", "Họ tên", "Ngày sinh", "Điện thoại", "Số thẻ BHYT"
        });
        tableBN = createTable(tableModelBN);
        
        tableModelLDT = createTableModel(new String[]{
                "Mã Lượt", "Tên BN", "Bác sĩ", "Khoa", "Phòng", "Ngày Khám", "Ngày Ra", "Trạng Thái"
        });
        tableLDT = createTable(tableModelLDT);

        tableModelBS = createTableModel(new String[]{
                "Mã BS", "Họ tên", "Ngày vào làm", "Chuyên môn", "Tên khoa"
        });
        tableBS = createTable(tableModelBS);

        tableModelPB = createTableModel(new String[]{
                "Mã phòng", "Loại phòng", "Số giường tối đa"
        });
        tablePB = createTable(tableModelPB);

        tableModelTH = createTableModel(new String[]{
                "Mã thuốc", "Tên thuốc", "Thành phần", "Giá bán"
        });
        tableTH = createTable(tableModelTH);
        //  Assemble frame
        setLayout(new BorderLayout());
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);

        // Select first item by default
        SwingUtilities.invokeLater(() -> cardLayout.show(contentArea, TAB_OVERVIEW));
    }


    // HEADER

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD0E4FF)),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));
        header.setPreferredSize(new Dimension(0, 60));

        // Logo + Name
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        logoPanel.setOpaque(false);


        JLabel logoText = new JLabel("HealthSphere");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoText.setForeground(ACCENT);

        JLabel subText = new JLabel("HMS");
        subText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subText.setForeground(new Color(0x6C757D));

        logoPanel.add(logoText);
        logoPanel.add(subText);

        // Center info
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        infoPanel.setOpaque(false);

        JLabel hotline = makeHeaderInfo("📞  Hotline:", "1800-MEDICARE");
        JLabel address = makeHeaderInfo("📍  Địa chỉ:", "123 Health Ave, TP. Hồ Chí Minh");
        infoPanel.add(hotline);
        infoPanel.add(address);

        // Right: login area
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);


        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);
        JLabel userName = new JLabel("...");
        userName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userName.setForeground(TEXT_PRIMARY);
        JLabel userRole = new JLabel("Quyền chỉnh sửa");
        userRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        userRole.setForeground(new Color(0x6C757D));
        userInfo.add(userName);
        userInfo.add(userRole);



        rightPanel.add(userInfo);


        header.add(logoPanel, BorderLayout.WEST);
        header.add(infoPanel, BorderLayout.CENTER);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    private JLabel makeHeaderInfo(String label, String value) {
        JLabel lbl = new JLabel("<html><b>" + label + "</b>&nbsp;" + value + "</html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }


    // SIDEBAR

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        String[][] menuItems = {
                {"",TAB_OVERVIEW},
                { "",TAB_PATIENT},
                { "",TAB_ENCOUNTER},
                { "",TAB_DOCTOR},
                {"",TAB_ROOM},
                {"",TAB_MEDICINE}
        };
        for (String[] item : menuItems) {
            JButton btn = buildSidebarButton(item[0], item[1]);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
            // Activate first button
            if (item[1].equals(TAB_OVERVIEW)) {
                setActiveButton(btn);
            }
        }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JButton buildSidebarButton(String icon, String label) {
        JButton btn = new JButton(icon + "  " + label) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isRollover() || getBackground().equals(SIDEBAR_SELECTED)) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground().equals(SIDEBAR_SELECTED) ? SIDEBAR_SELECTED : SIDEBAR_HOVER);
                    g2.fillRoundRect(6, 0, getWidth() - 12, getHeight(), 10, 10);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(TEXT_SIDEBAR);
        btn.setBackground(SIDEBAR_BG);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(200, 42));
        btn.setPreferredSize(new Dimension(200, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.repaint(); }
        });

        btn.addActionListener(e -> {
            setActiveButton(btn);
            // Map label → card name
            if (label.equals(TAB_OVERVIEW))  cardLayout.show(contentArea, TAB_OVERVIEW);
            else if (label.equals(TAB_PATIENT)) cardLayout.show(contentArea, TAB_PATIENT);
            else if (label.equals(TAB_ENCOUNTER)) cardLayout.show(contentArea, TAB_ENCOUNTER);
            else if (label.equals(TAB_DOCTOR))  cardLayout.show(contentArea, TAB_DOCTOR);
            else if (label.equals(TAB_ROOM))    cardLayout.show(contentArea, TAB_ROOM);
            else if (label.equals(TAB_MEDICINE)) cardLayout.show(contentArea, TAB_MEDICINE);
            else{
                // Tabs chưa implement — hiển thị placeholder
                cardLayout.show(contentArea, "placeholder_" + label);
            }
        });

        return btn;
    }

    private void setActiveButton(JButton btn) {
        if (activeSidebarBtn != null) {
            activeSidebarBtn.setBackground(SIDEBAR_BG);
            activeSidebarBtn.repaint();
        }
        activeSidebarBtn = btn;
        btn.setBackground(SIDEBAR_SELECTED);
        btn.repaint();
    }


    // CONTENT AREA (CardLayout)

    private JPanel buildContent() {
        contentArea.setBackground(CONTENT_BG);

        contentArea.add(wrapInScroll(overviewPanel), TAB_OVERVIEW);
        contentArea.add(buildManagementPanel(
                "Quản lý Bệnh Nhân", "Tên bệnh nhân:",
                searchFieldBN, btnSearchBN, btnRefreshBN,
                btnAddBN, btnEditBN, btnDeleteBN, tableBN
        ), TAB_PATIENT);
        contentArea.add(buildManagementPanel(
                "Quản lý Lượt Điều Trị", "Tên bệnh nhân:",
                searchFieldLDT, btnSearchLDT, btnRefreshLDT,
                btnAddLDT, btnEditLDT, null, tableLDT, btnInvoiceLDT, btnPrescribeLDT, btnDischargeLDT
        ), TAB_ENCOUNTER);
        contentArea.add(buildManagementPanel(
                "Quản lý Bác Sĩ", "Tên bác sĩ:",
                searchFieldBS, btnSearchBS, btnRefreshBS,
                btnAddBS, btnEditBS, btnDeleteBS, tableBS
        ), TAB_DOCTOR);
        contentArea.add(buildManagementPanel(
                "Quản lý Phòng Bệnh", "Loại phòng / Mã:",
                searchFieldPB, btnSearchPB, btnRefreshPB,
                btnAddPB, btnEditPB, btnDeletePB, tablePB
        ), TAB_ROOM);
        contentArea.add(buildManagementPanel(
                "Quản lý Thuốc", "Tên thuốc:",
                searchFieldTH, btnSearchTH, btnRefreshTH,
                btnAddTH, btnEditTH, btnDeleteTH, tableTH
        ), TAB_MEDICINE);


        return contentArea;
    }

    private JScrollPane wrapInScroll(JComponent comp) {
        JScrollPane sp = new JScrollPane(comp);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }

    /** Panel quản lý (search bar + table) theo style card */
    private JPanel buildManagementPanel(String title, String searchLabel,
                                        JTextField txtSearch,
                                        JButton btnSearch, JButton btnRefresh,
                                        JButton btnAdd, JButton btnEdit, JButton btnDelete,
                                        JTable table, JButton... extra) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CONTENT_BG);
        wrapper.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        //  Page title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        // Toolbar card
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbar.setBackground(CARD_BG);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xE0EAF6), 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));

        JLabel lbl = new JLabel(searchLabel);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(0x495057));

        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xC8D8EE), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        toolbar.add(lbl);
        toolbar.add(txtSearch);
        toolbar.add(btnSearch);
        toolbar.add(btnRefresh);

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(20, 1));
        toolbar.add(spacer);

        if (btnAdd != null) toolbar.add(btnAdd);
        if (btnEdit != null) toolbar.add(btnEdit);
        if (btnDelete != null) toolbar.add(btnDelete);
        if (extra != null) for (JButton b : extra) if (b != null) toolbar.add(b);

        //  Table card
        styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xE0EAF6), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        scroll.getViewport().setBackground(CARD_BG);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD_BG);
        tableCard.setBorder(new LineBorder(new Color(0xE0EAF6), 1, true));
        tableCard.add(scroll, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(titleLabel, BorderLayout.NORTH);
        top.add(toolbar,    BorderLayout.CENTER);

        wrapper.add(top,       BorderLayout.NORTH);
        wrapper.add(tableCard, BorderLayout.CENTER);
        return wrapper;
    }




    // TABLE HELPERS

    private DefaultTableModel createTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    private JTable createTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFillsViewportHeight(true);
        t.setRowHeight(28);
        return t;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(0xEEF2F8));
        table.setSelectionBackground(new Color(0xD6E8FF));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(0xEBF2FD));
        table.getTableHeader().setForeground(new Color(0x344563));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD0DEF0)));
        table.setIntercellSpacing(new Dimension(12, 0));
    }


    // BUTTON FACTORY

    private static JButton createActionButton(String text, Color bg) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);


        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);

        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.darker());
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }


    // POPULATE TABLES

    public void populatePatientTable(List<BenhNhanRowDTO> rows) {
        tableModelBN.setRowCount(0);
        for (BenhNhanRowDTO r : rows) {
            String dob   = r.getNgaySinh()    != null ? r.getNgaySinh().format(DATE_FMT) : "";
            String phone = r.getSoDienThoai() != null ? r.getSoDienThoai()               : "";
            // Hiển thị số thẻ BHYT hoặc dấu gạch ngang nếu không có
            String bhyt  = (r.getSoTheBHYT() != null && !r.getSoTheBHYT().isBlank())
                    ? r.getSoTheBHYT() : "—";
            tableModelBN.addRow(new Object[]{
                    r.getMaBenhNhan(), r.getTenBenhNhan(), dob, phone, bhyt
            });
        }
    }
    
    public void populateEncounterTable(List<LuotDieuTriRowDTO> rows) {
        tableModelLDT.setRowCount(0);
        for (LuotDieuTriRowDTO r : rows) {
            String ngayVao = r.getNgayBatDau() != null ? r.getNgayBatDau().format(DATE_FMT) : "";
            String ngayRa = r.getNgayKetThuc() != null ? r.getNgayKetThuc().format(DATE_FMT) : "";
            tableModelLDT.addRow(new Object[]{
                    r.getMaLuot(), r.getTenBenhNhan(), r.getTenBacSi(), r.getTenKhoa(), r.getMaPhong(), 
                    ngayVao, ngayRa, r.getTrangThai()
            });
        }
    }

    public void populateDoctorTable(List<BacSiRowDTO> rows) {
        tableModelBS.setRowCount(0);
        for (BacSiRowDTO r : rows) {
            String dob = r.getNgayVaoLam() != null ? r.getNgayVaoLam().format(DATE_FMT) : "";
            tableModelBS.addRow(new Object[]{
                    r.getMaBacSi(), r.getTenBacSi(), dob, r.getChuyenMon(), r.getTenKhoa()
            });
        }
    }

    public void populateRoomTable(List<PhongBenhRowDTO> rows) {
        tableModelPB.setRowCount(0);
        for (PhongBenhRowDTO r : rows) {
            tableModelPB.addRow(new Object[]{
                    r.getMaPhong(), r.getLoaiPhong(), r.getSoGiuongToiDa()
            });
        }
    }

    public void populateMedicineTable(List<ThuocRowDTO> rows) {
        tableModelTH.setRowCount(0);
        for (ThuocRowDTO r : rows) {
            tableModelTH.addRow(new Object[]{
                    r.getMaThuoc(), r.getTenThuoc(), r.getThanhPhan(), r.getGiaBan()
            });
        }
    }


    // --- GETTERS CHO ID ĐANG CHỌN (SELECTED ID) ---
    public String getSelectedPatientId() {
        int row = tableBN.getSelectedRow();
        return row < 0 ? null : tableModelBN.getValueAt(row, 0).toString();
    }
    public String getSelectedEncounterId() {
        int row = tableLDT.getSelectedRow();
        return row < 0 ? null : tableModelLDT.getValueAt(row, 0).toString();
    }
    public String getSelectedDoctorId() {
        int row = tableBS.getSelectedRow();
        return row < 0 ? null : tableModelBS.getValueAt(row, 0).toString();
    }
    public String getSelectedRoomId() {
        int row = tablePB.getSelectedRow();
        return row < 0 ? null : tableModelPB.getValueAt(row, 0).toString();
    }
    public String getSelectedMedicineId() {
        int row = tableTH.getSelectedRow();
        return row < 0 ? null : tableModelTH.getValueAt(row, 0).toString();
    }

    // --- GETTERS CHO CONTROLS BỆNH NHÂN ---
    public JTextField getSearchFieldBN()  { return searchFieldBN; }
    public JButton getBtnSearchBN()       { return btnSearchBN;   }
    public JButton getBtnRefreshBN()      { return btnRefreshBN;  }
    public JButton getBtnAddBN()          { return btnAddBN;      }
    public JButton getBtnEditBN()         { return btnEditBN;     }
    public JButton getBtnDeleteBN()       { return btnDeleteBN;   }
    
    // --- GETTERS CHO CONTROLS LƯỢT ĐIỀU TRỊ ---
    public JTextField getSearchFieldLDT()  { return searchFieldLDT; }
    public JButton getBtnSearchLDT()       { return btnSearchLDT;   }
    public JButton getBtnRefreshLDT()      { return btnRefreshLDT;  }
    public JButton getBtnAddLDT()          { return btnAddLDT;      }
    public JButton getBtnEditLDT()         { return btnEditLDT;     }
    public JButton getBtnInvoiceLDT()      { return btnInvoiceLDT;  }
    public JButton getBtnPrescribeLDT()    { return btnPrescribeLDT;}
    public JButton getBtnDischargeLDT()    { return btnDischargeLDT;}

    // --- GETTERS CHO CONTROLS BÁC SĨ ---
    public JTextField getSearchFieldBS()  { return searchFieldBS; }
    public JButton getBtnSearchBS()       { return btnSearchBS;   }
    public JButton getBtnRefreshBS()      { return btnRefreshBS;  }
    public JButton getBtnAddBS()          { return btnAddBS;      }
    public JButton getBtnEditBS()         { return btnEditBS;     }
    public JButton getBtnDeleteBS()       { return btnDeleteBS;   }

    // --- GETTERS CHO CONTROLS PHÒNG BỆNH ---
    public JTextField getSearchFieldPB()  { return searchFieldPB; }
    public JButton getBtnSearchPB()       { return btnSearchPB;   }
    public JButton getBtnRefreshPB()      { return btnRefreshPB;  }
    public JButton getBtnAddPB()          { return btnAddPB;      }
    public JButton getBtnEditPB()         { return btnEditPB;     }
    public JButton getBtnDeletePB()       { return btnDeletePB;   }

    // --- GETTERS CHO CONTROLS THUỐC ---
    public JTextField getSearchFieldTH()  { return searchFieldTH; }
    public JButton getBtnSearchTH()       { return btnSearchTH;   }
    public JButton getBtnRefreshTH()      { return btnRefreshTH;  }
    public JButton getBtnAddTH()          { return btnAddTH;      }
    public JButton getBtnEditTH()         { return btnEditTH;     }
    public JButton getBtnDeleteTH()       { return btnDeleteTH;   }

    // Overview
    public OverviewPanel getOverviewPanel() { return overviewPanel; }




}
