package ch.hevs.datasemlab.cityzen.timetravel;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ch.hevs.datasemlab.cityzen.R;

import static ch.hevs.datasemlab.cityzen.CulturalInterestImageDetailsActivity.LATITUDES;
import static ch.hevs.datasemlab.cityzen.CulturalInterestImageDetailsActivity.LONGITUDES;
import static ch.hevs.datasemlab.cityzen.CulturalInterestImageDetailsActivity.TITLES;

public class SimilarInterestActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng coordinates;

    private String[] mTitles;
    private String[] mLatitudes;
    private String[] mLongitudes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_interest);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapSimilar);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        mTitles = extras.getStringArray(TITLES);
        mLatitudes = extras.getStringArray(LATITUDES);
        mLongitudes = extras.getStringArray(LONGITUDES);

//        new FillMap().execute();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(15);

        for(int i=0; i<mTitles.length; i++){
            coordinates = new LatLng(Double.valueOf(mLatitudes[i]), Double.valueOf(mLongitudes[i]));
            mMap.addMarker(new MarkerOptions().position(coordinates).title(mTitles[i]));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
        }
    }

//    private class FillMap extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected Void doInBackground(String... strings) {
//
//            for(int i=0; i<mTitles.length; i++){
//                coordinates = new LatLng(Double.valueOf(mLatitudes[i]), Double.valueOf(mLongitudes[i]));
//                mMap.addMarker(new MarkerOptions().position(coordinates).title(mTitles[i]));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
//            }
//
//            return null;
//        }
//    }
}
