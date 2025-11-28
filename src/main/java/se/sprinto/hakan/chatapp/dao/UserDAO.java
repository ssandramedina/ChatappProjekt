package se.sprinto.hakan.chatapp.dao;

import se.sprinto.hakan.chatapp.model.User;

//interface för användaren med login + register

public interface UserDAO {
    //logga in med username och password
    User login(String username, String password);

    //registrera en ny user i databasen
    User register(User user);
}
