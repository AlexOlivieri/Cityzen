package ch.hevs.datasemlab.cityzen.notifications;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.datasemlab.cityzen.CityzenContracts;
import ch.hevs.datasemlab.cityzen.R;

import static ch.hevs.datasemlab.cityzen.IntroActivity2.NEAREST_INTEREST_LIST;

public class NotificationReceiverActivityWithGoogleMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng coordinates;
    private String myLatitude;
    private String myLongitude;
    private LatLng myCoordinatets;

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

        myLatitude = extras.getString(CityzenContracts.LATITUDE);
        myLongitude = extras.getString(CityzenContracts.LONGITUDE);

        if(extras.getParcelableArrayList(NEAREST_INTEREST_LIST)==null){
            Log.i(TAG, "No Nearest Interest List");
        }else {
            culturalInterestInfos = extras.getParcelableArrayList(NEAREST_INTEREST_LIST);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(12);

        myCoordinatets = new LatLng(Double.valueOf(myLatitude), Double.valueOf(myLongitude));

        ArrayList<Marker> myMarkers = new ArrayList<Marker>();

        for(int i=0; i<culturalInterestInfos.size(); i++){
            NearestCulturalInterestInfo nearestCulturalInterestInfo = culturalInterestInfos.get(i);
            coordinates = new LatLng(Double.valueOf(nearestCulturalInterestInfo.getLatitude()), Double.valueOf(nearestCulturalInterestInfo.getLongitude()));
            Log.i(TAG, "Description: " +nearestCulturalInterestInfo.getDescription());
//            myMarkers.add(new Marker(new MarkerOptions().position(coordinates).title(nearestCulturalInterestInfo.getDescription())));
            mMap.addMarker(new MarkerOptions().position(coordinates).title(nearestCulturalInterestInfo.getDescription()));
            Log.i(TAG, "New Marker: " +nearestCulturalInterestInfo.getLatitude() + ", " + nearestCulturalInterestInfo.getLongitude());
        }
        mMap.addMarker(new MarkerOptions().position(myCoordinatets).title("My Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinatets));
    }
}
