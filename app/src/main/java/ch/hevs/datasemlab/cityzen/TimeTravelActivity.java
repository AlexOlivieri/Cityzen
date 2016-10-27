package ch.hevs.datasemlab.cityzen;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;

import java.util.ArrayList;

import ch.hevs.datasemlab.cityzen.timetravel.ScreenSlidePagerAdapter;

public class TimeTravelActivity extends FragmentActivity {

    private static final String TAG = TimeTravelActivity.class.getSimpleName();

    private String title;

    private TextView countTextView;

    private int numberOfPages;

    private ViewPager mPager;

    private PagerAdapter mPagerAdapter;

    private int selectedPage;

    public static ArrayList<String> mCulturalInterestsList;
    public static ArrayList<String> mDatesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_travel);

        Bundle extras = getIntent().getExtras();
        title = extras.getString(CityzenContracts.TITLE);
        numberOfPages = extras.getInt(CityzenContracts.NUMBER_OF_CULTURAL_INTERESTS);
        mCulturalInterestsList = extras.getStringArrayList(CityzenContracts.IMAGES_ARRAY_LIST);
        mDatesList = extras.getStringArrayList(CityzenContracts.DATES_ARRAY_LIST);

    //TODO
//        TextView titleView = (TextView) findViewById(R.id.title_text_view);
//        titleView.setText(title);
//        countTextView = (TextView) findViewById(R.id.count_text_view);

//        new GetCulturalInterestsList().execute(title);
//        new GetCulturalInterestsNumberAsyncTask().execute(title);

        mPager = (ViewPager) findViewById(R.id.pager);

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), numberOfPages, title, mCulturalInterestsList, mDatesList);
        mPager.setAdapter(mPagerAdapter);
//        mPager.setPageTransformer(true, new RotateDownTransformer());
        mPager.setPageTransformer(true, new CubeOutTransformer());

        selectedPage = 0;
        if(savedInstanceState != null){
            selectedPage = savedInstanceState.getInt("SELECTED_PAGE");
        }

        mPager.setCurrentItem(selectedPage);
    }

//    private class GetCulturalInterestsList extends AsyncTask<String, Void, TupleQueryResult> {
//
//        @Override
//        protected TupleQueryResult doInBackground(String... strings) {
//
//            String title = strings[0];
//
//            Repository repo = new SPARQLRepository(TemporalActivity.REPOSITORY_URL);
//            repo.initialize();
//
//            RepositoryConnection conn = repo.getConnection();
//
//            TupleQueryResult result = null;
//
//            try {
//                StringBuilder qb = new StringBuilder();
//
//                qb.append("PREFIX schema: <http://www.hevs.ch/datasemlab/cityzen/schema#> \n");
//                qb.append("PREFIX owlTime: <http://www.w3.org/TR/owl-time#> \n");
//                qb.append("PREFIX edm: <http://www.europeana.eu/schemas/edm#> \n");
//                qb.append("PREFIX dc: <http://purl.org/dc/elements/1.1/> \n");
//                qb.append("PREFIX dcterms: <http://purl.org/dc/terms/> \n");
//
//                qb.append(" SELECT ?date ?imageURL \n ");
//                qb.append(" WHERE { \n");
//                qb.append(" ?culturalInterest dc:title ");
//                qb.append("\"" +title + "\" ; \n ");
//                qb.append("			dc:description ?description . \n");
//                qb.append(" ?digitalrepresentationAggregator edm:aggregatedCHO ?culturalInterest ; \n");
//                qb.append(" 		edm:hasView ?digitalrepresentation ; \n");
//                qb.append(" 		owlTime:hasBeginning ?beginningInstant . \n");
//                qb.append(" ?beginningInstant owlTime:inXSDDateTime ?date . \n");
//                qb.append(" ?digitalrepresentation dcterms:hasPart ?digitalItem . \n");
//                qb.append(" ?digitalItem schema:image_url ?imageURL . }\n");
//
//                qb.append(" ORDER BY DESC(?date) \n");
//
//                result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate();
//
//            } finally {
//                conn.close();
//            }
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(TupleQueryResult result) {
//            super.onPostExecute(result);
//            int i=0;
//
//            culturalInterestsList = new ArrayList<>();
//            datesList = new ArrayList<>();
//
//            while (result.hasNext()) {
//                BindingSet bs = result.next();
//
//                Value dateValue = bs.getValue("date");
//                Value imageURLValue = bs.getValue("imageURL");
//
//                String date = dateValue.stringValue();
//                String imageURL = imageURLValue.stringValue();
//
//                Log.i(TAG, "date: " + date);
//                Log.i(TAG, "imageURL: " + imageURL);
//
//                datesList.add(date);
//                culturalInterestsList.add(imageURL);
//            }
//            Log.i(TAG, "date List size:" + datesList.size());
//            Log.i(TAG, "url List size:" + culturalInterestsList.size());
//        }
//    }
}
