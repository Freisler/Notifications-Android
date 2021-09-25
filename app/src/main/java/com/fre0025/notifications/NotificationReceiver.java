package com.fre0025.notifications;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (FCMMessageReceiverService.FULL_SCREEN_ACTION.equals(intent.getAction()))
            FCMMessageReceiverService.CreateFullScreenNotification(context);
    }
}
