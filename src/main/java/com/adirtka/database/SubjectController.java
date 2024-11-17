package com.adirtka.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import model.Subject;
import database.Database;

import java.sql.*;

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

    @FXML
    private TextField nameField;

    @FXML
    private TextField teacherField;

    @FXML
    private TextField facultyField;

    private ObservableList<Subject> subjectData = FXCollections.observableArrayList();

    private Database db = new Database();

    @FXML
    private void initialize() {
        // Настройка колонок
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setVisible(false);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        teacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacher"));
        facultyColumn.setCellValueFactory(new PropertyValueFactory<>("faculty"));

        // Настройка редактирования колонок
        subjectTable.setEditable(true);
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        teacherColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        facultyColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // Обработчики сохранения изменений
        nameColumn.setOnEditCommit(event -> {
            Subject subject = event.getRowValue();
            subject.setName(event.getNewValue());
            updateSubjectInDatabase(subject);
        });

        teacherColumn.setOnEditCommit(event -> {
            Subject subject = event.getRowValue();
            subject.setTeacher(event.getNewValue());
            updateSubjectInDatabase(subject);
        });

        facultyColumn.setOnEditCommit(event -> {
            Subject subject = event.getRowValue();
            subject.setFaculty(event.getNewValue());
            updateSubjectInDatabase(subject);
        });

        // Добавление данных
        loadSubjectData();

        // Привязка данных к таблице
        subjectTable.setItems(subjectData);

    }

    private void loadTestData() {
        subjectData.add(new Subject(1, "Mathematics", "John Doe", "Science"));
        subjectData.add(new Subject(2, "Physics", "Jane Smith", "Science"));
        subjectData.add(new Subject(3, "History", "Robert Brown", "Humanities"));
    }

    private void loadSubjectData() {
        subjectData.clear();
        String query = "SELECT * FROM subjects";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(query)){

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String teacher = resultSet.getString("teacher");
                String faculty = resultSet.getString("faculty");

                // Добавляем объект в список
                subjectData.add(new Subject(id, name, teacher, faculty));
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void updateSubjectInDatabase(Subject subject) {
        String updateQuery = "UPDATE subjects SET name = ?, teacher = ?, faculty = ? WHERE id = ?";
        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {

            // Заполнение параметров запроса
            statement.setString(1, subject.getName());
            statement.setString(2, subject.getTeacher());
            statement.setString(3, subject.getFaculty());
            statement.setInt(4, subject.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addSubject() {
        String name = nameField.getText();
        String teacher = teacherField.getText();
        String faculty = facultyField.getText();

        if (name.isEmpty() || teacher.isEmpty() || faculty.isEmpty()) {
            System.out.println("Please enter all the fields");
            return;
        }

        String insertQuery = "INSERT INTO subjects (name, teacher, faculty) VALUES (?, ?, ?)";

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {

            // Заполнение параметров запроса
            statement.setString(1, name);
            statement.setString(2, teacher);
            statement.setString(3, faculty);
            statement.executeUpdate();

            // Очистка полей ввода
            nameField.clear();
            teacherField.clear();
            facultyField.clear();

            // Обновление таблицы
            loadSubjectData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteSubject(ActionEvent actionEvent) {
        Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();
        if (selectedSubject == null) {
            System.out.println("No subject selected!");
            return;
        }

        String deleteQuery = "DELETE FROM subjects WHERE id = ?";
        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteQuery)) {

            // Установить значение параметра id
            statement.setInt(1, selectedSubject.getId());
            statement.executeUpdate();

            // Удалить запись из TableView
            subjectData.remove(selectedSubject);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
