package ch.hevs.datasemlab.cityzen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import java.io.IOException;

public class CulturalInterestAudioDetailsActivity extends AppCompatActivity{

    private static final String TAG = CulturalInterestAudioDetailsActivity.class.getSimpleName();

    private String title;

    private TextView textViewDescription;
    //private VideoView videoView;

    private MediaPlayer audioPlayer;

    private MediaController videoController;

    private String audio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cultural_interest_audio_details);

        Toast.makeText(this, TAG, Toast.LENGTH_SHORT).show();

        Bundle extras = getIntent().getExtras();

        title = extras.getString(CityzenContracts.TITLE);
        byte[] imageByte = extras.getByteArray(CityzenContracts.IMAGE);

        TextView textViewTitle = (TextView) findViewById(R.id.text_view_title_details);
        textViewDescription = (TextView) findViewById(R.id.text_view_description_details);


        textViewTitle.setText(title);

        ImageView imageView = (ImageView) findViewById(R.id.image_view_audio);
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        imageView.setImageBitmap(Bitmap.createScaledBitmap(imageBitmap, 800, 600, false));

        new GetCulturalInterestsDescription().execute(title);

        Log.i(TAG, textViewDescription.getText().toString());
    }

    @Override
    protected void onStop(){
        super.onStop();
        audioPlayer.release();
        audioPlayer = null;
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

                qb.append(" SELECT DISTINCT ?description ?audio \n ");

                qb.append(" WHERE {?culturalInterest dc:title ");
                qb.append("\"" +title + "\" . \n ");
                qb.append(" ?culturalInterest dc:description ?description . \n");
                qb.append(" ?digitalrepresentationAggregator edm:aggregatedCHO ?culturalInterest . \n");
                qb.append(" ?digitalrepresentationAggregator edm:hasView ?digitalrepresentation . \n");
                qb.append(" ?digitalrepresentation dcterms:hasPart ?digitalItem . \n");
                qb.append(" ?digitalItem schema:image_url ?audio . }\n");

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
                Value audioValue = bs.getValue("audio");

                String description = descriptionValue.stringValue();
                audio = audioValue.stringValue();

                Log.i(TAG + "audio", audio);

                Uri audioURI = Uri.parse(audio);

                textViewDescription.setText(description);

                try {
                    audioPlayer = new MediaPlayer();
                    audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    //audioPlayer.setDisplay(audioHolder);
                    audioPlayer.setDataSource(audio);
                    audioPlayer.prepareAsync();
                    //audioPlayer.setOnPreparedListener();
                    audioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer){
                            audioPlayer.start();
                        }
                    });


                }catch(IllegalArgumentException e){
                    e.printStackTrace();
                    System.err.println("The link to the audio file is not a valid URL");
                }catch (IOException e){
                    e.printStackTrace();
                    System.err.println("Problem when getting the stream");
                }
            }
        }
    }
}
