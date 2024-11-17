package com.adirtka.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Subject;

public class SubjectController {

    @FXML
    private TableView<Subject> subjectTable;

    @FXML
    private TableColumn<Subject, Integer> idColumn;

    @FXML
    private TableColumn<Subject, String> nameColumn;

    @FXML
    private TableColumn<Subject, String> teacherColumn;

    @FXML
    private TableColumn<Subject, String> facultyColumn;

    private ObservableList<Subject> subjectData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Настройка колонок
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        teacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacher"));
        facultyColumn.setCellValueFactory(new PropertyValueFactory<>("faculty"));

        // Добавление данных
        loadTestData();

        // Привязка данных к таблице
        subjectTable.setItems(subjectData);
    }

    private void loadTestData() {
        subjectData.add(new Subject(1, "Mathematics", "John Doe", "Science"));
        subjectData.add(new Subject(2, "Physics", "Jane Smith", "Science"));
        subjectData.add(new Subject(3, "History", "Robert Brown", "Humanities"));
    }
}
