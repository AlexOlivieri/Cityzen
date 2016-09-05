package ch.hevs.datasemlab.cityzen.timetravel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;

import ch.hevs.datasemlab.cityzen.CityzenContracts;

/**
 * Created by Alex on 9/4/2016.
 */
public class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = ScreenSlidePagerAdapter.class.getSimpleName();

    private int mNumberOfPages;

    public static ArrayList<String> mCulturalInterestsList;
    public static ArrayList<String> mDatesList;

    private String mTitle;

    public ScreenSlidePagerAdapter(FragmentManager fm, int numberOfPages, String title,  ArrayList<String> culturalInterestsList, ArrayList<String> datesList){
        super(fm);

        this.mNumberOfPages = numberOfPages;
        this.mTitle = title;
        this.mCulturalInterestsList = culturalInterestsList;
        this.mDatesList = datesList;

//        new GetCulturalInterestsList().execute(title);

    }

    @Override
    public Fragment getItem(int position) {
        Log.i(TAG, "Position: " +position);
        final Bundle bundle = new Bundle();
        bundle.putInt(CityzenContracts.EXTRA_POSITION, position);
        bundle.putString(CityzenContracts.TITLE, mTitle);
        bundle.putString(CityzenContracts.IMAGES_ARRAY_LIST, mCulturalInterestsList.get(position));
        bundle.putString(CityzenContracts.DATES_ARRAY_LIST, mDatesList.get(position));

//        if(culturalInterestsList == null){
//            Log.i(TAG, "CulturalInterestsList is null");
//        }
//        bundle.putStringArrayList(CityzenContracts.IMAGES_ARRAY_LIST, culturalInterestsList);
//        bundle.putStringArrayList(CityzenContracts.DATES_ARRAY_LIST, datesList);

        final SlideFragment fragment = new SlideFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return this.mNumberOfPages;
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
