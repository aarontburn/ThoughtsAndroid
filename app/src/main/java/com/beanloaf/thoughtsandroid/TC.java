package com.beanloaf.thoughtsandroid;

import java.io.File;

public class TC {

    private TC() {
        throw new RuntimeException("This should not be instantiated.");
    }



    public static File UNSORTED_DIR;

    public static File SORTED_DIR;


    public static final String DEFAULT_TITLE = "<untitled>";

    public static final String DEFAULT_BODY = "<description>";

    public static final String DEFAULT_TAG = "<untagged>";

    public static final String DEFAULT_DATE = "<date>";

    public static final String CHANNEL_ID = "com.beanloaf.thoughtsandroid";

    public static final int NOTIFICATION_OPENER_ID = 1;






}
