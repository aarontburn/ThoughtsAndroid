package com.beanloaf.thoughtsandroid;


import android.widget.Button;

public class SettingsHandler {

    private final MainActivity main;

    private final Button backButton;

    public SettingsHandler(final MainActivity main) {
        this.main = main;
        backButton = main.findViewById(R.id.settingsBackButton);

        backButton.setOnClickListener(v -> main.swapLayouts(R.id.textViewLayout));



    }


}
