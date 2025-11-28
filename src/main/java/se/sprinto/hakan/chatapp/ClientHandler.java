package se.sprinto.hakan.chatapp;

import se.sprinto.hakan.chatapp.dao.MessageDAO;
import se.sprinto.hakan.chatapp.dao.MessageDatabaseDAO;
import se.sprinto.hakan.chatapp.dao.UserDAO;
import se.sprinto.hakan.chatapp.dao.UserDatabaseDAO;
import se.sprinto.hakan.chatapp.model.Message;
import se.sprinto.hakan.chatapp.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;

//Hanterar inlogg och registrering
public class ClientHandler implements Runnable {

    private final Socket socket; //sparar anstlitning till klient
    private final ChatServer server; //kopplad tills server
    private PrintWriter out; //skriver till klient
    private User user; //sparar en inloggad användare

    //DAO pratar med databasen
    private final UserDAO userDAO = new UserDatabaseDAO();
    private final MessageDAO messageDAO = new MessageDatabaseDAO();

    //Konstruktor
    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    //get metod, vilken användare är inloggad?
    public User getUser() {
        return user;
    }

    @Override
    public void run() {
        //läser från klienten
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             //skriver till klienten
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            this.out = writer; //sparar writer för senare användning

            writer.println("Välkommen! Har du redan ett konto? (ja/nej)");
            String answer = in.readLine();

            if ("ja".equalsIgnoreCase(answer)) {
                writer.println("Ange användarnamn:");
                String username = in.readLine();
                writer.println("Ange lösenord:");
                String password = in.readLine();

                user = userDAO.login(username, password); //kontrollerar databasen
                if (user == null) {
                    writer.println("Fel användarnamn eller lösenord.");
                    writer.println("Du måste skriva /quit nu för att avsluta denna klient");
                    writer.println("Pröva att återansluta med en ny klient");
                    return;
                }
            } else {
                writer.println("Skapa nytt konto. Ange användarnamn:");
                String username = in.readLine();
                writer.println("Ange lösenord:");
                String password = in.readLine();
                user = userDAO.register(new User(username, password));
                writer.println("Konto skapat. Välkommen, " + user.getUsername() + "!");
            }

            writer.println("Du är inloggad som: " + user.getUsername());
            writer.println("Nu kan du börja skriva meddelanden");
            writer.println("Skriv /quit för att avsluta");
            writer.println("Skriv /mymsgs för att lista alla dina meddelanden");

            System.out.println(user.getUsername() + " anslöt.");

            String message;
            while ((message = in.readLine()) != null) { //vi läser användarens svar till de avslutar
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                } else if (message.equalsIgnoreCase("/mymsgs")) {
                    List<Message> messages = messageDAO.getMessagesByUserId(user.getId());
                    if (messages.isEmpty()) {
                        out.println("Inga sparade meddelanden.");
                    } else {
                        out.println("Dina meddelanden:");
                        for (Message m : messages) {
                            out.println("[" + m.getTimestamp() + "] " + m.getText());
                        }
                    }
                } else {
                    server.broadcast(message, this);
                    messageDAO.saveMessage(new Message(user.getId(), message, LocalDateTime.now()));
                }
            }

        } catch (IOException e) {
            System.out.println("Problem med klient: " + e.getMessage());
        } finally {
            server.removeClient(this);
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void sendMessage(String msg) {
        if (out != null) out.println(msg);
    }
}