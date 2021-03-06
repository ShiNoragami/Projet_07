package com.sn.go4lunch.utils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sn.go4lunch.models.Restaurant;
import com.sn.go4lunch.models.User;

import java.util.List;

public class UserHelper {

    // COLLECTION REFERENCE
    private static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    // CREATE
    public static void createUser(String uid, String username, String urlPicture, Restaurant restaurant,
                                  List<Restaurant> likedRestaurants, boolean isEnabled) {
        User userToCreate = new User(uid, username, urlPicture, restaurant, likedRestaurants, isEnabled);
        UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // GET
    public static Task<DocumentSnapshot> getUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Task<QuerySnapshot> getAllUsers() {
        return UserHelper.getUsersCollection().get();
    }

    // UPDATE
    public static Task<Void> addLikedRestaurant(String uid, Restaurant restaurant) {
        return UserHelper.getUsersCollection().document(uid).update("likedRestaurants",
                FieldValue.arrayUnion(restaurant));
    }

    public static Task<Void> updateChosenRestaurant(String uid, Restaurant restaurant) {
        return UserHelper.getUsersCollection().document(uid).update("chosenRestaurant", restaurant);
    }

    public static void updateNotificationChoice(String uid, boolean isEnabled) {
        UserHelper.getUsersCollection().document(uid).update("notificationsEnabled", isEnabled);
    }


    // DELETE
    public static Task<Void> removeLikedRestaurant(String uid, Restaurant restaurant) {
        return UserHelper.getUsersCollection().document(uid).update("likedRestaurants",
                FieldValue.arrayRemove(restaurant));
    }

    public static void deleteUser(String uid) {
        UserHelper.getUsersCollection().document(uid).delete();
    }
}