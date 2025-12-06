module dev.hmap {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.commons.net;
    requires org.apache.commons.csv;

    opens dev.hmap to javafx.fxml;

    exports dev.hmap;
    exports dev.hmap.controllers;
    opens dev.hmap.controllers to javafx.fxml;
}