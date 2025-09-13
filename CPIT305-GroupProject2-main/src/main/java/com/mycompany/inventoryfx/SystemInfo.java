package com.mycompany.inventoryfx;

public class SystemInfo {

    // Get the current Java version running the app
    public static String javaVersion() {
        return System.getProperty("java.version");
    }

    // Get the current JavaFX version running the app
    public static String javafxVersion() {
        return System.getProperty("javafx.version");
    }

}
