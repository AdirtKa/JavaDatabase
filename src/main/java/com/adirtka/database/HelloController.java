package com.adirtka.database;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.*;

public class HelloController {
    String url = "jdbc:postgresql://localhost:5432/JavaTimetable";
    String user = "postgres";
    String password = "admin";

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");

        try (Connection connection = DriverManager.getConnection(url, user, password)){
            Statement test = connection.createStatement();
            ResultSet answer = test.executeQuery("SELECT * FROM classes");
            answer.next();
            System.out.println(answer.getString("class_name"));
        } catch (SQLException e){
            System.out.println("Ошибка подключения: " + e.getMessage());
        }
    }
}