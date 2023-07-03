module com.example.telemedicina {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.controlsfx.controls;
    requires fontawesomefx;
    requires org.testng;
    requires org.junit.jupiter.api;
    requires junit;

    opens it.univr.telemedicina to javafx.fxml;
    exports it.univr.telemedicina;
    exports it.univr.telemedicina.models.users;
    opens it.univr.telemedicina.models.users to javafx.fxml;
    exports it.univr.telemedicina.controller.doctor;
    opens it.univr.telemedicina.controller.doctor to javafx.fxml;
    exports it.univr.telemedicina.controller.chat;
    exports it.univr.telemedicina.controller.patient;
    opens it.univr.telemedicina.controller.patient to javafx.fxml;
    exports it.univr.telemedicina.models;
    opens it.univr.telemedicina.models to javafx.fxml;
}