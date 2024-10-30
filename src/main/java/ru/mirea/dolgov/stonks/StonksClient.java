package ru.mirea.dolgov.stonks;




import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.mirea.dolgov.stonks.util.DoubleDeserializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;

public class StonksClient {
    private final StonksService stonksService;
    private final DatabaseService databaseService;

    public StonksClient() throws SQLException {
        Retrofit client = new Retrofit.Builder()
                .baseUrl("https://www.cbr.ru")
                .addConverterFactory(JacksonConverterFactory.create(new XmlMapper()))
                .build();

        stonksService = client.create(StonksService.class);
        databaseService = new DatabaseServiceImpl();
    }

    public void fetchAndSaveMaxCurrency(LocalDate date) throws IOException, SQLException {
        Response<DailyCurs> response = stonksService
                .getDailyCurs(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).execute();

        
        if (response.isSuccessful() && response.body() != null) {
            DailyCurs dailyCurs = response.body();

            
            if (dailyCurs.getValutes() != null && !dailyCurs.getValutes().isEmpty()) {
                Optional<Valute> maxValute = dailyCurs.getValutes().stream()
                        .filter(valute -> !valute.getName().equals("СДР (специальные права заимствования)"))
                        .max(Comparator.comparingDouble(Valute::getValue));

                if (maxValute.isPresent()) {
                    System.out.println("Max currency: " + maxValute.get());
                    databaseService.saveMaxValuteOfDate("фамилияио", maxValute.get(), date);
                } else {
                    System.out.println("No valid currency found on this date.");
                }
            } else {
                System.out.println("Valute list is null or empty. Check the server response or mapping.");
            }
        } else {
            
            System.out.println("Failed to fetch data from the server. Response code: " + response.code() + ", message: " + response.message());
        }
    }
}
