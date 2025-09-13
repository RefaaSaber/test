package com.mycompany.inventoryfx;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class AdminScreen {

    // Form fields
    private TextField txtId, txtName, txtQty, txtPrice;

    // Show the Admin window (modal)
    public void show() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Admin Panel - Manage Products");

        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #F4F4F9;");

        // ===== Title =====
        Label title = new Label("Product Information");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(10));
        layout.setTop(title);

        // ===== Form =====
        GridPane grid = new GridPane();
        grid.setVgap(12);
        grid.setHgap(12);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Product ID:"), 0, 0);
        txtId = new TextField();
        grid.add(txtId, 1, 0);

        grid.add(new Label("Name:"), 0, 1);
        txtName = new TextField();
        grid.add(txtName, 1, 1);

        grid.add(new Label("Quantity:"), 0, 2);
        txtQty = new TextField();
        grid.add(txtQty, 1, 2);

        grid.add(new Label("Price:"), 0, 3);
        txtPrice = new TextField();
        grid.add(txtPrice, 1, 3);

        layout.setCenter(grid);

        // ===== Buttons (Add / Update / Delete) =====
        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(15));
        buttonBar.setStyle("-fx-background-color: #ECF0F1;");

        Button btnAdd = createStyledButton("Add", FontAwesomeIcon.PLUS, "#27AE60");
        Button btnUpdate = createStyledButton("Update", FontAwesomeIcon.PENCIL, "#2980B9");
        Button btnDelete = createStyledButton("Delete", FontAwesomeIcon.TRASH, "#C0392B");

        // Hook up actions
        btnAdd.setOnAction(e -> handleAdd());
        btnUpdate.setOnAction(e -> handleUpdate());
        btnDelete.setOnAction(e -> handleDelete());

        buttonBar.getChildren().addAll(btnAdd, btnUpdate, btnDelete);
        layout.setBottom(buttonBar);

        // ===== Show window =====
        Scene scene = new Scene(layout, 450, 350);
        stage.setScene(scene);
        stage.showAndWait();
    }

    // ===== Actions =====
    // Add product using Product static store
    private void handleAdd() {
        try {
            int id = parseInt(txtId.getText(), "ID");
            String name = requireText(txtName.getText(), "Name");
            int qty = parseInt(txtQty.getText(), "Quantity");
            double price = parseDouble(txtPrice.getText(), "Price");

            Product.add(id, name, qty, price);
            showInfo("Product added.");
            clearForm();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // Update product (by ID)
    private void handleUpdate() {
        try {
            int id = parseInt(txtId.getText(), "ID");
            String name = requireText(txtName.getText(), "Name");
            int qty = parseInt(txtQty.getText(), "Quantity");
            double price = parseDouble(txtPrice.getText(), "Price");

            Product.updateById(id, name, qty, price);
            showInfo("Product updated.");
            clearForm();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // Delete product (by ID)
    private void handleDelete() {
        try {
            int id = parseInt(txtId.getText(), "ID");
            Product.deleteById(id);
            showInfo("Product deleted.");
            clearForm();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // ===== Small helpers =====
    // Parse non-negative int with field name for error messages
    private int parseInt(String s, String field) {
        try {
            int v = Integer.parseInt(s.trim());
            if (v < 0) {
                throw new NumberFormatException();
            }
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(field + " must be a non-negative integer.");
        }
    }

    // Parse non-negative double with field name for error messages
    private double parseDouble(String s, String field) {
        try {
            double v = Double.parseDouble(s.trim());
            if (v < 0) {
                throw new NumberFormatException();
            }
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(field + " must be a non-negative number.");
        }
    }

    // Require non-empty trimmed text
    private String requireText(String s, String field) {
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty.");
        }
        return s.trim();
    }

    // Clear all form fields
    private void clearForm() {
        txtId.clear();
        txtName.clear();
        txtQty.clear();
        txtPrice.clear();
    }

    // Make a colored button with an icon + hover darken
    private Button createStyledButton(String text, FontAwesomeIcon icon, String color) {
        Button button = new Button(text);
        button.setGraphic(new FontAwesomeIconView(icon, "1.2em"));
        String defaultStyle = "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
        button.setStyle(defaultStyle);

        button.setOnMouseEntered(e -> {
            Color baseColor = Color.web(color);
            Color darkerColor = baseColor.darker();
            String hoverStyle = String.format(
                    "-fx-background-color: #%02x%02x%02x; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;",
                    (int) (darkerColor.getRed() * 255),
                    (int) (darkerColor.getGreen() * 255),
                    (int) (darkerColor.getBlue() * 255)
            );
            button.setStyle(hoverStyle);
        });
        button.setOnMouseExited(e -> button.setStyle(defaultStyle));
        return button;
    }

    // ===== Alerts =====
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Action");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation failed");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
