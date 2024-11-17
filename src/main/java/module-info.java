module com.adirtka.database {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires com.almasb.fxgl.all;
    requires java.sql;

    opens com.adirtka.database to javafx.fxml;
    exports com.adirtka.database;

    opens model to javafx.base;
    exports model;
}