package ch.hevs.datasemlab.cityzen.history;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import ch.hevs.datasemlab.cityzen.R;
import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;

import static ch.hevs.datasemlab.cityzen.IntroActivity2.TITLE;

public class MainCarouselActivity extends AppCompatActivity {

    private final String TAG = MainCarouselActivity.class.getSimpleName();

    private TextView titleTextView;
    private TextView descriptionTextView;

    private FeatureCoverFlow coverFlow;
    private CoverFlowAdapter adapter;

    private ArrayList<CulturalItem> culturalItems;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_carousel);

        mContext = getApplicationContext();

        //TODO
//        titleTextView = (TextView) findViewById(R.id.text_view_historical_title);
//        descriptionTextView = (TextView) findViewById(R.id.text_view_historical_description);

        String title = getIntent().getStringExtra(TITLE);
        Log.i(TAG, title);

        coverFlow = (FeatureCoverFlow) findViewById(R.id.coverflow);

        //        setData(title);

        new GetCulturalItems().execute(title);

//        culturalItems = new ArrayList<CulturalItem>();
        culturalItems = setStaticData();
        adapter = new CoverFlowAdapter(this, culturalItems);
        coverFlow.setAdapter(adapter);

//        adapter = new CoverFlowAdapter(this, culturalItems);
//        coverFlow.setAdapter(adapter);
//        coverFlow.setOnScrollPositionListener(onScrollListener());

//        adapter = new CoverFlowAdapter(this);
//          coverFlow.setAdapter(adapter);
//        coverFlow.setOnScrollPositionListener(onScrollListener());
    }

    private FeatureCoverFlow.OnScrollPositionListener onScrollListener() {
        return new FeatureCoverFlow.OnScrollPositionListener() {
            @Override
            public void onScrolledToPosition(int position) {
                Log.v("MainActiivty", "position: " + position);
            }

            @Override
            public void onScrolling() {
                Log.i("MainActivity", "scrolling");
            }
        };
    }

//    private void settingDummyData() {
//        games = new ArrayList<>();
//        games.add(new Game(R.mipmap.ic_launcher, "Assassin Creed 3"));
//        games.add(new Game(R.mipmap.ic_launcher, "Avatar 3D"));
//        games.add(new Game(R.mipmap.ic_launcher, "Call Of Duty Black Ops 3"));
//        games.add(new Game(R.mipmap.ic_launcher, "DotA 2"));
//        games.add(new Game(R.mipmap.ic_launcher, "Halo 5"));
//        games.add(new Game(R.mipmap.ic_launcher, "Left 4 Dead 2"));
//        games.add(new Game(R.mipmap.ic_launcher, "StarCraft"));
//        games.add(new Game(R.mipmap.ic_launcher, "The Witcher 3"));
//        games.add(new Game(R.mipmap.ic_launcher, "Tom raider 3"));
//        games.add(new Game(R.mipmap.ic_launcher, "Need for Speed Most Wanted"));
//    }

    private ArrayList<CulturalItem> setStaticData() {

        CulturalItem culturalItem;
        ArrayList<CulturalItem> arrayListOfHistoricalCulturalItem = new ArrayList<CulturalItem>();

        String mTitle="Title";
        String year = "196";
        String description;

        for(int i=0; i<6; i++){
            mTitle = mTitle.concat(String.valueOf(i));
            year = year.concat(String.valueOf(i));
            description = String.valueOf(i);
            byte[] imageAsByteArray = null;
            Bitmap bitmap = null;
            BitmapFactory.decodeResource(getBaseContext().getResources(), 1);
            if(i%2 == 1) {
                bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.image1);
                imageAsByteArray = fromBitmapToByteArray(bitmap);
            }else{
                bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.image2);
                imageAsByteArray = fromBitmapToByteArray(bitmap);
            }
            culturalItem = new CulturalItem(mTitle, imageAsByteArray, year, description);
            arrayListOfHistoricalCulturalItem.add(culturalItem);
        }
        return arrayListOfHistoricalCulturalItem;
    }

    private class GetCulturalItems extends AsyncTask<String, Void, ArrayList<CulturalItem>> {
//    private class GetCulturalItems extends AsyncTask<String, Void, String> {

        @Override
        protected ArrayList<CulturalItem> doInBackground(String... strings) {
//        protected String doInBackground(String... strings) {

            final String mTitle = strings[0];

//                Repository repo = new SPARQLRepository(TemporalActivity.REPOSITORY_URL);
//                repo.initialize();
//
//                RepositoryConnection conn = repo.getConnection();
//
//                TupleQueryResult resultQuery = null;
//
//                try {
//                    StringBuilder qb = new StringBuilder();
//
//                    qb.append("PREFIX schema: <http://www.hevs.ch/datasemlab/cityzen/schema#> \n");
//                    qb.append("PREFIX owlTime: <http://www.w3.org/TR/owl-time#> \n");
//                    qb.append("PREFIX edm: <http://www.europeana.eu/schemas/edm#> \n");
//                    qb.append("PREFIX dc: <http://purl.org/dc/elements/1.1/> \n");
//                    qb.append("PREFIX dcterms: <http://purl.org/dc/terms/> \n");
//
//                    qb.append(" SELECT ?imageURL ?date ?description \n ");
//                    qb.append(" WHERE { \n");
//                    qb.append(" ?culturalInterest dc:title ");
//                    qb.append("\"" + mTitle + "\" ; \n ");
//                    qb.append("			dc:description ?description . \n");
//                    qb.append(" ?digitalrepresentationAggregator edm:aggregatedCHO ?culturalInterest ; \n");
//                    qb.append(" 		edm:hasView ?digitalrepresentation ; \n");
//                    qb.append(" 		owlTime:hasBeginning ?beginningInstant . \n");
//                    qb.append(" ?beginningInstant owlTime:inXSDDateTime ?date . \n");
//                    qb.append(" ?digitalrepresentation dcterms:hasPart ?digitalItem . \n");
//                    qb.append(" ?digitalItem schema:thumbnail_url ?imageURL . }\n");
//
//                    qb.append(" ORDER BY DESC(?date) \n");
//
//                    Log.i(TAG, qb.toString());
//
//                    resultQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate();
//
//                } finally {
//                    conn.close();
//                }

            CulturalItem culturalItem;
            ArrayList<CulturalItem> result = new ArrayList<CulturalItem>();

            String year = "196";
            String description;

            for(int i=0; i<6; i++){
                year = year.concat(String.valueOf(i));
                description = String.valueOf(i);
                Log.i(TAG, "Desciption: " + description);
                byte[] imageAsByteArray = null;
                Bitmap bitmap = null;
                BitmapFactory.decodeResource(getBaseContext().getResources(), 1);
                if(i%2 == 1) {
                    bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.image1);
                    imageAsByteArray = fromBitmapToByteArray(bitmap);
                }else{
                    bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.image2);
                    imageAsByteArray = fromBitmapToByteArray(bitmap);
                }
                culturalItem = new CulturalItem(mTitle, imageAsByteArray, year, description);

                result.add(culturalItem);
            }

//                ArrayList historicalTitlesList = new ArrayList<>();
//                ArrayList historicalImagesList = new ArrayList<>();
//                ArrayList historicalYearyList = new ArrayList<>();
//                ArrayList historicalDescriptionsList = new ArrayList<>();
//
//                int i=1;
//
//                while (resultQuery.hasNext()) {
//
//                    Log.i(TAG, "hasResults");
//
//                    BindingSet bs = resultQuery.next();
//
//                    Value imageURLValue = bs.getValue("imageURL");
//                    Value yearValue = bs.getValue("date");
//                    Value descriptionValue = bs.getValue("description");
//
//                    Log.i(TAG, "year: " + imageURLValue.stringValue());
//                    Log.i(TAG, "imageURL: " + imageURLValue.toString());
//                    Log.i(TAG, "description: " + descriptionValue.stringValue());
//
//                    String imageURL = imageURLValue.stringValue();
//                    URL url = null;
//                    Bitmap bitmap = null;
//
//                    try {
//                        url = new URL(imageURL);
//                        InputStream inputStream = url.openStream();
//                        bitmap = BitmapFactory.decodeStream(inputStream);
//                        inputStream.close();
//                        //bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    }catch (MalformedURLException e){
//                        e.printStackTrace();
//                    }catch (IOException e) {
//                        e.printStackTrace();
//                        System.err.println("Title: ");
//                    }
//
//
//                    Log.i(TAG, "After InputStream");
//
//                    byte[] imageAsByteArray = fromBitmapToByteArray(bitmap);
//
//                    String year = yearValue.stringValue();
//                    String description = descriptionValue.stringValue();
//
//                    Log.i(TAG, "year: " + year);
//                    Log.i(TAG, "imageURL: " + imageURL);
//                    Log.i(TAG, "description: " + description);
//
//                    culturalItem = new CulturalItem(mTitle, imageAsByteArray, year, description);
//                    Log.i(TAG, "Size pre: " + String.valueOf(result.size()));
//                    result.add(culturalItem);
//                    Log.i(TAG, "Size post: " + String.valueOf(result.size()));
////                    Log.i(TAG, "Array Title: " + arrayListOfHistoricalCulturalItem.get(i).getTitle());
////                    Log.i(TAG, "Array Year: " + arrayListOfHistoricalCulturalItem.get(i).getYear());
////                    Log.i(TAG, "Array Description: " + arrayListOfHistoricalCulturalItem.get(i).getDescription());
////                    i++;
//                }
            Log.i(TAG, "Cultural Item Size: " + result.size());

            return result;
//            return "String";
        }

        @Override
        protected void onPostExecute(ArrayList<CulturalItem> result) {
//        protected void onPostExecute(String result) {
            super.onPostExecute(result);

//            Log.i(TAG, "Cultural Item Array Size: " + result.size());

            Log.i(TAG, "Cultural Item Array Size: " + result);

            Log.i(TAG, "Count: " + String.valueOf(adapter.getCount()));
//            adapter = new CoverFlowAdapter(mContext);
//            adapter = new CoverFlowAdapter(mContext,result);
//            coverFlow.setAdapter(adapter);
//            coverFlow.setOnScrollPositionListener(onScrollListener());
            adapter.updateEntries(result);
            adapter.notifyDataSetChanged();
            Log.i(TAG, "Count: " + String.valueOf(adapter.getCount()));
        }
    }

//    private void setData(String title) {
//
//        final String mTitle = title;
//
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {

//                Repository repo = new SPARQLRepository(TemporalActivity.REPOSITORY_URL);
//                repo.initialize();
//
//                RepositoryConnection conn = repo.getConnection();
//
//                TupleQueryResult result = null;
//
//                try {
//                    StringBuilder qb = new StringBuilder();
//
//                    qb.append("PREFIX schema: <http://www.hevs.ch/datasemlab/cityzen/schema#> \n");
//                    qb.append("PREFIX owlTime: <http://www.w3.org/TR/owl-time#> \n");
//                    qb.append("PREFIX edm: <http://www.europeana.eu/schemas/edm#> \n");
//                    qb.append("PREFIX dc: <http://purl.org/dc/elements/1.1/> \n");
//                    qb.append("PREFIX dcterms: <http://purl.org/dc/terms/> \n");
//
//                    qb.append(" SELECT ?imageURL ?date ?description \n ");
//                    qb.append(" WHERE { \n");
//                    qb.append(" ?culturalInterest dc:title ");
//                    qb.append("\"" + mTitle + "\" ; \n ");
//                    qb.append("			dc:description ?description . \n");
//                    qb.append(" ?digitalrepresentationAggregator edm:aggregatedCHO ?culturalInterest ; \n");
//                    qb.append(" 		edm:hasView ?digitalrepresentation ; \n");
//                    qb.append(" 		owlTime:hasBeginning ?beginningInstant . \n");
//                    qb.append(" ?beginningInstant owlTime:inXSDDateTime ?date . \n");
//                    qb.append(" ?digitalrepresentation dcterms:hasPart ?digitalItem . \n");
//                    qb.append(" ?digitalItem schema:thumbnail_url ?imageURL . }\n");
//
//                    qb.append(" ORDER BY DESC(?date) \n");
//
//                    Log.i(TAG, qb.toString());
//
//                    result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate();
//
//                } finally {
//                    conn.close();
//                }
//
//                CulturalItem culturalItem;
//                ArrayList<CulturalItem> arrayListOfHistoricalCulturalItem = new ArrayList<CulturalItem>();
//
//                ArrayList historicalTitlesList = new ArrayList<>();
//                ArrayList historicalImagesList = new ArrayList<>();
//                ArrayList historicalYearyList = new ArrayList<>();
//                ArrayList historicalDescriptionsList = new ArrayList<>();
//
//                int i=1;
//
//                while (result.hasNext()) {
//
//                    Log.i(TAG, "hasResults");
//
//                    BindingSet bs = result.next();
//
//                    Value imageURLValue = bs.getValue("imageURL");
//                    Value yearValue = bs.getValue("date");
//                    Value descriptionValue = bs.getValue("description");
//
//                    Log.i(TAG, "year: " + imageURLValue.stringValue());
//                    Log.i(TAG, "imageURL: " + imageURLValue.toString());
//                    Log.i(TAG, "description: " + descriptionValue.stringValue());
//
//                    String imageURL = imageURLValue.stringValue();
//                    URL url = null;
//                    Bitmap bitmap = null;
//
//                    try {
//                        url = new URL(imageURL);
//                        InputStream inputStream = url.openStream();
//                        bitmap = BitmapFactory.decodeStream(inputStream);
//                        inputStream.close();
//                        //bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    }catch (MalformedURLException e){
//                        e.printStackTrace();
//                    }catch (IOException e) {
//                        e.printStackTrace();
//                        System.err.println("Title: ");
//                    }
//
//
//                    Log.i(TAG, "After InputStream");
//
//                    byte[] imageAsByteArray = fromBitmapToByteArray(bitmap);
//
//                    String year = yearValue.stringValue();
//                    String description = descriptionValue.stringValue();
//
//                    Log.i(TAG, "year: " + year);
//                    Log.i(TAG, "imageURL: " + imageURL);
//                    Log.i(TAG, "description: " + description);
//
//                    culturalItem = new CulturalItem(mTitle, imageAsByteArray, year, description);
//                    Log.i(TAG, "Size pre: " + String.valueOf(arrayListOfHistoricalCulturalItem.size()));
//                    arrayListOfHistoricalCulturalItem.add(culturalItem);
//                    Log.i(TAG, "Size post: " + String.valueOf(arrayListOfHistoricalCulturalItem.size()));
////                    Log.i(TAG, "Array Title: " + arrayListOfHistoricalCulturalItem.get(i).getTitle());
////                    Log.i(TAG, "Array Year: " + arrayListOfHistoricalCulturalItem.get(i).getYear());
////                    Log.i(TAG, "Array Description: " + arrayListOfHistoricalCulturalItem.get(i).getDescription());
////                    i++;
//                }

//                Log.i(TAG, "Message Obtained");
//
//                Log.i(TAG, String.valueOf(arrayListOfHistoricalCulturalItem.size()));
//                Message msg = new Message();
//                msg = handlerCulturalInterestHistory.obtainMessage();
//
//                Log.i(TAG, "Message Obtained");
//
//                Bundle bundle = new Bundle();
//                bundle.putParcelableArrayList(NEAREST_INTEREST_LIST, arrayListOfHistoricalCulturalItem);
//
//                Log.i(TAG, "Bundle Created");
//
//                msg.setData(bundle);
//
//                Log.i(TAG, "Message Created");
//
//                handlerCulturalInterestHistory.sendMessage(msg);
//            }
//        };
//        Thread thread = new Thread(runnable);
//        thread.start();
//    }

//    private Handler handlerCulturalInterestHistory = new Handler() {
//
////        private Object[] rowValues = new Object[3];
//
//        @Override
//        public void handleMessage(Message msg) {
//
//            Bundle bundle = msg.getData();
//
//            culturalItems = bundle.getParcelableArrayList(NEAREST_INTEREST_LIST);
//
//            Log.i(TAG, "Inside Handler, CulturalItems Size: " + culturalItems.size());
//
//            adapter.updateEntries(culturalItems);
//        }
//    };

    private byte[] fromBitmapToByteArray(Bitmap image){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        boolean compression = image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Log.i(TAG, "is compressed? " + compression);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}
