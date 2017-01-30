package ch.hevs.datasemlab.cityzen.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.datasemlab.cityzen.R;

import static ch.hevs.datasemlab.cityzen.IntroActivity2.NEAREST_INTEREST_LIST;

public class NearestCulturalInterestsNofiticationManager extends Service {

    private final String TAG = NearestCulturalInterestsNofiticationManager.class.getSimpleName();

    public NearestCulturalInterestsNofiticationManager() {
    }

    @Override
    public void onCreate(){

//        Toast.makeText(this, "MyAlarmService.onCreate()", Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(getApplicationContext(), NotificationReceiverActivity.class);
//        intent.putExtra(IntroActivity.GPS_POSITION, "ciao");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
//
//        Notification notification = new Notification.Builder(getApplicationContext())
//                .setContentTitle("New Interest Points around you")
//                .setContentText("See the list of Cultural Interests around you")
//                .setSmallIcon(R.drawable.logo2)
//                .setContentIntent(pendingIntent)
////                .addAction(R.drawable.photo, "Call", pendingIntent)
//                .build();
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        notificationManager.notify(001, notification);

    }

    public int onStartCommand (Intent intent, int flags, int startId) {
        List<NearestCulturalInterestInfo> listOfNearestCI = intent.getParcelableArrayListExtra(NEAREST_INTEREST_LIST);
        Log.i(TAG, "Size" + String.valueOf(listOfNearestCI.size()));
        Log.i(TAG, String.valueOf(listOfNearestCI.get(0).getDescription()));

        List<NearestCulturalInterestInfo> newListOfNearestCulturalInterestInfos = listOfNearestCI;

        //TODO: see how to attach it to the notification
        Toast.makeText(this, "MyAlarmService.onCreate()", Toast.LENGTH_LONG).show();
        Intent intentForNotification = new Intent(getApplicationContext(), NotificationReceiverActivityWithGoogleMaps.class);

//        intentForNotification.putExtra(IntroActivity.GPS_POSITION, "ciao");
        intentForNotification.putParcelableArrayListExtra(NEAREST_INTEREST_LIST, (ArrayList<NearestCulturalInterestInfo>) newListOfNearestCulturalInterestInfos);
        intentForNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intentForNotification, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("New Interest Points around you")
                .setContentText("See the list of Cultural Interests around you")
                .setSmallIcon(R.drawable.logo2)
                .setContentIntent(pendingIntent)
//                .addAction(R.drawable.photo, "Call", pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(001, notification);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
