package com.fre0025.notifications;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.NotificationManagerCompat;
import static com.fre0025.notifications.FCMMessageReceiverService.VOIP_NOTIFICATION_ID;

public class IncomingCall extends Activity {
    public TextView who;
    public TextView number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);
        who = findViewById(R.id.tv_who);
        number = findViewById(R.id.tv_number);
        who.setText(getIntent().getStringExtra("title"));
        number.setText(getIntent().getStringExtra("text"));

        ImageView accept = findViewById(R.id.accept);
        ImageView reject = findViewById(R.id.reject);

        accept.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Toast.makeText(IncomingCall.this, "CALL ACCEPTED", Toast.LENGTH_SHORT).show();
                NotificationManagerCompat.from(getApplicationContext()).cancel(VOIP_NOTIFICATION_ID);
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(IncomingCall.this, "CALL REJECTED", Toast.LENGTH_SHORT).show();
                NotificationManagerCompat.from(getApplicationContext()).cancel(VOIP_NOTIFICATION_ID);
                finish();
            }
        });
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS

        );
    }

}