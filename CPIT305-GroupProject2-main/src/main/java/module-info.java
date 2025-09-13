module com.mycompany.cpit.project {
    
    requires javafx.controls;
    requires javafx.fxml; 
    requires de.jensd.fx.glyphs.fontawesome;
    opens com.mycompany.inventoryfx to javafx.fxml, javafx.graphics;
}
