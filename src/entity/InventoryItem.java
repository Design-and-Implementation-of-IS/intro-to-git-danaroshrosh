package entity;

import java.util.Date;
import java.util.List;

public class InventoryItem {
    private String serialNum;
    private String itemName;
    private String itemDescription;
    private String itemCategory;
    private String quantityAvailable;
    private Date expirationDate;
    private String minimumStockRequired;

    public InventoryItem(String serialNum, String itemName, String itemDescription, String itemCategory,
    		String quantityAvailable, Date expirationDate, String minimumStockRequired) {
        this.serialNum = serialNum;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemCategory = itemCategory;
        this.quantityAvailable = quantityAvailable;
        this.expirationDate = expirationDate;
        this.minimumStockRequired = minimumStockRequired;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(String quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getMinimumStockRequired() {
        return minimumStockRequired;
    }

    public void setMinimumStockRequired(String minimumStockRequired) {
        this.minimumStockRequired = minimumStockRequired;
    }
    public boolean isExpired() {
    	if(expirationDate==null)
    		return false;
        return expirationDate.before(new Date());
    }

    public boolean isLowStock(int threshold) {
        try {
            return Integer.parseInt(quantityAvailable) < threshold;
        } catch (NumberFormatException e) {
            System.err.println("Invalid quantityAvailable value: " + quantityAvailable);
            return true; 
        }
    }
    public Supplier getSupplierForItem(List<SupplierInventoryLink> links, List<Supplier> suppliers) {
        // Find the supplierId for this item's serialNum
        for (SupplierInventoryLink link : links) {
            if (link.getSerialNum().equals(this.serialNum)) {
                String supplierId = link.getSupplierId();
                // Find supplier with this supplierId
                for (Supplier supplier : suppliers) {
                    if (supplier.getSupplierId().equals(supplierId)) {
                        return supplier;
                    }
                }
            }
        }
        // If no supplier found
        return null;
    }

}
