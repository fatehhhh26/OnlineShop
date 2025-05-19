import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

class OnlineShopSystem extends JFrame {
    private List<Product> products;
    private List<CartItem> salesHistory;
    private DefaultTableModel productTableModel;
    private DefaultTableModel salesTableModel;
    private JTable productTable, salesTable;
    private JTabbedPane tabbedPane;

    public OnlineShopSystem() {
        initializeData();
        setupUI();
    }

    private void initializeData() {
        products = new ArrayList<>();
        salesHistory = new ArrayList<>();

        // Sample products
        products.add(new Product(1, "Laptop Gaming", 15000000, 10, "Electronics"));
        products.add(new Product(2, "Mouse Wireless", 250000, 50, "Electronics"));
        products.add(new Product(3, "Keyboard Mechanical", 800000, 30, "Electronics"));
        products.add(new Product(4, "Headset Gaming", 500000, 25, "Electronics"));
        products.add(new Product(5, "Smartphone", 5000000, 20, "Electronics"));
        products.add(new Product(6, "Tablet", 3000000, 15, "Electronics"));
    }

    private void setupUI() {
        setTitle("Online Shop - Admin Panel");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // Product Management Tab
        JPanel productPanel = createProductManagementPanel();
        tabbedPane.addTab("Manajemen Produk", productPanel);

        // Sales History Tab
        JPanel salesPanel = createSalesHistoryPanel();
        tabbedPane.addTab("Riwayat Penjualan", salesPanel);

        // Dashboard Tab
        JPanel dashboardPanel = createDashboardPanel();
        tabbedPane.addTab("Dashboard", dashboardPanel);

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel createProductManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Product table
        String[] columnNames = {"ID", "Nama", "Harga", "Stok", "Kategori"};
        productTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(productTableModel);
        updateProductTable();

        JScrollPane scrollPane = new JScrollPane(productTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Tambah Produk");
        JButton editBtn = new JButton("Edit Produk");
        JButton deleteBtn = new JButton("Hapus Produk");
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> showAddProductDialog());
        editBtn.addActionListener(e -> showEditProductDialog());
        deleteBtn.addActionListener(e -> deleteSelectedProduct());
        refreshBtn.addActionListener(e -> updateProductTable());

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSalesHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Produk", "Quantity", "Subtotal", "Waktu"};
        salesTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        salesTable = new JTable(salesTableModel);
        updateSalesTable();

        JScrollPane scrollPane = new JScrollPane(salesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Total Products
        JPanel totalProductsPanel = createInfoPanel("Total Produk", String.valueOf(products.size()));

        // Total Sales
        double totalSales = salesHistory.stream().mapToDouble(CartItem::getSubtotal).sum();
        JPanel totalSalesPanel = createInfoPanel("Total Penjualan",
                String.format("Rp %.2f", totalSales));

        // Low Stock Alert
        long lowStockCount = products.stream().filter(p -> p.getStock() < 10).count();
        JPanel lowStockPanel = createInfoPanel("Produk Stok Rendah", String.valueOf(lowStockCount));

        // Best Selling Category
        JPanel categoryPanel = createInfoPanel("Kategori Terlaris", "Electronics");

        panel.add(totalProductsPanel);
        panel.add(totalSalesPanel);
        panel.add(lowStockPanel);
        panel.add(categoryPanel);

        return panel;
    }

    private JPanel createInfoPanel(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(Color.WHITE);

        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(new Color(0, 123, 255));

        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private void updateProductTable() {
        productTableModel.setRowCount(0);
        for (Product product : products) {
            Object[] row = {
                    product.getId(),
                    product.getName(),
                    product.getFormattedPrice(),
                    product.getStock(),
                    product.getCategory()
            };
            productTableModel.addRow(row);
        }
    }

    private void updateSalesTable() {
        salesTableModel.setRowCount(0);
        for (CartItem item : salesHistory) {
            Object[] row = {
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getFormattedSubtotal(),
                    new Date().toString()
            };
            salesTableModel.addRow(row);
        }
    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog(this, "Tambah Produk", true);
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(6, 2, 5, 5));

        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockField = new JTextField();
        JTextField categoryField = new JTextField();

        dialog.add(new JLabel("Nama:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Harga:"));
        dialog.add(priceField);
        dialog.add(new JLabel("Stok:"));
        dialog.add(stockField);
        dialog.add(new JLabel("Kategori:"));
        dialog.add(categoryField);

        JButton saveBtn = new JButton("Simpan");
        JButton cancelBtn = new JButton("Batal");

        saveBtn.addActionListener(e -> {
            try {
                int newId = products.size() + 1;
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                int stock = Integer.parseInt(stockField.getText());
                String category = categoryField.getText();

                products.add(new Product(newId, name, price, stock, category));
                updateProductTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Format angka tidak valid!");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(saveBtn);
        dialog.add(cancelBtn);
        dialog.setVisible(true);
    }

    private void showEditProductDialog() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk yang akan diedit!");
            return;
        }

        Product product = products.get(selectedRow);

        JDialog dialog = new JDialog(this, "Edit Produk", true);
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(6, 2, 5, 5));

        JTextField nameField = new JTextField(product.getName());
        JTextField priceField = new JTextField(String.valueOf(product.getPrice()));
        JTextField stockField = new JTextField(String.valueOf(product.getStock()));
        JTextField categoryField = new JTextField(product.getCategory());

        dialog.add(new JLabel("Nama:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Harga:"));
        dialog.add(priceField);
        dialog.add(new JLabel("Stok:"));
        dialog.add(stockField);
        dialog.add(new JLabel("Kategori:"));
        dialog.add(categoryField);

        JButton saveBtn = new JButton("Simpan");
        JButton cancelBtn = new JButton("Batal");

        saveBtn.addActionListener(e -> {
            try {
                double price = Double.parseDouble(priceField.getText());
                int stock = Integer.parseInt(stockField.getText());

                product.setPrice(price);
                product.setStock(stock);
                updateProductTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Produk berhasil diupdate!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Format angka tidak valid!");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(saveBtn);
        dialog.add(cancelBtn);
        dialog.setVisible(true);
    }

    private void deleteSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk yang akan dihapus!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus produk ini?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            products.remove(selectedRow);
            updateProductTable();
            JOptionPane.showMessageDialog(this, "Produk berhasil dihapus!");
        }
    }

    // Getter methods for CustomerInterface
    public List<Product> getProducts() { return products; }
    public void addSale(CartItem item) {
        salesHistory.add(item);
        updateSalesTable();
    }
}