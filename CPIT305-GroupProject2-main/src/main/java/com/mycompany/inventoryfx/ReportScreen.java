package com.mycompany.inventoryfx;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReportScreen {

    private TextArea txtArea; // text area to show the report

    // Show the Report window
    public void show() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Low Stock Report");

        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #FFFFFF;");

        // ===== Title =====
        Label title = new Label("Low Stock Report");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; -fx-padding: 15;");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        layout.setTop(title);

        // ===== Report Area =====
        txtArea = new TextArea();
        txtArea.setEditable(false);
        txtArea.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 13px;");
        BorderPane.setMargin(txtArea, new Insets(0, 15, 15, 15));
        layout.setCenter(txtArea);

        // ===== Load Button =====
        Button btnLoad = new Button("Load Report");
        btnLoad.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        btnLoad.setMaxWidth(Double.MAX_VALUE);

        // Style + hover effect
        String defaultStyle = "-fx-background-color: #34495E; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;";
        String hoverStyle = "-fx-background-color: #2C3E50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;";
        btnLoad.setStyle(defaultStyle);
        btnLoad.setOnMouseEntered(e -> btnLoad.setStyle(hoverStyle));
        btnLoad.setOnMouseExited(e -> btnLoad.setStyle(defaultStyle));

        // Action: generate report
        btnLoad.setOnAction(e -> loadReport());
        layout.setBottom(btnLoad);

        // ===== Show Window =====
        Scene scene = new Scene(layout, 550, 400);
        stage.setScene(scene);
        stage.showAndWait();
    }

    // Build and display the report based on Product data
    private void loadReport() {
        txtArea.clear();

        // Header
        StringBuilder sb = new StringBuilder("=== Low Stock Report (threshold "
                + Product.getLowStockThreshold() + ") ===\n\n");

        // List products that are at or below threshold
        Product.all().stream()
                .filter(p -> p.getQuantity() <= Product.getLowStockThreshold())
                .forEach(p -> sb.append(String.format(
                "ID: %d | Name: %s | Qty: %d | Price: %.2f%n",
                p.getId(), p.getName(), p.getQuantity(), p.getPrice()
        )));

        // If nothing was added, show message
        if (sb.toString().endsWith("===\n\n")) {
            sb.append("No items are low on stock.\n");
        }

        // Display in text area
        txtArea.setText(sb.toString());
    }
}
