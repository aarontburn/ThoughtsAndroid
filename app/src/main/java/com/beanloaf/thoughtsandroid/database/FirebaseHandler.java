package com.beanloaf.thoughtsandroid.database;

import android.content.Context;

import com.beanloaf.thoughtsandroid.objects.ThoughtUser;
import com.beanloaf.thoughtsandroid.views.MainActivity;

public class FirebaseHandler {


    private static final String DATABASE_URL = "https://thoughts-4144a-default-rtdb.firebaseio.com/users/";


    private ThoughtUser user;

    private String apiURL;


    private final MainActivity main;




    public FirebaseHandler(final MainActivity main) {
        this.main = main;




    }

    private void registerURL() {
        try {
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null when registering URL.");
            }

            apiURL = DATABASE_URL + user.localId + ".json?auth=" + user.idToken;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean signInUser(final String email, final String password) {
        System.out.println("here");

        if (user != null) {
            System.err.println("Error: a user is already logged in! Sign out first.");
            return false;
        }

        final ThoughtUser returningUser = AuthHandler.signIn(email, password);

        if (returningUser != null) {
            user = returningUser;
            return true;
        }
        System.err.println("Error logging in user.");
        return false;

    }


    public boolean registerNewUser(final String displayName, final String email, final String password) {
        if (user != null) {
            System.err.println("Error: a user is already logged in! Sign out first.");
            return false;
        }
        final ThoughtUser newUser = AuthHandler.signUp(displayName, email, password);
        if (newUser != null) {
            user = newUser;
            return true;
        }
        System.err.println("Error registering new user.");
        return false;
    }







}
