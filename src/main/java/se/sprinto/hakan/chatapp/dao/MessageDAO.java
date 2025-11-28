package se.sprinto.hakan.chatapp.dao;

import se.sprinto.hakan.chatapp.model.Message;

import java.util.List;

public interface MessageDAO {
    void saveMessage(Message message);
    //För att spara meddelanden

    //Här vill vi spara användarens meddelanden i en lista
    List<Message> getMessagesByUserId(int userId);
}
