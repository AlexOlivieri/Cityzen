package ch.hevs.datasemlab.cityzen;

import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class CulturalInterestsGalleryActivity2 extends AppCompatActivity {

    private final String TAG = CulturalInterestsGalleryActivity.class.getSimpleName();

    private int startingDate;
    private int finishingDate;

    private String[] cursorColumnsTitle = {"_id", "Title", "Image"};

    private MatrixCursor cursorCulturalInterests = new MatrixCursor(cursorColumnsTitle);

    private ListView listView;

    private CulturalInterestsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cultural_interests_gallery);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        Bundle extras = getIntent().getExtras();

        startingDate = extras.getInt(TemporalActivity.STARTING_DATE);
        finishingDate = extras.getInt(TemporalActivity.FINISHING_DATE);

        Toast.makeText(this, String.valueOf(startingDate) + " , " + String.valueOf(finishingDate), Toast.LENGTH_SHORT).show();

        new GetCulturalInterests().execute();

        listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), CulturalInterestDetailsActivity.class);
                adapterView.getItemAtPosition(position);

                TextView textView = (TextView) view.findViewById(R.id.text_view_cultural_item_title);
                intent.putExtra(CityzenContracts.TITLE, textView.getText());

                ImageView imageView = (ImageView) view.findViewById(R.id.image_view_cultural_interest_image);
                Drawable drawable = imageView.getDrawable();
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
                Bitmap bitmap = bitmapDrawable .getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                intent.putExtra(CityzenContracts.IMAGE, imageInByte);
                Log.i(TAG, String.valueOf(textView.getText()));
                startActivity(intent);
            }
        });



//        GridView gridView = (GridView) findViewById(R.id.gridview);
//        gridView.setAdapter(new CulturalInterestsAdapter(this));
//
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                Toast.makeText(CulturalInterestsGalleryActivity.this, "" + position,
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private class GetCulturalInterests extends AsyncTask<String, Void, TupleQueryResult> {

        @Override
        protected TupleQueryResult doInBackground(String... strings) {

            Repository repo = new SPARQLRepository(TemporalActivity.REPOSITORY_URL);
            repo.initialize();

            RepositoryConnection conn = repo.getConnection();

            String date = null;

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

                qb.append(" SELECT DISTINCT ?title ?image \n ");

                qb.append(" WHERE {?culturalInterest dc:title ?title . \n ");

                qb.append(" ?digitalrepresentationAggregator edm:aggregatedCHO ?culturalInterest . \n");
                qb.append(" ?digitalrepresentationAggregator edm:hasView ?digitalrepresentation . \n");
                qb.append(" ?digitalrepresentation dcterms:hasPart ?digitalItem . \n");
                qb.append(" ?digitalItem schema:thumbnail_url ?image . \n");
                qb.append(" ?digitalrepresentationAggregator owlTime:hasBeginning ?instant . \n");

                qb.append(" ?instant owlTime:inXSDDateTime ?date . ");

                qb.append(" FILTER ( ?date >= \"" + startingDate + "\" && ?date <= \"" + finishingDate + "\") } ");

                qb.append("ORDER BY ?date");
                //qb.append(" LIMIT 1 ");

                result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate();

            } finally {
                conn.close();
            }
            return result;
        }

        private Object[] rowValues = new Object[3];

        @Override
        protected void onPostExecute(TupleQueryResult result) {
            super.onPostExecute(result);
            int i=0;
            while (result.hasNext()) {
                BindingSet bs = result.next();
                rowValues[0] = String.valueOf(i);
                Value titleValue = bs.getValue("title");
                Value imageValue = bs.getValue("image");
                String title = titleValue.stringValue();
                rowValues[1] = title;
                String image = imageValue.stringValue();
                URL url = null;
                Bitmap bitmap = null;
                byte[] bitmapArray = null;
                try {
                    url = new URL(image);
                    InputStream inputStream = url.openStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    bitmapArray = stream.toByteArray();
                    rowValues[2] = bitmapArray;
                }catch (MalformedURLException e){
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Title: " + title);
                }
//                Log.i(TAG,  title + ", " + description + ", " + image);

                i++;

                cursorCulturalInterests.addRow(rowValues);
            }
//            Log.i(TAG + "number of rows", String.valueOf(cursorCulturalInterests.getCount()));
//            while(cursorCulturalInterests.moveToNext()) {
//                Log.i(TAG + " id: ", cursorCulturalInterests.getString(cursorCulturalInterests.getColumnIndex("_id")));
//                Log.i(TAG + " Title: ", cursorCulturalInterests.getString(cursorCulturalInterests.getColumnIndex("Title")));
//                Log.i(TAG + " Description: ", cursorCulturalInterests.getString(cursorCulturalInterests.getColumnIndex("Description")));
//                Log.i(TAG + " Image: ", cursorCulturalInterests.getString(cursorCulturalInterests.getColumnIndex("Image")));
//           }
            adapter = new CulturalInterestsAdapter(CulturalInterestsGalleryActivity2.this, cursorCulturalInterests, 0);
            listView.setAdapter(adapter);
            //adapter.changeCursor(cursorCulturalInterests);
        }
    }
}
