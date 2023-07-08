package com.beanloaf.thoughtsandroid.handlers;


import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.beanloaf.thoughtsandroid.views.MainActivity;
import com.beanloaf.thoughtsandroid.R;
import com.beanloaf.thoughtsandroid.objects.SettingItem;
import com.beanloaf.thoughtsandroid.res.TC;

import java.util.HashMap;
import java.util.Map;

public class SettingsHandler {

    private final MainActivity main;

    private final Button backButton, openCloudSettingsButton, cloudSettingsBackButton;

    private final LinearLayout settingsList;
    private final ConstraintLayout generalSettingsLayout, cloudSettingsLayout;


    public Map<String, SettingItem> settingsMap = new HashMap<>();

    private final CloudSettingsHandler cloudSettingsHandler;


    public SettingsHandler(final MainActivity main) {
        this.main = main;
        backButton = main.findViewById(R.id.settingsBackButton);
        settingsList = main.findViewById(R.id.settingsList);

        generalSettingsLayout = main.findViewById(R.id.generalSettingsLayout);
        cloudSettingsLayout = main.findViewById(R.id.cloudSettingsLayout);
        openCloudSettingsButton = main.findViewById(R.id.openCloudSettingsButton);
        cloudSettingsBackButton = main.findViewById(R.id.cloudSettingsBackButton);


        attachEvents();

        registerSettings();

        cloudSettingsHandler = new CloudSettingsHandler(main);
    }



    private void attachEvents() {
        backButton.setOnClickListener(v -> main.swapLayouts(R.id.listViewLayout));

        cloudSettingsBackButton.setOnClickListener(v -> {
            generalSettingsLayout.setVisibility(View.VISIBLE);
            cloudSettingsLayout.setVisibility(View.GONE);
        });

        openCloudSettingsButton.setOnClickListener(v -> {
            generalSettingsLayout.setVisibility(View.GONE);
            cloudSettingsLayout.setVisibility(View.VISIBLE);
        });

    }



    private void registerSettings() {
        settingsList.addView(createSettingsItem(TC.Settings.LIGHT_MODE, null));
        settingsList.addView(createSettingsItem(TC.Settings.PULL_ON_STARTUP, null));
        settingsList.addView(createSettingsItem(TC.Settings.PUSH_ON_EXIT, null));

        settingsList.addView(createSettingsItem(TC.Settings.QUICK_LAUNCH,
                (b, isChecked) -> main.notificationHandler.toggleQuickLaunchNotification(isChecked)));


    }

    private SettingItem createSettingsItem(final String displayText, final CompoundButton.OnCheckedChangeListener listener) {
        final SettingItem settingItem = new SettingItem(main, displayText, listener);

        if (settingsMap.containsKey(displayText)) {
            throw new RuntimeException("Duplicate setting text found: " + displayText);
        }

        settingsMap.put(displayText, settingItem);
        return settingItem;
    }


}
