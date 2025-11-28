package se.sprinto.hakan.chatapp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private final int port; //port som är kopplad till server
    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    //konstruktor
    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("Server startar på port " + port + "..."); //skriver ut att servern startar

        try (ServerSocket serverSocket = new ServerSocket(port)) { //skapar en serversocket till porten
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void broadcast(String message, ClientHandler sender) {
        System.out.println("Meddelande från " + sender.getUser().getUsername() + ": " + message);
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(sender.getUser().getUsername() + ": " + message);
            }
        }
    }

    void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println(client.getUser().getUsername() + " kopplade från.");
    }


}

