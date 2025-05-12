module fr.coding.sfq {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens fr.coding.sfq to javafx.fxml;
    exports fr.coding.sfq;
}