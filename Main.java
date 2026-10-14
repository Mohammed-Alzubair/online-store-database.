import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Sports Store Management System - Student 012
 * Single-file Java Swing application connecting to online_store_012.
 *
 * Compile:  javac -cp mysql-connector-j-9.7.0.jar Main.java
 * Run:      java  -cp .:mysql-connector-j-9.7.0.jar Main
 * Windows:  java  -cp .;mysql-connector-j-9.7.0.jar Main
 */
public class Main extends JFrame {

    // ── Colours ───────────────────────────────────────────────────────────────
    private static final Color BG        = new Color(0x12, 0x12, 0x18);
    private static final Color CARD      = new Color(0x1c, 0x1c, 0x26);
    private static final Color SIDEBAR   = new Color(0x16, 0x16, 0x20);
    private static final Color BLUE      = new Color(0x63, 0x66, 0xf1);
    private static final Color GREEN     = new Color(0x10, 0xb9, 0x81);
    private static final Color ORANGE    = new Color(0xfb, 0x92, 0x3c);
    private static final Color RED       = new Color(0xef, 0x44, 0x44);
    private static final Color PURPLE    = new Color(0x8b, 0x5c, 0xf6);
    private static final Color CYAN      = new Color(0x06, 0xb6, 0xd4);
    private static final Color TXT_WHITE = new Color(0xee, 0xee, 0xf5);
    private static final Color TXT_GRAY  = new Color(0x94, 0xa3, 0xb8);
    private static final Color HOVER     = new Color(0xff, 0xff, 0xff, 30);

    // ── JDBC ──────────────────────────────────────────────────────────────────
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/online_store_012";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "MOHammed77@";

    // ── Layout ────────────────────────────────────────────────────────────────
    private CardLayout cardLayout;
    private JPanel     contentPanel;

    // drag support for undecorated frame
    private Point dragStart;

    // stat labels updated on dashboard
    private JLabel lblProducts, lblCustomers, lblOrders, lblRevenue;

    // ── Constructor ───────────────────────────────────────────────────────────
    public Main() {
        setUndecorated(true);
        setSize(1280, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        add(buildTitleBar(),  BorderLayout.NORTH);
        add(buildSidebar(),   BorderLayout.WEST);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);
        contentPanel.add(buildDashboard(),  "Dashboard");
        contentPanel.add(buildProducts(),   "Products");
        contentPanel.add(buildCustomers(),  "Customers");
        contentPanel.add(buildOrders(),     "Orders");
        contentPanel.add(buildViews(),      "Views");
        contentPanel.add(buildTriggers(),   "Triggers");
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
        loadDashboardStats();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(SIDEBAR);
        bar.setPreferredSize(new Dimension(0, 40));
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x2a,0x2a,0x38)));

        JLabel title = new JLabel("  Sports Store_012 - Student_012 ID: 312024705012");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(TXT_WHITE);
        bar.add(title, BorderLayout.WEST);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btns.setOpaque(false);
        btns.add(titleBtn("─", new Color(0x3a,0x3a,0x4a), e -> setState(Frame.ICONIFIED)));
        btns.add(titleBtn("□", new Color(0x3a,0x3a,0x4a), e -> {
            if (getExtendedState() == JFrame.MAXIMIZED_BOTH) setExtendedState(JFrame.NORMAL);
            else setExtendedState(JFrame.MAXIMIZED_BOTH);
        }));
        btns.add(titleBtn("✕", RED, e -> System.exit(0)));
        bar.add(btns, BorderLayout.EAST);

        bar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { dragStart = e.getPoint(); }
        });
        bar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point loc = getLocation();
                setLocation(loc.x + e.getX() - dragStart.x,
                        loc.y + e.getY() - dragStart.y);
            }
        });
        return bar;
    }

    private JButton titleBtn(String text, Color bg, ActionListener al) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setForeground(TXT_WHITE);
        b.setBackground(bg);
        b.setPreferredSize(new Dimension(40, 40));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.addActionListener(al);
        return b;
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(SIDEBAR);
        side.setPreferredSize(new Dimension(200, 0));
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0x2a,0x2a,0x38)));

        JLabel logo = new JLabel("Sports Store_012");
        logo.setFont(new Font("SansSerif", Font.BOLD, 15));
        logo.setForeground(BLUE);
        logo.setBorder(new EmptyBorder(20, 18, 20, 0));
        side.add(logo);

        String[][] items = {
                {"D", "Dashboard_012"},
                {"P", "Products_012"},
                {"C", "Customers_012"},
                {"O", "Orders_012"},
                {"V", "Views_012"},
                {"T", "Triggers_012"}
        };
        for (String[] item : items) {
            side.add(sidebarBtn(item[0] + "  " + item[1], item[1]));
        }
        side.add(Box.createVerticalGlue());
        return side;
    }

    private JButton sidebarBtn(String label, String card) {
        JButton b = new JButton(label);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setForeground(TXT_GRAY);
        b.setBackground(SIDEBAR);
        b.setMaximumSize(new Dimension(200, 44));
        b.setPreferredSize(new Dimension(200, 44));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(0, 18, 0, 0));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setForeground(TXT_WHITE);
                b.setBackground(new Color(0x22, 0x22, 0x30));
            }
            public void mouseExited(MouseEvent e) {
                b.setForeground(TXT_GRAY);
                b.setBackground(SIDEBAR);
            }
        });
        // strip _012 from card name so CardLayout still finds the panel
        String cardKey = card.replace("_012", "");
        b.addActionListener(e -> cardLayout.show(contentPanel, cardKey));
        return b;
    }

    private JPanel buildDashboard() {
        JPanel page = new JPanel(new BorderLayout(0, 16));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel hdr = new JLabel("Dashboard_012");
        hdr.setFont(new Font("SansSerif", Font.BOLD, 22));
        hdr.setForeground(TXT_WHITE);
        page.add(hdr, BorderLayout.NORTH);

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        lblProducts  = new JLabel("...");
        lblCustomers = new JLabel("...");
        lblOrders    = new JLabel("...");
        lblRevenue   = new JLabel("...");
        statsRow.add(statCard("Total Products_012",  lblProducts,  BLUE));
        statsRow.add(statCard("Customers_012",        lblCustomers, GREEN));
        statsRow.add(statCard("Orders_012",           lblOrders,    ORANGE));
        statsRow.add(statCard("Revenue_012",          lblRevenue,   PURPLE));

        JPanel bottom = new JPanel(new GridLayout(1, 2, 16, 0));
        bottom.setOpaque(false);
        bottom.add(dashTable("Sales Statistics_012",
                "SELECT product_name_012, total_sold_012, total_sales_012 FROM v_manager_sales_stat_012",
                new String[]{"product_name_012", "total_sold_012", "total_sales_012"}));
        bottom.add(dashTable("Low Stock Alerts_012",
                "SELECT p.product_name_012, a.alert_msg_012, a.alert_time_012 FROM lowstockalerts_012 a JOIN products_012 p ON a.product_id_012 = p.product_id_012 ORDER BY a.alert_time_012 DESC LIMIT 5",
                new String[]{"product_name_012", "alert_msg_012", "alert_time_012"}));

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(statsRow, BorderLayout.NORTH);
        center.add(bottom,   BorderLayout.CENTER);
        page.add(center, BorderLayout.CENTER);
        return page;
    }

    private JPanel statCard(String title, JLabel valLbl, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(0x2a,0x2a,0x3a), 1, true),
                new EmptyBorder(18, 20, 18, 20)));

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.PLAIN, 12));
        t.setForeground(TXT_GRAY);

        valLbl.setFont(new Font("SansSerif", Font.BOLD, 28));
        valLbl.setForeground(accent);

        card.add(t,      BorderLayout.NORTH);
        card.add(valLbl, BorderLayout.CENTER);
        return card;
    }

    private JPanel dashTable(String title, String sql, String[] cols) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(0x2a,0x2a,0x3a), 1, true),
                new EmptyBorder(14, 16, 14, 16)));

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        t.setForeground(TXT_WHITE);
        card.add(t, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        try (Connection cn = getConnection();
             Statement  st = cn.createStatement();
             ResultSet  rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Object[] row = new Object[cols.length];
                for (int i = 0; i < cols.length; i++) row[i] = rs.getObject(i + 1);
                model.addRow(row);
            }
        } catch (SQLException ex) { /* table stays empty */ }

        JTable tbl = styledTable(model);
        card.add(new JScrollPane(tbl) {{ getViewport().setBackground(CARD); setBorder(null); }},
                BorderLayout.CENTER);
        return card;
    }

    private void loadDashboardStats() {
        try (Connection cn = getConnection()) {
            try (Statement s = cn.createStatement()) {
                ResultSet r = s.executeQuery("SELECT COUNT(*) FROM products_012");
                if (r.next()) lblProducts.setText(String.valueOf(r.getInt(1)));
            }
            try (Statement s = cn.createStatement()) {
                ResultSet r = s.executeQuery("SELECT COUNT(*) FROM customers_012");
                if (r.next()) lblCustomers.setText(String.valueOf(r.getInt(1)));
            }
            try (Statement s = cn.createStatement()) {
                ResultSet r = s.executeQuery("SELECT COUNT(*) FROM orders_012");
                if (r.next()) lblOrders.setText(String.valueOf(r.getInt(1)));
            }
            try (Statement s = cn.createStatement()) {
                ResultSet r = s.executeQuery(
                        "SELECT COALESCE(SUM(total_amount_012),0) FROM orders_012 WHERE order_status_012='Completed'");
                if (r.next()) lblRevenue.setText("$" + String.format("%.0f", r.getDouble(1)));
            }
        } catch (SQLException ex) {
            lblProducts.setText("N/A");
        }
    }

    private DefaultTableModel productModel;

    private JPanel buildProducts() {
        JPanel page = new JPanel(new BorderLayout(0, 12));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel hdr = new JLabel("Products_012");
        hdr.setFont(new Font("SansSerif", Font.BOLD, 22));
        hdr.setForeground(TXT_WHITE);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        productModel = new DefaultTableModel(
                new String[]{"product_id_012","product_name_012","retail_price_012","purchase_cost_012","member_discount_012","reorder_threshold_012","status_012","supplier_name_012","stock_qty_012"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = styledTable(productModel);

        toolbar.add(colorBtn("Refresh_012",           BLUE,   e -> loadProducts(productModel)));
        toolbar.add(colorBtn("Add Product_012",        GREEN,  e -> showAddProduct(tbl, productModel)));
        toolbar.add(colorBtn("Edit_012",               ORANGE, e -> showEditProduct(tbl, productModel)));
        toolbar.add(colorBtn("Delete_012",             RED,    e -> deleteProduct(tbl, productModel)));
        toolbar.add(colorBtn("Cashier View_012",       CYAN,   e -> openViewWindow("Cashier View_012",
                "SELECT * FROM v_cashier_product_012",
                new String[]{"product_id_012","product_name_012","retail_price_012","member_discount_012","stock_qty_012"})));
        toolbar.add(colorBtn("Member Discounts_012",   PURPLE, e -> openViewWindow("Member Discounts_012",
                "SELECT * FROM v_member_discount_product_012",
                new String[]{"product_id_012","product_name_012","retail_price_012","member_discount_012","stock_qty_012"})));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(hdr,     BorderLayout.WEST);
        top.add(toolbar, BorderLayout.EAST);

        JScrollPane sp = new JScrollPane(tbl);
        sp.getViewport().setBackground(CARD);
        sp.setBorder(new LineBorder(new Color(0x2a,0x2a,0x3a), 1, true));

        page.add(top, BorderLayout.NORTH);
        page.add(sp,  BorderLayout.CENTER);
        loadProducts(productModel);
        return page;
    }

    private void loadProducts(DefaultTableModel m) {
        m.setRowCount(0);
        String sql = "SELECT p.product_id_012, p.product_name_012, p.retail_price_012, p.purchase_cost_012, "
                + "p.member_discount_012, p.reorder_threshold_012, p.status_012, "
                + "COALESCE(s.supplier_name_012,'-') AS supplier, i.stock_qty_012 "
                + "FROM products_012 p "
                + "JOIN inventory_012 i ON p.product_id_012 = i.product_id_012 "
                + "LEFT JOIN suppliers_012 s ON p.supplier_id_012 = s.supplier_id_012";
        try (Connection cn = getConnection();
             Statement  st = cn.createStatement();
             ResultSet  rs = st.executeQuery(sql)) {
            while (rs.next())
                m.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getDouble(4),
                        rs.getDouble(5), rs.getInt(6), rs.getString(7), rs.getString(8), rs.getInt(9)
                });
        } catch (SQLException ex) { showError(ex); }
    }

    private void showAddProduct(JTable tbl, DefaultTableModel m) {
        JTextField fName  = tf(""), fRetail = tf(""), fCost = tf(""),
                fDisc  = tf(""), fThresh = tf(""), fSup  = tf("1");
        JPanel form = formPanel(
                "product_name_012:", fName,
                "retail_price_012:", fRetail,
                "purchase_cost_012:", fCost,
                "member_discount_012 (0.00-1.00):", fDisc,
                "reorder_threshold_012:", fThresh,
                "supplier_id_012:", fSup);
        if (JOptionPane.showConfirmDialog(this, form, "Add Product_012",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        try (Connection cn = getConnection()) {
            PreparedStatement ps = cn.prepareStatement(
                    "INSERT INTO products_012 (product_name_012,retail_price_012,purchase_cost_012,"
                            + "member_discount_012,reorder_threshold_012,supplier_id_012) VALUES (?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, fName.getText().trim());
            ps.setDouble(2, Double.parseDouble(fRetail.getText().trim()));
            ps.setDouble(3, Double.parseDouble(fCost.getText().trim()));
            ps.setDouble(4, Double.parseDouble(fDisc.getText().trim()));
            ps.setInt(5,    Integer.parseInt(fThresh.getText().trim()));
            ps.setInt(6,    Integer.parseInt(fSup.getText().trim()));
            ps.executeUpdate();
            ResultSet gk = ps.getGeneratedKeys();
            if (gk.next()) {
                int pid = gk.getInt(1);
                cn.createStatement().executeUpdate(
                        "INSERT INTO inventory_012 (product_id_012,stock_qty_012,last_restock_date_012) VALUES ("
                                + pid + ",0,CURDATE())");
            }
            loadProducts(m);
        } catch (Exception ex) { showError(ex); }
    }

    private void showEditProduct(JTable tbl, DefaultTableModel m) {
        int row = tbl.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a row first."); return; }
        int id = (int) m.getValueAt(row, 0);
        JTextField fName  = tf(m.getValueAt(row,1).toString());
        JTextField fRetail= tf(m.getValueAt(row,2).toString());
        JTextField fCost  = tf(m.getValueAt(row,3).toString());
        JTextField fDisc  = tf(m.getValueAt(row,4).toString());
        JTextField fThresh= tf(m.getValueAt(row,5).toString());
        JPanel form = formPanel(
                "product_name_012:", fName,
                "retail_price_012:", fRetail,
                "purchase_cost_012:", fCost,
                "member_discount_012:", fDisc,
                "reorder_threshold_012:", fThresh);
        if (JOptionPane.showConfirmDialog(this, form, "Edit Product_012",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "UPDATE products_012 SET product_name_012=?,retail_price_012=?,purchase_cost_012=?,"
                             + "member_discount_012=?,reorder_threshold_012=? WHERE product_id_012=?")) {
            ps.setString(1, fName.getText().trim());
            ps.setDouble(2, Double.parseDouble(fRetail.getText().trim()));
            ps.setDouble(3, Double.parseDouble(fCost.getText().trim()));
            ps.setDouble(4, Double.parseDouble(fDisc.getText().trim()));
            ps.setInt(5,    Integer.parseInt(fThresh.getText().trim()));
            ps.setInt(6, id);
            ps.executeUpdate();
            loadProducts(m);
        } catch (Exception ex) { showError(ex); }
    }

    private void deleteProduct(JTable tbl, DefaultTableModel m) {
        int row = tbl.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a row first."); return; }
        int id = (int) m.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete product_id_012: " + id + "?",
                "Confirm Delete_012", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try (Connection cn = getConnection()) {
            cn.createStatement().executeUpdate("DELETE FROM inventory_012  WHERE product_id_012=" + id);
            cn.createStatement().executeUpdate("DELETE FROM products_012   WHERE product_id_012=" + id);
            loadProducts(m);
        } catch (SQLException ex) { showError(ex); }
    }

    private DefaultTableModel custModel;

    private JPanel buildCustomers() {
        JPanel page = new JPanel(new BorderLayout(0, 12));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel hdr = new JLabel("Customers_012");
        hdr.setFont(new Font("SansSerif", Font.BOLD, 22));
        hdr.setForeground(TXT_WHITE);

        custModel = new DefaultTableModel(
                new String[]{"customer_id_012","full_name_012","email_012","phone_012","address_012","member_discount_012","total_points_012","user_role_012"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = styledTable(custModel);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);
        toolbar.add(colorBtn("Refresh_012",       BLUE,   e -> loadCustomers(custModel)));
        toolbar.add(colorBtn("Add Customer_012",   GREEN,  e -> showAddCustomer(custModel)));
        toolbar.add(colorBtn("Edit_012",           ORANGE, e -> showEditCustomer(tbl, custModel)));
        toolbar.add(colorBtn("Delete_012",         RED,    e -> deleteCustomer(tbl, custModel)));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(hdr,     BorderLayout.WEST);
        top.add(toolbar, BorderLayout.EAST);

        JScrollPane sp = new JScrollPane(tbl);
        sp.getViewport().setBackground(CARD);
        sp.setBorder(new LineBorder(new Color(0x2a,0x2a,0x3a), 1, true));

        page.add(top, BorderLayout.NORTH);
        page.add(sp,  BorderLayout.CENTER);
        loadCustomers(custModel);
        return page;
    }

    private void loadCustomers(DefaultTableModel m) {
        m.setRowCount(0);
        try (Connection cn = getConnection();
             Statement  st = cn.createStatement();
             ResultSet  rs = st.executeQuery("SELECT * FROM customers_012")) {
            while (rs.next())
                m.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getDouble(6), rs.getInt(7), rs.getString(8)
                });
        } catch (SQLException ex) { showError(ex); }
    }

    private void showAddCustomer(DefaultTableModel m) {
        JTextField fName = tf(""), fEmail = tf(""), fPhone = tf(""),
                fAddr = tf(""), fDisc  = tf("0.00"), fRole = tf("Customer");
        JPanel form = formPanel(
                "full_name_012:", fName,
                "email_012:", fEmail,
                "phone_012:", fPhone,
                "address_012:", fAddr,
                "member_discount_012:", fDisc,
                "user_role_012 (Customer/Cashier/Manager):", fRole);
        if (JOptionPane.showConfirmDialog(this, form, "Add Customer_012",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO customers_012 (full_name_012,email_012,phone_012,address_012,member_discount_012,user_role_012)"
                             + " VALUES (?,?,?,?,?,?)")) {
            ps.setString(1, fName.getText().trim());
            ps.setString(2, fEmail.getText().trim());
            ps.setString(3, fPhone.getText().trim());
            ps.setString(4, fAddr.getText().trim());
            ps.setDouble(5, Double.parseDouble(fDisc.getText().trim()));
            ps.setString(6, fRole.getText().trim());
            ps.executeUpdate();
            loadCustomers(m);
        } catch (Exception ex) { showError(ex); }
    }

    private void showEditCustomer(JTable tbl, DefaultTableModel m) {
        int row = tbl.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a row first."); return; }
        int id = (int) m.getValueAt(row, 0);
        JTextField fName  = tf(str(m,row,1)), fEmail = tf(str(m,row,2)),
                fPhone = tf(str(m,row,3)), fAddr  = tf(str(m,row,4)),
                fDisc  = tf(str(m,row,5)), fRole  = tf(str(m,row,7));
        JPanel form = formPanel(
                "full_name_012:", fName,
                "email_012:", fEmail,
                "phone_012:", fPhone,
                "address_012:", fAddr,
                "member_discount_012:", fDisc,
                "user_role_012:", fRole);
        if (JOptionPane.showConfirmDialog(this, form, "Edit Customer_012",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "UPDATE customers_012 SET full_name_012=?,email_012=?,phone_012=?,address_012=?,"
                             + "member_discount_012=?,user_role_012=? WHERE customer_id_012=?")) {
            ps.setString(1, fName.getText().trim());
            ps.setString(2, fEmail.getText().trim());
            ps.setString(3, fPhone.getText().trim());
            ps.setString(4, fAddr.getText().trim());
            ps.setDouble(5, Double.parseDouble(fDisc.getText().trim()));
            ps.setString(6, fRole.getText().trim());
            ps.setInt(7, id);
            ps.executeUpdate();
            loadCustomers(m);
        } catch (Exception ex) { showError(ex); }
    }

    private void deleteCustomer(JTable tbl, DefaultTableModel m) {
        int row = tbl.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a row first."); return; }
        int id = (int) m.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete customer_id_012: " + id + "?",
                "Confirm Delete_012", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "DELETE FROM customers_012 WHERE customer_id_012=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            loadCustomers(m);
        } catch (SQLException ex) { showError(ex); }
    }

    private DefaultTableModel orderModel;

    private JPanel buildOrders() {
        JPanel page = new JPanel(new BorderLayout(0, 12));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel hdr = new JLabel("Orders_012");
        hdr.setFont(new Font("SansSerif", Font.BOLD, 22));
        hdr.setForeground(TXT_WHITE);

        orderModel = new DefaultTableModel(
                new String[]{"order_id_012","full_name_012","total_amount_012","order_status_012","order_time_012"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = styledTable(orderModel);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);
        toolbar.add(colorBtn("Refresh_012",       BLUE,  e -> loadOrders(orderModel)));
        toolbar.add(colorBtn("Create Order_012",  GREEN, e -> showCreateOrder(orderModel)));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(hdr,     BorderLayout.WEST);
        top.add(toolbar, BorderLayout.EAST);

        JScrollPane sp = new JScrollPane(tbl);
        sp.getViewport().setBackground(CARD);
        sp.setBorder(new LineBorder(new Color(0x2a,0x2a,0x3a), 1, true));

        page.add(top, BorderLayout.NORTH);
        page.add(sp,  BorderLayout.CENTER);
        loadOrders(orderModel);
        return page;
    }

    private void loadOrders(DefaultTableModel m) {
        m.setRowCount(0);
        String sql = "SELECT o.order_id_012, c.full_name_012, o.total_amount_012, o.order_status_012, o.order_time_012 "
                + "FROM orders_012 o JOIN customers_012 c ON o.customer_id_012=c.customer_id_012 "
                + "ORDER BY o.order_time_012 DESC";
        try (Connection cn = getConnection();
             Statement  st = cn.createStatement();
             ResultSet  rs = st.executeQuery(sql)) {
            while (rs.next())
                m.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2),
                        rs.getDouble(3), rs.getString(4), rs.getTimestamp(5)
                });
        } catch (SQLException ex) { showError(ex); }
    }

    private void showCreateOrder(DefaultTableModel m) {
        String custId = JOptionPane.showInputDialog(this,
                "Enter customer_id_012:", "Create Order_012 - Step 1", JOptionPane.PLAIN_MESSAGE);
        if (custId == null || custId.isBlank()) return;

        String items = JOptionPane.showInputDialog(this,
                "Enter product_id_012:buy_qty_012 pairs separated by commas\n(e.g. 1:2,5:1)",
                "Create Order_012 - Step 2", JOptionPane.PLAIN_MESSAGE);
        if (items == null || items.isBlank()) return;

        try (Connection cn = getConnection()) {
            cn.setAutoCommit(false);
            double total = 0;
            Map<Integer,Integer> lineItems = new LinkedHashMap<>();
            Map<Integer,Double>  prices    = new LinkedHashMap<>();

            for (String part : items.split(",")) {
                String[] kv = part.trim().split(":");
                int pid = Integer.parseInt(kv[0].trim());
                int qty = Integer.parseInt(kv[1].trim());
                PreparedStatement psp = cn.prepareStatement(
                        "SELECT retail_price_012 FROM products_012 WHERE product_id_012=?");
                psp.setInt(1, pid);
                ResultSet rsp = psp.executeQuery();
                if (!rsp.next()) throw new Exception("product_id_012 " + pid + " not found.");
                double price = rsp.getDouble(1);
                total += price * qty;
                lineItems.put(pid, qty);
                prices.put(pid, price);
            }

            PreparedStatement psO = cn.prepareStatement(
                    "INSERT INTO orders_012 (customer_id_012,total_amount_012,order_status_012) VALUES (?,?,'Pending')",
                    Statement.RETURN_GENERATED_KEYS);
            psO.setInt(1, Integer.parseInt(custId.trim()));
            psO.setDouble(2, total);
            psO.executeUpdate();
            ResultSet gk = psO.getGeneratedKeys();
            gk.next();
            int oid = gk.getInt(1);

            for (Map.Entry<Integer,Integer> e : lineItems.entrySet()) {
                PreparedStatement psI = cn.prepareStatement(
                        "INSERT INTO orderitems_012 (order_id_012,product_id_012,buy_qty_012,unit_price_012) VALUES (?,?,?,?)");
                psI.setInt(1, oid);
                psI.setInt(2, e.getKey());
                psI.setInt(3, e.getValue());
                psI.setDouble(4, prices.get(e.getKey()));
                psI.executeUpdate();
            }
            cn.commit();
            JOptionPane.showMessageDialog(this,
                    "order_id_012: " + oid + " created!\ntotal_amount_012: $" + String.format("%.2f", total));
            loadOrders(m);
            loadDashboardStats();
        } catch (Exception ex) { showError(ex); }
    }

    private JPanel buildViews() {
        JPanel page = new JPanel(new BorderLayout(0, 16));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel hdr = new JLabel("Database Views_012");
        hdr.setFont(new Font("SansSerif", Font.BOLD, 22));
        hdr.setForeground(TXT_WHITE);
        page.add(hdr, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setOpaque(false);

        cards.add(viewCard("v_cashier_product_012",
                "product_id_012, product_name_012,\nretail_price_012, member_discount_012,\nstock_qty_012",
                "SELECT * FROM v_cashier_product_012",
                new String[]{"product_id_012","product_name_012","retail_price_012","member_discount_012","stock_qty_012"}, CYAN));

        cards.add(viewCard("v_manager_sales_stat_012",
                "product_id_012, product_name_012,\ntotal_sold_012, total_sales_012",
                "SELECT * FROM v_manager_sales_stat_012",
                new String[]{"product_id_012","product_name_012","total_sold_012","total_sales_012"}, BLUE));

        cards.add(viewCard("v_member_discount_product_012",
                "product_id_012, product_name_012,\nretail_price_012, member_discount_012,\nstock_qty_012",
                "SELECT * FROM v_member_discount_product_012",
                new String[]{"product_id_012","product_name_012","retail_price_012","member_discount_012","stock_qty_012"}, PURPLE));

        page.add(cards, BorderLayout.CENTER);
        return page;
    }

    private JPanel viewCard(String title, String desc, String sql, String[] cols, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(0x2a,0x2a,0x3a), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        t.setForeground(accent);

        JLabel d = new JLabel("<html>" + desc.replace("\n","<br>") + "</html>");
        d.setFont(new Font("SansSerif", Font.PLAIN, 12));
        d.setForeground(TXT_GRAY);

        JButton btn = colorBtn("Open View_012", accent,
                e -> openViewWindow(title, sql, cols));

        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.setOpaque(false);
        top.add(t, BorderLayout.NORTH);
        top.add(d, BorderLayout.CENTER);

        card.add(top, BorderLayout.NORTH);
        card.add(btn, BorderLayout.SOUTH);
        return card;
    }

    private void openViewWindow(String title, String sql, String[] cols) {
        JFrame win = new JFrame(title);
        win.setSize(800, 500);
        win.setLocationRelativeTo(this);
        win.getContentPane().setBackground(BG);

        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        try (Connection cn = getConnection();
             Statement  st = cn.createStatement();
             ResultSet  rs = st.executeQuery(sql)) {
            ResultSetMetaData rmd = rs.getMetaData();
            int nc = rmd.getColumnCount();
            while (rs.next()) {
                Object[] row = new Object[nc];
                for (int i = 0; i < nc; i++) row[i] = rs.getObject(i + 1);
                m.addRow(row);
            }
        } catch (SQLException ex) { showError(ex); }

        JTable tbl = styledTable(m);
        JScrollPane sp = new JScrollPane(tbl);
        sp.getViewport().setBackground(CARD);
        sp.setBorder(null);
        win.add(sp);
        win.setVisible(true);
    }

    private JPanel buildTriggers() {
        JPanel page = new JPanel(new BorderLayout(0, 16));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel hdr = new JLabel("Triggers_012");
        hdr.setFont(new Font("SansSerif", Font.BOLD, 22));
        hdr.setForeground(TXT_WHITE);
        page.add(hdr, BorderLayout.NORTH);

        JPanel infoRow = new JPanel(new GridLayout(1, 2, 16, 0));
        infoRow.setOpaque(false);
        infoRow.add(trigInfoCard("trg_low_stock_alert_012",
                "Event: AFTER INSERT ON orderitems_012\n\n"
                        + "Action: Deducts buy_qty_012 from inventory_012.\n"
                        + "If stock_qty_012 <= reorder_threshold_012,\n"
                        + "inserts a row into lowstockalerts_012.", ORANGE));
        infoRow.add(trigInfoCard("trg_calc_member_points_012",
                "Event: AFTER UPDATE ON orders_012\n\n"
                        + "Action: When order_status_012 = 'Completed',\n"
                        + "awards FLOOR(total_amount_012) loyalty points,\n"
                        + "records in pointsrecords_012, and\n"
                        + "updates total_points_012.", PURPLE));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnRow.setOpaque(false);
        btnRow.add(colorBtn("Run Trigger Test_012", GREEN,  e -> runTriggerTest()));
        btnRow.add(colorBtn("Show Alerts_012",       ORANGE, e -> openViewWindow(
                "lowstockalerts_012",
                "SELECT a.alert_id_012, p.product_name_012, a.alert_msg_012, a.alert_time_012 "
                        + "FROM lowstockalerts_012 a JOIN products_012 p ON a.product_id_012=p.product_id_012 "
                        + "ORDER BY a.alert_time_012 DESC",
                new String[]{"alert_id_012","product_name_012","alert_msg_012","alert_time_012"})));
        btnRow.add(colorBtn("Show Points_012",       PURPLE, e -> openViewWindow(
                "pointsrecords_012",
                "SELECT r.record_id_012, c.full_name_012, r.order_id_012, r.gained_points_012, r.record_time_012 "
                        + "FROM pointsrecords_012 r JOIN customers_012 c ON r.customer_id_012=c.customer_id_012 "
                        + "ORDER BY r.record_time_012 DESC",
                new String[]{"record_id_012","full_name_012","order_id_012","gained_points_012","record_time_012"})));

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(infoRow, BorderLayout.NORTH);
        center.add(btnRow,  BorderLayout.CENTER);
        page.add(center, BorderLayout.CENTER);
        return page;
    }

    private JPanel trigInfoCard(String title, String body, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(0x2a,0x2a,0x3a), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        t.setForeground(accent);

        JLabel b = new JLabel("<html>" + body.replace("\n","<br>") + "</html>");
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setForeground(TXT_GRAY);

        card.add(t, BorderLayout.NORTH);
        card.add(b, BorderLayout.CENTER);
        return card;
    }

    private void runTriggerTest() {
        try (Connection cn = getConnection()) {
            cn.setAutoCommit(false);

            PreparedStatement psO = cn.prepareStatement(
                    "INSERT INTO orders_012 (customer_id_012,total_amount_012,order_status_012) VALUES (1,199.99,'Pending')",
                    Statement.RETURN_GENERATED_KEYS);
            psO.executeUpdate();
            ResultSet gk = psO.getGeneratedKeys();
            gk.next();
            int oid = gk.getInt(1);

            PreparedStatement psI = cn.prepareStatement(
                    "INSERT INTO orderitems_012 (order_id_012,product_id_012,buy_qty_012,unit_price_012) VALUES (?,9,1,89.99)");
            psI.setInt(1, oid);
            psI.executeUpdate();

            PreparedStatement psU = cn.prepareStatement(
                    "UPDATE orders_012 SET order_status_012='Completed' WHERE order_id_012=?");
            psU.setInt(1, oid);
            psU.executeUpdate();

            cn.commit();

            StringBuilder sb = new StringBuilder("=== Trigger Test Results_012 ===\n");
            sb.append("order_id_012: ").append(oid).append("\n\n");

            ResultSet ra = cn.createStatement().executeQuery(
                    "SELECT alert_msg_012, alert_time_012 FROM lowstockalerts_012 ORDER BY alert_time_012 DESC LIMIT 1");
            sb.append("--- trg_low_stock_alert_012 ---\n");
            if (ra.next()) {
                sb.append("alert_msg_012:  ").append(ra.getString(1)).append("\n");
                sb.append("alert_time_012: ").append(ra.getTimestamp(2)).append("\n");
            } else { sb.append("No alert generated.\n"); }

            sb.append("\n--- trg_calc_member_points_012 ---\n");
            ResultSet rp = cn.createStatement().executeQuery(
                    "SELECT c.full_name_012, r.gained_points_012, c.total_points_012, r.record_time_012 "
                            + "FROM pointsrecords_012 r JOIN customers_012 c ON r.customer_id_012=c.customer_id_012 "
                            + "WHERE r.order_id_012=" + oid);
            if (rp.next()) {
                sb.append("full_name_012:      ").append(rp.getString(1)).append("\n");
                sb.append("gained_points_012:  ").append(rp.getInt(2)).append("\n");
                sb.append("total_points_012:   ").append(rp.getInt(3)).append("\n");
                sb.append("record_time_012:    ").append(rp.getTimestamp(4)).append("\n");
            } else { sb.append("No points record found.\n"); }

            JOptionPane.showMessageDialog(this, sb.toString(),
                    "Trigger Test_012", JOptionPane.INFORMATION_MESSAGE);
            loadDashboardStats();

        } catch (Exception ex) { showError(ex); }
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable tbl = new JTable(model);
        tbl.setBackground(CARD);
        tbl.setForeground(TXT_WHITE);
        tbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tbl.setRowHeight(28);
        tbl.setGridColor(new Color(0x2a,0x2a,0x3a));
        tbl.setShowGrid(true);
        tbl.setSelectionBackground(BLUE);
        tbl.setSelectionForeground(Color.WHITE);
        tbl.setIntercellSpacing(new Dimension(1, 1));
        tbl.getTableHeader().setBackground(new Color(0x22,0x22,0x32));
        tbl.getTableHeader().setForeground(TXT_WHITE);
        tbl.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tbl.getTableHeader().setBorder(null);
        tbl.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setForeground(sel ? Color.WHITE : TXT_WHITE);
                setBackground(sel ? BLUE : (r % 2 == 0 ? CARD : new Color(0x20,0x20,0x2e)));
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
        return tbl;
    }

    private JButton colorBtn(String text, Color bg, ActionListener al) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(8, 14, 8, 14));
        b.addActionListener(al);
        return b;
    }

    private JTextField tf(String val) {
        JTextField f = new JTextField(val, 20);
        f.setBackground(new Color(0x28,0x28,0x38));
        f.setForeground(TXT_WHITE);
        f.setCaretColor(TXT_WHITE);
        f.setBorder(new CompoundBorder(
                new LineBorder(new Color(0x3a,0x3a,0x4a), 1),
                new EmptyBorder(4, 8, 4, 8)));
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return f;
    }

    private JPanel formPanel(Object... pairs) {
        JPanel p = new JPanel(new GridLayout(pairs.length / 2, 2, 8, 8));
        p.setBackground(CARD);
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        for (Object o : pairs) {
            if (o instanceof String) {
                JLabel l = new JLabel((String) o);
                l.setForeground(TXT_GRAY);
                l.setFont(new Font("SansSerif", Font.PLAIN, 12));
                p.add(l);
            } else { p.add((Component) o); }
        }
        return p;
    }

    private String str(DefaultTableModel m, int row, int col) {
        Object v = m.getValueAt(row, col);
        return v == null ? "" : v.toString();
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error_012", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(Main::new);
    }
}
