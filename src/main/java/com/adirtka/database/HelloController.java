package com.adirtka.database;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class HelloController {

    String url = "jdbc:postgresql://%s:5432/JavaTimetable";
    String user = "adirtka";

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");

        Properties properties = new Properties();

        final String configFilePath = "src/main/resources/config.properties";
        try (FileInputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        } catch (IOException e){
            System.out.println("Ошибка чтения конфигурации: " + e.getMessage());
        }

        String password = properties.getProperty("db.password");
        url = String.format(url, properties.getProperty("db.address"));

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