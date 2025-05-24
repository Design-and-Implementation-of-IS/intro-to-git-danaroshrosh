package boundary;


import javax.swing.*;

import control.InventoryDataControl;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("DentalCare - Inventory System");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("DentalCare Inventory System", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JButton importButton = new JButton("Update via XML");
        JButton button3 = new JButton("Suppilers Data");
        JButton manageButton = new JButton("Manage Inventory");

        importButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button3.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        manageButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        buttonPanel.add(importButton);
        buttonPanel.add(button3);
        buttonPanel.add(manageButton);
        add(buttonPanel, BorderLayout.CENTER);

        // Action listeners
        importButton.addActionListener(this::onImportClick);
       button3.addActionListener(e -> new SuppliersDataScreen());
        manageButton.addActionListener(e -> new InventoryManagementScreen());
      

        setVisible(true);
    }
    private void onImportClick(ActionEvent e) {
        String path = "xml/from_Sapir.xml";  // Hardcoded XML path

        try {
            InventoryDataControl logic = new InventoryDataControl();
            logic.importInventoryFromXML(path);
            JOptionPane.showMessageDialog(this, "Inventory imported successfully from XML.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Import failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}
