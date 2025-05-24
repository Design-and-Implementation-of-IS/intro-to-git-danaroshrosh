package entity;

public class SupplierInventoryLink {
    private String supplierId;
    private String serialNum;

    public SupplierInventoryLink(String supplierId, String serialNum) {
        this.supplierId = supplierId;
        this.serialNum = serialNum;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }
}
