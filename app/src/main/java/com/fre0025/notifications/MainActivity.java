package com.fre0025.notifications;

import android.annotation.SuppressLint;

import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.fre0025.notifications.FCMMessageReceiverService.CLASSIC_NOTIFICATION_ID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyTag";
    private TextView tv_token;
    private TextView tokenText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(getIntent() != null && getIntent().hasExtra(getPackageName())){
            if (manager != null) {
                manager.cancel(CLASSIC_NOTIFICATION_ID);
            }
            finish();
        }

        setContentView(R.layout.activity_main);
        tv_token = findViewById(R.id.tv_token);
        tokenText = findViewById(R.id.tv_token2);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()) {
                    String token = Objects.requireNonNull(task.getResult()).getToken();
                    Log.d(TAG, "onComplete: Token: " + token);
                    tv_token.setText("FCM client token generated:");
                    tokenText.setText(token);
                } else {
                    tv_token.setText("Token generation failed\n");
                }
            }
        });

    }
}