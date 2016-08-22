package ch.hevs.datasemlab.cityzen;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

    private final String TAG = TemporalActivity.class.getSimpleName();

    public static final String REPOSITORY_URL = "http://ec2-52-39-53-29.us-west-2.compute.amazonaws.com:8080/openrdf-sesame/repositories/CityZenDM";

    private int currentYear;

    private SeekBar seekBarStart;
    private TextView textView1;
    private SeekBar seekBarFinish;
    private TextView textView2;
    private Button button;

    private int oldestStartingDate;
    private String oldestDate;

    private int startingDateFromPreferences;
    private int finishingDateFromPreferences;

    private SharedPreferences sharedPreferences;

    private ConnectivityManager check;
    private boolean isConnected = false;

    private IntentFilter networkingFilter;

    public NetworkChangeBroadcastReceiver networkChangeBroadcastReceiver;

    Handler networkChangeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            Bundle bundle = msg.getData();
            boolean networkState = bundle.getBoolean(CityzenContracts.NETWORK_STATE);
            if(networkState){
                button.setClickable(networkState);
                seekBarStart.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });

                seekBarFinish.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                new GetOldestStartingDateTask().execute(REPOSITORY_URL);
            }
            Toast.makeText(getApplicationContext(), String.valueOf(networkState), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporal);

        /////////////////////////////////////////////////////////////////
        ///             Receiver for Networking Changes
        /////////////////////////////////////////////////////////////////
        networkingFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeBroadcastReceiver = new NetworkChangeBroadcastReceiver(networkChangeHandler);
        registerReceiver(networkChangeBroadcastReceiver, networkingFilter);



        Log.i(TAG, "On Create");

        //sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = this.getSharedPreferences(CityzenContracts.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);

        startingDateFromPreferences = sharedPreferences.getInt(CityzenContracts.STARTING_DATE, -1);
        finishingDateFromPreferences = sharedPreferences.getInt(CityzenContracts.FINISHING_DATE, -1);
//        Log.i(TAG, startingDateFromPreferences + " - " + finishingDateFromPreferences);



        ///////////////////////////////////////////////////////////////////////////////
        //
        // If the data have been already chosen, and we do not waant to modify them,
        // we retrieve the data from the content provider.
        //
        ///////////////////////////////////////////////////////////////////////////////


        seekBarStart = (SeekBar) findViewById(R.id.seek_bar_start);
        seekBarFinish = (SeekBar) findViewById(R.id.seek_bar_finish);
        textView1 = (TextView) findViewById(R.id.edit_text_start);
        textView2 = (TextView) findViewById(R.id.edit_text_finish);

        button = (Button) findViewById(R.id.button_go);
        Log.i(TAG, "Button: " + String.valueOf(button.isClickable()));

        currentYear = getCurrentYear();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i(TAG, "On Pause");
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.i(TAG, "On Resume");

        if (startingDateFromPreferences != -1 && finishingDateFromPreferences != -1) {

            Log.i(TAG, "Date from Preferences: " + startingDateFromPreferences + " - " + finishingDateFromPreferences);

            Intent intent = new Intent(this, CulturalInterestsGalleryActivityFromDB.class);
            intent.putExtra(CityzenContracts.STARTING_DATE, startingDateFromPreferences);
            intent.putExtra(CityzenContracts.FINISHING_DATE, finishingDateFromPreferences);

            startActivity(intent);
        }else {

            check = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = check.getActiveNetworkInfo();
            isConnected = networkInfo != null && networkInfo.isConnected();
            if (isConnected) {
                button.setClickable(true);
            } else {
                button.setClickable(false);
            }
            //            NetworkInfo[] info = check.getAllNetworkInfo();
            //            for (int i = 0; i < info.length; i++) {
            //                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
            //                    isConnected = true;
            //                    Log.i(TAG, "Internet is connected");
            //                    button.setClickable(true);
            //                    break;
            //                } else {
            //                    Log.i(TAG, "Internet is not connected");
            //                    button.setClickable(false);
            //                }
            //            }

            if (isConnected) {
                //TODO perform all tasks from internet
                new GetOldestStartingDateTask().execute(REPOSITORY_URL);
            } else {
                //TODO Take info from SQLite if presents
                seekBarStart.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });

                seekBarFinish.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                Toast.makeText(this, "Please Connect to internet", Toast.LENGTH_SHORT).show();
            }


//            if (startingDateFromPreferences != -1 && finishingDateFromPreferences != -1) {
//
//                oldestStartingDate = sharedPreferences.getInt(oldestDate, -1);
//
//                Log.i(TAG, "OldestStartingDate: " + oldestStartingDate);
//
//                seekBarStart.setMax(currentYear - oldestStartingDate);
//                seekBarFinish.setMax(currentYear - oldestStartingDate);
//
//                ////////////////////////////////////////////////////////////////////////
//                /// The set progress for the two bars works differently
//                /// - SeekBarStart must be set from the subtracting: STARTING CHOSEN - OLDEST
//                /// - SeekBarFinish must be set from the subtracting: CURRENT YEAR - FINISHING CHOSEN
//                ////////////////////////////////////////////////////////////////////////
//                seekBarStart.setProgress(startingDateFromPreferences - oldestStartingDate);
//                seekBarFinish.setProgress(currentYear - finishingDateFromPreferences);
//            }

            seekBarStart.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {

                    check = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = check.getActiveNetworkInfo();
                    isConnected = networkInfo != null && networkInfo.isConnected();

                    if (isConnected) {
                        seekBar.setMax(currentYear - oldestStartingDate);
                        //                Log.i(TAG + "Progress Value", String.valueOf(progressValue));
                        int chosenStartingDate = oldestStartingDate + progressValue;

                        //                Log.i(TAG + " ChosenStartingDate", String.valueOf(chosenStartingDate));

                        if (isLegalMove(chosenStartingDate)) {
                            textView1.setText(String.valueOf(chosenStartingDate));
                            seekBar.setProgress(seekBar.getProgress());
                        } else {
                            Toast.makeText(getApplicationContext(), "Starting Date can not be successive to the Finishing Date", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //TODO Take info from SQLite if presents
                        Toast.makeText(getApplicationContext(), "Internet is not connected", Toast.LENGTH_SHORT).show();
                    }
                }

                private boolean isLegalMove(int chosenStartingDate) {
                    //               Log.i(TAG + " finishing time : ", String.valueOf(chosenStartingDate));
                    if (chosenStartingDate <= Integer.parseInt(textView2.getText().toString())) {
                        return true;
                    } else {
                        return false;
                    }
                }

                // TODO
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //                Log.i(TAG + "BarStart:onStart", "To Define");
                }

                // TODO
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //                Log.i(TAG + "BarStart:onStop", "To Define");
                }
            });

            //int maxFinish = oldestFinishingDate - newestFinishingDate;
            //seekBarFinish.setMax(maxFinish);

            seekBarFinish.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {

                    check = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = check.getActiveNetworkInfo();
                    isConnected = networkInfo != null && networkInfo.isConnected();

                    if (isConnected) {

                        seekBar.setMax(currentYear - oldestStartingDate);
                        //                Log.i(TAG + "Progress Value", String.valueOf(progressValue));
                        int chosenFinishingDate = currentYear - progressValue;

                        ///                Log.i(TAG + " ChosenFinishingDate: ", String.valueOf(chosenFinishingDate));

                        if (isLegalMove(chosenFinishingDate)) {
                            textView2.setText(String.valueOf(chosenFinishingDate));
                            seekBar.setProgress(seekBar.getProgress());
                        } else {
                            Toast.makeText(getApplicationContext(), "Finishing Date can not be antecedent to the Starting Date", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //TODO Take info from SQLite if presents
                        Toast.makeText(getApplicationContext(), "Internet is not connected", Toast.LENGTH_SHORT).show();
                    }
                }

                private boolean isLegalMove(int chosenFinishingValue) {
                    if ((seekBarStart == null) || (chosenFinishingValue >= Integer.parseInt(textView1.getText().toString()))) {
                        return true;
                    } else {
                        return false;
                    }
                }

                // TODO
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //                Log.i(TAG + "BarFinish:onStart", "To Define");
                }

                // TODO
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //                Log.i(TAG + "BarFinish:onStop", "To Define");
                }
            });

            Log.i(TAG, "On Resume");
        }
    }

    public void exploreCulturalInterests(View view){

        check = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = check.getActiveNetworkInfo();
        isConnected = networkInfo != null && networkInfo.isConnected();

        if(isConnected) {

            int startingDate = Integer.parseInt(textView1.getText().toString());
            int finishingDate = Integer.parseInt(textView2.getText().toString());

            //sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences sharedPreferences = this.getSharedPreferences(CityzenContracts.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(CityzenContracts.STARTING_DATE, oldestStartingDate + seekBarStart.getProgress());
            editor.putInt(CityzenContracts.FINISHING_DATE, currentYear - seekBarFinish.getProgress());
            editor.commit();


            Log.i(TAG, "Intent Starting Progress: " + String.valueOf(seekBarStart.getProgress()));
            Log.i(TAG, "Intent Starting Max: " + String.valueOf(seekBarStart.getMax()));
            Log.i(TAG, "Intent Finishing Progress: " + String.valueOf(seekBarFinish.getProgress()));
            Log.i(TAG, "Intent Finishing Max: " + String.valueOf(seekBarFinish.getMax()));

            //Intent exploreCulturalInterestsIntent = new Intent(this, CulturalInterestsGalleryActivity2.class);
            Intent exploreCulturalInterestsIntent = new Intent(this, CulturalInterestsGalleryActivity3.class);
            //        Log.e(TAG + CityzenContracts.STARTING_DATE, textView1.getText().toString());
            //        Log.e(TAG + CityzenContracts.FINISHING_DATE, textView2.getText().toString());
            exploreCulturalInterestsIntent.putExtra(CityzenContracts.STARTING_DATE, startingDate);
            exploreCulturalInterestsIntent.putExtra(CityzenContracts.FINISHING_DATE, finishingDate);


            startActivity(exploreCulturalInterestsIntent);

        }else{
            //TODO Take info from SQLite if presents
            Toast.makeText(getApplicationContext(), "Internet is not connected", Toast.LENGTH_SHORT).show();
        }
    }

    private int getCurrentYear() {
        Calendar calendar = new GregorianCalendar();
        int year = calendar.get(Calendar.YEAR);

        return year;
    }

//    Handler handlerStartingDate = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//
//            Bundle bundle = msg.getData();
//            String date = bundle.getString(STARTING_DATE);
//
//            Log.i(TAG, date);
//
//            textView1.setText(date);
//        }
//    };


//    private void sendStartingDateQuery(View view, String url) {
//
//        final String urlRepository = url;
//
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//
//                Repository repo = new SPARQLRepository(urlRepository);
//                repo.initialize();
//
//                RepositoryConnection conn = repo.getConnection();
//
//                String date = null;
//
//                try {
//                    StringBuilder qb = new StringBuilder();
//
//                    qb.append("PREFIX : <http://www.hevs.ch/datasemlab/cityzen/schema#> \n");
//                    qb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
//                    qb.append("PREFIX owlTime: <http://www.w3.org/TR/owl-time#> \n");
//
//                    qb.append(" SELECT DISTINCT ?startingDate \n ");
//                    //                    +
//                    //                    "(strafter(str(?beginningInstant), \"#\") AS ?date) " +
//                    //                    "?place (strafter(str(?place), \"#\") AS ?localisation) \n ");
//                    qb.append(" WHERE {?temporalEntity rdf:type owlTime:TemporalEntity ; \n ");
//                    qb.append(" owlTime:hasBeginning ?beginningInstant . \n");
//
//                    qb.append(" ?beginningInstant owlTime:inXSDDateTime ?startingDate } ");
//
//                    qb.append("ORDER BY ?startingDate");
//                    qb.append(" LIMIT 1 ");
//
//                    TupleQueryResult result =
//                            conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate();
//
//                    while (result.hasNext()) {
//                        BindingSet bs = result.next();
//                        Value dateValue = bs.getValue("startingDate");
//                        date = dateValue.stringValue();
//
//                        Message msg = new Message();
//                        msg = handlerStartingDate.obtainMessage();
//
//                        Bundle bundle = new Bundle();
//                        bundle.putString(STARTING_DATE, date);
//                        msg.setData(bundle);
//                        handlerStartingDate.sendMessage(msg);
//
//                    }
//                    Log.i(TAG, date);
//                } finally {
//                    conn.close();
//                }
//            }
//        };
//        Thread thread = new Thread(runnable);
//        thread.start();
//    }


    private class GetOldestStartingDateTask extends AsyncTask<String, Void, String>{

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
                qb.append(" WHERE {?temporalEntity rdf:type :DigitalRepresentationAggregator ; \n ");
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

                }
                Log.i(TAG + "date: ", date);
            } finally {
                conn.close();
            }
            return date;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            oldestStartingDate = Integer.parseInt(s);
            Log.i(TAG + "oldestStartingDate", "OnPostExecute " + String.valueOf(oldestStartingDate));

            //sharedPreferences = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences sharedPreferences = getSharedPreferences(CityzenContracts.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(oldestDate, oldestStartingDate);
            editor.commit();

            if(startingDateFromPreferences == -1 || finishingDateFromPreferences == -1 ){
                textView1.setText(s);
                textView2.setText(String.valueOf(currentYear));
            }else{
                textView1.setText(String.valueOf(startingDateFromPreferences));
                textView2.setText(String.valueOf(finishingDateFromPreferences));
            }
        }
    }
}