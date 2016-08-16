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

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

public class CulturalInterestDetailsActivity extends AppCompatActivity {

    private static final String TAG = CulturalInterestDetailsActivity.class.getSimpleName();

    private String title;

    private TextView textViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cultural_interest_details);

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

                qb.append(" SELECT DISTINCT ?description \n ");

                qb.append(" WHERE {?culturalInterest dc:title ");
                qb.append("\"" +title + "\" . \n ");
                qb.append(" ?culturalInterest dc:description ?description . } ");

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

                String description = descriptionValue.stringValue();

                textViewDescription.setText(description);
            }
        }
    }
}
