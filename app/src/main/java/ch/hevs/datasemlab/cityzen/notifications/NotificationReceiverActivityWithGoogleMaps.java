package ch.hevs.datasemlab.cityzen.notifications;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import ch.hevs.datasemlab.cityzen.R;

import static ch.hevs.datasemlab.cityzen.IntroActivity2.NEAREST_INTEREST_LIST;

public class NotificationReceiverActivityWithGoogleMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng coordinates;

//    private String[] mTitles;
//    private String[] mLatitudes;
//    private String[] mLongitudes;

    private final String TAG = NotificationReceiverActivityWithGoogleMaps.class.getSimpleName();

    private List<NearestCulturalInterestInfo> culturalInterestInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_receiver_with_google_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.notification_receiver);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        extras.setClassLoader(getClassLoader());

        if(extras==null){
            Log.i(TAG, "no extras added");
        }

        //String message = extras.getString(IntroActivity.GPS_POSITION);

        Log.i(TAG, "Size" + ", " + extras.size());

        if(extras.getParcelableArrayList(NEAREST_INTEREST_LIST)==null){
            Log.i(TAG, "No Nearest Interest List");
        }else {

            culturalInterestInfos = extras.getParcelableArrayList(NEAREST_INTEREST_LIST);
            Log.i(TAG, culturalInterestInfos.get(0).getDescription());
        }

//        Bundle extras = getIntent().getExtras();
//        mTitles = extras.getStringArray(TITLES);
//        mLatitudes = extras.getStringArray(LATITUDES);
//        mLongitudes = extras.getStringArray(LONGITUDES);

//        new FillMap().execute();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(15);

        for(int i=0; i<culturalInterestInfos.size(); i++){
            NearestCulturalInterestInfo nearestCulturalInterestInfo = culturalInterestInfos.get(i);
            coordinates = new LatLng(Double.valueOf(nearestCulturalInterestInfo.getLatitude()), Double.valueOf(nearestCulturalInterestInfo.getLongitude()));
            mMap.addMarker(new MarkerOptions().position(coordinates).title(nearestCulturalInterestInfo.getDescription()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
        }
//        for(int i=0; i<mTitles.length; i++){
//            coordinates = new LatLng(Double.valueOf(mLatitudes[i]), Double.valueOf(mLongitudes[i]));
//            mMap.addMarker(new MarkerOptions().position(coordinates).title(mTitles[i]));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
//        }
    }
}
