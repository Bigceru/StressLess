module com.example.telemedicina {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.controlsfx.controls;

    opens it.univr.telemedicina to javafx.fxml;
    exports it.univr.telemedicina;
    exports it.univr.telemedicina.controller;
    opens it.univr.telemedicina.controller to javafx.fxml;
    exports it.univr.telemedicina.users;
    opens it.univr.telemedicina.users to javafx.fxml;
    exports it.univr.telemedicina.controller.doctor;
    opens it.univr.telemedicina.controller.doctor to javafx.fxml;
}