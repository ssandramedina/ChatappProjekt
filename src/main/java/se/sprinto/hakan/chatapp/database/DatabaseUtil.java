package se.sprinto.hakan.chatapp.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.Properties;

//klassen ansvarar för våra databasinställningar
public class DatabaseUtil {

    private static DatabaseUtil instance; //statiskt fält
    private String url;
    private String user;
    private String password;
    //databasens url, user och password

    //en privat konstruktor
    private DatabaseUtil() {
        loadProperties(); //laddar våra inställningar
    }
// hämtar vår singleton-instans
    public static synchronized DatabaseUtil getInstance() {
        if (instance == null) { //om instansen är null
            instance = new DatabaseUtil(); //så skapar vi en ny instans
        }
        return instance;
    }

    //Läser våra properties
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {

            Properties props = new Properties(); //vi skapar en ny props
            props.load(input); //vi läser in data från denna props

            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");

        } catch (IOException e) {
            throw new RuntimeException("Misslyckades att läsa application.properties", e);
        }
    }

    //nu kan vi ansluta till databasen
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}