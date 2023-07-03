package it.univr.telemedicina.controller.doctor;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.models.Pressure;
import it.univr.telemedicina.models.Therapy;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;

public class DoctorTableController implements Initializable {
    @FXML
    public TableView<Object> doctorTable;
    @FXML
    public TableColumn<Object, Integer> columnPatient;
    @FXML
    public TableColumn<Object, LocalDate> columnDate;
    @FXML
    public TableColumn<Object, String> columnCondition;
    @FXML
    public TableColumn<Object, String> columnButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // If the ArrayList for pressure table is empty, I'm here to display therapy table
        if(DoctorStatisticsSceneController.nicePressureForTable.isEmpty())
            setTableTherapy();
        else
            setTablePressure();
    }

    private void setTablePressure() {
        ObservableList<Object> pressures = FXCollections.observableArrayList();

        // Initialize value for the table
        pressures.addAll(DoctorStatisticsSceneController.nicePressureForTable);

        //Setting columns
        columnPatient.setCellValueFactory(new PropertyValueFactory<>("idPatient"));
        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnCondition.setCellValueFactory(new PropertyValueFactory<>("conditionPressure"));
        columnButton.setCellValueFactory(new PropertyValueFactory<>("button"));    // Analyze column

        //Set items in table
        doctorTable.setItems(pressures);
    }

    private void setTableTherapy() {
        ObservableList<Object> therapies = FXCollections.observableArrayList();

        // Initialize value for the table
        therapies.addAll(DoctorStatisticsSceneController.niceTherapyForTable);

        // change column Name
        columnPatient.setText("ID Patient");
        columnDate.setText("Nome Terapia");
        columnCondition.setText("Data Inizio");
        columnButton.setText("Data Fine");

        // Setting columns
        columnPatient.setCellValueFactory(new PropertyValueFactory<>("idPatient"));
        columnDate.setCellValueFactory(new PropertyValueFactory<>("therapyName"));
        columnCondition.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        columnButton.setCellValueFactory(new PropertyValueFactory<>("endDate"));    // Analyze column

        //Set items in table
        doctorTable.setItems(therapies);
    }
}
