package com.beanloaf.thoughtsandroid.handlers;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.beanloaf.thoughtsandroid.views.MainActivity;
import com.beanloaf.thoughtsandroid.R;

import kotlin.NotImplementedError;

public class CloudSettingsHandler {

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


    // register layout
    private final ConstraintLayout registerLayout;
    private final Button registerButton;
    private final Button registerBackButton;
    private final EditText registerDisplayNameInput, registerEmailInput, registerPasswordInput, registerReenterPasswordInput;


    // info layout
    private final ConstraintLayout infoLayout;
    private final Button signOutButton;
    /*  ---------  */


    public CloudSettingsHandler(final MainActivity main) {
        this.main = main;

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
        /* -------------- */


        setLayoutList();
        attachEvents();
        swapLayouts(R.id.loginRegisterLayout);

    }

    private void attachEvents() {

        // login/register layout
        loginLayoutButton.setOnClickListener(v -> swapLayouts(R.id.loginLayout));
        registerLayoutButton.setOnClickListener(v -> swapLayouts(R.id.registerLayout));


        // login layout
        loginBackButton.setOnClickListener(v -> swapLayouts(R.id.loginRegisterLayout));
        loginButton.setOnClickListener(v -> {
            main.firebaseHandler.signInUser(loginEmailInput.getText().toString(), loginPasswordInput.getText().toString());
        });

        // register layout
        registerBackButton.setOnClickListener(v -> swapLayouts(R.id.loginRegisterLayout));
        registerButton.setOnClickListener(v -> {});

        // info layout
        signOutButton.setOnClickListener(v -> {});

    }

    private void swapLayouts(final int layoutID) {
        ConstraintLayout layout = null;

        if (layoutID == R.id.loginRegisterLayout) {
            layout = loginRegisterLayout;
        } else if (layoutID == R.id.loginLayout) {
            layout = loginLayout;
        } else if (layoutID == R.id.registerLayout) {
            layout = registerLayout;
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
