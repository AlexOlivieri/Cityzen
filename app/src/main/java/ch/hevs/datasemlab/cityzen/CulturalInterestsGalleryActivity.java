package ch.hevs.datasemlab.cityzen;

import android.app.Activity;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

public class CulturalInterestsGalleryActivity extends AppCompatActivity {

    private final String TAG = Activity.class.getName();

    private String[] cursorColumnsTitle = {"Title", "Description", "Image"};

    private MatrixCursor cursorCulturalInterests = new MatrixCursor(cursorColumnsTitle);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cultural_interests_gallery);

        Bundle extras = getIntent().getExtras();

        int startingDate = extras.getInt(TemporalActivity.STARTING_DATE);
        int finishingDate = extras.getInt(TemporalActivity.FINISHING_DATE);

        Toast.makeText(this, String.valueOf(startingDate) + " , " + String.valueOf(finishingDate), Toast.LENGTH_SHORT).show();

        new GetCulturalInterests().execute();

        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new CulturalInterestsAdapter(this));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(CulturalInterestsGalleryActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class GetCulturalInterests extends AsyncTask<String, Void, TupleQueryResult> {

        @Override
        protected TupleQueryResult doInBackground(String... strings) {

            Repository repo = new SPARQLRepository("http://ec2-52-39-53-29.us-west-2.compute.amazonaws.com:8080/openrdf-sesame/repositories/CityZenDM");
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

                qb.append(" SELECT DISTINCT ?title ?description ?image \n ");

                qb.append(" WHERE {?culturalInterest dc:title ?title . \n ");
                qb.append(" ?culturalInterest dc:description ?description . \n");

                qb.append(" ?digitalrepresentationAggregator edm:aggregatedCHO ?culturalInterest . \n");
                qb.append(" ?digitalrepresentationAggregator rdf:type schema:DigitalRepresentationAggregator . \n");
                qb.append(" ?digitalrepresentationAggregator edm:hasView ?digitalrepresentation . \n");
                qb.append(" ?digitalrepresentation dcterms:hasPart ?digitalItem . \n");
                qb.append(" ?digitalItem schema:image_url ?image . \n");
                qb.append(" schema:DigitalRepresentationAggregator rdfs:subClassOf owlTime:TemporalEntity . \n");
                qb.append(" ?temporalEntity rdf:type owlTime:TemporalEntity . \n");
                qb.append(" ?temporalEntity owlTime:hasBeginning ?instant . \n");

                qb.append(" ?instant owlTime:inXSDDateTime ?date . ");

                qb.append(" FILTER ( ?date >= \"1950\" && ?date <= \"1980\") } ");

                qb.append("ORDER BY ?date");
                //qb.append(" LIMIT 1 ");

                result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate();

            } finally {
                conn.close();
            }
            return result;
        }

        private String[] rowValues = new String[3];

        @Override
        protected void onPostExecute(TupleQueryResult result) {
            super.onPostExecute(result);
            while (result.hasNext()) {
                BindingSet bs = result.next();
                Value titleValue = bs.getValue("title");
                Value descriptionValue = bs.getValue("description");
                Value imageValue = bs.getValue("image");
                String title = titleValue.stringValue();
                rowValues[0] = title;
                String description = descriptionValue.stringValue();
                rowValues[1] = description;
                String image = imageValue.stringValue();
                rowValues[2] = image;
                Log.i(TAG,  title + ", " + description + ", " + image);

                cursorCulturalInterests.addRow(rowValues);
            }
            Log.i(TAG + "number of rows", String.valueOf(cursorCulturalInterests.getCount()));
            if(cursorCulturalInterests.moveToNext()) {
                Log.i(TAG, cursorCulturalInterests.getString(cursorCulturalInterests.getColumnIndex("Title")));
                Log.i(TAG, cursorCulturalInterests.getString(cursorCulturalInterests.getColumnIndex("Description")));
                Log.i(TAG, cursorCulturalInterests.getString(cursorCulturalInterests.getColumnIndex("Image")));
            }
        }
    }
}
