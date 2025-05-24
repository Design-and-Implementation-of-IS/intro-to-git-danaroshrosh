package control;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import model.InventoryItem;
import model.Supplier;
import model.SupplierInventoryLink;
import model.Consts;
import model.Consts.Manipulation;
import model.Customer;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.json.simple.DeserializationException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InventoryDataControl {
    private List<InventoryItem> inventory = new ArrayList<>();
    private List<SupplierInventoryLink> links = new ArrayList<>();
    private List<Supplier> suppliers = new ArrayList<>();



    public List<InventoryItem> getAllInventory() {
        return inventory;
    }
    public List<InventoryItem> getAllInventoryItems() {
        return new ArrayList<>(inventory); 
    }
    public List<SupplierInventoryLink> getlinks() {
        return new ArrayList<>(links); 
    }
    public List<Supplier> getsuppliers() {
        return new ArrayList<>(suppliers); 
    }
    public void loadDataFromAccessDB(String dbPath) {
    	String url = "jdbc:ucanaccess://" + dbPath;
    	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        inventory.clear();
        suppliers.clear();
        links.clear();

        try (Connection conn = DriverManager.getConnection(url)) {

            // Load inventory items
            String inventoryQuery = "SELECT * FROM TblInventory";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(inventoryQuery)) {
                while (rs.next()) {
                    String serialNum = rs.getString("serialNum");
                    String itemName = rs.getString("ItemName");
                    String itemDescription = rs.getString("ItemDescription");
                    String itemCategory = rs.getString("ItemCategory");
                    String quantityAvailable = rs.getString("quantityAvailable");
                    String minimumStockRequired = rs.getString("minimumStockRequired");

                    Date expirationDate = null;
                    try {
                        String expirationStr = rs.getString("ExpirationDate");
                        if (expirationStr != null && !expirationStr.isEmpty()) {
                            expirationDate = formatter.parse(expirationStr);
                        }
                    } catch (Exception e) {
                        e.printStackTrace(); // handle parse error
                    }

                    InventoryItem item = new InventoryItem(
                        serialNum,
                        itemName,
                        itemDescription,
                        itemCategory,
                        quantityAvailable,
                        expirationDate,
                        minimumStockRequired
                    );
                    inventory.add(item);
                }
            }

         // Load suppliers
            String supplierQuery = "SELECT * FROM TblExternalSuppliers";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(supplierQuery)) {
                while (rs.next()) {
                    String supplierId = rs.getString("supplierId");
                    String supplierName = rs.getString("supplierName");

                    Date lastDeliveryDate = null;
                    try {
                        String deliveryStr = rs.getString("lastDeliveryDate");
                        if (deliveryStr != null && !deliveryStr.isEmpty()) {
                            lastDeliveryDate = formatter.parse(deliveryStr);
                        }
                    } catch (Exception e) {
                        e.printStackTrace(); // handle parse error
                    }

                    Supplier supplier = new Supplier(supplierId, supplierName, lastDeliveryDate);
                    suppliers.add(supplier);
                }
            }


            // Load supplier-inventory links
            String linkQuery = "SELECT * FROM TblSupplierInventory";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(linkQuery)) {
                while (rs.next()) {
                    SupplierInventoryLink link = new SupplierInventoryLink(
                        rs.getString("supplierId"),
                        rs.getString("serialNum")
                    );
                    links.add(link);
                }
            }

            System.out.println("Data successfully loaded from Access DB.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void importInventoryFromXML(String path) {
        try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://src/model/ex1_solution_2025_ArchiTechs.accdb")) {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
            doc.getDocumentElement().normalize();
            NodeList nl = doc.getElementsByTagName("InventoryItem");

            int errors = 0;

            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nl.item(i);

                    // Inventory fields
                    String serialNumber = el.getAttribute("serialNumber");
                    String name = el.getElementsByTagName("Name").item(0).getTextContent();
                    String description = el.getElementsByTagName("Description").item(0).getTextContent();
                    String category = el.getElementsByTagName("Category").item(0).getTextContent();
                    int quantityInStock = Integer.parseInt(el.getElementsByTagName("QuantityInStock").item(0).getTextContent());
                    String expirationDate = el.getElementsByTagName("ExpirationDate").item(0).getTextContent();
                    int minimumStockRequired = Integer.parseInt(el.getElementsByTagName("MinimumStockRequired").item(0).getTextContent());

                    // Supplier fields
                    Element supplierEl = (Element) el.getElementsByTagName("Supplier").item(0);
                    String supplierId = supplierEl.getElementsByTagName("SupplierID").item(0).getTextContent();
                    String supplierName = supplierEl.getElementsByTagName("SupplierName").item(0).getTextContent();
                    String lastDeliveryDate = supplierEl.getElementsByTagName("LastDeliveryDate").item(0).getTextContent();

                    try {
                        // Insert or update inventory item
                        String checkInvQuery = "SELECT COUNT(*) FROM TblInventory WHERE serialNum = ?";
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkInvQuery)) {
                            checkStmt.setString(1, serialNumber);
                            ResultSet rs = checkStmt.executeQuery();
                            rs.next();
                            int count = rs.getInt(1);
                            rs.close();

                            if (count > 0) {
                                String updateInv = "UPDATE TblInventory SET ItemName = ?, ItemDescription = ?, ItemCategory = ?, quantityAvailable = ?, ExpirationDate = ?, minimumStockRequired = ? WHERE serialNum = ?";
                                try (PreparedStatement stmt = conn.prepareStatement(updateInv)) {
                                    stmt.setString(1, name);
                                    stmt.setString(2, description);
                                    stmt.setString(3, category);
                                    stmt.setInt(4, quantityInStock);
                                    stmt.setString(5, expirationDate);
                                    stmt.setInt(6, minimumStockRequired);
                                    stmt.setString(7, serialNumber);
                                    stmt.executeUpdate();
                                }
                            } else {
                                String insertInv = "INSERT INTO TblInventory (serialNum, ItemName, ItemDescription, ItemCategory, quantityAvailable, ExpirationDate, minimumStockRequired) VALUES (?, ?, ?, ?, ?, ?, ?)";
                                try (PreparedStatement stmt = conn.prepareStatement(insertInv)) {
                                    stmt.setString(1, serialNumber);
                                    stmt.setString(2, name);
                                    stmt.setString(3, description);
                                    stmt.setString(4, category);
                                    stmt.setInt(5, quantityInStock);
                                    stmt.setString(6, expirationDate);
                                    stmt.setInt(7, minimumStockRequired);
                                    stmt.executeUpdate();
                                }
                            }
                        }

                        // Insert or update supplier
                        String checkSupQuery = "SELECT COUNT(*) FROM TblExternalSuppliers WHERE SupplierId = ?";
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkSupQuery)) {
                            checkStmt.setString(1, supplierId);
                            ResultSet rs = checkStmt.executeQuery();
                            rs.next();
                            int count = rs.getInt(1);
                            rs.close();

                            if (count > 0) {
                                String updateSup = "UPDATE TblExternalSuppliers SET SupplierName = ?, lastDeliveryDate = ? WHERE SupplierId = ?";
                                try (PreparedStatement stmt = conn.prepareStatement(updateSup)) {
                                    stmt.setString(1, supplierName);
                                    stmt.setString(2, lastDeliveryDate);
                                    stmt.setString(3, supplierId);
                                    stmt.executeUpdate();
                                }
                            } else {
                                String insertSup = "INSERT INTO TblSupplier (SupplierId, SupplierName, lastDeliveryDate) VALUES (?, ?, ?)";
                                try (PreparedStatement stmt = conn.prepareStatement(insertSup)) {
                                    stmt.setString(1, supplierId);
                                    stmt.setString(2, supplierName);
                                    stmt.setString(3, lastDeliveryDate);
                                    stmt.executeUpdate();
                                }
                            }
                        }

                        // Link item and supplier
                        String checkLinkQuery = "SELECT COUNT(*) FROM TblSupplierInventory WHERE supplierId = ? AND serialNum = ?";
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkLinkQuery)) {
                            checkStmt.setString(1, supplierId);
                            checkStmt.setString(2, serialNumber);
                            ResultSet rs = checkStmt.executeQuery();
                            rs.next();
                            int count = rs.getInt(1);
                            rs.close();

                            if (count == 0) {
                                String insertLink = "INSERT INTO TblSupplierInventory (supplierId, serialNum) VALUES (?, ?)";
                                try (PreparedStatement stmt = conn.prepareStatement(insertLink)) {
                                    stmt.setString(1, supplierId);
                                    stmt.setString(2, serialNumber);
                                    stmt.executeUpdate();
                                }
                            }
                        }
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                        errors++;
                    }
                }
            }

            System.out.println((errors == 0) ? "Inventory and suppliers imported successfully!" :
                    String.format("Import completed with %d errors.", errors));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    public InventoryItem getItemBySerial(String serialNum) {
        for (InventoryItem item : inventory) {
            if (item.getSerialNum().equals(serialNum)) {
                return item;
            }
        }
        return null;
    }

    



    // Utility method for getting text content from tag
    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return "";
    }
}



