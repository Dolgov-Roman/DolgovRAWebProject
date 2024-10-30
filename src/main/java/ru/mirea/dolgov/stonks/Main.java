package ru.mirea.dolgov.stonks;

import ru.mirea.dolgov.stonks.StonksClient;

import java.io.IOException;
import java.time.LocalDate;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            StonksClient client = new StonksClient();
            LocalDate dateOfBirth = LocalDate.of(2004, 12, 23); // Замените на вашу дату рождения
            client.fetchAndSaveMaxCurrency(dateOfBirth);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}


