package com.beanloaf.thoughtsandroid.views;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
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

import com.beanloaf.thoughtsandroid.R;
import com.beanloaf.thoughtsandroid.database.FirebaseHandler;
import com.beanloaf.thoughtsandroid.handlers.NotificationHandler;
import com.beanloaf.thoughtsandroid.handlers.SettingsHandler;
import com.beanloaf.thoughtsandroid.objects.ThoughtObject;
import com.beanloaf.thoughtsandroid.objects.ThoughtUser;
import com.beanloaf.thoughtsandroid.res.TC;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;


public class MainActivity extends AppCompatActivity implements PropertyChangeListener {


    public ThoughtObject selectedFile;


    private ConstraintLayout textViewLayout; // visible on startup
    private ConstraintLayout listViewLayout;
    private ConstraintLayout settingsLayout;
    private ConstraintLayout[] layoutList;

    private void addLayoutToList() {
        layoutList = new ConstraintLayout[]{textViewLayout, listViewLayout, settingsLayout};
    }


    public Button listButton; // toggles between text view and list view


    private Button sortButton, newButton, deleteButton, doneButton;
    private Button prevButton, nextButton;
    private CheckBox lockTitleCheckBox, lockTagCheckBox;

    private TableRow buttonContainer;

    private TextView titleGhostText, tagGhostText, bodyGhostText;

    private EditText titleTextField, tagTextField, bodyTextField;
    private TextView dateText;


    public ListView listView;
    public SettingsHandler settings;
    public NotificationHandler notificationHandler;
    public FirebaseHandler firebaseHandler;

    public PropertyChangeSupport pcs = new PropertyChangeSupport(this);


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addPropertyChangeListener(this);

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


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        final String fileDir = getFilesDir().toString();

        TC.UNSORTED_DIR = new File(fileDir + "/unsorted/");
        TC.SORTED_DIR = new File(fileDir + "/sorted/");
        TC. LOGIN_DIR = new File(fileDir + "/res/");

        TC.UNSORTED_DIR.mkdir();
        TC.SORTED_DIR.mkdir();
        TC.LOGIN_DIR.mkdir();


        findViews();
        addLayoutToList();

        attachEvents();

        swapLayouts(R.id.textViewLayout);

        listView = new ListView(this);
        settings = new SettingsHandler(this);

        notificationHandler = new NotificationHandler(this);
        firebaseHandler = new FirebaseHandler(this);

    }

    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        switch (propertyChangeEvent.getPropertyName()) {
            case TC.Properties.LOWER_KEYBOARD:
                final View current = getCurrentFocus();
                if (current != null) {
                    current.clearFocus();

                    // Lower keyboard
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(current.getWindowToken(), 0);
                }
                break;
        }

    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void firePropertyChangeEvent(final String eventName, final Object obj) {
        pcs.firePropertyChange(eventName, null, obj);
    }

    public void firePropertyChangeEvent(final String eventName) {
        pcs.firePropertyChange(eventName, null, null);
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

        buttonContainer = findViewById(R.id.buttonContainer);

        titleGhostText = findViewById(R.id.titleGhostText);
        tagGhostText = findViewById(R.id.tagGhostText);
        bodyGhostText = findViewById(R.id.bodyGhostText);

        titleTextField = findViewById(R.id.inputTitleText);
        dateText = findViewById(R.id.dateText);
        tagTextField = findViewById(R.id.inputTagText);
        bodyTextField = findViewById(R.id.inputBodyText);
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
        dateText.setText(obj.getDate().isEmpty() ? "" : ("Created on : " + obj.getDate()));
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
            swapLayouts(textViewLayout.getVisibility() == View.VISIBLE ? R.id.listViewLayout : R.id.textViewLayout);
            listView.validateItemList();
        });

        prevButton.setOnClickListener(v -> {
            // prev

        });

        nextButton.setOnClickListener(v -> {
            
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

    public void swapLayouts(final int layoutID) {
        resetFocus();

        if (layoutID == R.id.textViewLayout) {
            listButton.setText("List");

            for (final ConstraintLayout layout : layoutList) {
                layout.setVisibility(View.GONE);
            }
            textViewLayout.setVisibility(View.VISIBLE);

            prevButton.setText("<");
            prevButton.setOnClickListener(v -> {
                // TODO implement

            });


            nextButton.setText(">");
            nextButton.setOnClickListener(v -> {
                // TODO implement
            });


        } else if (layoutID == R.id.listViewLayout) {

            listButton.setText("Back");

            for (final ConstraintLayout layout : layoutList) {
                layout.setVisibility(View.GONE);
            }
            listViewLayout.setVisibility(View.VISIBLE);

            prevButton.setText("⟳");
            prevButton.setOnClickListener(v -> listView.refreshThoughtLists());

            nextButton.setText("⚙");
            nextButton.setOnClickListener(v -> swapLayouts(R.id.settingsLayout));

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