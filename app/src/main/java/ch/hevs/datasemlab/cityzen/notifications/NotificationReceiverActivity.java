package ch.hevs.datasemlab.cityzen.notifications;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ch.hevs.datasemlab.cityzen.IntroActivity;
import ch.hevs.datasemlab.cityzen.R;

public class NotificationReceiverActivity extends AppCompatActivity {

    private final String TAG = NotificationReceiverActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_receiver);

        Bundle extras = getIntent().getExtras();
        String message = extras.getString(IntroActivity.GPS_POSITION);

        Log.i(TAG, message);
    }
}
