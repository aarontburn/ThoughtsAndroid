package com.beanloaf.thoughtsandroid.handlers;

import android.view.View;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.beanloaf.thoughtsandroid.views.MainActivity;
import com.beanloaf.thoughtsandroid.R;

public class CloudSettingsHandler {

    private final MainActivity main;

    private final ConstraintLayout loginRegisterLayout, loginLayout, registerLayout;
    private ConstraintLayout[] layoutList;

    private void setLayoutList() {
        layoutList = new ConstraintLayout[]{loginRegisterLayout, loginLayout, registerLayout};
    }


    private final Button loginLayoutButton, registerLayoutButton, loginButton;


    private final Button loginBackButton;

    public CloudSettingsHandler(final MainActivity main) {
        this.main = main;

        loginLayoutButton = main.findViewById(R.id.cloudLoginLayoutButton);
        registerLayoutButton = main.findViewById(R.id.cloudRegisterLayoutButton);

        loginRegisterLayout = main.findViewById(R.id.loginRegisterLayout);
        loginLayout = main.findViewById(R.id.loginLayout);
        registerLayout = main.findViewById(R.id.registerLayout);


        loginButton = main.findViewById(R.id.loginButton);
        loginBackButton = main.findViewById(R.id.loginBackButton);
        setLayoutList();

        attachEvents();

    }

    private void attachEvents() {


        loginLayoutButton.setOnClickListener(v -> swapLayouts(R.id.loginLayout));

        loginBackButton.setOnClickListener(v -> swapLayouts(R.id.loginRegisterLayout));
    }

    private void swapLayouts(final int layoutID) {
        ConstraintLayout layout = null;

        if (layoutID == R.id.loginRegisterLayout) {
            layout = loginRegisterLayout;
        } else if (layoutID == R.id.loginLayout) {
            layout = loginLayout;
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
