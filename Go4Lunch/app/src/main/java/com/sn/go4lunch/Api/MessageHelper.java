package com.sn.go4lunch.Api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.sn.go4lunch.Model.Message;
import com.sn.go4lunch.Model.User;



public class MessageHelper {

    //CREATE

    public static Task<DocumentReference> createMessageForChat(String textMessage, User userSender) {

        // Create the Message object
        Message message = new Message(textMessage, userSender);

        // Store Message to Firestore
        return ChatHelper.getChatCollection()
                .add(message);
    }

    public static Task<DocumentReference> createMessageWithImageForChat(
            String urlImage, String textMessage, User userSender) {

        // Creating Message with the URL image
        Message message = new Message(textMessage, urlImage, userSender);

        // Storing Message on Firestore
        return ChatHelper.getChatCollection()
                .add(message);
    }

    //GETTER

    public static Query getAllMessageForChat() {
        return ChatHelper.getChatCollection()
                .orderBy("dateCreated")
                .limit(50);
    }
}