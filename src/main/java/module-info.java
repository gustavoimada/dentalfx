module unoeste.fipp.dentalfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires org.json;
    requires io;
    requires kernel;
    requires layout;
    requires net.sf.jasperreports.core;
    requires javafx.web;
    requires javafx.graphics;


    opens unoeste.fipp.dentalfx to javafx.fxml;
    opens unoeste.fipp.dentalfx.db.entidades to javafx.fxml;
    exports unoeste.fipp.dentalfx;
    exports unoeste.fipp.dentalfx.db.entidades;
}