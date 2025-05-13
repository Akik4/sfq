module fr.coding.sfq {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires org.checkerframework.checker.qual;
    requires jakarta.interceptor;

    opens fr.coding.sfq to javafx.fxml;
    exports fr.coding.sfq;
    exports fr.coding.sfq.util;
    opens fr.coding.sfq.util to javafx.fxml;
    opens fr.coding.sfq.models to org.hibernate.orm.core;
}