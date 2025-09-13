package com.mycompany.inventoryfx;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginScreen extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Inventory Manager - Login");

        // ===== Root container =====
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(25));
        mainLayout.setStyle("-fx-background-color: #F4F4F9;"); // light gray background

        // ===== App logo/title =====
        Text logo = new Text("Inventory Manager 🏪");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        logo.setFill(Color.web("#2C3E50"));

        // ===== Form grid (username + password) =====
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);

        // User icon + username field
        FontAwesomeIconView userIcon = new FontAwesomeIconView(FontAwesomeIcon.USER);
        userIcon.setSize("1.5em");
        userIcon.setFill(Color.GRAY);
        grid.add(userIcon, 0, 0);

        TextField txtUser = new TextField();
        txtUser.setPromptText("Username");
        txtUser.setStyle("-fx-font-size: 14px;");
        grid.add(txtUser, 1, 0);

        // Lock icon + password field
        FontAwesomeIconView passIcon = new FontAwesomeIconView(FontAwesomeIcon.LOCK);
        passIcon.setSize("1.5em");
        passIcon.setFill(Color.GRAY);
        grid.add(passIcon, 0, 1);

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Password");
        txtPass.setStyle("-fx-font-size: 14px;");
        grid.add(txtPass, 1, 1);

        // ===== Login button =====
        Button btnLogin = new Button("Login");
        btnLogin.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SIGN_IN));
        btnLogin.setMaxWidth(Double.MAX_VALUE);

        // Button styles + hover
        String defaultStyle = "-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 5;";
        String hoverStyle = "-fx-background-color: #2980B9; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 5;";
        btnLogin.setStyle(defaultStyle);
        btnLogin.setOnMouseEntered(e -> btnLogin.setStyle(hoverStyle));
        btnLogin.setOnMouseExited(e -> btnLogin.setStyle(defaultStyle));

        // Container for form + button
        VBox loginBox = new VBox(15, grid, btnLogin);
        loginBox.setAlignment(Pos.CENTER);

        // ===== Login action =====
        btnLogin.setOnAction(e -> {
            String user = txtUser.getText();
            String pass = txtPass.getText();

            if (user.equals("admin") && pass.equals("123")) {
                new MainScreen("Admin").show();
                primaryStage.close();
            } else if (user.equals("user") && pass.equals("123")) {
                new MainScreen("User").show();
                primaryStage.close();
            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid login", ButtonType.OK).showAndWait();
            }
        });

        // ===== Assemble + show =====
        mainLayout.getChildren().addAll(logo, loginBox);
        Scene scene = new Scene(mainLayout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
