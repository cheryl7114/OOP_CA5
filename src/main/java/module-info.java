module org.example.oop_ca5 {
    requires org.json;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;

    opens org.example.oop_ca5.GUI to javafx.fxml;
    opens org.example.oop_ca5.DTOs to javafx.base;
    exports org.example.oop_ca5;
}