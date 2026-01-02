module dev.hmap {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.commons.net;
    requires org.apache.commons.csv;

    // requires com.zaxxer.hikari;
    requires static lombok;

    requires org.pcap4j.core;
    requires java.sql;
    requires org.hibernate.orm.core;

    requires jakarta.persistence;
    requires jakarta.transaction;

    opens dev.hmap to javafx.fxml;

    exports dev.hmap;
    exports dev.hmap.controller;
    opens dev.hmap.controller to javafx.fxml;
    opens dev.hmap.model to org.hibernate.orm.core;

}