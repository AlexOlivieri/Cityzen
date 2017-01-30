package ch.hevs.datasemlab.cityzen.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ch.hevs.datasemlab.cityzen.R;

public class CreateTheNotificationActivity extends AppCompatActivity {

    public static final String GPS_POSITION = "gps_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_the_notification);
    }

    public void createNotification(View view){
        Intent intent = new Intent(this, NotificationReceiverActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        Notification notification = new Notification.Builder(this)
                .setContentTitle("New Interest Points around you")
                .setContentText("See the list of Cultural Interests around you")
                .setSmallIcon(R.drawable.logo2)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.photo, "Call", pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);

    }
}
