package com.adirtka.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import model.Schedule;
import model.Subject;
import database.Database;

import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

    @FXML
    private TableView<Schedule> scheduleTable;

    @FXML
    private TableColumn<Schedule, String> daySchedule; // Колонки для расписания

    @FXML
    private TableColumn<Schedule, String> timeSchedule;

    @FXML
    private TableColumn<Schedule, String> classroomSchedule;

    @FXML
    TableColumn<Schedule, Integer> idSchedule;

    @FXML
    private TextField dayField;
    @FXML
    private TextField timeField;
    @FXML
    private TextField classroomField;


    private ObservableList<Schedule> scheduleData = FXCollections.observableArrayList();
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

        idSchedule.setCellValueFactory(new PropertyValueFactory<>("id"));
        idSchedule.setVisible(false);
        daySchedule.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
        timeSchedule.setCellValueFactory(new PropertyValueFactory<>("time"));
        classroomSchedule.setCellValueFactory(new PropertyValueFactory<>("classroom"));

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

        scheduleTable.setEditable(true);
        classroomSchedule.setCellFactory(TextFieldTableCell.forTableColumn());
        timeSchedule.setCellFactory(TextFieldTableCell.forTableColumn());
        daySchedule.setCellFactory(TextFieldTableCell.forTableColumn());

        classroomSchedule.setOnEditCommit(event -> {
            Schedule schedule = event.getRowValue();
            schedule.setClassroom(event.getNewValue());
            updateScheduleInDatabase(schedule);
        });

        timeSchedule.setOnEditCommit(event -> {
            Schedule schedule = event.getRowValue();
            schedule.setTime(event.getNewValue());
            updateScheduleInDatabase(schedule);
        });

        daySchedule.setOnEditCommit(event -> {
            Schedule schedule = event.getRowValue();
            schedule.setDayOfWeek(event.getNewValue());
            updateScheduleInDatabase(schedule);
        });


        // Добавление данных
        loadSubjectData();

        // Привязка данных к таблице
        subjectTable.setItems(subjectData);
        scheduleTable.setItems(scheduleData);

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

    private void updateScheduleInDatabase(Schedule schedule){
        String updateQuery = "UPDATE schedule SET day_of_week = ?, classroom = ?, time = ? WHERE id = ?";
        try (Connection connection = db.getConnection();
        PreparedStatement statement = connection.prepareStatement(updateQuery)){

            // Преобразование строки во время
            Time time;
            try {
                LocalTime localTime = LocalTime.parse(schedule.getTime(), DateTimeFormatter.ofPattern("HH:mm"));
                time = Time.valueOf(localTime);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Time Format");
                alert.setHeaderText("Time must be in the format HH:mm (e.g., 14:30).");
                alert.showAndWait();
                return;
            }

            statement.setString(1, schedule.getDayOfWeek());
            statement.setTime(2, time);
            statement.setString(3, schedule.getClassroom());
            statement.setInt(4, schedule.getId());
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

    @FXML
    private void showSchedule() {
        Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();
        if (selectedSubject == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Subject Selected");
            alert.setHeaderText("Please select a subject to view its schedule.");
            alert.showAndWait();
            return;
        }

        // Получаем расписание для выбранного предмета
        String query = "SELECT id, day_of_week, time, classroom FROM schedule WHERE subject_id = ?";
        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, selectedSubject.getId());
            ResultSet resultSet = statement.executeQuery();

            scheduleData.clear(); // Очищаем старое расписание

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String dayOfWeek = resultSet.getString("day_of_week");
                String time = resultSet.getString("time");
                String classroom = resultSet.getString("classroom");

                scheduleData.add(new Schedule(id, dayOfWeek, time, classroom));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addSchedule() {
        Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();
        if (selectedSubject == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Subject Selected");
            alert.setHeaderText("Please select a subject to add a schedule.");
            alert.showAndWait();
            return;
        }

        String dayOfWeek = dayField.getText().trim();
        String timeString = timeField.getText().trim();
        String classroom = classroomField.getText().trim();

        // Проверка на заполнение всех полей
        if (dayOfWeek.isEmpty() || timeString.isEmpty() || classroom.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("All fields must be filled!");
            alert.showAndWait();
            return;
        }

        // Преобразование строки во время
        Time time;
        try {
            LocalTime localTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"));
            time = Time.valueOf(localTime);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Time Format");
            alert.setHeaderText("Time must be in the format HH:mm (e.g., 14:30).");
            alert.showAndWait();
            return;
        }

        String insertQuery = "INSERT INTO schedule (subject_id, day_of_week, time, classroom) VALUES (?, ?, ?, ?)";
        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {

            statement.setInt(1, selectedSubject.getId());
            statement.setString(2, dayOfWeek);
            statement.setTime(3, time);  // Используем объект java.sql.Time
            statement.setString(4, classroom);

            statement.executeUpdate();

            // Добавляем новое расписание в список и обновляем таблицу
            showSchedule();
            clearScheduleFields();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Schedule added successfully!");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to add schedule.");
            alert.showAndWait();
        }
    }

    // Метод для очистки полей ввода
    private void clearScheduleFields() {
        dayField.clear();
        timeField.clear();
        classroomField.clear();
    }

    @FXML
    private void deleteSchedule() {
        Schedule selectedSchedule = scheduleTable.getSelectionModel().getSelectedItem();

        // Проверка на выбор строки
        if (selectedSchedule == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Schedule Selected");
            alert.setHeaderText("Please select a schedule to delete.");
            alert.showAndWait();
            return;
        }

        //String deleteQuery = "DELETE FROM schedule WHERE subject_id = ? AND day_of_week = ? AND time = ? AND classroom = ?";
        String deleteQuery = "DELETE FROM schedule WHERE id = ?";
        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteQuery)) {

            // Устанавливаем параметры запроса
            Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();
            if (selectedSubject == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Subject Selected");
                alert.setHeaderText("Please select a subject first.");
                alert.showAndWait();
                return;
            }

            statement.setInt(1, selectedSchedule.getId());

//            statement.setInt(1, selectedSubject.getId());
//            statement.setString(2, selectedSchedule.getDayOfWeek());
//            statement.setTime(3, Time.valueOf(selectedSchedule.getTime()));
//            statement.setString(4, selectedSchedule.getClassroom());

            // Выполнение удаления
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Удаление из таблицы отображения
                scheduleData.remove(selectedSchedule);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Schedule deleted successfully!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to delete schedule.");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to delete schedule.");
            alert.showAndWait();
        }
    }

}
