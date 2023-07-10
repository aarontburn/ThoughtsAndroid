package com.beanloaf.thoughtsandroid.database;

import android.view.View;

import com.beanloaf.thoughtsandroid.objects.ThoughtObject;
import com.beanloaf.thoughtsandroid.objects.ThoughtUser;
import com.beanloaf.thoughtsandroid.res.TC;
import com.beanloaf.thoughtsandroid.views.MainActivity;
import com.google.common.io.BaseEncoding;

import org.apache.commons.codec.binary.Base32;
import org.json.JSONObject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseHandler implements PropertyChangeListener {


    private static final String DATABASE_URL = "https://thoughts-4144a-default-rtdb.firebaseio.com/users/";


    public ThoughtUser user;

    private String apiURL;


    private final MainActivity main;

    private boolean isOnline;

    private List<ThoughtObject> cloudThoughtsList;


    public FirebaseHandler(final MainActivity main) {
        this.main = main;
        main.addPropertyChangeListener(this);

        checkUserFile();

    }


    private void checkUserFile() {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(TC.LOGIN_DIR, "user.json")));
            final StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();

            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            final JSONObject data = new JSONObject(stringBuilder.toString());

            final String email = (String) data.get("email");
            final String password = (String) data.get("password");


            if (signInUser(email, AuthHandler.sp(password, true))) {
                start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        switch (propertyChangeEvent.getPropertyName()) {
            case TC.Properties.SIGN_OUT:
                signOut();
                break;
        }

    }


    public void start() {
        if (isConnectedToDatabase()) {
            registerURL();
            refreshItems();

            main.firePropertyChangeEvent(TC.Properties.CONNECTED_TO_DATABASE, user);
            System.out.println(cloudThoughtsList);
        }

    }

    public void signOut() {
        user = null;
        isOnline = false;
        cloudThoughtsList = null;
    }

    public boolean isConnectedToDatabase() {
        return isConnectedToInternet() && user != null;

    }

    private boolean isConnectedToInternet() {
        try {
            isOnline = (Runtime.getRuntime().exec("ping -c 1 google.com").waitFor() == 0);
            return isOnline;
        } catch (Exception e) {
            return false;
        }
    }


    private void refreshItems() {
        if (!isOnline) {
            return;
        }
        cloudThoughtsList = new ArrayList<>();

        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(apiURL).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            final int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                if (responseCode == 401) {
                    System.err.println("Invalid credentials at refreshItems()");
                    return;
                } else {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + connection.getResponseCode());
                }
            }

            final BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = responseReader.readLine()) != null) {
                responseBuilder.append(line);
            }
            responseReader.close();

            final JSONObject json = new JSONObject(responseBuilder.toString());


            final Base32 b32 = new Base32();

            for (Iterator<String> it = json.keys(); it.hasNext(); ) {
                final String path = it.next();


                final String filePath = new String(b32.decode(path)).replace("_", " ") + ".json";
                final String title = new String(b32.decode((String) ((JSONObject) json.get(path)).get("Title")));
                final String tag = new String(b32.decode((String) ((JSONObject) json.get(path)).get("Tag")));
                final String date = new String(b32.decode((String) ((JSONObject) json.get(path)).get("Date")));
                final String body = new String(b32.decode(((String) ((JSONObject) json.get(path)).get("Body"))
                        .replace("\\n", "\n").replace("\\t", "\t")));
                cloudThoughtsList.add(new ThoughtObject(main, true, title, date, tag, body, new File(filePath)));
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void registerURL() {
        try {
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null when registering URL.");
            }

            apiURL = DATABASE_URL + user.localId + ".json?auth=" + user.idToken;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean pull() {
        if (cloudThoughtsList == null || cloudThoughtsList.size() == 0) {
            return false;
        }

        refreshItems();
        for (final ThoughtObject obj : cloudThoughtsList) {
            obj.save();
        }

        return true;
    }

    public boolean push() {
        if (!isConnectedToDatabase()) {
            System.out.println("Not connected to the internet!");
            return false;
        }

        new Thread(() -> {
            try {
                final File[] sortedFileDirectory = TC.SORTED_DIR.listFiles();
                for (final File file : Objects.requireNonNull(sortedFileDirectory)) {
                    final ThoughtObject tObj = this.main.listView.readFileContents(file, true);
                    if (tObj != null) {
                        addEntryIntoDatabase(tObj);
                    }
                }
                refreshItems();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return true;
    }

    private void addEntryIntoDatabase(final ThoughtObject obj) {
        if (!isConnectedToDatabase()) {
            System.out.println("Not connected to the internet!");
            return;
        }

        try {
            final String path = obj.getFile().replace(".json", "").replace(" ", "_");

            final String json = String.format("{\"%s\": { \"Body\": \"%s\", \"Date\": \"%s\", \"Tag\": \"%s\", \"Title\": \"%s\"}}",
                    BaseEncoding.base32().encode(path.getBytes()).replace("=", ""),
                    BaseEncoding.base32().encode(obj.getBody().getBytes()),
                    BaseEncoding.base32().encode(obj.getDate().getBytes()),
                    BaseEncoding.base32().encode(obj.getTag().getBytes()),
                    BaseEncoding.base32().encode(obj.getTitle().replace("\n", "\\\\n")
                            .replace("\t", "\\\\t").getBytes()));

            final HttpURLConnection connection = (HttpURLConnection) new URL(apiURL).openConnection();
            connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + user.idToken);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(json);
            writer.flush();
            writer.close();

            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Data successfully inserted to the database.");
            } else {
                System.out.println("Failed to insert data to the database. Response code: " + responseCode);
            }

            refreshItems();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public boolean signInUser(final String email, final String password) {
        if (!isConnectedToInternet()) {
            System.err.println("Not connected to the internet.");
            return false;
        }

        final ThoughtUser returningUser = AuthHandler.signIn(email, password);

        if (returningUser != null) {
            user = returningUser;

            saveLoginInformation(email, password);
            return true;
        }
        System.err.println("Error logging in user.");
        return false;

    }


    public boolean registerNewUser(final String displayName, final String email, final String password) {
        if (!isConnectedToInternet()) {
            System.err.println("Not connected to the internet.");
            return false;
        }

        final ThoughtUser newUser = AuthHandler.signUp(displayName, email, password);
        if (newUser != null) {
            user = newUser;

            saveLoginInformation(email, password);

            return true;
        }
        System.err.println("Error registering new user.");
        return false;
    }


    private void saveLoginInformation(final String email, final String password) {
        System.out.println("Saving login info");
        try (FileOutputStream fWriter = new FileOutputStream(new File(TC.LOGIN_DIR, "user.json"))) {

            final Map<String, String> textContent = new HashMap<>();

            textContent.put("email", email);
            textContent.put("password", password != null && password.isEmpty() ? password : AuthHandler.sp(password, false));

            final JSONObject objJson = new JSONObject(textContent);
            fWriter.write(objJson.toString().getBytes());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
