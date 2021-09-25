package com.fre0025.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.fre0025.notifications.App.FCM_CHANNEL_ID1;
import static com.fre0025.notifications.App.FCM_CHANNEL_ID2;

public class FCMMessageReceiverService extends FirebaseMessagingService {
    public static final String TAG = "MyTag";
    public static final int CLASSIC_NOTIFICATION_ID = 0;
    public static final int VOIP_NOTIFICATION_ID = 1;
    static final String FULL_SCREEN_ACTION = "full_screen_action";
    private static String title = "";
    private static String text = "";
    private static Uri soundUri1;
    private static Uri soundUri2;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: called");
        Log.d(TAG, "onMessageReceived: MessageReceived from: " + remoteMessage.getFrom());

        //GET DATA FROM REQUEST
        Map<String, String> data = remoteMessage.getData();
        String notificationType = data.get("type");
        title = data.get("title");
        text = data.get("body");
        String image = data.get("image");
        Bitmap bitmap = getBitmapFromURL(image);
        NotificationCompat.BigPictureStyle bigPicStyle = new NotificationCompat.BigPictureStyle().bigPicture(bitmap);
        String color = data.get("color");
        soundUri1 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.insight);
        soundUri2 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.call);

        Intent showIntent = new Intent(this, MainActivity.class);
        showIntent.putExtra(getPackageName(), CLASSIC_NOTIFICATION_ID);
        PendingIntent dismissIntent = PendingIntent.getActivity(this, 0, showIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //BUILD NOTIFICATIONS
        if(notificationType.equals("classic")) {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, FCM_CHANNEL_ID1)
                            .setSmallIcon(R.drawable.ic_chat)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_animal))
                            .setContentTitle(title)
                            .setContentText(text)
                            .setAutoCancel(true)
                            .setVibrate(new long[]{0, 100, 200, 300})
                            .setSound(soundUri1)
                            .setColor(Color.parseColor(color))
                            .setStyle(bigPicStyle)
                            .addAction(R.drawable.ic_delete, "Dismiss", dismissIntent)
                            .setContentIntent(dismissIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = notificationBuilder.build();
            notificationManager.notify(CLASSIC_NOTIFICATION_ID, notification);
        }else if(notificationType.equals("voip")) {
            Intent intent = new Intent(FULL_SCREEN_ACTION, null, this, NotificationReceiver.class);
            PendingIntent pendingIntentVoip = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null)
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntentVoip);
            NotificationManagerCompat.from(this).cancel(VOIP_NOTIFICATION_ID); //cancel last notification for repeated tests
        }

        //ADDITIONAL LOGS
        if(remoteMessage.getData().size() > 0) {
            Log.d(TAG, "onMessageReceived: Data Size: " + remoteMessage.getData().size());
            for (String key : remoteMessage.getData().keySet()) {
                Log.d(TAG,"onMessageReceived Key: " + key + " Data: " + remoteMessage.getData().get(key));
            }
            Log.d(TAG, "onMessageReceived: Data: " + remoteMessage.getData().toString());
        }

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = Build.VERSION.SDK_INT >= 20 ? pm.isInteractive() : pm.isScreenOn(); // check if screen is on
        if (!isScreenOn) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myApp:notificationLock");
            wl.acquire(3000); //set your time in milliseconds
        }
    }

    static void CreateFullScreenNotification(Context context) {
        Intent intent = new Intent(context, IncomingCall.class);
        intent.putExtra("title", title);
        intent.putExtra("text", text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, FCM_CHANNEL_ID2)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setContentIntent(pendingIntent)
                        .setSound(soundUri2)
                        .setFullScreenIntent(pendingIntent, true);
        NotificationManagerCompat.from(context).notify(VOIP_NOTIFICATION_ID, notificationBuilder.build());
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.d(TAG,"onDeletedMessages: called");
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG,"onNewToken: called");
    }
}