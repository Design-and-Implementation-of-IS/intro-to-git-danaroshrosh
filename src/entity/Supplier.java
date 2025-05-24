package entity;

import java.util.Date;

public class Supplier {
    private String supplierId;
    private String supplierName;
    private Date lastDeliveryDate;

    public Supplier(String supplierId, String supplierName, Date lastDeliveryDate) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.lastDeliveryDate = lastDeliveryDate;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
 

    public Date getLastDeliveryDate() {
        return lastDeliveryDate;
    }

    public void setLastDeliveryDate(Date lastDeliveryDate) {
        this.lastDeliveryDate = lastDeliveryDate;
    }
}
