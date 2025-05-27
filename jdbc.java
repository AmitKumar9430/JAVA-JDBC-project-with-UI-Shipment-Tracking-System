import javax.swing.*; // Provides classes for building graphical user interfaces (GUIs) in Java.
import javax.swing.border.TitledBorder; // Allows creating borders with a title for Swing components.
import javax.swing.table.DefaultTableModel; // Provides a default data model for JTable components.
import java.awt.*; // Contains classes for creating user interfaces and for painting graphics and images.
import java.awt.event.*; // Provides interfaces and classes for handling AWT (Abstract Window Toolkit) events.
import java.io.File; // Represents file and directory pathnames in an abstract manner.
import javax.swing.event.DocumentEvent; // Describes changes to a document's content for Swing text components.
import javax.swing.event.DocumentListener; // Receives notifications when changes are made to a document.
import javax.swing.Timer; // Fires one or more action events at specified intervals.
import java.util.List; // An ordered collection (interface) for storing lists of objects.
import java.util.ArrayList; // A resizable-array implementation of the List interface.
import java.io.PrintWriter; // Prints formatted representations of objects to text-output streams.
import java.io.IOException; // Signals that an I/O exception has occurred.
import java.sql.*; // Provides the API for accessing and processing data in a relational database.
import java.text.SimpleDateFormat; // Formats and parses dates in a locale-sensitive manner.
import javax.swing.table.DefaultTableCellRenderer; // Allows customization of how cells in a JTable are rendered.
import java.util.Date; // Represents a specific instant in time, with millisecond precision.
class ShipmentTrackingSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/shiptmenttrack_db";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";
    private static Connection connection;
    // Admin credentials
    private static final String ADMIN_USERNAME = "dd";
    private static final String ADMIN_PASSWORD = "1";
    // UI Colors
    private static final Color PRIMARY_COLOR = new Color(23, 158, 195);
    private static final Color SECONDARY_COLOR = new Color(255, 65, 54);
    private static final Color ACCENT_COLOR = new Color(52, 199, 89);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color TEXT_COLOR = new Color(34, 34, 34);
    private static final Color PANEL_COLOR = new Color(255, 255, 255);

    public static void main(String[] args) {
        if (!showLoginDialog()) {
            System.exit(0);
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            setupDatabase();
        } catch (Exception e) {
            showErrorDialog(null, "Database connection error: " + e.getMessage());
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
     private static void setupDatabase() throws SQLException {
         try (Statement stmt = connection.createStatement()) {
             // Create main shipments table

             String sql = "CREATE TABLE IF NOT EXISTS shipments (" +
                     "shipment_id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "customer_name VARCHAR(50) NOT NULL, " +
                     "origin VARCHAR(50) NOT NULL, " +
                     "destination VARCHAR(50) NOT NULL, " +
                     "cost DECIMAL(10,2) NOT NULL, " +
                     "delivery_date DATE NOT NULL, " +
                     "status VARCHAR(20) DEFAULT 'Pending', " +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
             stmt.execute(sql);

             // Create deleted shipments archive table
             sql = "CREATE TABLE IF NOT EXISTS deleted_shipments (" +
                     "deletion_id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "shipment_id INT NOT NULL, " +
                     "customer_name VARCHAR(50) NOT NULL, " +
                     "origin VARCHAR(50) NOT NULL, " +
                     "destination VARCHAR(50) NOT NULL, " +
                     "cost DECIMAL(10,2) NOT NULL, " +
                     "delivery_date DATE NOT NULL, " +
                     "status VARCHAR(20) DEFAULT 'Pending', " +
                     "created_at TIMESTAMP, " +
                     "deleted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                     "deleted_by VARCHAR(50) DEFAULT 'admin')";
             stmt.execute(sql);

             // Ensure auto-increment works properly
             stmt.execute("SET @@auto_increment_increment=1");
             stmt.execute("SET @@auto_increment_offset=1");
         }
     }

    private static boolean showLoginDialog() {
        JDialog loginDialog = new JDialog((Frame) null, "Admin Login", true);
        loginDialog.setSize(400, 250);
        loginDialog.setLayout(new GridBagLayout());
        loginDialog.getContentPane().setBackground(BACKGROUND_COLOR);
        loginDialog.setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("SHIPMENT TRACKING SYSTEM", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginDialog.add(titleLabel, gbc);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(TEXT_COLOR);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField userField = new JTextField(20);
        styleTextField(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(TEXT_COLOR);
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JPasswordField passField = new JPasswordField(20);
        styleTextField(passField);

        JButton loginButton = new JButton("Login");
        styleButton(loginButton, ACCENT_COLOR);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginDialog.add(userLabel, gbc);
        gbc.gridx = 1;
        loginDialog.add(userField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginDialog.add(passLabel, gbc);
        gbc.gridx = 1;
        loginDialog.add(passField, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        loginDialog.add(loginButton, gbc);

        final boolean[] loginSuccess = {false};
        loginButton.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                loginSuccess[0] = true;
                loginDialog.dispose();
            } else {
                showErrorDialog(loginDialog, "Invalid username or password");
                passField.setText("");
            }
        });

        loginDialog.getRootPane().setDefaultButton(loginButton);
        loginDialog.setVisible(true);

        return loginSuccess[0];
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Shipment Tracking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setMinimumSize(new Dimension(900, 650));
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(BACKGROUND_COLOR);

        // Header Panel with gradient
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), 0, new Color(100, 200, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("Shipment Management System", JLabel.CENTER);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        JLabel userLabel = new JLabel("Logged in as: " + ADMIN_USERNAME);
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        headerPanel.add(userLabel, BorderLayout.EAST);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(true);
        tabbedPane.setBackground(PANEL_COLOR);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // Add fade-in effect when switching tabs
        tabbedPane.addChangeListener(e -> {
            Component selected = tabbedPane.getSelectedComponent();
            if (selected != null) {
                selected.setVisible(false);
                new Timer(30, new ActionListener() {
                    float alpha = 0f;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        alpha += 0.1f;
                        selected.setVisible(true);
                        ((JComponent)selected).setOpaque(true);
                        if (alpha >= 1f) {
                            ((Timer)e.getSource()).stop();
                        }
                    }
                }).start();
            }
        });

        // Create tabs
        tabbedPane.addTab("Add Shipment", createAddShipmentPanel());
        tabbedPane.addTab("View Shipments", createViewShipmentsPanel());
        tabbedPane.addTab("Search/Filter", createSearchFilterPanel());
        tabbedPane.addTab("Update Status", createUpdatePanel());
        tabbedPane.addTab("Delete", createDeletePanel());
        tabbedPane.addTab("Deletion History", createDeletionHistoryPanel());
        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel createAddShipmentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Customer Name:", "Origin:", "Destination:", "Cost ($):", "Delivery Date (YYYY-MM-DD):"};
        JTextField[] fields = new JTextField[5];
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Pending", "In Transit", "Delivered", "Cancelled"});

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setForeground(TEXT_COLOR);
            label.setFont(new Font("Arial", Font.PLAIN, 14));

            fields[i] = new JTextField(20);
            styleTextField(fields[i]);

            gbc.gridx = 0;
            gbc.gridy = i;
            panel.add(label, gbc);
            gbc.gridx = 1;
            panel.add(fields[i], gbc);
        }

        // Add status field
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = labels.length;
        panel.add(statusLabel, gbc);
        gbc.gridx = 1;
        panel.add(statusCombo, gbc);

        JButton addButton = new JButton("Add Shipment");
        styleButton(addButton, ACCENT_COLOR);
        gbc.gridx = 1;
        gbc.gridy = labels.length + 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            try {
                String name = fields[0].getText().trim();
                String origin = fields[1].getText().trim();
                String destination = fields[2].getText().trim();
                double cost = Double.parseDouble(fields[3].getText().trim());
                String dateStr = fields[4].getText().trim();
                String status = (String) statusCombo.getSelectedItem();

                // Validate date format
                if (!dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    throw new IllegalArgumentException("Date must be in YYYY-MM-DD format");
                }

                // Convert to SQL date
                Date deliveryDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                java.sql.Date sqlDate = new java.sql.Date(deliveryDate.getTime());

                addShipment(name, origin, destination, cost, sqlDate, status);
                clearFields(fields);
            } catch (NumberFormatException ex) {
                showErrorDialog(panel, "Please enter valid numbers for Cost");
            } catch (IllegalArgumentException ex) {
                showErrorDialog(panel, ex.getMessage());
            } catch (Exception ex) {
                showErrorDialog(panel, "Error: " + ex.getMessage());
            }
        });

        return panel;
    }

    private static JPanel createViewShipmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Table model with more columns
        String[] columnNames = {"ID", "Customer", "Origin", "Destination", "Cost", "Delivery Date", "Status", "Created At"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        // Center align numeric columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Cost
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Date
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Created At

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true));

        // Refresh button
        JButton refreshButton = new JButton("Refresh Data");
        styleButton(refreshButton, PRIMARY_COLOR);
        refreshButton.addActionListener(e -> refreshShipmentData(model));

        // Export button
        JButton exportButton = new JButton("Export to CSV");
        styleButton(exportButton, ACCENT_COLOR);
        exportButton.addActionListener(e -> exportToCSV(table));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.add(exportButton);
        buttonPanel.add(refreshButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Load initial data
        refreshShipmentData(model);

        return panel;
    }

     private static JPanel createSearchFilterPanel() {
         JPanel panel = new JPanel(new BorderLayout(15, 15));
         panel.setBackground(PANEL_COLOR);
         panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

         // Results table
         String[] columnNames = {"ID", "Customer", "Origin", "Destination", "Cost", "Status", "Delivery Date"};
         DefaultTableModel model = new DefaultTableModel(columnNames, 0);
         JTable resultsTable = new JTable(model);
         JScrollPane scrollPane = new JScrollPane(resultsTable);

         // Filter components
         JTextField shipmentIdField = new JTextField();
         JTextField minCostField = new JTextField();
         JTextField maxCostField = new JTextField();
         JTextField startDateField = new JTextField();
         JTextField endDateField = new JTextField();
         JComboBox<String> statusFilter = new JComboBox<>(new String[]{
                 "All Statuses", "Pending", "In Transit", "Delivered", "Cancelled"
         });

         // Style all components
         styleTextField(shipmentIdField);
         styleTextField(minCostField);
         styleTextField(maxCostField);
         styleTextField(startDateField);
         styleTextField(endDateField);
         statusFilter.setFont(new Font("Arial", Font.PLAIN, 14));

         // Create filter panel
         JPanel filterPanel = new JPanel(new GridLayout(0, 2, 10, 10));
         filterPanel.setBackground(PANEL_COLOR);
         filterPanel.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                 "Advanced Filters",
                 TitledBorder.LEFT,
                 TitledBorder.TOP,
                 new Font("Arial", Font.BOLD, 12),
                 PRIMARY_COLOR
         ));

         // Add components to filter panel
         filterPanel.add(new JLabel("Shipment ID:"));
         filterPanel.add(shipmentIdField);
         filterPanel.add(new JLabel("Min Cost:"));
         filterPanel.add(minCostField);
         filterPanel.add(new JLabel("Max Cost:"));
         filterPanel.add(maxCostField);
         filterPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
         filterPanel.add(startDateField);
         filterPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
         filterPanel.add(endDateField);
         filterPanel.add(new JLabel("Status:"));
         filterPanel.add(statusFilter);

         // Create search timer with 500ms delay
         Timer searchTimer = new Timer(500, e -> {
             String idText = shipmentIdField.getText().trim();

             if (!idText.isEmpty()) {
                 try {
                     int shipmentId = Integer.parseInt(idText);
                     // Call separate method for ID search
                     searchShipmentsById(model, shipmentId);
                 } catch (NumberFormatException ex) {
                     // If not a valid number, use regular filtering
                     filterShipments(model,
                             minCostField.getText().trim(),
                             maxCostField.getText().trim(),
                             startDateField.getText().trim(),
                             endDateField.getText().trim(),
                             (String) statusFilter.getSelectedItem()
                     );
                 }
             } else {
                 // If ID field is empty, use regular filtering
                 filterShipments(model,
                         minCostField.getText().trim(),
                         maxCostField.getText().trim(),
                         startDateField.getText().trim(),
                         endDateField.getText().trim(),
                         (String) statusFilter.getSelectedItem()
                 );
             }
         });
         searchTimer.setRepeats(false);

         // Document listener for text fields
         DocumentListener documentListener = new DocumentListener() {
             @Override public void insertUpdate(DocumentEvent e) { searchTimer.restart(); }
             @Override public void removeUpdate(DocumentEvent e) { searchTimer.restart(); }
             @Override public void changedUpdate(DocumentEvent e) { searchTimer.restart(); }
         };

         // Add listeners to all text fields
         shipmentIdField.getDocument().addDocumentListener(documentListener);
         minCostField.getDocument().addDocumentListener(documentListener);
         maxCostField.getDocument().addDocumentListener(documentListener);
         startDateField.getDocument().addDocumentListener(documentListener);
         endDateField.getDocument().addDocumentListener(documentListener);

         // Add listener for status combo box
         statusFilter.addActionListener(e -> searchTimer.restart());

         // Add panels to main panel
         panel.add(filterPanel, BorderLayout.NORTH);
         panel.add(scrollPane, BorderLayout.CENTER);

         // Load initial data
         filterShipments(model,
                 minCostField.getText().trim(),
                 maxCostField.getText().trim(),
                 startDateField.getText().trim(),
                 endDateField.getText().trim(),
                 (String) statusFilter.getSelectedItem()
         );

         return panel;
     }
     private static void searchShipmentsById(DefaultTableModel model, int id) {
         try {
             model.setRowCount(0);
             String sql = "SELECT * FROM shipments WHERE shipment_id = ?";

             try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                 stmt.setInt(1, id);

                 try (ResultSet rs = stmt.executeQuery()) {
                     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                     while (rs.next()) {
                         model.addRow(new Object[]{
                                 rs.getInt("shipment_id"),
                                 rs.getString("customer_name"),
                                 rs.getString("origin"),
                                 rs.getString("destination"),
                                 String.format("$%.2f", rs.getDouble("cost")),
                                 rs.getString("status"),
                                 dateFormat.format(rs.getDate("delivery_date"))
                         });
                     }

                     if (model.getRowCount() == 0) {
                         JOptionPane.showMessageDialog(null,
                                 "No shipments found with ID: " + id,
                                 "No Results",
                                 JOptionPane.INFORMATION_MESSAGE);
                     }
                 }
             }
         } catch (SQLException ex) {
             showErrorDialog(null, "Database error: " + ex.getMessage());
         }
     }
    private static JPanel createUpdatePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(PANEL_COLOR);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                "Update Shipment Status",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                PRIMARY_COLOR
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("Shipment ID:");
        idLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField idField = new JTextField(15);
        styleTextField(idField);

        JLabel statusLabel = new JLabel("New Status:");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{
                "Pending", "In Transit", "Delivered", "Cancelled"
        });

        JButton searchButton = new JButton("Find Shipment");
        styleButton(searchButton, PRIMARY_COLOR);

        JButton updateButton = new JButton("Update Status");
        styleButton(updateButton, ACCENT_COLOR);
        updateButton.setEnabled(false);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(idField, gbc);
        gbc.gridx = 2;
        inputPanel.add(searchButton, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(statusLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(statusCombo, gbc);
        gbc.gridx = 2;
        inputPanel.add(updateButton, gbc);

        // Details panel
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane detailsScroll = new JScrollPane(detailsArea);

        // Add to main panel
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(detailsScroll, BorderLayout.CENTER);

        // Action listeners
        searchButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String details = getShipmentDetails(id);
                if (details != null) {
                    detailsArea.setText(details);
                    updateButton.setEnabled(true);
                } else {
                    detailsArea.setText("No shipment found with ID: " + id);
                    updateButton.setEnabled(false);
                }
            } catch (NumberFormatException ex) {
                showErrorDialog(panel, "Please enter a valid shipment ID");
                updateButton.setEnabled(false);
            } catch (SQLException ex) {
                showErrorDialog(panel, "Database error: " + ex.getMessage());
                updateButton.setEnabled(false);
            }
        });

        updateButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String newStatus = (String) statusCombo.getSelectedItem();

                int confirm = JOptionPane.showConfirmDialog(
                        panel,
                        "Are you sure you want to update shipment #" + id + " to '" + newStatus + "'?",
                        "Confirm Update",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    updateShipmentStatus(id, newStatus);
                    detailsArea.setText(getShipmentDetails(id));
                    JOptionPane.showMessageDialog(panel, "Status updated successfully!");
                }
            } catch (Exception ex) {
                showErrorDialog(panel, "Error updating status: " + ex.getMessage());
            }
        });

        return panel;
    }

    private static JPanel createDeletePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(PANEL_COLOR);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
                "Delete Shipment",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                SECONDARY_COLOR
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("Shipment ID:");
        idLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField idField = new JTextField(15);
        styleTextField(idField);

        JButton searchButton = new JButton("Find Shipment");
        styleButton(searchButton, PRIMARY_COLOR);

        JButton deleteButton = new JButton("Delete Shipment");
        styleButton(deleteButton, SECONDARY_COLOR);
        deleteButton.setEnabled(false);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(idField, gbc);
        gbc.gridx = 2;
        inputPanel.add(searchButton, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 2;
        inputPanel.add(deleteButton, gbc);

        // Details panel
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane detailsScroll = new JScrollPane(detailsArea);

        // Add to main panel
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(detailsScroll, BorderLayout.CENTER);

        // Action listeners
        searchButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String details = getShipmentDetails(id);
                if (details != null) {
                    detailsArea.setText(details);
                    deleteButton.setEnabled(true);
                } else {
                    detailsArea.setText("No shipment found with ID: " + id);
                    deleteButton.setEnabled(false);
                }
            } catch (NumberFormatException ex) {
                showErrorDialog(panel, "Please enter a valid shipment ID");
                deleteButton.setEnabled(false);
            } catch (SQLException ex) {
                showErrorDialog(panel, "Database error: " + ex.getMessage());
                deleteButton.setEnabled(false);
            }
        });

        deleteButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());

                int confirm = JOptionPane.showConfirmDialog(
                        panel,
                        "Are you sure you want to permanently delete shipment #" + id + "?\nThis action cannot be undone.",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    deleteShipment(id);
                    detailsArea.setText("Shipment #" + id + " has been deleted.");
                    deleteButton.setEnabled(false);
                    idField.setText("");
                }
            } catch (NumberFormatException ex) {
                showErrorDialog(panel, "Please enter a valid shipment ID");
            } catch (SQLException ex) {
                showErrorDialog(panel, "Database error: " + ex.getMessage());
            }
        });

        return panel;
    }

    // Database operations
    private static void addShipment(String name, String origin, String destination, double cost, java.sql.Date date, String status) throws SQLException {
        String sql = "INSERT INTO shipments (customer_name, origin, destination, cost, delivery_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, origin);
            stmt.setString(3, destination);
            stmt.setDouble(4, cost);
            stmt.setDate(5, date);
            stmt.setString(6, status);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating shipment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    JOptionPane.showMessageDialog(null,
                            "Shipment added successfully!\nGenerated ID: " + generatedId,
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    throw new SQLException("Creating shipment failed, no ID obtained.");
                }
            }
        }
    }

    private static void refreshShipmentData(DefaultTableModel model) {
        try {
            model.setRowCount(0); // Clear existing data

            String sql = "SELECT * FROM shipments ORDER BY shipment_id DESC";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("shipment_id"),
                            rs.getString("customer_name"),
                            rs.getString("origin"),
                            rs.getString("destination"),
                            String.format("$%.2f", rs.getDouble("cost")),
                            dateFormat.format(rs.getDate("delivery_date")),
                            rs.getString("status"),
                            rs.getTimestamp("created_at").toString()
                    });
                }
            }
        } catch (SQLException ex) {
            showErrorDialog(null, "Error loading shipment data: " + ex.getMessage());
        }
    }

    private static void searchShipments(DefaultTableModel model, String searchText, String searchType) {
        try {
            model.setRowCount(0);

            String sql;
            PreparedStatement stmt;

            if (searchText.isEmpty()) {
                sql = "SELECT * FROM shipments ORDER BY shipment_id DESC";
                stmt = connection.prepareStatement(sql);
            } else {
                switch (searchType) {
                    case "All Fields":
                        sql = "SELECT * FROM shipments WHERE " +
                                "customer_name LIKE ? OR " +
                                "origin LIKE ? OR " +
                                "destination LIKE ? OR " +
                                "status LIKE ? " +
                                "ORDER BY shipment_id DESC";
                        stmt = connection.prepareStatement(sql);
                        String likePattern = "%" + searchText + "%";
                        for (int i = 1; i <= 4; i++) {
                            stmt.setString(i, likePattern);
                        }
                        break;
                    default:
                        String columnName = searchType.replace(" ", "_").toLowerCase();
                        sql = "SELECT * FROM shipments WHERE " + columnName + " LIKE ? ORDER BY shipment_id DESC";
                        stmt = connection.prepareStatement(sql);
                        stmt.setString(1, "%" + searchText + "%");
                        break;
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("shipment_id"),
                            rs.getString("customer_name"),
                            rs.getString("origin"),
                            rs.getString("destination"),
                            String.format("$%.2f", rs.getDouble("cost")),
                            rs.getString("status"),
                            dateFormat.format(rs.getDate("delivery_date"))
                    });
                }
            }
        } catch (SQLException ex) {
            showErrorDialog(null, "Error searching shipments: " + ex.getMessage());
        }
    }

     private static void filterShipments(DefaultTableModel model,
                                         String minCost, String maxCost,
                                         String startDate, String endDate,
                                         String status) {
         try {
             model.setRowCount(0);

             StringBuilder sql = new StringBuilder("SELECT * FROM shipments WHERE 1=1");
             List<Object> parameters = new ArrayList<>();

             // Cost filter
             if (!minCost.isEmpty()) {
                 try {
                     double min = Double.parseDouble(minCost);
                     sql.append(" AND cost >= ?");
                     parameters.add(min);
                 } catch (NumberFormatException e) {
                     showErrorDialog(null, "Invalid minimum cost value");
                     return;
                 }
             }

             if (!maxCost.isEmpty()) {
                 try {
                     double max = Double.parseDouble(maxCost);
                     sql.append(" AND cost <= ?");
                     parameters.add(max);
                 } catch (NumberFormatException e) {
                     showErrorDialog(null, "Invalid maximum cost value");
                     return;
                 }
             }

             // Date filter
             SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
             dateFormat.setLenient(false); // Strict date parsing

             if (!startDate.isEmpty()) {
                 try {
                     Date parsedDate = dateFormat.parse(startDate);
                     sql.append(" AND delivery_date >= ?");
                     parameters.add(new java.sql.Date(parsedDate.getTime()));
                 } catch (Exception e) {
                     showErrorDialog(null, "Invalid start date format (use YYYY-MM-DD)");
                     return;
                 }
             }

             if (!endDate.isEmpty()) {
                 try {
                     Date parsedDate = dateFormat.parse(endDate);
                     sql.append(" AND delivery_date <= ?");
                     parameters.add(new java.sql.Date(parsedDate.getTime()));
                 } catch (Exception e) {
                     showErrorDialog(null, "Invalid end date format (use YYYY-MM-DD)");
                     return;
                 }
             }

             // Status filter - only add if not "All Statuses"
             if (!status.equals("All Statuses")) {
                 sql.append(" AND status = ?");
                 parameters.add(status);
             }

             sql.append(" ORDER BY shipment_id DESC");

             try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
                 // Set all parameters
                 for (int i = 0; i < parameters.size(); i++) {
                     Object param = parameters.get(i);
                     if (param instanceof Double) {
                         stmt.setDouble(i + 1, (Double) param);
                     } else if (param instanceof java.sql.Date) {
                         stmt.setDate(i + 1, (java.sql.Date) param);
                     } else {
                         stmt.setString(i + 1, param.toString());
                     }
                 }

                 try (ResultSet rs = stmt.executeQuery()) {
                     SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd");

                     while (rs.next()) {
                         model.addRow(new Object[]{
                                 rs.getInt("shipment_id"),
                                 rs.getString("customer_name"),
                                 rs.getString("origin"),
                                 rs.getString("destination"),
                                 String.format("$%.2f", rs.getDouble("cost")),
                                 rs.getString("status"),
                                 displayFormat.format(rs.getDate("delivery_date"))
                         });
                     }

                     if (model.getRowCount() == 0) {
                         JOptionPane.showMessageDialog(null,
                                 "No shipments match the filter criteria",
                                 "No Results",
                                 JOptionPane.INFORMATION_MESSAGE);
                     }
                 }
             }
         } catch (SQLException ex) {
             showErrorDialog(null, "Database error: " + ex.getMessage());
         }
     }

    private static String getShipmentDetails(int id) throws SQLException {
        String sql = "SELECT * FROM shipments WHERE shipment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    StringBuilder details = new StringBuilder();
                    details.append("Shipment ID: ").append(rs.getInt("shipment_id")).append("\n");
                    details.append("Customer: ").append(rs.getString("customer_name")).append("\n");
                    details.append("Origin: ").append(rs.getString("origin")).append("\n");
                    details.append("Destination: ").append(rs.getString("destination")).append("\n");
                    details.append("Cost: $").append(String.format("%.2f", rs.getDouble("cost"))).append("\n");
                    details.append("Status: ").append(rs.getString("status")).append("\n");
                    details.append("Delivery Date: ").append(dateFormat.format(rs.getDate("delivery_date"))).append("\n");
                    details.append("Created At: ").append(timestampFormat.format(rs.getTimestamp("created_at"))).append("\n");

                    return details.toString();
                }
            }
        }
        return null;
    }

//    private static void updateShipmentStatus(int id, String newStatus) throws SQLException {
//        String sql = "UPDATE shipments SET status = ? WHERE shipment_id = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setString(1, newStatus);
//            stmt.setInt(2, id);
//            stmt.executeUpdate();
//        }
//    }
private static void updateShipmentStatus(int id, String newStatus) throws SQLException {
    String sql = "UPDATE shipments SET status = ? WHERE shipment_id = ?";

    try {
        connection.setAutoCommit(false);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    } catch (SQLException e) {
        throw new SQLException("Failed to update status: " + e.getMessage());
    }
}

     private static void deleteShipment(int id) throws SQLException {
         // First archive the record
         String archiveSql = "INSERT INTO deleted_shipments " +
                 "(shipment_id, customer_name, origin, destination, cost, " +
                 "delivery_date, status, created_at) " +
                 "SELECT shipment_id, customer_name, origin, destination, " +
                 "cost, delivery_date, status, created_at " +
                 "FROM shipments WHERE shipment_id = ?";

         // Then delete from main table
         String deleteSql = "DELETE FROM shipments WHERE shipment_id = ?";

         try {

             System.out.println("Starting deletion of shipment #" + id); // Debug log
             connection.setAutoCommit(false);
             try (PreparedStatement archiveStmt = connection.prepareStatement(archiveSql);
                  PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {

                 // Archive the record
                 archiveStmt.setInt(1, id);
                 archiveStmt.executeUpdate();

                 // Delete from main table
                 deleteStmt.setInt(1, id);
                 deleteStmt.executeUpdate();

                 connection.commit();
                 System.out.println("Successfully deleted shipment #" + id);
             } catch (SQLException e) {
                 connection.rollback(); // Rollback if error occurs
                 throw e;
             } finally {
                 connection.setAutoCommit(true);
             }
         } catch (SQLException e) {
             throw new SQLException("Failed to delete shipment: " + e.getMessage());
         }
     }

     private static JPanel createDeletionHistoryPanel() {
         JPanel panel = new JPanel(new BorderLayout(15, 15));
         panel.setBackground(PANEL_COLOR);
         panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

         // Table model for deletion history
         String[] columnNames = {"Deletion ID", "Shipment ID", "Customer", "Origin",
                 "Destination", "Cost", "Status", "Created At", "Deleted At"};
         DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
             @Override
             public boolean isCellEditable(int row, int column) {
                 return false;
             }
         };

         JTable historyTable = new JTable(model);
         historyTable.setFont(new Font("Arial", Font.PLAIN, 12));
         historyTable.setRowHeight(25);
         historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

         // Search Panel
         JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
         searchPanel.setBackground(PANEL_COLOR);

         JLabel searchLabel = new JLabel("Search:");
         JTextField searchField = new JTextField(20);
         styleTextField(searchField);

         JComboBox<String> searchOptions = new JComboBox<>(
                 new String[]{"All Fields", "Deletion ID", "Shipment ID", "Customer", "Status"}
         );

         JButton searchButton = new JButton("Search");
         styleButton(searchButton, PRIMARY_COLOR);

         JButton clearSearchButton = new JButton("Clear");
         styleButton(clearSearchButton, new Color(150, 150, 150));

         searchPanel.add(searchLabel);
         searchPanel.add(searchField);
         searchPanel.add(searchOptions);
         searchPanel.add(searchButton);
         searchPanel.add(clearSearchButton);

         // Center align numeric columns
         DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
         centerRenderer.setHorizontalAlignment(JLabel.CENTER);
         historyTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
         historyTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
         historyTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

         JScrollPane scrollPane = new JScrollPane(historyTable);
         scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true));

         // Button panel - DECLARED BEFORE USE
         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
         buttonPanel.setBackground(PANEL_COLOR);

         // Refresh button
         JButton refreshButton = new JButton("Refresh History");
         styleButton(refreshButton, PRIMARY_COLOR);

         // Delete buttons
         JButton deleteSelectedButton = new JButton("Delete Selected");
         styleButton(deleteSelectedButton, SECONDARY_COLOR);

         JButton deleteBySearchButton = new JButton("Delete Search Results");
         styleButton(deleteBySearchButton, new Color(200, 50, 50));

         buttonPanel.add(refreshButton);
         buttonPanel.add(deleteSelectedButton);
         buttonPanel.add(deleteBySearchButton);

         // Add components to main panel
         panel.add(searchPanel, BorderLayout.NORTH);
         panel.add(scrollPane, BorderLayout.CENTER);
         panel.add(buttonPanel, BorderLayout.SOUTH);

         // Action Listeners
         searchButton.addActionListener(e -> {
             String searchText = searchField.getText().trim();
             String searchType = (String) searchOptions.getSelectedItem();
             loadDeletionHistory(model, searchText, searchType);
         });

         clearSearchButton.addActionListener(e -> {
             searchField.setText("");
             loadDeletionHistory(model, "", "All Fields");
         });

         refreshButton.addActionListener(e ->
                 loadDeletionHistory(model, searchField.getText(), (String) searchOptions.getSelectedItem()));

         deleteSelectedButton.addActionListener(e -> {
             int selectedRow = historyTable.getSelectedRow();
             if (selectedRow == -1) {
                 showErrorDialog(panel, "Please select a record to delete");
                 return;
             }

             int deletionId = (int) model.getValueAt(selectedRow, 0);
             int shipmentId = (int) model.getValueAt(selectedRow, 1);
             String customerName = (String) model.getValueAt(selectedRow, 2);

             int confirm = JOptionPane.showConfirmDialog(
                     panel,
                     "Permanently delete record?\n\n" +
                             "Deletion ID: " + deletionId + "\n" +
                             "Original Shipment ID: " + shipmentId + "\n" +
                             "Customer: " + customerName,
                     "Confirm Permanent Deletion",
                     JOptionPane.YES_NO_OPTION,
                     JOptionPane.WARNING_MESSAGE
             );

             if (confirm == JOptionPane.YES_OPTION) {
                 try {
                     deleteFromHistory(deletionId);
                     model.removeRow(selectedRow);
                     JOptionPane.showMessageDialog(
                             panel,
                             "Record permanently deleted from history",
                             "Deletion Complete",
                             JOptionPane.INFORMATION_MESSAGE
                     );
                 } catch (SQLException ex) {
                     showErrorDialog(panel, "Error deleting from history: " + ex.getMessage());
                 }
             }
         });

         deleteBySearchButton.addActionListener(e -> {
             String searchText = searchField.getText().trim();
             String searchType = (String) searchOptions.getSelectedItem();

             if (model.getRowCount() == 0) {
                 showErrorDialog(panel, "No records to delete");
                 return;
             }

             int confirm = JOptionPane.showConfirmDialog(
                     panel,
                     "Permanently delete ALL " + model.getRowCount() + " displayed records?",
                     "Confirm Mass Deletion",
                     JOptionPane.YES_NO_OPTION,
                     JOptionPane.WARNING_MESSAGE
             );
         });

         // Load initial data
         loadDeletionHistory(model, "", "All Fields");

         return panel;
     }

     private static void deleteFromHistory(int deletionId) throws SQLException {
         String sql = "DELETE FROM deleted_shipments WHERE deletion_id = ?";
         try (PreparedStatement stmt = connection.prepareStatement(sql)) {
             stmt.setInt(1, deletionId);
             stmt.executeUpdate();
         }
     }

     private static void loadDeletionHistory(DefaultTableModel model, String searchText, String searchType) {
         try {
             model.setRowCount(0);
             String sql = "SELECT * FROM deleted_shipments WHERE 1=1";

             // Add search conditions
             if (!searchText.isEmpty()) {
                 switch (searchType) {
                     case "Deletion ID":
                         sql += " AND deletion_id = ?";
                         break;
                     case "Shipment ID":
                         sql += " AND shipment_id = ?";
                         break;
                     case "Customer":
                         sql += " AND customer_name LIKE ?";
                         searchText = "%" + searchText + "%";
                         break;
                     case "Status":
                         sql += " AND status LIKE ?";
                         searchText = "%" + searchText + "%";
                         break;
                     default: // All Fields
                         sql += " AND (deletion_id LIKE ? OR shipment_id LIKE ? OR " +
                                 "customer_name LIKE ? OR status LIKE ?)";
                         searchText = "%" + searchText + "%";
                         break;
                 }
             }

             sql += " ORDER BY deleted_at DESC";

             try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                 // Set parameters
                 if (!searchText.isEmpty()) {
                     if (searchType.equals("All Fields")) {
                         for (int i = 1; i <= 4; i++) {
                             stmt.setString(i, searchText);
                         }
                     } else {
                         stmt.setString(1, searchText);
                     }
                 }

                 ResultSet rs = stmt.executeQuery();
                 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                 while (rs.next()) {
                     model.addRow(new Object[]{
                             rs.getInt("deletion_id"),
                             rs.getInt("shipment_id"),
                             rs.getString("customer_name"),
                             rs.getString("origin"),
                             rs.getString("destination"),
                             String.format("$%.2f", rs.getDouble("cost")),
                             rs.getString("status"),
                             dateFormat.format(rs.getTimestamp("created_at")),
                             dateFormat.format(rs.getTimestamp("deleted_at"))
                     });
                 }
             }
         } catch (SQLException ex) {
             showErrorDialog(null, "Error loading history: " + ex.getMessage());
         }
     }

     private static void deleteSelectedRecords(JTable table, DefaultTableModel model) {
         int[] selectedRows = table.getSelectedRows();
         if (selectedRows.length == 0) {
             showErrorDialog(table, "Please select records to delete");
             return;
         }

         int confirm = JOptionPane.showConfirmDialog(
                 table,
                 "Permanently delete " + selectedRows.length + " selected records?",
                 "Confirm Deletion",
                 JOptionPane.YES_NO_OPTION,
                 JOptionPane.WARNING_MESSAGE
         );

         if (confirm == JOptionPane.YES_OPTION) {
             try {
                 connection.setAutoCommit(false);

                 for (int i = selectedRows.length - 1; i >= 0; i--) {
                     int modelRow = table.convertRowIndexToModel(selectedRows[i]);
                     int deletionId = (Integer) model.getValueAt(modelRow, 0);

                     try (PreparedStatement stmt = connection.prepareStatement(
                             "DELETE FROM deleted_shipments WHERE deletion_id = ?")) {
                         stmt.setInt(1, deletionId);
                         stmt.executeUpdate();
                     }

                     model.removeRow(modelRow);
                 }

                 connection.commit();
                 JOptionPane.showMessageDialog(
                         table,
                         "Successfully deleted " + selectedRows.length + " records",
                         "Deletion Complete",
                         JOptionPane.INFORMATION_MESSAGE
                 );
             } catch (SQLException ex) {
                 try {
                     connection.rollback();
                 } catch (SQLException e) {
                     showErrorDialog(table, "Error rolling back: " + e.getMessage());
                 }
                 showErrorDialog(table, "Error deleting records: " + ex.getMessage());
             } finally {
                 try {
                     connection.setAutoCommit(true);
                 } catch (SQLException ex) {
                     showErrorDialog(table, "Error resetting auto-commit: " + ex.getMessage());
                 }
             }
         }
     }

     private static void deleteAllSearchResults(DefaultTableModel model, String searchText, String searchType) {
         if (model.getRowCount() == 0) {
             showErrorDialog(null, "No records to delete");
             return;
         }

         int confirm = JOptionPane.showConfirmDialog(
                 null,
                 "Permanently delete ALL " + model.getRowCount() + " displayed records?",
                 "Confirm Mass Deletion",
                 JOptionPane.YES_NO_OPTION,
                 JOptionPane.WARNING_MESSAGE
         );

         if (confirm == JOptionPane.YES_OPTION) {
             try {
                 connection.setAutoCommit(false);

                 // Build DELETE query based on current search
                 String sql = "DELETE FROM deleted_shipments WHERE 1=1";

                 if (!searchText.isEmpty()) {
                     switch (searchType) {
                         case "Deletion ID":
                             sql += " AND deletion_id = ?";
                             break;
                         case "Shipment ID":
                             sql += " AND shipment_id = ?";
                             break;
                         case "Customer":
                             sql += " AND customer_name LIKE ?";
                             searchText = "%" + searchText + "%";
                             break;
                         case "Status":
                             sql += " AND status LIKE ?";
                             searchText = "%" + searchText + "%";
                             break;
                         default:
                             sql += " AND (deletion_id LIKE ? OR shipment_id LIKE ? OR " +
                                     "customer_name LIKE ? OR status LIKE ?)";
                             searchText = "%" + searchText + "%";
                             break;
                     }
                 }

                 try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                     // Set parameters
                     if (!searchText.isEmpty()) {
                         if (searchType.equals("All Fields")) {
                             for (int i = 1; i <= 4; i++) {
                                 stmt.setString(i, searchText);
                             }
                         } else {
                             stmt.setString(1, searchText);
                         }
                     }

                     int deletedCount = stmt.executeUpdate();
                     connection.commit();

                     // Refresh the table
                     loadDeletionHistory(model, "", "All Fields");

                     JOptionPane.showMessageDialog(
                             null,
                             "Successfully deleted " + deletedCount + " records",
                             "Deletion Complete",
                             JOptionPane.INFORMATION_MESSAGE
                     );
                 }
             } catch (SQLException ex) {
                 try {
                     connection.rollback();
                 } catch (SQLException e) {
                     showErrorDialog(null, "Error rolling back: " + e.getMessage());
                 }
                 showErrorDialog(null, "Error deleting records: " + ex.getMessage());
             } finally {
                 try {
                     connection.setAutoCommit(true);
                 } catch (SQLException ex) {
                     showErrorDialog(null, "Error resetting auto-commit: " + ex.getMessage());
                 }
             }
         }
     }
     private static void loadDeletionHistory(DefaultTableModel model) {
         try {
             model.setRowCount(0);

             String sql = "SELECT * FROM deleted_shipments ORDER BY deleted_at DESC";
             try (Statement stmt = connection.createStatement();
                  ResultSet rs = stmt.executeQuery(sql)) {

                 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                 while (rs.next()) {
                     model.addRow(new Object[]{
                             rs.getInt("deletion_id"),
                             rs.getInt("shipment_id"),
                             rs.getString("customer_name"),
                             rs.getString("origin"),
                             rs.getString("destination"),
                             String.format("$%.2f", rs.getDouble("cost")),
                             rs.getString("status"),
                             dateFormat.format(rs.getTimestamp("created_at")),
                             dateFormat.format(rs.getTimestamp("deleted_at"))
                     });
                 }
             }
         } catch (SQLException ex) {
             showErrorDialog(null, "Error loading deletion history: " + ex.getMessage());
         }
     }
    private static void exportToCSV(JTable table) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export to CSV");
        fileChooser.setSelectedFile(new File("shipments_export_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv"));

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (PrintWriter writer = new PrintWriter(file)) {
                // Write headers
                for (int i = 0; i < table.getColumnCount(); i++) {
                    writer.print(table.getColumnName(i));
                    if (i < table.getColumnCount() - 1) {
                        writer.print(",");
                    }
                }
                writer.println();

                // Write data
                for (int row = 0; row < table.getRowCount(); row++) {
                    for (int col = 0; col < table.getColumnCount(); col++) {
                        Object value = table.getValueAt(row, col);
                        writer.print(value != null ? value.toString().replace(",", "") : "");
                        if (col < table.getColumnCount() - 1) {
                            writer.print(",");
                        }
                    }
                    writer.println();
                }

                JOptionPane.showMessageDialog(null,
                        "Data exported successfully to:\n" + file.getAbsolutePath(),
                        "Export Complete",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                showErrorDialog(null, "Error exporting to CSV: " + ex.getMessage());
            }
        }
    }

    // UI Helper methods
    private static void styleTextField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT_COLOR, 2, true),
                        BorderFactory.createEmptyBorder(4, 4, 4, 4)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        });
    }

    private static void styleButton(JButton button, Color baseColor) {
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(baseColor.darker());
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
    }

    private static void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    private static void showErrorDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}