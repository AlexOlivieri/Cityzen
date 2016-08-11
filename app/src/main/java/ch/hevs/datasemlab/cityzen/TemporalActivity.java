package ch.hevs.datasemlab.cityzen;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TemporalActivity extends AppCompatActivity {

    private final String TAG = Activity.class.getName();

    private final String STARTING_DATE = "starting_date";

    private SeekBar seekBarStart;
    private TextView textView1;
    private SeekBar seekBarFinish;
    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporal);

        seekBarStart = (SeekBar) findViewById(R.id.seek_bar_start);
        //seekBarStart.setMax();
        seekBarFinish = (SeekBar) findViewById(R.id.seek_bar_finish);
        textView1 = (TextView) findViewById(R.id.edit_text_start);
        textView2 = (TextView) findViewById(R.id.edit_text_finish);
        textView2.setText(getCurrentYear());
        Button button = (Button) findViewById(R.id.button_go);

        String cityzenURL = "http://ec2-52-39-53-29.us-west-2.compute.amazonaws.com:8080/openrdf-sesame/repositories/CityZenDM";

        sendStartingDateQuery(textView1, cityzenURL);

        new GetLastStartingDateTask().execute(cityzenURL);


    }

    private String getCurrentYear() {
        Calendar calendar = new GregorianCalendar();
        int year = calendar.get(Calendar.YEAR);

        return String.valueOf(year);
    }

    Handler handlerStartingDate = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            String date = bundle.getString(STARTING_DATE);

            Log.i(TAG, date);

            textView1.setText(date);
        }
    };


    private void sendStartingDateQuery(View view, String url) {

        final String urlRepository = url;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                Repository repo = new SPARQLRepository(urlRepository);
                repo.initialize();

                RepositoryConnection conn = repo.getConnection();

                String date = null;

                try {
                    StringBuilder qb = new StringBuilder();

                    qb.append("PREFIX : <http://www.hevs.ch/datasemlab/cityzen/schema#> \n");
                    qb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
                    qb.append("PREFIX owlTime: <http://www.w3.org/TR/owl-time#> \n");

                    qb.append(" SELECT DISTINCT ?startingDate \n ");
                    //                    +
                    //                    "(strafter(str(?beginningInstant), \"#\") AS ?date) " +
                    //                    "?place (strafter(str(?place), \"#\") AS ?localisation) \n ");
                    qb.append(" WHERE {?temporalEntity rdf:type owlTime:TemporalEntity ; \n ");
                    qb.append(" owlTime:hasBeginning ?beginningInstant . \n");

                    qb.append(" ?beginningInstant owlTime:inXSDDateTime ?startingDate } ");

                    qb.append("ORDER BY ?startingDate");
                    qb.append(" LIMIT 1 ");

                    TupleQueryResult result =
                            conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate();

                    while (result.hasNext()) {
                        BindingSet bs = result.next();
                        Value dateValue = bs.getValue("startingDate");
                        date = dateValue.stringValue();

                        Message msg = new Message();
                        msg = handlerStartingDate.obtainMessage();

                        Bundle bundle = new Bundle();
                        bundle.putString(STARTING_DATE, date);
                        msg.setData(bundle);
                        handlerStartingDate.sendMessage(msg);

                    }
                    Log.i(TAG, date);
                } finally {
                    conn.close();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private class GetLastStartingDateTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {

            Repository repo = new SPARQLRepository(strings[0]);
            repo.initialize();

            RepositoryConnection conn = repo.getConnection();

            String date = null;

            try {
                StringBuilder qb = new StringBuilder();

                qb.append("PREFIX : <http://www.hevs.ch/datasemlab/cityzen/schema#> \n");
                qb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
                qb.append("PREFIX owlTime: <http://www.w3.org/TR/owl-time#> \n");

                qb.append(" SELECT DISTINCT ?startingDate \n ");
                //                    +
                //                    "(strafter(str(?beginningInstant), \"#\") AS ?date) " +
                //                    "?place (strafter(str(?place), \"#\") AS ?localisation) \n ");
                qb.append(" WHERE {?temporalEntity rdf:type owlTime:TemporalEntity ; \n ");
                qb.append(" owlTime:hasBeginning ?beginningInstant . \n");

                qb.append(" ?beginningInstant owlTime:inXSDDateTime ?startingDate } ");

                qb.append("ORDER BY ?startingDate");
                qb.append(" LIMIT 1 ");

                TupleQueryResult result =
                        conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate();

                while (result.hasNext()) {
                    BindingSet bs = result.next();
                    Value dateValue = bs.getValue("startingDate");
                    date = dateValue.stringValue();

                    Message msg = new Message();
                    msg = handlerStartingDate.obtainMessage();

                    Bundle bundle = new Bundle();
                    bundle.putString(STARTING_DATE, date);
                    msg.setData(bundle);
                    handlerStartingDate.sendMessage(msg);

                }
                Log.i(TAG, date);
            } finally {
                conn.close();
            }
            return date;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            textView2.setText(s);
        }
    }
}