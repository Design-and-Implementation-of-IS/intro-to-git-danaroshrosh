package view;

import model.Supplier;
import control.InventoryDataControl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class SuppliersDataScreen extends JFrame {

    private InventoryDataControl control = new InventoryDataControl();
    private JTable table;
    private DefaultTableModel tableModel;

    public SuppliersDataScreen() {
        control.loadDataFromAccessDB("src/model/ex1_solution_2025_ArchiTechs.accdb");

        setTitle("DentalCare - Suppliers List");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Suppliers Overview", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setBorder(new EmptyBorder(10, 0, 10, 0));
        getContentPane().add(header, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Last Delivery Date"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Main Menu");
        backButton.setPreferredSize(new Dimension(200, 40));
        backButton.addActionListener(e -> {
            dispose();
            new MainMenu(); 
        });

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        footerPanel.add(backButton);
        getContentPane().add(footerPanel, BorderLayout.SOUTH);

        // Load data into table
        loadSupplierData();

        setVisible(true);
    }

    private void loadSupplierData() {
        tableModel.setRowCount(0); // clear table first
        List<Supplier> suppliers = control.getsuppliers();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        for (Supplier s : suppliers) {
            String formattedDate = s.getLastDeliveryDate() != null
                    ? formatter.format(s.getLastDeliveryDate())
                    : "N/A";
            tableModel.addRow(new Object[]{
                s.getSupplierId(),
                s.getSupplierName(),
                formattedDate
            });
        }
    }
}
