package ch.hevs.datasemlab.cityzen.history_new;

import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;

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

import ch.hevs.datasemlab.cityzen.CityzenContracts;
import ch.hevs.datasemlab.cityzen.R;

import static ch.hevs.datasemlab.cityzen.IntroActivity2.TITLE;

public class MainCarouselActivity extends FragmentActivity{

    private final String TAG = MainCarouselActivity.class.getSimpleName();

    private ViewPager viewPager;

    public static final String[] PROJECTION = {"_id", "description", "image", "year"};

    private MatrixCursor mCursorCulturalInterests = new MatrixCursor(PROJECTION);

    private CulturalInterestCursorPagerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);

        String title = getIntent().getStringExtra(TITLE);
        setData(title);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int heightPixels = dm.heightPixels;
        int widthPixels = dm.widthPixels;

        viewPager = (ViewPager) findViewById(R.id.vpPager_2);
        viewPager.setPadding(widthPixels/4, 0, heightPixels/4, 0);
        viewPager.setClipToPadding(false);
        viewPager.setPageMargin(20);
        mAdapter = new CulturalInterestCursorPagerAdapter(getSupportFragmentManager(), CaruselItemFragment.class, PROJECTION, mCursorCulturalInterests);
        viewPager.setAdapter(mAdapter);

    }

    private void setData(String title) {

        final String mTitle = title;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                Repository repo = new SPARQLRepository(CityzenContracts.REPOSITORY_URL);
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

                    qb.append(" SELECT ?imageURL ?date ?description \n ");
                    qb.append(" WHERE { \n");
                    qb.append(" ?culturalInterest dc:title ");
                    qb.append("\"" + mTitle + "\" ; \n ");
                    qb.append("			dc:description ?description . \n");
                    qb.append(" ?digitalrepresentationAggregator edm:aggregatedCHO ?culturalInterest ; \n");
                    qb.append(" 		edm:hasView ?digitalrepresentation ; \n");
                    qb.append(" 		owlTime:hasBeginning ?beginningInstant . \n");
                    qb.append(" ?beginningInstant owlTime:inXSDDateTime ?date . \n");
                    qb.append(" ?digitalrepresentation dcterms:hasPart ?digitalItem . \n");
                    qb.append(" ?digitalItem schema:thumbnail_url ?imageURL . }\n");

                    qb.append(" ORDER BY DESC(?date) \n");

                    Log.i(TAG, qb.toString());

                    result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate();

                    int identifier = 0;

                    byte[] bitmapArray = null;

                    while (result.hasNext()) {

                        Log.i(TAG, "hasResults");

                        BindingSet bs = result.next();

                        Value imageURLValue = bs.getValue("imageURL");
                        Value yearValue = bs.getValue("date");
                        Value descriptionValue = bs.getValue("description");

                        Log.i(TAG, "year: " + imageURLValue.stringValue());
                        Log.i(TAG, "imageURL: " + imageURLValue.toString());
                        Log.i(TAG, "description: " + descriptionValue.stringValue());

                        String imageURL = imageURLValue.stringValue();


                        try {
                            URL url = new URL(imageURL);
                            InputStream inputStream = url.openStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            bitmapArray = stream.toByteArray();
                            inputStream.close();
                            //bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.err.println("Title: ");
                        }

                        String year = yearValue.stringValue();
                        String description = descriptionValue.stringValue();

                        Message msg = handlerCulturalInterestHistory.obtainMessage();

                        Log.i(TAG, "Message Obtained");

                        Bundle bundle = new Bundle();
                        bundle.putString("_id", String.valueOf(identifier));
                        bundle.putString("DESCRIPTION", description);
                        bundle.putByteArray("IMAGE", bitmapArray);
                        bundle.putString("YEAR", year);

                        identifier++;

                        msg.setData(bundle);
                        handlerCulturalInterestHistory.sendMessage(msg);
                    }
                } finally {
                    conn.close();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private Handler handlerCulturalInterestHistory = new Handler() {

        private Object[] rowValues = new Object[4];

        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();

            String identifier = bundle.getString("_id");
            String year = bundle.getString("YEAR");
            byte[] image = bundle.getByteArray("IMAGE");
            String description = bundle.getString("DESCRIPTION");

            rowValues[0] = identifier;
            rowValues[1] = description;
            rowValues[2] = image;
            rowValues[3] = year;

            Log.i(TAG, "Add Row");

            mCursorCulturalInterests.addRow(rowValues);

            if(mCursorCulturalInterests == null){
                Log.i(TAG, "mCursorCulturalInterests is null");
            }

            if(mAdapter == null){
                Log.i(TAG, "adapter is null");
            }
//            viewPager.setAdapter(mAdapter);

            Log.i(TAG, "MatrixCursor size: " +mCursorCulturalInterests.getCount());

            mAdapter.swapCursor(mCursorCulturalInterests);
            Log.i(TAG, "Adapter count: " +mAdapter.getCount());
            viewPager.setAdapter(mAdapter);
        }
    };
}
