package se.sprinto.hakan.chatapp.dao;

import se.sprinto.hakan.chatapp.model.User;
import se.sprinto.hakan.chatapp.database.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//klassen hanterar våra login och registreringar till SQL-databasen
public class UserDatabaseDAO implements UserDAO {

    private final DatabaseUtil db = DatabaseUtil.getInstance();

    @Override
    public User login(String username, String password) {
        //Vi lagrar username och password
        String sql = "SELECT * FROM User WHERE username = ? AND password = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) { //ser om det finns en rad
                    return new User( //skapar ett User-objekt med värderna från raden
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; //Ifall login misslyckas
    }

    @Override
    public User register(User user) {
        //vår SQL för att skapa en ny användare
        String sql = "INSERT INTO User (username, password) VALUES (?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.executeUpdate();

            //Vi hämtar det id som databasen har skapat
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) { //undersöker ifall det finns ett id
                    user.setId(keys.getInt(1)); //hämtar värde och sparar id
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user; //returnerar vår användare
    }
}