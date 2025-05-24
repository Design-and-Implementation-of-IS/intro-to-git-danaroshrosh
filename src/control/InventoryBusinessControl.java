package control;


import model.InventoryItem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryBusinessControl {

    private final InventoryDataControl dataControl;

    public InventoryBusinessControl(InventoryDataControl dataControl) {
        this.dataControl = dataControl;
    }


    public List<InventoryItem> getLowStockInventoryItems(int threshold) {
        List<InventoryItem> lowStock = new ArrayList<>();
        for (InventoryItem InventoryItem : dataControl.getAllInventory()) {
            if (InventoryItem.isLowStock(threshold)) {
                lowStock.add(InventoryItem);
            }
        }
        return lowStock;
    }

    public List<InventoryItem> getExpiredInventoryItems() {
        List<InventoryItem> expired = new ArrayList<>();
        for (InventoryItem InventoryItem : dataControl.getAllInventory()) {
            if (InventoryItem.isExpired()) {
                expired.add(InventoryItem);
            }
        }
        return expired;
    }
    public void updateInventoryItem(InventoryItem item) {
        String dbPath = "src/model/ex1_solution_2025_ArchiTechs.accdb";
        String url = "jdbc:ucanaccess://" + dbPath;

        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "UPDATE TblInventory SET ItemDescription = ?, quantityAvailable = ? WHERE serialNum = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, item.getItemDescription());
                stmt.setString(2, item.getQuantityAvailable());
                stmt.setString(3, item.getSerialNum());
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addOrUpdateInventoryItem(InventoryItem inventoryItem) {
    	dataControl.getAllInventory().removeIf(existing -> existing.getSerialNum() == inventoryItem.getSerialNum());
    	dataControl.getAllInventory().add(inventoryItem);
    }
   

}
