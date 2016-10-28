package ch.hevs.datasemlab.cityzen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class CulturalInterestImageDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = CulturalInterestImageDetailsActivity.class.getSimpleName();

    private String title;
    private String imageURL;

    private TextView textViewDescription;
    private ImageView imageView;

    private GoogleMap mMap;
    private LatLng coordinates;
    private String position;

    private int numberOfCulturalInterestsWithThisTitle;

    public static ArrayList<String> mCulturalInterestsList;
    public static ArrayList<String> mDatesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cultural_interest_image_details);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



//        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        title = extras.getString(CityzenContracts.TITLE);
        imageURL = extras.getString(CityzenContracts.IMAGE);

        Log.i(TAG, "IMAGE URL: " + imageURL);

        new GetCulturalInterestsNumberAsyncTask().execute(title);
        new GetCulturalInterestsList().execute(title);


//        byte[] imageByte = extras.getByteArray(CityzenContracts.IMAGE);

        TextView textViewTitle = (TextView) findViewById(R.id.text_view_title_details);
//        textViewTitle.setBackgroundColor(Color.GREEN);
        imageView = (ImageView) findViewById(R.id.image_view_image_details);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.i(TAG, "On Long Click");

                Intent intent = new Intent(getApplicationContext(), TimeTravelActivity.class);
                intent.putExtra(CityzenContracts.TITLE, title);
                intent.putExtra(CityzenContracts.NUMBER_OF_CULTURAL_INTERESTS, numberOfCulturalInterestsWithThisTitle);
                if(mCulturalInterestsList == null || mDatesList == null){
                    Log.i(TAG, "one of the list is null");
                }
                intent.putExtra(CityzenContracts.IMAGES_ARRAY_LIST, mCulturalInterestsList);
                intent.putExtra(CityzenContracts.DATES_ARRAY_LIST, mDatesList);
                startActivity(intent);
                return true;
            }
        });

        textViewDescription = (TextView) findViewById(R.id.text_view_description_details);

        Button addButton = (Button) findViewById(R.id.button_add_interest);

        textViewTitle.setText(title);
        new ImageViewLoader().execute(imageURL);


//        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
//        imageView.setImageBitmap(Bitmap.createScaledBitmap(imageBitmap, 800, 600, false));

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

    private class GetCulturalInterestsNumberAsyncTask extends AsyncTask<String, Void, Integer> {


        @Override
        protected Integer doInBackground(String... strings) {

            Repository repo = new SPARQLRepository(TemporalActivity.REPOSITORY_URL);
            repo.initialize();

            RepositoryConnection conn = repo.getConnection();

            String title = strings[0];

            TupleQueryResult result = null;

            try {
                StringBuilder qb = new StringBuilder();

                qb.append("PREFIX edm: <http://www.europeana.eu/schemas/edm#> \n");
                qb.append("PREFIX dc: <http://purl.org/dc/elements/1.1/> \n");

                qb.append(" SELECT (COUNT(DISTINCT ?culturalInterest) AS ?count) \n ");

                qb.append(" WHERE {?culturalInterest dc:title ");
                qb.append("\"" + title + "\" . \n } ");

                Log.i(TAG, qb.toString());

                result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate();

            } finally {
                conn.close();
            }

            Integer count = Integer.valueOf(result.next().getBinding("count").getValue().stringValue());

            Log.i(TAG, "CulturalInterest Number - doInBackgrouund: " + String.valueOf(count.intValue()));

            return count;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            numberOfCulturalInterestsWithThisTitle = result.intValue();
            Log.i(TAG, "CulturalInterest Number - onPostExecute: " + String.valueOf(numberOfCulturalInterestsWithThisTitle));

        }
    }

    private class GetCulturalInterestsList extends AsyncTask<String, Void, TupleQueryResult> {

        @Override
        protected TupleQueryResult doInBackground(String... strings) {

            String title = strings[0];

            Repository repo = new SPARQLRepository(TemporalActivity.REPOSITORY_URL);
            repo.initialize();

            RepositoryConnection conn = repo.getConnection();

            TupleQueryResult result = null;

            try {
                StringBuilder qb = new StringBuilder();

                qb.append("PREFIX schema: <http://www.hevs.ch/datasemlab/cityzen/schema#> \n");
                qb.append("PREFIX owlTime: <http://www.w3.org/TR/owl-time#> \n");
                qb.append("PREFIX edm: <http://www.europeana.eu/schemas/edm#> \n");
                qb.append("PREFIX dc: <http://purl.org/dc/elements/1.1/> \n");
                qb.append("PREFIX dcterms: <http://purl.org/dc/terms/> \n");

                qb.append(" SELECT ?date ?imageURL \n ");
                qb.append(" WHERE { \n");
                qb.append(" ?culturalInterest dc:title ");
                qb.append("\"" +title + "\" ; \n ");
                qb.append("			dc:description ?description . \n");
                qb.append(" ?digitalrepresentationAggregator edm:aggregatedCHO ?culturalInterest ; \n");
                qb.append(" 		edm:hasView ?digitalrepresentation ; \n");
                qb.append(" 		owlTime:hasBeginning ?beginningInstant . \n");
                qb.append(" ?beginningInstant owlTime:inXSDDateTime ?date . \n");
                qb.append(" ?digitalrepresentation dcterms:hasPart ?digitalItem . \n");
                qb.append(" ?digitalItem schema:thumbnail_url ?imageURL . }\n");

                qb.append(" ORDER BY DESC(?date) \n");

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

            mCulturalInterestsList = new ArrayList<>();
            mDatesList = new ArrayList<>();

            while (result.hasNext()) {
                BindingSet bs = result.next();

                Value dateValue = bs.getValue("date");
                Value imageURLValue = bs.getValue("imageURL");

                String date = dateValue.stringValue();
                String imageURL = imageURLValue.stringValue();

                Log.i(TAG, "date: " + date);
                Log.i(TAG, "imageURL: " + imageURL);

                mDatesList.add(date);
                mCulturalInterestsList.add(imageURL);
            }
            Log.i(TAG, "date List size:" + mDatesList.size());
            Log.i(TAG, "url List size:" + mCulturalInterestsList.size());
        }
    }

    public void addInterestToItinerary(View view){

        int numberOfPreferences = 0;

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.itinery),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if(!sharedPref.getAll().isEmpty())
            numberOfPreferences = sharedPref.getAll().size();

        String key = String.valueOf(numberOfPreferences);

        Log.i(TAG, "Key: " + key);

        editor.putString(key, title);
        editor.commit();

        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
    }

    private class ImageViewLoader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {

            String title = strings[0];

            URL url = null;
            Bitmap bitmap = null;

            try {
                url = new URL(imageURL);
                InputStream inputStream = url.openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                //bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
                System.err.println("Title: " + title);
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            imageView.setImageBitmap(result);
        }
    }
}
