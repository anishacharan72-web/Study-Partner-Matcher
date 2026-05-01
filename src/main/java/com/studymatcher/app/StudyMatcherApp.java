package com.studymatcher.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.multidex.MultiDexApplication;
import android.os.Build;

import com.google.firebase.FirebaseApp;

/**
 * Application class — initialises Firebase and creates notification channels.
 */
public class StudyMatcherApp extends MultiDexApplication {

    public static final String CHANNEL_ID_MATCH     = "channel_match";
    public static final String CHANNEL_ID_MESSAGE   = "channel_message";
    public static final String CHANNEL_ID_RATING    = "channel_rating";
    public static final String CHANNEL_ID_REMINDER  = "channel_reminder";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = getSystemService(NotificationManager.class);

            nm.createNotificationChannel(new NotificationChannel(
                    CHANNEL_ID_MATCH, "New Matches", NotificationManager.IMPORTANCE_DEFAULT));
            nm.createNotificationChannel(new NotificationChannel(
                    CHANNEL_ID_MESSAGE, "Messages", NotificationManager.IMPORTANCE_HIGH));
            nm.createNotificationChannel(new NotificationChannel(
                    CHANNEL_ID_RATING, "Rating Prompts", NotificationManager.IMPORTANCE_DEFAULT));
            nm.createNotificationChannel(new NotificationChannel(
                    CHANNEL_ID_REMINDER, "Session Reminders", NotificationManager.IMPORTANCE_HIGH));
        }
    }
}
