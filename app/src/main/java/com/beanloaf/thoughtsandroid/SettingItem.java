package com.beanloaf.thoughtsandroid;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;


public class SettingItem extends RelativeLayout {


    private SwitchCompat mySwitch;

    private TextView textView;

    private String settingName;


    public SettingItem(final Context context) {
        super(context);
    }

    public SettingItem(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingItem(final Context context, final String settingName, final CompoundButton.OnCheckedChangeListener listener) {
        super(context);

        this.settingName = settingName;

        textView = new TextView(context);
        this.addView(textView);
        textView.setText(settingName);
        textView.setTextColor(ContextCompat.getColor(context, R.color.textColor));
        textView.setTextSize(20);

        final RelativeLayout.LayoutParams textLayoutParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        textLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        textView.setLayoutParams(textLayoutParams);


        mySwitch = new SwitchCompat(context);

        this.addView(mySwitch);
        final RelativeLayout.LayoutParams switchLayoutParams = (RelativeLayout.LayoutParams) mySwitch.getLayoutParams();
        switchLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mySwitch.setLayoutParams(switchLayoutParams);

        mySwitch.setOnCheckedChangeListener(listener);

        this.setPadding(0, 10, 0, 10);




    }

    public String getSettingName() {
        return this.settingName;
    }

    public boolean isChecked() {
        return mySwitch.isChecked();
    }






}
