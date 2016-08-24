package ch.hevs.datasemlab.cityzen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class CulturalInterestImageDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = CulturalInterestImageDetailsActivity.class.getSimpleName();

    private String title;

    private TextView textViewDescription;

    private GoogleMap mMap;
    private LatLng coordinates;
    private String position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cultural_interest_image_details);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        Toast.makeText(this, TAG, Toast.LENGTH_SHORT).show();

        Bundle extras = getIntent().getExtras();

        title = extras.getString(CityzenContracts.TITLE);
        byte[] imageByte = extras.getByteArray(CityzenContracts.IMAGE);

        TextView textViewTitle = (TextView) findViewById(R.id.text_view_title_details);
        ImageView imageView = (ImageView) findViewById(R.id.image_view_image_details);
        textViewDescription = (TextView) findViewById(R.id.text_view_description_details);

        textViewTitle.setText(title);
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        imageView.setImageBitmap(Bitmap.createScaledBitmap(imageBitmap, 800, 600, false));

        new GetCulturalInterestsDescription().execute(title);
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

            Repository repo = new SPARQLRepository(TemporalActivity.REPOSITORY_URL);
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

                qb.append(" SELECT DISTINCT ?description ?latitude ?longitude ?spatialThing \n ");

                qb.append(" WHERE {?culturalInterest dc:title ");
                qb.append("\"" +title + "\" . \n ");
                Log.i(TAG, "Title: " + title);
                qb.append(" ?culturalInterest dc:description ?description ; \n ");
                qb.append(" geo:location ?spatialThing . \n ");
                qb.append(" ?spatialThing geo:lat ?latitude ; \n ");
                qb.append(" geo:long ?longitude . \n } ");

                Log.i(TAG + " query: ", qb.toString());

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
            while (result.hasNext()) {
                BindingSet bs = result.next();

                Value descriptionValue = bs.getValue("description");
                Value latitudeValue = bs.getValue("latitude");
                Value longitudeValue = bs.getValue("longitude");
                Value spacialThingValue = bs.getValue("spatialThing");

                String description = descriptionValue.stringValue();
                String latitude = latitudeValue.stringValue();
                String longitude = longitudeValue.stringValue();
                String spatialThing = spacialThingValue.stringValue();

                textViewDescription.setText(description);
                coordinates = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                position = spatialThing;

            }
            mMap.addMarker(new MarkerOptions().position(coordinates).title(position));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
        }
    }
}
