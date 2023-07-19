package com.beanloaf.thoughtsandroid.handlers;

import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.beanloaf.thoughtsandroid.objects.ThoughtUser;
import com.beanloaf.thoughtsandroid.res.TC;
import com.beanloaf.thoughtsandroid.views.MainActivity;
import com.beanloaf.thoughtsandroid.R;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class CloudSettingsHandler implements PropertyChangeListener {

    private final MainActivity main;

    private ConstraintLayout[] layoutList;

    private void setLayoutList() {
        layoutList = new ConstraintLayout[]{
                loginRegisterLayout,
                loginLayout,
                registerLayout,
                infoLayout
        };
    }

    /*  Layout lists and components  */

    // login/register layout
    private final ConstraintLayout loginRegisterLayout;
    private final Button loginLayoutButton, registerLayoutButton;


    // login layout
    private final ConstraintLayout loginLayout;
    private final Button loginButton;
    private final Button loginBackButton;
    private final EditText loginEmailInput, loginPasswordInput;
    private final CheckBox loginShowPasswordCheckBox;


    // register layout
    private final ConstraintLayout registerLayout;
    private final Button registerButton;
    private final Button registerBackButton;
    private final EditText registerDisplayNameInput, registerEmailInput, registerPasswordInput, registerReenterPasswordInput;


    // info layout
    private final ConstraintLayout infoLayout;
    private final TextView infoDisplayName, infoUserID, infoUserEmail;
    private final Button signOutButton;
    /*  ---------  */

    private ThoughtUser user;


    public CloudSettingsHandler(final MainActivity main) {
        this.main = main;
        main.addPropertyChangeListener(this);
        /*  LAYOUTS  */
        // login register layout
        loginRegisterLayout = main.findViewById(R.id.loginRegisterLayout);
        loginLayoutButton = main.findViewById(R.id.cloudLoginLayoutButton);
        registerLayoutButton = main.findViewById(R.id.cloudRegisterLayoutButton);

        // login layout
        loginLayout = main.findViewById(R.id.loginLayout);
        loginButton = main.findViewById(R.id.loginButton);
        loginBackButton = main.findViewById(R.id.loginBackButton);
        loginEmailInput = main.findViewById(R.id.loginEmailInput);
        loginPasswordInput = main.findViewById(R.id.loginPasswordInput);
        loginShowPasswordCheckBox = main.findViewById(R.id.loginShowPasswordCheckBox);

        // register layout
        registerLayout = main.findViewById(R.id.registerLayout);
        registerButton = main.findViewById(R.id.registerButton);
        registerBackButton = main.findViewById(R.id.registerBackButton);
        registerDisplayNameInput = main.findViewById(R.id.registerDisplayNameInput);
        registerEmailInput = main.findViewById(R.id.registerEmailInput);
        registerPasswordInput = main.findViewById(R.id.registerPasswordInput);
        registerReenterPasswordInput = main.findViewById(R.id.registerReenterPasswordInput);

        // info layout
        infoLayout = main.findViewById(R.id.infoLayout);
        signOutButton = main.findViewById(R.id.signOutButton);
        infoDisplayName = main.findViewById(R.id.infoDisplayName);
        infoUserID = main.findViewById(R.id.infoUserID);
        infoUserEmail = main.findViewById(R.id.infoUserEmail);
        /* -------------- */


        setLayoutList();
        attachEvents();
        swapLayouts(R.id.loginRegisterLayout);

    }

    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        switch (propertyChangeEvent.getPropertyName()) {
            case TC.Properties.CONNECTED_TO_DATABASE:
                main.firePropertyChangeEvent(TC.Properties.LOWER_KEYBOARD);

                user = (ThoughtUser) propertyChangeEvent.getNewValue();

                infoDisplayName.setText("Name: \n" + user.displayName);
                infoUserID.setText("User ID : \n" + user.localId);
                infoUserEmail.setText("Email : \n" + user.email);

                swapLayouts(R.id.infoLayout);
                break;
            case TC.Properties.SIGN_OUT:
                user = null;
                swapLayouts(R.id.loginRegisterLayout);
                break;
        }

    }

    public void createWindow() {
        if (user == null) {
            swapLayouts(R.id.loginRegisterLayout);
        } else {
            swapLayouts(R.id.infoLayout);
        }
    }

    private void attachEvents() {

        // login/register layout
        loginLayoutButton.setOnClickListener(v -> swapLayouts(R.id.loginLayout));
        registerLayoutButton.setOnClickListener(v -> swapLayouts(R.id.registerLayout));


        // login layout
        loginBackButton.setOnClickListener(v -> swapLayouts(R.id.loginRegisterLayout));
        loginButton.setOnClickListener(v -> {
            final String email = loginEmailInput.getText().toString();
            final String password = loginPasswordInput.getText().toString();

            if (main.firebaseHandler.signInUser(email, password)) {
                main.firebaseHandler.start();
            }
        });

        loginShowPasswordCheckBox.setOnCheckedChangeListener((b, isChecked) -> {
            loginPasswordInput.setTransformationMethod(isChecked ? null : new PasswordTransformationMethod());
        });

        // register layout
        registerBackButton.setOnClickListener(v -> swapLayouts(R.id.loginRegisterLayout));
        registerButton.setOnClickListener(v -> {

            final String name = registerDisplayNameInput.getText().toString();
            final String email = registerEmailInput.getText().toString();
            final String password = registerPasswordInput.getText().toString();
            final String reenterPassword = registerReenterPasswordInput.getText().toString();

            if (!password.equals(reenterPassword)) {
                System.err.println("Passwords do not match");
                return;
            }


            if (main.firebaseHandler.registerNewUser(name, email, password)) {
                main.firebaseHandler.start();
            }

        });

        // info layout
        signOutButton.setOnClickListener(v -> main.firePropertyChangeEvent(TC.Properties.SIGN_OUT));

    }

    private void swapLayouts(final int layoutID) {
        ConstraintLayout layout = null;

        if (layoutID == R.id.loginRegisterLayout) {
            layout = loginRegisterLayout;
        } else if (layoutID == R.id.loginLayout) {
            layout = loginLayout;
        } else if (layoutID == R.id.registerLayout) {
            layout = registerLayout;
        } else if (layoutID == R.id.infoLayout) {
            layout = infoLayout;
        }


        if (layout == null) {
            throw new RuntimeException("Layout is null");
        }

        for (final ConstraintLayout l : layoutList) {
            l.setVisibility(View.GONE);
        }

        layout.setVisibility(View.VISIBLE);

    }


}
