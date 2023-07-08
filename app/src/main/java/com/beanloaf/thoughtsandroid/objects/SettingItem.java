package com.beanloaf.thoughtsandroid.objects;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.beanloaf.thoughtsandroid.R;


public class SettingItem extends RelativeLayout {


    private CustomSwitch mySwitch;

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
        textLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        textView.setLayoutParams(textLayoutParams);


        mySwitch = new CustomSwitch(context);
        this.addView(mySwitch);
        final RelativeLayout.LayoutParams switchLayoutParams = (RelativeLayout.LayoutParams) mySwitch.getLayoutParams();
        switchLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        switchLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
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


    private class CustomSwitch extends SwitchCompat {

        public CustomSwitch(final Context context) {
            super(context);
        }

        public CustomSwitch(final Context context, final AttributeSet attrs) {
            super(context, attrs);
        }

        public CustomSwitch(final Context context, final AttributeSet attrs, final int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public void setChecked(final boolean checked) {
            super.setChecked(checked);
            changeColor(checked);
        }

        private void changeColor(final boolean isChecked) {

            // default values, applied if switch isn't toggled
            int thumbColor = Color.argb(255, 236, 236, 236);
            int trackColor = Color.argb(255, 176, 176, 176);

            if (isChecked) {
                thumbColor = Color.argb(255, 0, 152, 217);
                trackColor = Color.argb(255, 65, 116, 137);
            }

            try {
                getThumbDrawable().setColorFilter(thumbColor, PorterDuff.Mode.MULTIPLY);
                getTrackDrawable().setColorFilter(trackColor, PorterDuff.Mode.MULTIPLY);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }


}
