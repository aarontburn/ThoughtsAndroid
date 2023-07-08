package com.beanloaf.thoughtsandroid.views;

import android.view.View;
import android.widget.LinearLayout;


import com.beanloaf.thoughtsandroid.R;
import com.beanloaf.thoughtsandroid.objects.ListItem;
import com.beanloaf.thoughtsandroid.objects.TagListItem;
import com.beanloaf.thoughtsandroid.objects.ThoughtObject;
import com.beanloaf.thoughtsandroid.res.TC;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListView {


    private final MainActivity main;
    private final LinearLayout tagList;
    public final LinearLayout itemList;

    public final TagListItem unsortedThoughtList, sortedThoughtList;

    public final Map<String, TagListItem> thoughtListByTag = new HashMap<>();

    public TagListItem selectedTag;


    public ListView(final MainActivity main) {
        this.main = main;
        this.tagList = main.findViewById(R.id.tagList);
        this.itemList = main.findViewById(R.id.itemList);

        unsortedThoughtList = new TagListItem(main, this, "Unsorted");
        sortedThoughtList = new TagListItem(main, this, "Sorted");


        refreshThoughtLists();
        unsortedThoughtList.callOnClick();
        main.setTextFields(unsortedThoughtList.get(0));

    }


    public void refreshThoughtLists() {
        final long startTime = System.currentTimeMillis();

        File[] unsortedFiles = TC.UNSORTED_DIR.listFiles();
        File[] sortedFiles = TC.SORTED_DIR.listFiles();

        unsortedThoughtList.clear();
        sortedThoughtList.clear();
        thoughtListByTag.clear();
        tagList.removeAllViews();

        if (unsortedFiles == null) {
            TC.UNSORTED_DIR.mkdir();
            unsortedFiles = TC.UNSORTED_DIR.listFiles();
        }
        if (sortedFiles == null) {
            TC.SORTED_DIR.mkdir();
            sortedFiles = TC.SORTED_DIR.listFiles();

        }


        for (final File file : unsortedFiles) {
            final ThoughtObject content = readFileContents(file, false);

            if (content != null) {
                unsortedThoughtList.add(content);
            }
        }


        for (final File file : sortedFiles) {
            final ThoughtObject content = readFileContents(file, true);

            if (content != null) {
                sortedThoughtList.add(content);
                final String tag = content.getTag();


                TagListItem list = thoughtListByTag.get(tag);
                if (list == null) { // tag doesn't exist in list yet
                    list = new TagListItem(main, this, tag);
                    thoughtListByTag.put(tag, list);
                }
                list.add(content);
                content.setParent(list);
            }

        }
        tagList.addView(unsortedThoughtList);
        tagList.addView(sortedThoughtList);


        final List<String> set = new ArrayList<>(thoughtListByTag.keySet());
        set.sort(String.CASE_INSENSITIVE_ORDER);

        for (final String key : set) {
            tagList.addView(thoughtListByTag.get(key));
        }


        unsortedThoughtList.getList().sort(ThoughtObject::compareTo);
        sortedThoughtList.getList().sort(ThoughtObject::compareTo);


        System.out.println("Total refresh time: " + (System.currentTimeMillis() - startTime) + "ms");

    }

    public ThoughtObject readFileContents(final File filePath, final boolean isSorted) {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            final StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();

            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            final JSONObject data = new JSONObject(stringBuilder.toString());

            return new ThoughtObject(main, isSorted,
                    data.get("title").toString().trim(),
                    data.get("date").toString().trim(),
                    data.get("tag").toString().trim(),
                    data.get("body").toString().trim(),
                    filePath);

        } catch (Exception e) {
            System.err.println("Found invalid file " + filePath.getPath());
        }
        return null;
    }

    public void newFile(final ThoughtObject obj) {
        unsortedThoughtList.add(obj);

    }

    public void sort(final ThoughtObject obj) {
        if (obj == null) {
            return;
        }


        System.out.println((obj.isSorted() ? "Unsorting " : "Sorting ") + obj.getTitle());

        obj.sort();

        if (obj.isSorted()) { // sort
            unsortedThoughtList.remove(obj);
            sortedThoughtList.add(obj);

            obj.setParent(addTagToTagList(obj));


        } else { // unsort

            sortedThoughtList.remove(obj);
            unsortedThoughtList.add(obj);

            removeTagFromTagList(obj);
        }
        itemList.removeView(itemList.findViewWithTag(obj.getFile()));

    }

    public void getNext() {


    }

    private void removeTagFromTagList(final ThoughtObject obj) {
        final TagListItem list = obj.getParent();

        if (list == null) return;

        list.remove(obj);

        if (list.isEmpty()) {
            thoughtListByTag.remove(list.getTag());
            // remove tag from view
            tagList.removeView(tagList.findViewWithTag(list.getTag()));

        }
    }

    private TagListItem addTagToTagList(final ThoughtObject obj) {
        final String tag = obj.getTag();

        TagListItem list = thoughtListByTag.get(tag);

        if (list == null) { // tag doesn't exist in list yet
            list = new TagListItem(main, this, tag);
            thoughtListByTag.put(tag, list);
            int index = getAlphaIndex(list);

            tagList.addView(list, index);

        }
        list.add(obj);
        return list;
    }

    private int getAlphaIndex(final TagListItem tagListItem) {
        return binaryGetIndex(tagListItem.getTag(), 2, tagList.getChildCount() - 1);

    }

    private int binaryGetIndex(final String tag, final int left, final int right) {
        int mid = left + (right - left) / 2;

        if (left > right) {
            return right + 1;
        }


        final View m = tagList.getChildAt(mid);
        final View n = tagList.getChildAt(mid + 1);


        TagListItem midItem = null;
        TagListItem nextItem = null;

        if (m != null && m.getClass() == TagListItem.class) {
            midItem = (TagListItem) m;
        }
        if (n != null && n.getClass() == TagListItem.class) {
            nextItem = (TagListItem) n;
        }

        if (midItem == null || nextItem == null) {
            return mid + 1;
        }

        final int midToTag = tag.compareToIgnoreCase(midItem.getTag());
        final int nextToTag = tag.compareToIgnoreCase(nextItem.getTag());

//        System.out.println("Left: " + left + " Right: " + right + " Mid: " + mid);
//        System.out.println("Comparing " + tag + " to " + midItem.getTag() + " and " + nextItem.getTag());
//        System.out.println(tag + " : " + midItem.getTag() + ": " + midToTag + " | " + tag + " : " + nextItem.getTag() + ": " + nextToTag);


        if (midToTag < 0) {
            return binaryGetIndex(tag, left, mid - 1);

        } else {
            if (nextToTag < 0) {
                return mid + 1;
            } else {
                return binaryGetIndex(tag, mid + 1, right);
            }
        }


    }


    public void delete(final ThoughtObject obj) {
        System.out.println("Deleting " + obj.getTitle());


        if (!obj.isSorted()) { // object is unsorted
            int index = unsortedThoughtList.indexOf(obj);

            unsortedThoughtList.remove(obj);

            if (index > 0 && index < unsortedThoughtList.size()) {
                index--;
            } else if (index >= unsortedThoughtList.size()) {
                index = unsortedThoughtList.size() - 1;
            }

            main.setTextFields(unsortedThoughtList.size() - 1 >= 0 ? unsortedThoughtList.get(index) : null);


        } else {    // object is sorted
            final TagListItem list = obj.getParent();

            if (list == null) return;

            int index = list.indexOf(obj);

            sortedThoughtList.remove(obj);
            removeTagFromTagList(obj);

            if (index > 0 && index < selectedTag.size()) {
                index--;
            } else if (index >= selectedTag.size()) {
                index = selectedTag.size() - 1;
            }

            main.setTextFields(selectedTag.size() - 1 >= 0 ? selectedTag.get(index) : null);

        }

        obj.delete();
        itemList.removeView(itemList.findViewWithTag(obj.getFile()));


    }

    public void validateTag(final ThoughtObject obj) {
        final String tag = obj.getTag();

        final TagListItem list = obj.getParent();

        if (list != null) {
            if (!list.getTag().equals(tag)) {
                // add to new list
                final TagListItem newList = addTagToTagList(obj);
                // remove from old list
                removeTagFromTagList(obj);

                obj.setParent(newList);

            }
        }

    }


    public void validateItemList() {
        for (final ThoughtObject obj : selectedTag.getList()) {
            final ListItem listItem = itemList.findViewWithTag(obj.getFile());


            if (listItem == null) {
                itemList.addView(new ListItem(main, obj));


            } else if (!String.valueOf(listItem.getText()).equals(obj.getTitle())) {
                listItem.setText(obj.getTitle());
            }

        }
    }


}
