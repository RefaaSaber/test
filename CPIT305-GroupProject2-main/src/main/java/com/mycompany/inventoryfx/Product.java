package com.mycompany.inventoryfx;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Product {

    // ======== Instance model (one product) ========
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleIntegerProperty quantity = new SimpleIntegerProperty();
    private final SimpleDoubleProperty price = new SimpleDoubleProperty();

    public Product(int id, String name, int quantity, double price) {
        this.id.set(id);
        this.name.set(name);
        this.quantity.set(quantity);
        this.price.set(price);
    }

    // JavaFX property accessors (used by TableView cellValueFactory)
    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    // Regular getters/setters
    public int getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public int getQuantity() {
        return quantity.get();
    }

    public double getPrice() {
        return price.get();
    }

    public void setId(int v) {
        id.set(v);
    }

    public void setName(String v) {
        name.set(v);
    }

    public void setQuantity(int v) {
        quantity.set(v);
    }

    public void setPrice(double v) {
        price.set(v);
    }

    // ======== App-wide in-memory store (shared list) ========
    private static final ObservableList<Product> PRODUCTS = FXCollections.observableArrayList(
            new Product(1, "Apple", 50, 1.5),
            new Product(2, "Banana", 20, 1.0),
            new Product(3, "Orange", 10, 2.0),
            new Product(4, "Milk", 5, 3.0)
    );

    // Low-stock threshold used by reports/dashboard
    private static int lowStockThreshold = 5;

    // Expose store + threshold
    public static ObservableList<Product> all() {
        return PRODUCTS;
    }

    public static int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public static void setLowStockThreshold(int v) {
        lowStockThreshold = Math.max(0, v);
    }

    // ======== CRUD helpers (with basic validation) ========
    public static void add(int id, String name, int qty, double price) {
        validate(id, name, qty, price);
        // prevent duplicate IDs
        if (PRODUCTS.stream().anyMatch(p -> p.getId() == id)) {
            throw new IllegalArgumentException("A product with this ID already exists.");
        }
        PRODUCTS.add(new Product(id, name, qty, price));
    }

    public static void updateById(int id, String name, int qty, double price) {
        validate(id, name, qty, price);
        Product target = PRODUCTS.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No product found with ID " + id));
        target.setName(name);
        target.setQuantity(qty);
        target.setPrice(price);
    }

    public static void deleteById(int id) {
        Product target = PRODUCTS.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
        if (target != null) {
            PRODUCTS.remove(target);
        } else {
            throw new IllegalArgumentException("No product found with ID " + id);
        }
    }

    public static void clearAll() {
        PRODUCTS.clear();
    }

    public static void resetSample() {
        PRODUCTS.setAll(
                new Product(1, "Apple", 50, 1.5),
                new Product(2, "Banana", 20, 1.0),
                new Product(3, "Orange", 10, 2.0),
                new Product(4, "Milk", 5, 3.0)
        );
    }

    // ======== Stats helpers (used by dashboard/reports) ========
    public static int totalCount() {
        return PRODUCTS.size();
    }

    public static int lowStockCount() {
        int t = lowStockThreshold;
        return (int) PRODUCTS.stream().filter(p -> p.getQuantity() <= t).count();
    }

    public static double stockValue() {
        return PRODUCTS.stream().mapToDouble(p -> p.getQuantity() * p.getPrice()).sum();
    }

    public static String newestItemName() {
        return PRODUCTS.isEmpty() ? "-" : PRODUCTS.get(PRODUCTS.size() - 1).getName();
    }

    // ======== Input validation for CRUD ========
    private static void validate(int id, String name, int qty, double price) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be > 0.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (qty < 0) {
            throw new IllegalArgumentException("Quantity must be >= 0.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price must be >= 0.");
        }
    }
}
