import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

class CustomerInterface extends JFrame {
    private List<Product> products;
    private List<CartItem> cart;
    private DefaultTableModel productTableModel;
    private DefaultTableModel cartTableModel;
    private JTable productTable, cartTable;
    private JLabel totalLabel;
    private OnlineShopSystem adminSystem;

    public CustomerInterface() {
        initializeData();
        setupUI();
    }

    private void initializeData() {
        cart = new ArrayList<>();

        // Initialize products (same as admin)
        products = new ArrayList<>();
        products.add(new Product(1, "Laptop Gaming", 15000000, 10, "Electronics"));
        products.add(new Product(2, "Mouse Wireless", 250000, 50, "Electronics"));
        products.add(new Product(3, "Keyboard Mechanical", 800000, 30, "Electronics"));
        products.add(new Product(4, "Headset Gaming", 500000, 25, "Electronics"));
        products.add(new Product(5, "Smartphone", 5000000, 20, "Electronics"));
        products.add(new Product(6, "Tablet", 3000000, 15, "Electronics"));
    }

    private void setupUI() {
        setTitle("Online Shop - Customer");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Left panel - Products
        JPanel leftPanel = createProductPanel();
        splitPane.setLeftComponent(leftPanel);

        // Right panel - Cart
        JPanel rightPanel = createCartPanel();
        splitPane.setRightComponent(rightPanel);

        splitPane.setDividerLocation(450);
        add(splitPane);

        setVisible(true);
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Daftar Produk"));

        String[] columnNames = {"ID", "Nama", "Harga", "Stok"};
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

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Cari");

        searchBtn.addActionListener(e -> searchProducts(searchField.getText()));

        searchPanel.add(new JLabel("Cari produk:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Add to cart button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addToCartBtn = new JButton("Tambah ke Keranjang");

        addToCartBtn.addActionListener(e -> addToCart());
        buttonPanel.add(addToCartBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Keranjang Belanja"));

        String[] columnNames = {"Produk", "Quantity", "Subtotal"};
        cartTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartTable = new JTable(cartTableModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Total panel
        JPanel totalPanel = new JPanel(new FlowLayout());
        totalLabel = new JLabel("Total: Rp 0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalPanel.add(totalLabel);

        panel.add(totalPanel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton removeBtn = new JButton("Hapus dari Keranjang");
        JButton checkoutBtn = new JButton("Checkout");
        JButton clearBtn = new JButton("Kosongkan Keranjang");

        removeBtn.addActionListener(e -> removeFromCart());
        checkoutBtn.addActionListener(e -> checkout());
        clearBtn.addActionListener(e -> clearCart());

        buttonPanel.add(removeBtn);
        buttonPanel.add(checkoutBtn);
        buttonPanel.add(clearBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void updateProductTable() {
        productTableModel.setRowCount(0);
        for (Product product : products) {
            Object[] row = {
                    product.getId(),
                    product.getName(),
                    product.getFormattedPrice(),
                    product.getStock()
            };
            productTableModel.addRow(row);
        }
    }

    private void updateCartTable() {
        cartTableModel.setRowCount(0);
        double total = 0;

        for (CartItem item : cart) {
            Object[] row = {
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getFormattedSubtotal()
            };
            cartTableModel.addRow(row);
            total += item.getSubtotal();
        }

        totalLabel.setText(String.format("Total: Rp %.2f", total));
    }

    private void searchProducts(String keyword) {
        productTableModel.setRowCount(0);
        for (Product product : products) {
            if (product.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                    product.getCategory().toLowerCase().contains(keyword.toLowerCase())) {
                Object[] row = {
                        product.getId(),
                        product.getName(),
                        product.getFormattedPrice(),
                        product.getStock()
                };
                productTableModel.addRow(row);
            }
        }
    }

    private void addToCart() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk yang akan ditambahkan!");
            return;
        }

        // Get product from the displayed table
        int productId = (Integer) productTableModel.getValueAt(selectedRow, 0);
        Product product = products.stream()
                .filter(p -> p.getId() == productId)
                .findFirst()
                .orElse(null);

        if (product == null) {
            JOptionPane.showMessageDialog(this, "Produk tidak ditemukan!");
            return;
        }

        if (product.getStock() <= 0) {
            JOptionPane.showMessageDialog(this, "Stok produk habis!");
            return;
        }

        // Ask for quantity
        String quantityStr = JOptionPane.showInputDialog(this,
                "Masukkan jumlah (Max: " + product.getStock() + "):");

        if (quantityStr == null) return;

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0 || quantity > product.getStock()) {
                JOptionPane.showMessageDialog(this, "Jumlah tidak valid!");
                return;
            }

            // Check if product already in cart
            CartItem existingItem = cart.stream()
                    .filter(item -> item.getProduct().getId() == product.getId())
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
            } else {
                cart.add(new CartItem(product, quantity));
            }

            updateCartTable();
            JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan ke keranjang!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format jumlah tidak valid!");
        }
    }

    private void removeFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item yang akan dihapus!");
            return;
        }

        cart.remove(selectedRow);
        updateCartTable();
        JOptionPane.showMessageDialog(this, "Item berhasil dihapus dari keranjang!");
    }

    private void clearCart() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang sudah kosong!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin mengosongkan keranjang?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            cart.clear();
            updateCartTable();
            JOptionPane.showMessageDialog(this, "Keranjang berhasil dikosongkan!");
        }
    }

    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang kosong!");
            return;
        }

        double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();

        String message = String.format(
                "Total belanja: Rp %.2f\n\nDetail pembelian:\n", total);

        for (CartItem item : cart) {
            message += String.format("- %s x%d = %s\n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getFormattedSubtotal());
        }

        message += "\nLanjutkan pembayaran?";

        int confirm = JOptionPane.showConfirmDialog(this, message,
                "Konfirmasi Checkout", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Process payment
            processPayment();
        }
    }

    private void processPayment() {
        // Update product stock
        for (CartItem item : cart) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
        }

        updateProductTable();

        // Show success message
        JOptionPane.showMessageDialog(this,
                "Pembayaran berhasil!\nTerima kasih atas pembelian Anda!",
                "Pembayaran Berhasil",
                JOptionPane.INFORMATION_MESSAGE);

        // Clear cart
        cart.clear();
        updateCartTable();
    }
}