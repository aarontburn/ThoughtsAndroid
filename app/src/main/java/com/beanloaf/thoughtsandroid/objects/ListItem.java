package com.beanloaf.thoughtsandroid.objects;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.beanloaf.thoughtsandroid.views.MainActivity;

public class ListItem extends androidx.appcompat.widget.AppCompatButton {

    public ThoughtObject thoughtObject;

    public ListItem(final Context context) {
        super(context);
    }

    public ListItem(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ListItem(final Context context, final ThoughtObject thoughtObject) {
        super(context);
        super.setTag(thoughtObject.getFile());

        this.thoughtObject = thoughtObject;

        this.setText(thoughtObject.getTitle());
        this.setBackgroundColor(Color.parseColor("#707070"));
        this.setTextColor(Color.parseColor("#FFFFFF"));

        this.setMinHeight(15);
        this.setMinimumHeight(0);
        this.setTransformationMethod(null);


        final ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 5, 5);

        this.setLayoutParams(params);

        this.setOnClickListener(v -> {
            ((MainActivity) context).listButton.callOnClick(); // toggles back to main screen
            ((MainActivity) context).setTextFields(thoughtObject);
        });

    }



}
