package ch.hevs.datasemlab.cityzen;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.datasemlab.cityzen.demo.CategoryDialog;
import ch.hevs.datasemlab.cityzen.demo.UserCredibility;
import ch.hevs.datasemlab.cityzen.notifications.NearestCulturalInterestInfo;
import ch.hevs.datasemlab.cityzen.notifications.NearestCulturalInterestsNofiticationManager;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class IntroActivity2 extends AppCompatActivity implements LocationListener{   //TODO - Needed for Notification //implements LocationListener {

    private final String TAG = IntroActivity2.class.getSimpleName();

    private final String REPOSITORY_URL = CityzenContracts.REPOSITORY_URL;

    public static final String TITLE = "Title";

    private final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 1;

    public static final String GPS_POSITION = "gps_position";
    public static final String NEAREST_INTEREST_LIST = "c_i_list";
    private LocationManager locationManager;

    private TextView textViewLatitude;
    private TextView textViewLongitude;

    private String provider;

    private List<NearestCulturalInterestInfo> listOFNearestCI = new ArrayList<NearestCulturalInterestInfo>();

    private String mLatitude;
    private String mLongitude;

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
//        textViewLatitude = (TextView) findViewById(latitude);
//        textViewLongitude = (TextView) findViewById(R.id.logitude);

//        createNotification();
    }

    //    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();

        // TODO - Commented Needed for Communication
        createNotification();

        if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COURSE_LOCATION);
        }

        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();

        // TODO - Needed for Notification
        if (ContextCompat.checkSelfPermission(IntroActivity2.this,ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COURSE_LOCATION);
        }
        locationManager.removeUpdates(this);
    }

    // TODO - Needed for Notification
    private void createNotification() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ContextCompat.checkSelfPermission(IntroActivity2.this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(IntroActivity2.this, new String[]{ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COURSE_LOCATION);
        }

        Location location = locationManager.getLastKnownLocation(provider);

//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);

        // Initialize the location fields
        if (location != null) {
            Log.i(TAG, "Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            Log.e(TAG, "Provider has not been selected.");
//            textViewLatitude.setText("Location not available");
//            textViewLongitude.setText("Location not available");
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

//        new GetCulturalInterestsList().execute("Association féminine des boulangers du Valais romand");

//        Intent intent = new Intent(this, ch.hevs.datasemlab.cityzen.history_new.MainCarouselActivity.class);
//        intent.putExtra(TITLE, "Association féminine des boulangers du Valais romand");
//        startActivity(intent);

//        Intent intent = new Intent(this, ItineraryActivity.class);
//        startActivity(intent);
    }

    //TODO - Commented - Needed for Notification
    @Override
    public void onLocationChanged(Location location) {

        if (location == null) {
            Log.e(TAG, "location null");
        } else {
            Log.e(TAG, "location not null");
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        mLatitude = String.valueOf(latitude);
        mLongitude = String.valueOf(longitude);
        Log.e(TAG, mLatitude);
        Log.e(TAG, mLongitude);

        String[] coordinates = {String.valueOf(latitude), String.valueOf(longitude)};
        queryNearestCI(coordinates);

//        textViewLatitude.setText(String.valueOf(latitude));
//        textViewLongitude.setText(String.valueOf(longitude));

    }


    //TODO - Needed for Notification

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled");
        Log.d(TAG, "provider: " + provider);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();

    }

    //TODO - Commented - Needed For Notification
    private void queryNearestCI(String[] coordinates) {

        final double mlatitude = Double.valueOf(coordinates[0]);
        final double mlongitude = Double.valueOf(coordinates[1]);

        Log.i(TAG, "Latitude: " + mlatitude);
        Log.i(TAG, "Longitude: " + mlongitude);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                Repository repo = new SPARQLRepository(REPOSITORY_URL);
                repo.initialize();

                RepositoryConnection conn = repo.getConnection();

                TupleQueryResult result = null;

                try {
                    StringBuilder qb = new StringBuilder();

                    qb.append("PREFIX schema: <http://www.hevs.ch/datasemlab/cityzen/schema#> \n");
                    qb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
                    qb.append("PREFIX owlTime: <http://www.w3.org/TR/owl-time#> \n");
                    qb.append("PREFIX edm: <http://www.europeana.eu/schemas/edm#> \n");
                    qb.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n");
                    qb.append("PREFIX dc: <http://purl.org/dc/elements/1.1/> \n");
                    qb.append("PREFIX dcterms: <http://purl.org/dc/terms/> \n");
                    qb.append("PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> \n");

                    qb.append(" SELECT DISTINCT ?title ?latitude ?longitude\n ");

                    qb.append(" WHERE {?culturalInterest dc:title ?title ;");
                    qb.append(" rdf:type schema:CulturalPlace ;");
                    qb.append(" geo:location ?spatialThing . \n ");
                    qb.append(" ?spatialThing geo:lat ?latitude ; \n ");
                    qb.append(" geo:long ?longitude . }\n");


                    Log.i(TAG + " query: ", qb.toString());

                    result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate();

                } finally {
                    conn.close();
                }

                while (result.hasNext()) {
                    BindingSet bs = result.next();

                    Value descriptionValue = bs.getValue("title");
                    Value latitudeValue = bs.getValue("latitude");
                    Value longitudeValue = bs.getValue("longitude");

                    String description = descriptionValue.stringValue();
                    double latitude = Double.valueOf(latitudeValue.stringValue());
                    double longitude = Double.valueOf(longitudeValue.stringValue());


                    if (distance(latitude, longitude, mlatitude, mlongitude, "KM", 5)) {
                        String latitudeString = String.valueOf(latitude);
                        String longitudeString = String.valueOf(longitude);
                        NearestCulturalInterestInfo nearestCI = new NearestCulturalInterestInfo(description, latitudeString, longitudeString);
                        listOFNearestCI.add(nearestCI);
                    }
                }

                Log.i(TAG, "Number of Nearest Cultural Interest" + listOFNearestCI.size());

                Message msg = handlerAlarm.obtainMessage();

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(NEAREST_INTEREST_LIST, (ArrayList<? extends Parcelable>) listOFNearestCI);

                msg.setData(bundle);
                handlerAlarm.sendMessage(msg);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private boolean distance(double lat1, double lon1, double lat2, double lon2, String unit, double distanceMax) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        if (dist < distanceMax) {
            return true;
        }
        return false;
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts decimal degrees to radians						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

//    private class GetNearestCulturalInterests extends AsyncTask<String, Void, TupleQueryResult> {
//
//        private double mlatitude;
//        private double mlongitude;
//
//        @Override
//        protected TupleQueryResult doInBackground(String... strings) {
//
//            Repository repo = new SPARQLRepository(TemporalActivity.REPOSITORY_URL);
//            repo.initialize();
//
//            mlatitude = Double.valueOf(strings[0]);
//            mlongitude = Double.valueOf(strings[1]);
//
//            RepositoryConnection conn = repo.getConnection();
//
//            TupleQueryResult result = null;
//
//            try {
//                StringBuilder qb = new StringBuilder();
//
//                qb.append("PREFIX schema: <http://www.hevs.ch/datasemlab/cityzen/schema#> \n");
//                qb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
//                qb.append("PREFIX owlTime: <http://www.w3.org/TR/owl-time#> \n");
//                qb.append("PREFIX edm: <http://www.europeana.eu/schemas/edm#> \n");
//                qb.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n");
//                qb.append("PREFIX dc: <http://purl.org/dc/elements/1.1/> \n");
//                qb.append("PREFIX dcterms: <http://purl.org/dc/terms/> \n");
//                qb.append("PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> \n");
//
//                qb.append(" SELECT DISTINCT ?title ?latitude ?longitude\n ");
//
//                qb.append(" WHERE {?culturalInterest dc:title ?title ;");
//                qb.append(" rdf:type schema:CulturalPlace ;");
//                qb.append(" geo:location ?spatialThing . \n ");
//                qb.append(" ?spatialThing geo:lat ?latitude ; \n ");
//                qb.append(" geo:long ?longitude . }\n");
//
//
//                Log.i(TAG + " query: ", qb.toString());
//
//                result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate();
//
//            } finally {
//                conn.close();
//            }
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(TupleQueryResult result) {
//            super.onPostExecute(result);
//            int i = 0;
//
//            while (result.hasNext()) {
//                BindingSet bs = result.next();
//
//                Value descriptionValue = bs.getValue("title");
//                Value latitudeValue = bs.getValue("latitude");
//                Value longitudeValue = bs.getValue("longitude");
//
//                String description = descriptionValue.stringValue();
//                double latitude = Double.valueOf(latitudeValue.stringValue());
//                double longitude = Double.valueOf(longitudeValue.stringValue());
//
//
//                if (distance(latitude, longitude, mlatitude, mlongitude, "KM", 5)) {
//                    String latitudeString = String.valueOf(latitude);
//                    String longitudeString = String.valueOf(longitude);
//                    NearestCulturalInterestInfo nearestCI = new NearestCulturalInterestInfo(description, latitudeString, longitudeString);
//                    listOFNearestCI.add(nearestCI);
//                }
//            }
//
//            //TODO use handler
//            Log.i(TAG, "Size of the list: " +String.valueOf(listOFNearestCI.size()));
//        }






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

    // TODO - Needed for Notification
    private Handler handlerAlarm = new Handler() {

//        private Object[] rowValues = new Object[3];

        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();

            List<NearestCulturalInterestInfo> myList = bundle.getParcelableArrayList(NEAREST_INTEREST_LIST);
            if(myList.size() != 0) {
                Log.i(TAG, String.valueOf(myList.get(0).getDescription()));
            }

            Intent intent = new Intent(getBaseContext(), NearestCulturalInterestsNofiticationManager.class);

            intent.putParcelableArrayListExtra(NEAREST_INTEREST_LIST, (ArrayList<? extends Parcelable>) myList);
            intent.putExtra(CityzenContracts.LATITUDE, mLatitude);
            intent.putExtra(CityzenContracts.LONGITUDE, mLongitude);

//            Log.i(TAG, "List of NearestCI Size: " + listOFNearestCI.size());

            AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Activity.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), 0, intent, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Log.i(TAG, String.valueOf(System.currentTimeMillis()));
                alarmManager.setExact(AlarmManager.RTC, System.currentTimeMillis() + 30000 , pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 30000, pendingIntent);
            }
        }
    };

}
