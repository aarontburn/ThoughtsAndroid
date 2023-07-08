package com.beanloaf.thoughtsandroid.handlers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.beanloaf.thoughtsandroid.views.MainActivity;
import com.beanloaf.thoughtsandroid.R;
import com.beanloaf.thoughtsandroid.res.TC;

import java.util.Objects;

public class NotificationHandler {


    private final MainActivity main;

    public NotificationHandler(final MainActivity main) {
        this.main = main;

        if (Objects.requireNonNull(main.settings.settingsMap.get(TC.Settings.QUICK_LAUNCH)).isChecked())
            showQuickLaunchNotification();

    }


    public void toggleQuickLaunchNotification(final boolean isToggled) {
        if (isToggled) showQuickLaunchNotification();
        else ((NotificationManager) main.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(TC.NOTIFICATION_OPENER_ID);
    }


    private void showQuickLaunchNotification() {
        final NotificationManager manager = (NotificationManager) main.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = manager.getNotificationChannel(TC.CHANNEL_ID);
            if (channel == null) {
                channel = new NotificationChannel(TC.CHANNEL_ID, "Thoughts Channel", NotificationManager.IMPORTANCE_NONE);
                channel.setDescription("Thoughts");
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }


        final Intent notificationIntent = new Intent(main, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(main, TC.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(main.getResources(), R.mipmap.ic_launcher))
                .setContentTitle("Thoughts")
                .setContentText("Tap to open the app.")
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOngoing(true)
                .setAutoCancel(false);

        builder.setContentIntent(PendingIntent.getActivity(main, 0, notificationIntent, 0));


        final NotificationManagerCompat m = NotificationManagerCompat.from(main.getApplicationContext());

        if (ActivityCompat.checkSelfPermission(main,
                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        m.notify(TC.NOTIFICATION_OPENER_ID, builder.build());
    }


}
