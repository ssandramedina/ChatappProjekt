package se.sprinto.hakan.chatapp.dao;

import se.sprinto.hakan.chatapp.model.Message;
import se.sprinto.hakan.chatapp.database.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//Vi implementerar MessageDAO
public class MessageDatabaseDAO implements MessageDAO {

    //Singleton
    private final DatabaseUtil db = DatabaseUtil.getInstance();

    @Override
    public void saveMessage(Message message) {
        //Vår SQL sats som sparar en ny rad i Message-tabellen
        String sql = "INSERT INTO Message (user_id, text, timestamp) VALUES (?, ?, ?)";

        //Här öppnar vi Connection och PreparedStatement
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, message.getUserId());
            stmt.setString(2, message.getText());
            stmt.setTimestamp(3, Timestamp.valueOf(message.getTimestamp()));

            //Vi kör SQL satsen
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace(); //skriver ut exception
        }
    }

    @Override
    public List<Message> getMessagesByUserId(int userId) {

        //En lista med de meddalnden vi kan läsa in från databasen
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM Message WHERE user_id = ?"; //Vi hämtar alla rader

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId); //Vi skickar värden till databasen

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id"); //hämtar kolumnen id
                    String text = rs.getString("text"); //Hämtar textvärde från kolumnen text.
                    LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime(); //hämtar tidsstämpel ->konverterar den till LocalDateTiime

                    Message message = new Message(userId, text, timestamp); //skapar ett Message objekt med de värden som hämtats
                    message.setId(id); //sparar id't

                    messages.add(message); //Lägger till medddelanden i listan
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages; //returnerar listan med meddelanden
    }
}