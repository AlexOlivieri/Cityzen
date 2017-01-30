package ch.hevs.datasemlab.cityzen;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import ch.hevs.datasemlab.cityzen.demo.CategoryDialog;
import ch.hevs.datasemlab.cityzen.demo.ItineraryActivity;
import ch.hevs.datasemlab.cityzen.demo.UserCredibility;
import ch.hevs.datasemlab.cityzen.notifications.CreateTheNotificationActivity;

public class IntroActivity extends AppCompatActivity {

    private final String TAG = IntroActivity.class.getSimpleName();

    public static final String GPS_POSITION = "gps_position";
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String provider;
    private double latitude;
    private double longitude;

    private TextView textViewLatitude;
    private TextView textViewLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ImageView imageView = (ImageView) findViewById(R.id.logo);
        imageView.setImageResource(R.drawable.logo2);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        imageView.startAnimation(myFadeInAnimation);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

                View detailsView = layoutInflater.inflate(R.layout.details_popup_layout, null);

                final PopupWindow popupWindow = new PopupWindow(detailsView, 800, 600);

                Button dismissButton = (Button) detailsView.findViewById(R.id.dismiss_button);
                dismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            }
        });

        //TODO
        textViewLatitude = (TextView) findViewById(R.id.latitude);
        textViewLongitude = (TextView) findViewById(R.id.logitude);
        createNotification();
    }

//    @Override
//    protected void onPause(){
//        super.onPause();
//        Log.i(TAG, "On Pause");
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.removeUpdates(locationListener);
//    }

    private void createNotification() {

        Location location;

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            Log.e(TAG, "No Network provided enabled");
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }else{
//            this.canGetLocation = true;
            if(isNetworkEnabled){
                locationListener = new MyLocationListener();
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
                if(locationManager != null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(location != null){
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
            if(isGPSEnabled){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                if(locationManager != null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(location != null){
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
        }



        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        Location location = locationManager.getLastKnownLocation(provider);

        locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            textViewLatitude.setText("No permission");

            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag("fragment_edit_name");
        String fragmentName = "fragment_edit_name";
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        switch (item.getItemId()) {
            case R.id.action_category_settings:
                CategoryDialog categoryDialog = new CategoryDialog();
//                categoryDialog.show(manager, "fragment_edit_name");
                categoryDialog.show(manager, fragmentName);
                break;
            case R.id.action_see_credibility:
                Intent intent = new Intent(this, UserCredibility.class);
                startActivity(intent);
            default:
                break;
        }
        return true;
    }

    public void exploreCulturalInterests(View view) {

        Intent intent = new Intent(this, TemporalActivity.class);
        startActivity(intent);
    }

    public void goToItinerary(View view) {
        Intent intent = new Intent(this, ItineraryActivity.class);
        startActivity(intent);
    }

    public void testNotification(View view) {
        Intent intent = new Intent(this, CreateTheNotificationActivity.class);
        startActivity(intent);
    }


//    public void solveConflict(View view){
//        FragmentManager manager = getFragmentManager();
//        Fragment frag = manager.findFragmentByTag("fragment_edit_name");
//        String fragmentName = "fragment_edit_name";
//        if (frag != null) {
//            manager.beginTransaction().remove(frag).commit();
//        }
//        ConflictResolvingDialog dialog = new ConflictResolvingDialog();
//        dialog.show(manager, fragmentName);
//    }

//    public void showDetails(View view){
//        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//
//        View detailsView = layoutInflater.inflate(R.layout.details_popup_layout, null);
////        final PopupWindow popupWindow = new PopupWindow(detailsView, LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT);
//        final PopupWindow popupWindow = new PopupWindow(detailsView, 1200 , 800);
////        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
//
//
//        Button dismissButton = (Button) detailsView.findViewById(R.id.dismiss_button);
//        dismissButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                popupWindow.dismiss();
//            }
//        });
//
////        popupWindow.showAsDropDown(view);
//        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
//
//    }

    private class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            //latitude = location.getLatitude();
            //longitude = location.getLongitude();
            if(location == null){
                Log.e(TAG, "location null");
            }else{
                Log.e(TAG, "location not null");
            }
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.e(TAG, String.valueOf(latitude));
            Log.e(TAG, String.valueOf(longitude));

            textViewLongitude.setText("Gps received");

//            initializeCoordinates(latitude, longitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG,"onProviderDisabled");
            Log.d(TAG, "provider: " + provider);
        }
    }

//    Handler handlerGPSCoordinated = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//
//            Bundle bundle = msg.getData();
//            latitude = bundle.getDouble("latitude");
//            longitude = bundle.getDouble("longitude");
//
//            Log.i(TAG, "On Create: " + String.valueOf(latitude));
//            Log.i(TAG, "On Create: " + String.valueOf(longitude));
//
//            textViewLatitude.setText(String.valueOf(latitude));
//            textViewLongitude.setText(String.valueOf(longitude));
//
//            Intent intent = new Intent(getApplicationContext(), NotificationReceiverActivity.class);
//            intent.putExtra(GPS_POSITION, "ciao");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
//
//            AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 60 * 1000, pendingIntent);
//
//            Notification notification = new Notification.Builder(getApplicationContext())
//                    .setContentTitle("New Interest Points around you")
//                    .setContentText("See the list of Cultural Interests around you")
//                    .setSmallIcon(R.drawable.logo2)
//                    .setContentIntent(pendingIntent)
//                    .addAction(R.drawable.photo, "Call", pendingIntent)
//                    .build();
//
//            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            notification.flags = Notification.FLAG_AUTO_CANCEL;
//            notificationManager.notify(0, notification);
//        }
//    };

//    private void initializeCoordinates(final double latitude, final double longitude) {
//
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//
//                Log.i(TAG, "Inside Runnable");
//
//                Message msg = handlerGPSCoordinated.obtainMessage();
//
//                Bundle bundle = new Bundle();
//                bundle.putDouble("latitude", latitude);
//                bundle.putDouble("longitude", longitude);
//
//                msg.setData(bundle);
//                handlerGPSCoordinated.sendMessage(msg);
//
//            }
//        };
//        Thread thread = new Thread(runnable);
//        thread.start();
//    }
}
