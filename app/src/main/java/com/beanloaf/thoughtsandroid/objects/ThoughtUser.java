package com.beanloaf.thoughtsandroid.objects;

public class ThoughtUser {

    public String displayName, localId, email, idToken, refreshToken, expiresIn;

    public boolean registered;

    public ThoughtUser(final String localId,
                       final String email,
                       final String displayName,
                       final String idToken,
                       final boolean registered,
                       final String refreshToken,
                       final String expiresIn) {
        this.displayName = displayName;
        this.localId = localId;
        this.email = email;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.registered = registered;

    }

}
