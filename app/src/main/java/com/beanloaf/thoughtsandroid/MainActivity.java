package com.beanloaf.thoughtsandroid;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;


public class MainActivity extends AppCompatActivity {


    public ThoughtObject selectedFile;


    private ConstraintLayout textViewLayout; // visible on startup
    private ConstraintLayout listViewLayout; // gone on startup
    private ConstraintLayout settingsLayout;
    public Button listButton; // toggles between text view and list view


    private Button sortButton, newButton, deleteButton, doneButton;
    private Button prevButton, nextButton, settingsBackButton;
    private CheckBox lockTitleCheckBox, lockTagCheckBox;

    private TableRow buttonContainer;

    private TextView titleGhostText, tagGhostText, bodyGhostText;

    private EditText titleTextField, tagTextField, bodyTextField;
    private TextView dateText;
    private ListView listView;
    private SettingsHandler settings;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try { // hides the top bar by default
            getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE)).requestDismissKeyguard(this, null);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }


        showNotification("Thoughts", "Tap to open the app.");


        TC.UNSORTED_DIR = new File(getFilesDir().toString() + "/unsorted/");
        TC.SORTED_DIR = new File(getFilesDir().toString() + "/sorted/");

        findViews();

        attachEvents();

        listView = new ListView(this);
        settings = new SettingsHandler(this);



    }

    private void findViews() {

        // Layouts
        textViewLayout = findViewById(R.id.textViewLayout);
        listViewLayout = findViewById(R.id.listViewLayout);
        settingsLayout = findViewById(R.id.settingsLayout);


        // Checkboxes
        lockTitleCheckBox = findViewById(R.id.lockTitleCheckBox);
        lockTagCheckBox = findViewById(R.id.lockTagCheckBox);


        // Buttons
        listButton = findViewById(R.id.listButton);
        sortButton = findViewById(R.id.sortButton);
        newButton = findViewById(R.id.newButton);
        deleteButton = findViewById(R.id.deleteButton);
        doneButton = findViewById(R.id.doneButton);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        settingsBackButton = findViewById(R.id.settingsBackButton);

        buttonContainer = findViewById(R.id.buttonContainer);

        titleGhostText = findViewById(R.id.titleGhostText);
        tagGhostText = findViewById(R.id.tagGhostText);
        bodyGhostText = findViewById(R.id.bodyGhostText);

        titleTextField = findViewById(R.id.inputTitleText);
        dateText = findViewById(R.id.dateText);
        tagTextField = findViewById(R.id.inputTagText);
        bodyTextField = findViewById(R.id.inputBodyText);
    }


    private void showNotification(final String title, final String message) {
        final NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = manager.getNotificationChannel(TC.CHANNEL_ID);
            if (channel == null) {
                channel = new NotificationChannel(TC.CHANNEL_ID, "Thoughts Channel", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("Thoughts");
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }


        final Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, TC.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOngoing(true)
                .setAutoCancel(false);

        builder.setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, 0));


        final NotificationManagerCompat m = NotificationManagerCompat.from(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        m.notify(TC.NOTIFICATION_OPENER_ID, builder.build());
    }


    public void setTextFields(ThoughtObject obj) {
        if (obj == null) {
            ThoughtObject defaultObj = new ThoughtObject(this, false,
                    "Thoughts",
                    "",
                    "by @beanloaf",
                    "Get started by creating or selecting a file.",
                    null);


            titleTextField.setFocusable(false);
            tagTextField.setFocusable(false);
            bodyTextField.setFocusable(false);

            obj = defaultObj;
        } else {
            titleTextField.setFocusableInTouchMode(true);
            tagTextField.setFocusableInTouchMode(true);
            bodyTextField.setFocusableInTouchMode(true);
        }


        selectedFile = obj;

        titleTextField.setText(obj.getTitle().equals(TC.DEFAULT_TITLE) ? "" : obj.getTitle());
        dateText.setText(obj.getDate());
        tagTextField.setText(obj.getTag().equals(TC.DEFAULT_TAG) ? "" : obj.getTag());
        bodyTextField.setText(obj.getBody().equals(TC.DEFAULT_BODY) ? "" : obj.getBody());

        refreshGhostText();
    }

    private void refreshGhostText() {
        titleGhostText.setVisibility(String.valueOf(titleTextField.getText()).isEmpty() ? View.VISIBLE : View.GONE);
        tagGhostText.setVisibility(String.valueOf(tagTextField.getText()).isEmpty() ? View.VISIBLE : View.GONE);
        bodyGhostText.setVisibility(String.valueOf(bodyTextField.getText()).isEmpty() ? View.VISIBLE : View.GONE);
    }


    private void attachEvents() {


        sortButton.setOnClickListener(v -> listView.sort(selectedFile));


        newButton.setOnClickListener(v -> {
            final ThoughtObject newObject = new ThoughtObject(this,
                    lockTitleCheckBox.isChecked() && selectedFile != null ? selectedFile.getTitle() : "",
                    lockTagCheckBox.isChecked() && selectedFile != null ? selectedFile.getTag() : "",
                    "");


            newObject.save();
            listView.newFile(newObject);

            resetFocus();
            setTextFields(newObject);
        });


        deleteButton.setOnClickListener(v -> {
            if (selectedFile != null) listView.delete(selectedFile);
        });


        doneButton.setOnClickListener(v -> resetFocus());


        listButton.setOnClickListener(v -> {
            swapLayouts(textViewLayout);
            listView.validateItemList();
        });

        prevButton.setOnClickListener(v -> {
            // prev

        });

        nextButton.setOnClickListener(v -> {
            // next
        });


        titleTextField.setOnFocusChangeListener((view, b) -> {
            if (selectedFile != null) {
                selectedFile.setTitle(String.valueOf(titleTextField.getText()));
                saveCurrentThoughtObject();
            }
        });

        tagTextField.setOnFocusChangeListener((view, b) -> {
            if (selectedFile != null) {
                selectedFile.setTag(String.valueOf(tagTextField.getText()));
                saveCurrentThoughtObject();

                if (selectedFile.isSorted()) listView.validateTag(selectedFile);

            }

        });

        titleTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                titleGhostText.setVisibility(String.valueOf(titleTextField.getText()).isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        tagTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tagGhostText.setVisibility(String.valueOf(tagTextField.getText()).isEmpty() ? View.VISIBLE : View.GONE);

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        bodyTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bodyGhostText.setVisibility(String.valueOf(bodyTextField.getText()).isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        bodyTextField.setOnFocusChangeListener((view, b) -> {
            final int isVisible = view.isFocused() ? View.GONE : View.VISIBLE;
            titleTextField.setVisibility(isVisible);
            tagTextField.setVisibility(isVisible);
            dateText.setVisibility(isVisible);
            buttonContainer.setVisibility(isVisible);
            doneButton.setVisibility(view.isFocused() ? View.VISIBLE : View.GONE);
            if (selectedFile != null) {
                selectedFile.setBody(String.valueOf(bodyTextField.getText()));
                saveCurrentThoughtObject();
            }

        });
    }

    private void swapLayouts(final ConstraintLayout layout) {
        resetFocus();
        final int layoutID = layout.getId();


        if (layoutID == R.id.textViewLayout) {
            listButton.setText("List");
            textViewLayout.setVisibility(View.VISIBLE);
            listViewLayout.setVisibility(View.GONE);

            prevButton.setText("<");
            prevButton.setOnClickListener(v -> {
                // prev
            });


            nextButton.setText(">");
            nextButton.setOnClickListener(v -> {
                // next
            });


        } else if (layoutID == R.id.listViewLayout){

            listButton.setText("Back");
            textViewLayout.setVisibility(View.GONE);
            listViewLayout.setVisibility(View.VISIBLE);

            prevButton.setText("⟳");
            prevButton.setOnClickListener(v -> listView.refreshThoughtLists());

            nextButton.setText("⚙");
            nextButton.setOnClickListener(v -> {
            });

        } else if (layoutID == R.id.settingsLayout) {
            settingsLayout.setVisibility(View.VISIBLE);

        }


    }

    private void resetFocus() {
        final View current = getCurrentFocus();
        if (current != null) {
            current.clearFocus();

            // Lower keyboard
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(current.getWindowToken(), 0);
        }

        saveCurrentThoughtObject();

    }

    private void saveCurrentThoughtObject() {
        if (selectedFile != null) {
            System.out.println("Saving...");
            selectedFile.save();
        }
    }


    @Override
    protected void onPause() { // when app pauses (phone screen turns off or switches off app)
        final KeyguardManager keyguardManager = ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE));
        System.out.println("pause");

        if (keyguardManager != null) {

            if (keyguardManager.isKeyguardLocked()) { // locked
//                onBackPressed();

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
//                    setShowWhenLocked(false);
//                } else {
//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//                }



            }

        }
        super.onPause();
    }

    @Override
    protected void onResume() { // when app resumes (return back to app or phone turns back on to app)
        final KeyguardManager keyguardManager = ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE));
        System.out.println("resumed");

        if (keyguardManager != null) {


            if (keyguardManager.isKeyguardLocked()) { // locked

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
//                    setShowWhenLocked(true);
//                    keyguardManager.requestDismissKeyguard(this, null);
//                } else {
//                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//                }
            }

        }


        super.onResume();
    }

    @Override
    protected void onNewIntent(final Intent intent) {

        super.onNewIntent(intent);
    }


}