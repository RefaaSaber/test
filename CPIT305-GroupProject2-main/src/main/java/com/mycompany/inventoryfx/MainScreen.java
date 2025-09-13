package com.mycompany.inventoryfx;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.FlowPane;

public class MainScreen {

    private final String role;               // current user role ("Admin" or "User")
    private BorderPane contentPane;          // center container where pages are swapped
    private TableView<Product> table;        // products table on Products/Admin pages

    // Dashboard numbers
    private Text totalTxt, lowTxt, valueTxt, newestTxt;

    // Sidebar buttons (track active for styling)
    private Button btnDashboard, btnProducts, btnReports, btnAdmin, activeBtn;

    // Sidebar button styles
    private static final String NAV_BASE
            = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;";
    private static final String NAV_ACTIVE
            = "-fx-background-color: #34495E; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8px;";

    public MainScreen(String role) {
        this.role = role;
    }

    // Launch the main window
    public void show() {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Inventory Manager");

        BorderPane mainLayout = new BorderPane();

        // Left: sidebar navigation
        VBox navBar = createNavBar();
        mainLayout.setLeft(navBar);

        // Center: page container (we swap different pages here)
        contentPane = new BorderPane();
        contentPane.setPadding(new Insets(15));
        mainLayout.setCenter(contentPane);

        // Default page: Dashboard
        contentPane.setCenter(buildDashboardPage());
        activate(btnDashboard);

        Scene scene = new Scene(mainLayout, 1000, 620);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ===== Sidebar (logo + nav buttons + logout) =====
    private VBox createNavBar() {
        VBox navBar = new VBox(20);
        navBar.setPadding(new Insets(20));
        navBar.setStyle("-fx-background-color: #2C3E50;");
        navBar.setAlignment(Pos.TOP_CENTER);

        Text logo = new Text("Inventory 🏪");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        logo.setFill(Color.WHITE);

        // Main nav buttons
        btnDashboard = createNavButton("Dashboard", FontAwesomeIcon.HOME);
        btnProducts = createNavButton("Products", FontAwesomeIcon.CUBES);
        btnReports = createNavButton("Reports", FontAwesomeIcon.CLIPBOARD);

        // Actions on click: swap center page + activate style
        btnDashboard.setOnAction(e -> {
            contentPane.setCenter(buildDashboardPage());
            activate(btnDashboard);
        });
        btnProducts.setOnAction(e -> {
            contentPane.setCenter(buildProductsPage());
            activate(btnProducts);
        });
        btnReports.setOnAction(e -> {
            contentPane.setCenter(buildReportsPage());
            activate(btnReports);
        });

        navBar.getChildren().addAll(logo, new Separator(), btnDashboard, btnProducts, btnReports);

        // Admin-only page
        if (role.equalsIgnoreCase("Admin")) {
            btnAdmin = createNavButton("Admin Panel", FontAwesomeIcon.USER_SECRET);
            btnAdmin.setOnAction(e -> {
                contentPane.setCenter(buildAdminPage());
                activate(btnAdmin);
            });
            navBar.getChildren().add(btnAdmin);
        }

        // Logout button (close main window and reopen LoginScreen)
        Button btnLogout = createNavButton("Logout", FontAwesomeIcon.SIGN_OUT);
        btnLogout.setOnAction(e -> {
            ((Stage) btnLogout.getScene().getWindow()).close();
            try {
                new LoginScreen().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        navBar.getChildren().add(new Separator());
        navBar.getChildren().add(btnLogout);

        return navBar;
    }

    // Create a styled nav button with icon + hover effect
    private Button createNavButton(String text, FontAwesomeIcon iconName) {
        Button button = new Button(text);
        FontAwesomeIconView icon = new FontAwesomeIconView(iconName);
        icon.setFill(Color.WHITE);
        icon.setSize("1.5em");

        button.setGraphic(icon);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setGraphicTextGap(15);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle(NAV_BASE);

        // Hover style only when not the active page
        button.setOnMouseEntered(e -> {
            if (button != activeBtn) {
                button.setStyle(NAV_ACTIVE);
            }
        });
        button.setOnMouseExited(e -> {
            if (button != activeBtn) {
                button.setStyle(NAV_BASE);
            }
        });

        return button;
    }

    // Mark a sidebar button as active (style)
    private void activate(Button b) {
        if (activeBtn != null) {
            activeBtn.setStyle(NAV_BASE);
        }
        b.setStyle(NAV_ACTIVE);
        activeBtn = b;
    }

    // Simple page header
    private HBox pageHeader(String title) {
        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        HBox box = new HBox(lbl);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 0, 10, 0));
        return box;
    }

    // ===== Dashboard Page (cards only) =====
    private BorderPane buildDashboardPage() {
        BorderPane pane = new BorderPane();
        pane.setTop(new VBox(pageHeader("Dashboard")));

        FlowPane statsBar = createStatsBar();
        pane.setCenter(statsBar);

        refreshStats(); // fill numbers from Product store
        return pane;
    }

    // Create 4 stat cards row
    private FlowPane createStatsBar() {
        FlowPane stats = new FlowPane(20, 20);
        stats.setPadding(new Insets(10, 0, 20, 0));
        stats.setAlignment(Pos.CENTER_LEFT);

        totalTxt = new Text("-");
        lowTxt = new Text("-");
        valueTxt = new Text("-");
        newestTxt = new Text("-");

        stats.getChildren().addAll(
                makeCard("Total Products", totalTxt, FontAwesomeIcon.CUBES, "#1E88E5", "#1565C0"),
                makeCard("Low Stock", lowTxt, FontAwesomeIcon.EXCLAMATION, "#FB8C00", "#EF6C00"),
                makeCard("Stock Value", valueTxt, FontAwesomeIcon.DOLLAR, "#2ECC71", "#27AE60"),
                makeCard("Newest Item", newestTxt, FontAwesomeIcon.STAR, "#8E44AD", "#6C3483")
        );
        return stats;
    }

    // Build a single stat card
    private VBox makeCard(String title, Text valueText, FontAwesomeIcon iconName,
            String colorStart, String colorEnd) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefWidth(230);
        card.setMinHeight(120);
        card.setStyle(
                "-fx-background-radius: 16;"
                + "-fx-background-color: linear-gradient(to bottom right, " + colorStart + ", " + colorEnd + ");"
        );

        DropShadow ds = new DropShadow();
        ds.setRadius(12);
        ds.setOffsetY(2);
        ds.setColor(Color.color(0, 0, 0, 0.18));
        card.setEffect(ds);

        FontAwesomeIconView icon = new FontAwesomeIconView(iconName);
        icon.setFill(Color.WHITE);
        icon.setSize("22px");

        Text titleText = new Text(title.toUpperCase());
        titleText.setFill(Color.WHITE);
        titleText.setOpacity(0.95);
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        valueText.setFill(Color.WHITE);
        valueText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 26));

        HBox topRow = new HBox(10, icon, titleText);
        topRow.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(topRow, valueText);

        // Subtle hover animation
        card.setOnMouseEntered(e -> {
            card.setCursor(Cursor.HAND);
            card.setScaleX(1.02);
            card.setScaleY(1.02);
            card.setTranslateY(-2);
        });
        card.setOnMouseExited(e -> {
            card.setCursor(Cursor.DEFAULT);
            card.setScaleX(1.0);
            card.setScaleY(1.0);
            card.setTranslateY(0);
        });

        return card;
    }

    // Update dashboard numbers from Product store
    private void refreshStats() {
        totalTxt.setText(String.valueOf(Product.totalCount()));
        lowTxt.setText(String.valueOf(Product.lowStockCount()));
        valueTxt.setText(String.format("$%.2f", Product.stockValue()));
        newestTxt.setText(Product.newestItemName());
    }

    // ===== Products Page (table + add/delete) =====
    private BorderPane buildProductsPage() {
        BorderPane pane = new BorderPane();

        // Top actions
        Button addBtn = new Button("Add Product", new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        addBtn.setOnAction(e -> onAddProductDialog());

        Button delBtn = new Button("Delete Selected", new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        delBtn.setOnAction(e -> {
            Product sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                warn("Please select a product to delete.");
                return;
            }
            try {
                Product.deleteById(sel.getId());
                table.refresh();
                refreshStats();
            } catch (Exception ex) {
                error(ex.getMessage());
            }
        });

        HBox actions = new HBox(10, addBtn, delBtn);
        actions.setAlignment(Pos.CENTER_LEFT);

        // Table bound to the shared list
        table = buildTableBoundTo(Product.all());

        // Optional row double-click
        table.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2 && !row.isEmpty()) {
                    info("Double-clicked: " + row.getItem().getName());
                }
            });
            return row;
        });

        pane.setTop(new VBox(10, pageHeader("Products"), actions));
        pane.setCenter(new ScrollPane(table));
        return pane;
    }

    // Create a table and bind it to the given list
    private TableView<Product> buildTableBoundTo(ObservableList<Product> list) {
        TableView<Product> tv = new TableView<>();

        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cd -> cd.getValue().idProperty().asObject());

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cd -> cd.getValue().nameProperty());
        nameCol.setPrefWidth(220);

        TableColumn<Product, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(cd -> cd.getValue().quantityProperty().asObject());

        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cd -> cd.getValue().priceProperty().asObject());

        tv.getColumns().addAll(idCol, nameCol, qtyCol, priceCol);
        tv.setItems(list);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tv;
    }

    // Modal dialog to add a new product
    private void onAddProductDialog() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Add Product");
        ButtonType saveBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);
        g.setPadding(new Insets(10));

        TextField idF = new TextField();
        idF.setPromptText("e.g., 1001");
        TextField nameF = new TextField();
        nameF.setPromptText("Name");
        TextField qtyF = new TextField();
        qtyF.setPromptText("e.g., 10");
        TextField priceF = new TextField();
        priceF.setPromptText("e.g., 2.5");

        g.addRow(0, new Label("ID:"), idF);
        g.addRow(1, new Label("Name:"), nameF);
        g.addRow(2, new Label("Quantity:"), qtyF);
        g.addRow(3, new Label("Price:"), priceF);

        dialog.getDialogPane().setContent(g);

        dialog.setResultConverter(bt -> {
            if (bt == saveBtn) {
                try {
                    int id = Integer.parseInt(idF.getText().trim());
                    String name = nameF.getText().trim();
                    int qty = Integer.parseInt(qtyF.getText().trim());
                    double price = Double.parseDouble(priceF.getText().trim());
                    Product.add(id, name, qty, price);
                    return new Product(id, name, qty, price); // value not used; add() already mutated store
                } catch (Exception ex) {
                    error(ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ignored -> {
            table.refresh();
            refreshStats();
        });
    }

    // ===== Reports Page (inline text report) =====
    private BorderPane buildReportsPage() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(15));

        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPromptText("Click 'Load Report' to view low stock items...");

        Button loadBtn = new Button("Load Report", new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        loadBtn.setOnAction(e -> {
            StringBuilder sb = new StringBuilder("=== Low Stock Report (threshold "
                    + Product.getLowStockThreshold() + ") ===\n\n");
            Product.all().stream()
                    .filter(p -> p.getQuantity() <= Product.getLowStockThreshold())
                    .forEach(p -> sb.append(
                    String.format("ID: %d | Name: %s | Qty: %d | Price: %.2f%n",
                            p.getId(), p.getName(), p.getQuantity(), p.getPrice())
            ));
            if (sb.toString().endsWith("===\n\n")) {
                sb.append("No items are low on stock.\n");
            }
            reportArea.setText(sb.toString());
        });

        HBox actions = new HBox(loadBtn);
        actions.setPadding(new Insets(10, 0, 0, 0));

        pane.setTop(new VBox(10, pageHeader("Reports")));
        pane.setCenter(reportArea);
        pane.setBottom(actions);
        return pane;
    }

    // ===== Admin Page (inline management tools) =====
    private BorderPane buildAdminPage() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(15));
        pane.setTop(pageHeader("Admin Panel"));

        // Left form: add/update/delete + threshold controls
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        TextField idF = new TextField();
        idF.setPromptText("ID (e.g., 1001)");
        TextField nameF = new TextField();
        nameF.setPromptText("Name");
        TextField qtyF = new TextField();
        qtyF.setPromptText("Quantity");
        TextField priceF = new TextField();
        priceF.setPromptText("Price");

        form.addRow(0, new Label("ID:"), idF);
        form.addRow(1, new Label("Name:"), nameF);
        form.addRow(2, new Label("Quantity:"), qtyF);
        form.addRow(3, new Label("Price:"), priceF);

        Button addBtn = new Button("Add", new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        addBtn.setOnAction(e -> {
            try {
                Product.add(
                        Integer.parseInt(idF.getText().trim()),
                        nameF.getText().trim(),
                        Integer.parseInt(qtyF.getText().trim()),
                        Double.parseDouble(priceF.getText().trim())
                );
                if (table != null) {
                    table.refresh();
                }
                info("Added.");
                idF.clear();
                nameF.clear();
                qtyF.clear();
                priceF.clear();
                refreshStats();
            } catch (Exception ex) {
                error(ex.getMessage());
            }
        });

        Button updBtn = new Button("Update by ID", new FontAwesomeIconView(FontAwesomeIcon.PENCIL));
        updBtn.setOnAction(e -> {
            try {
                Product.updateById(
                        Integer.parseInt(idF.getText().trim()),
                        nameF.getText().trim(),
                        Integer.parseInt(qtyF.getText().trim()),
                        Double.parseDouble(priceF.getText().trim())
                );
                if (table != null) {
                    table.refresh();
                }
                info("Updated.");
                idF.clear();
                nameF.clear();
                qtyF.clear();
                priceF.clear();
                refreshStats();
            } catch (Exception ex) {
                error(ex.getMessage());
            }
        });

        Button delBtn = new Button("Delete by ID", new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        delBtn.setOnAction(e -> {
            try {
                Product.deleteById(Integer.parseInt(idF.getText().trim()));
                if (table != null) {
                    table.refresh();
                }
                info("Deleted.");
                idF.clear();
                nameF.clear();
                qtyF.clear();
                priceF.clear();
                refreshStats();
            } catch (Exception ex) {
                error(ex.getMessage());
            }
        });

        // Low-stock threshold control
        Spinner<Integer> thresholdSpinner = new Spinner<>(0, 1000, Product.getLowStockThreshold(), 1);
        thresholdSpinner.setEditable(true);
        Button applyThreshold = new Button("Apply Threshold");
        applyThreshold.setOnAction(e -> {
            Product.setLowStockThreshold(thresholdSpinner.getValue());
            refreshStats();
            info("Low-stock threshold set to " + Product.getLowStockThreshold());
        });

        Button resetBtn = new Button("Reset Sample Data", new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        resetBtn.setOnAction(e -> {
            Product.resetSample();
            if (table != null) {
                table.refresh();
            }
            refreshStats();
            info("Sample data restored.");
        });

        Button clearBtn = new Button("Clear All", new FontAwesomeIconView(FontAwesomeIcon.ERASER));
        clearBtn.setOnAction(e -> {
            if (confirm("Clear all products?")) {
                Product.clearAll();
                if (table != null) {
                    table.refresh();
                }
                refreshStats();
                info("All products cleared.");
            }
        });

        VBox left = new VBox(12,
                form,
                new HBox(10, addBtn, updBtn),
                new HBox(10, delBtn),
                new Separator(),
                new HBox(10, new Label("Low-Stock Threshold:"), thresholdSpinner, applyThreshold),
                new HBox(10, resetBtn, clearBtn)
        );
        left.setAlignment(Pos.TOP_LEFT);

        // Right: admin table (same shared list) with row click to fill form
        TableView<Product> adminTable = buildTableBoundTo(Product.all());
        adminTable.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (!row.isEmpty()) {
                    Product p = row.getItem();
                    idF.setText(String.valueOf(p.getId()));
                    nameF.setText(p.getName());
                    qtyF.setText(String.valueOf(p.getQuantity()));
                    priceF.setText(String.valueOf(p.getPrice()));
                }
            });
            return row;
        });

        pane.setLeft(left);
        BorderPane.setMargin(left, new Insets(0, 15, 0, 0));
        pane.setCenter(new ScrollPane(adminTable));
        return pane;
    }

    // ===== Simple alert helpers =====
    private void info(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    private void warn(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private void error(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    private boolean confirm(String msg) {
        return new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL)
                .showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
