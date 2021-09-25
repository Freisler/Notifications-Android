package com.fre0025.notifications;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.annotations.Nullable;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import static com.fre0025.notifications.FCMMessageReceiverService.CLASSIC_NOTIFICATION_ID;

public class App extends Application {
    public static final String FCM_CHANNEL_ID1 = "FCM classic";
    public static final String FCM_CHANNEL_ID2 = "FCM voip";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannelForClassic();
        createNotificationChannelForVoIP();
    }

    private void createNotificationChannelForVoIP() {
        Uri soundUri = Uri.parse(
                "android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.call);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();
        NotificationChannel channel = new NotificationChannel
                (FCM_CHANNEL_ID2, "VoIP Push", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("VoIP PUSH channel");
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setSound(soundUri, audioAttributes);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null)
            manager.createNotificationChannel(channel);
    }

    private void createNotificationChannelForClassic() {
        Uri soundUri = Uri.parse(
                "android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.insight);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();
        NotificationChannel channel = new NotificationChannel
                (FCM_CHANNEL_ID1, "Classic Push", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Classic notification channel");
        channel.enableVibration(true);
        channel.setSound(soundUri, audioAttributes);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null)
            manager.createNotificationChannel(channel);
    }
}
