package com.beanloaf.thoughtsandroid;


import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

public class SettingsHandler {

    private final MainActivity main;

    private final Button backButton;

    private final LinearLayout settingsLayout;


    public Map<String, SettingItem> settingsList = new HashMap<>();


    public SettingsHandler(final MainActivity main) {
        this.main = main;
        backButton = main.findViewById(R.id.settingsBackButton);
        settingsLayout = main.findViewById(R.id.settingsList);


        backButton.setOnClickListener(v -> main.swapLayouts(R.id.listViewLayout));

        registerSettings();
    }

    private void registerSettings() {
        settingsLayout.addView(createSettingsItem(TC.Settings.QUICK_LAUNCH,
                (b, isChecked) -> main.notificationHandler.toggleQuickLaunchNotification(isChecked)));


    }

    private SettingItem createSettingsItem(final String displayText, final CompoundButton.OnCheckedChangeListener listener) {
        final SettingItem settingItem = new SettingItem(main, displayText, listener);

        if (settingsList.containsKey(displayText)) {
            throw new RuntimeException("Duplicate setting text found: " + displayText);
        }
        settingsList.put(displayText, settingItem);
        return settingItem;
    }


}
