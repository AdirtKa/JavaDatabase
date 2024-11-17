package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import model.Subject;

public class Database {
    private  String url = "jdbc:postgresql://%s:5432/JavaTimetable";
    private String password = "";

    public Database(){
        loadCredentials();
    }

    public  Connection getConnection() throws SQLException {

        String USER = "adirtka";
        return DriverManager.getConnection(url, USER, password);
    }

    private void loadCredentials(){
        String configFilePath = "src/main/resources/config.properties";

        try (FileInputStream input = new FileInputStream(configFilePath)) {
            Properties properties = new Properties();
            properties.load(input);

            // Извлечение значений из конфигурации
            this.url = String.format(this.url, properties.getProperty("db.ip"));
            this.password = properties.getProperty("db.password");

        } catch (IOException e) {
            System.out.println("Ошибка чтения конфигурации: " + e.getMessage());
        }

    }


    public static void main(String[] args){
        Database db = new Database();
        try {
            Connection con = db.getConnection();
            Statement stmt = con.createStatement();
            stmt.execute("SELECT * FROM subjects");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
