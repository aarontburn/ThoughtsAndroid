package com.beanloaf.thoughtsandroid.objects;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.beanloaf.thoughtsandroid.views.ListView;
import com.beanloaf.thoughtsandroid.views.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class TagListItem extends androidx.appcompat.widget.AppCompatButton implements Comparable<TagListItem>{

    public static final int UNSELECTED_COLOR = Color.parseColor("#5c5c5c");

    public static final int SELECTED_COLOR = Color.parseColor("#6291a6");

    public boolean isSelected;

    private String tag;

    private final List<ThoughtObject> taggedObjects = new ArrayList<>();

    private MainActivity context;

    public TagListItem(final Context context) {
        super(context);
    }

    public TagListItem(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public TagListItem(final Context context, final ListView listView, final String tag) {

        super(context);
        super.setTag(tag);
        this.context = (MainActivity) context;

        this.setText(tag);
        this.tag = tag;
        toggleColor();
        this.setTextColor(Color.parseColor("#FFFFFF"));
        this.setMinHeight(15);
        this.setMinimumHeight(0);
        this.setTransformationMethod(null);

        final ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 5, 5);

        this.setLayoutParams(params);


        this.setOnClickListener(v -> {
            for (final String key : listView.thoughtListByTag.keySet()) {
                final TagListItem item = listView.thoughtListByTag.get(key);

                if (item != null) {
                    listView.thoughtListByTag.get(key).setSelected(false);
                }

            }
            listView.unsortedThoughtList.setSelected(false);
            listView.sortedThoughtList.setSelected(false);

            this.setSelected(true);

            listView.itemList.removeAllViews();

            taggedObjects.sort(ThoughtObject::compareTo);

//            new Handler().postAtFrontOfQueue(() -> {
//                for (final ThoughtObject obj : taggedObjects) {
//                    final ListItem listItem = new ListItem(context, obj);
//                    listView.itemList.addView(listItem);
//
//                }
//            });

            for (final ThoughtObject obj : taggedObjects) {
                final ListItem listItem = new ListItem(context, obj);
                listView.itemList.addView(listItem);

            }


            listView.selectedTag = this;

        });

    }




    public void toggleColor() {
        this.setBackgroundColor(isSelected ? SELECTED_COLOR : UNSELECTED_COLOR);
    }

    public void setSelected(final boolean isSelected) {
        this.isSelected = isSelected;
        toggleColor();
    }

    public String getTag() {
        return this.tag;
    }

    @Override
    public boolean equals(final Object other) {
        if (!this.getClass().equals(other.getClass())) {
            return false;
        }

        return this.tag.equals(((TagListItem) other).getTag());
    }


    public List<ThoughtObject> getList() {
        return this.taggedObjects;
    }

    public TagListItem add(final ThoughtObject obj) {
        if (!this.taggedObjects.contains(obj)) {
            this.taggedObjects.add(obj);
        }
        return this;
    }

    public TagListItem remove(final ThoughtObject obj) {
        this.taggedObjects.remove(obj);

        return this;
    }

    public ThoughtObject get(final int index) {
        try {
            return this.taggedObjects.get(index);
        } catch (Exception e) {
            return this.taggedObjects.size() > 0 ? this.taggedObjects.get(0) : null;
        }

    }

    public int size() {
        return this.taggedObjects.size();
    }

    public boolean isEmpty() {
        return this.taggedObjects.size() == 0;
    }

    public boolean contains(final ThoughtObject obj) {
        return this.taggedObjects.contains(obj);
    }

    public TagListItem clear() {
        this.taggedObjects.clear();
        return this;
    }

    public int indexOf(final ThoughtObject obj) {
        return this.taggedObjects.indexOf(obj);
    }


    @Override
    public int compareTo(final TagListItem tagListItem) {
        return this.getTag().compareToIgnoreCase(tagListItem.getTag());
    }

}
