package ch.hevs.datasemlab.cityzen;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

public class CulturalInterestVideoDetailsActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final String TAG = CulturalInterestVideoDetailsActivity.class.getSimpleName();

    private final String REPOSITORY_URL = CityzenContracts.REPOSITORY_URL;

    private String title;

    private TextView textViewDescription;
    private VideoView videoView;
    private int videoPosition = 0;

    private MediaController videoController;

    private GoogleMap mMap;
    private LatLng coordinates;
    private String position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cultural_interest_video_details);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();

        title = extras.getString(CityzenContracts.TITLE);
        //byte[] imageByte = extras.getByteArray(CityzenContracts.IMAGE);

        TextView textViewTitle = (TextView) findViewById(R.id.text_view_title_details);
//        textViewTitle.setBackgroundColor(Color.GREEN);
        textViewDescription = (TextView) findViewById(R.id.text_view_description_details);
        videoView = (VideoView) findViewById(R.id.video_view_details);

        videoView.requestFocus();

        videoController = new MediaController(this);
        videoController.setAnchorView(videoView);
        videoView.setMediaController(videoController);

        textViewTitle.setText(title);

        new GetCulturalInterestsDescription().execute(title);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("Position", videoView.getCurrentPosition());
        videoView.pause();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        videoPosition = savedInstanceState.getInt("Position");
        videoView.seekTo(videoPosition);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(15);

        // Add a marker in Sydney, Australia, and move the camera.
        //LatLng sydney = new LatLng(46.2294, 7.362);
//        mMap.addMarker(new MarkerOptions().position(coordinates).title(position));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));

    }

    private class GetCulturalInterestsDescription extends AsyncTask<String, Void, TupleQueryResult> {

        @Override
        protected TupleQueryResult doInBackground(String... strings) {

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

                qb.append(" SELECT DISTINCT ?description ?video ?latitude ?longitude ?spatialThing \n ");

                qb.append(" WHERE {?culturalInterest dc:title ");
                qb.append("\"" +title + "\" . \n ");
                qb.append(" ?culturalInterest dc:description ?description ; \n");
                qb.append(" geo:location ?spatialThing . \n ");
                qb.append(" ?spatialThing geo:lat ?latitude ; \n ");
                qb.append(" geo:long ?longitude . \n ");
                qb.append(" ?digitalrepresentationAggregator edm:aggregatedCHO ?culturalInterest . \n");
                qb.append(" ?digitalrepresentationAggregator edm:hasView ?digitalrepresentation . \n");
                qb.append(" ?digitalrepresentation dcterms:hasPart ?digitalItem . \n");
                qb.append(" ?digitalItem schema:image_url ?video . }\n");


                result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate();

            } finally {
                conn.close();
            }
            return result;
        }

        @Override
        protected void onPostExecute(TupleQueryResult result) {
            super.onPostExecute(result);
            int i=0;
            if (result.hasNext()) {
                BindingSet bs = result.next();

                Value descriptionValue = bs.getValue("description");
                Value videoValue = bs.getValue("video");
                Value latitudeValue = bs.getValue("latitude");
                Value longitudeValue = bs.getValue("longitude");
                Value spacialThingValue = bs.getValue("spatialThing");

                String description = descriptionValue.stringValue();
                String video = videoValue.stringValue();
                String latitude = latitudeValue.stringValue();
                String longitude = longitudeValue.stringValue();
                String spatialThing = spacialThingValue.stringValue();


                Uri videoURI = Uri.parse(video);

                coordinates = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                position = spatialThing;
                textViewDescription.setText(description);

                Log.i(TAG, "URI to String: " + videoURI.toString());

                videoView.setVideoURI(videoURI);
//                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//
//                    public void onPrepared(MediaPlayer mediaPlayer) {
//                        videoView.start();
//                    }
//                });

                videoView.start();

            }
            mMap.addMarker(new MarkerOptions().position(coordinates).title(position));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
        }
    }
}
