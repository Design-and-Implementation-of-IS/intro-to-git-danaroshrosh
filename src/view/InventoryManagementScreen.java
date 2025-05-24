package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import control.InventoryBusinessControl;
import control.InventoryDataControl;
import model.InventoryItem;
import model.Supplier;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.awt.event.ActionListener;

public class InventoryManagementScreen extends JFrame {
	private static final String[] ALL_COLUMNS = {
		    "SerialNumber", "Name", "Description", "Category", "Quantity", "Expiry Date", "Supplier", "Edit"
		};

		private static final String[] BASIC_COLUMNS = {
		    "SerialNumber", "Name", "Description", "Category", "Quantity", "Expiry Date", "Supplier"
		};


    private InventoryDataControl control = new InventoryDataControl();
    private InventoryBusinessControl control2 = new InventoryBusinessControl(control);

    private JTable table;
    private DefaultTableModel tableModel;

    public InventoryManagementScreen() {
        setTitle("DentalCare - Inventory Management");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout(10, 10));

        // Top Label
        JLabel header = new JLabel("DentalCare Inventory", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setBorder(new EmptyBorder(10, 0, 10, 0));
        getContentPane().add(header, BorderLayout.NORTH);

       

        table = new JTable(tableModel);
        // Table Setup
        tableModel = new DefaultTableModel(new String[]{
            "SerialNumber", "Name", "Description", "Category", "Quantity", "Expiry Date", "Supplier", "Edit"
        }, 0);
        table.setModel(tableModel);
        table.setRowHeight(25);
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton allItemsButton = new JButton("Show All Items");

        JButton lowStockButton = new JButton("Show Low Stock");
        JButton expiredButton = new JButton("Show Expired");

        lowStockButton.setPreferredSize(new Dimension(180, 40));
        expiredButton.setPreferredSize(new Dimension(180, 40));

     
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Button Actions
        lowStockButton.addActionListener(e -> showLowStockItems(10));
        expiredButton.addActionListener(e -> showExpiredItems());

        setVisible(true);
        control.loadDataFromAccessDB("src/model/ex1_solution_2025_ArchiTechs.accdb");
        allItemsButton.setPreferredSize(new Dimension(180, 40));
        allItemsButton.addActionListener(e -> showAllItems());
        buttonPanel.add(allItemsButton);
        buttonPanel.add(lowStockButton);
        buttonPanel.add(expiredButton);
        JButton backButton = new JButton("Back to Main Menu");
        backButton.setPreferredSize(new Dimension(200, 40));
        backButton.addActionListener(e -> {
            dispose();
            new MainMenu(); 
        });
        buttonPanel.add(backButton);
        JButton editButton = new JButton("Edit Selected Item");

        editButton.setPreferredSize(new Dimension(200, 40));

    
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an item to edit.");
            } else {
                editRow(selectedRow);
            }
        });

      

        buttonPanel.add(editButton);
        
       

    }
    private void editRow(int row) {
        String serialNum = (String) tableModel.getValueAt(row, 0);
        InventoryItem itemToEdit = control.getItemBySerial(serialNum);
        if (itemToEdit == null) return;

        JTextField descField = new JTextField(itemToEdit.getItemDescription());
        JTextField qtyField = new JTextField(itemToEdit.getQuantityAvailable());

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Description:"));
        panel.add(descField);
        panel.add(new JLabel("Quantity Available:"));
        panel.add(qtyField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Item",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            itemToEdit.setItemDescription(descField.getText());
            itemToEdit.setQuantityAvailable(qtyField.getText());
            control2.updateInventoryItem(itemToEdit);
            showAllItems();
        }
    }


   
    

    private void showLowStockItems(int threshold) {
        tableModel = new DefaultTableModel(BASIC_COLUMNS, 0);
        table.setModel(tableModel);

        for (InventoryItem item : control2.getLowStockInventoryItems(threshold)) {
            String supplierName = "N/A";
            Supplier supplier = item.getSupplierForItem(control.getlinks(), control.getsuppliers());
            if (supplier != null) supplierName = supplier.getSupplierName();

            tableModel.addRow(new Object[]{
                item.getSerialNum(),
                item.getItemName(),
                item.getItemDescription(),
                item.getItemCategory(),
                item.getQuantityAvailable(),
                item.getExpirationDate(),
                supplierName
            });
        }
    }


    private void showExpiredItems() {
        tableModel = new DefaultTableModel(BASIC_COLUMNS, 0);
        table.setModel(tableModel);

        for (InventoryItem item : control2.getExpiredInventoryItems()) {
            String supplierName = "N/A";
            Supplier supplier = item.getSupplierForItem(control.getlinks(), control.getsuppliers());
            if (supplier != null) supplierName = supplier.getSupplierName();

            tableModel.addRow(new Object[]{
                item.getSerialNum(),
                item.getItemName(),
                item.getItemDescription(),
                item.getItemCategory(),
                item.getQuantityAvailable(),
                item.getExpirationDate(),
                supplierName
            });
        }
    }

    private void showAllItems() {
        tableModel = new DefaultTableModel(ALL_COLUMNS, 0);
        table.setModel(tableModel);

        for (InventoryItem item : control.getAllInventory()) {
            String supplierName = "N/A";
            Supplier supplier = item.getSupplierForItem(control.getlinks(), control.getsuppliers());
            if (supplier != null) supplierName = supplier.getSupplierName();

            tableModel.addRow(new Object[]{
                item.getSerialNum(),
                item.getItemName(),
                item.getItemDescription(),
                item.getItemCategory(),
                item.getQuantityAvailable(),
                item.getExpirationDate(),
                supplierName,
                "Edit"
            });
        }

        table.getColumn("Edit").setCellRenderer(new ButtonRenderer());
        table.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox(), table));
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if ("Edit".equals(label)) {
                        editRow(selectedRow);
                    } 
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            selectedRow = row;
            return button;
        }

        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }
    }


    
}