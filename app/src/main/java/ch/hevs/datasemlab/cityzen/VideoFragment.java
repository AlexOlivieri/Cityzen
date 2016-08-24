package ch.hevs.datasemlab.cityzen;

import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VideoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {
    private final static String TAG = VideoFragment.class.getSimpleName();

    private int mStartingDate;
    private int mFinishingDate;

    private String[] mCursorColumnsTitle = {"_id", "Title", "Image"};

    private MatrixCursor mCursorCulturalInterests = new MatrixCursor(mCursorColumnsTitle);

    private CulturalInterestsAdapter mAdapter;

    private OnFragmentInteractionListener mListener;

    private ListView listView;

    private static Handler mHandler;

    public VideoFragment() {
        // Required empty public constructor
    }


    public static VideoFragment newInstance(int startingDate, int finishingDate, Handler handler) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putInt(CityzenContracts.STARTING_DATE, startingDate);
        args.putInt(CityzenContracts.FINISHING_DATE, finishingDate);
        fragment.setArguments(args);
        Log.i(TAG, "return fragment");
        mHandler = handler;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStartingDate = getArguments().getInt(CityzenContracts.STARTING_DATE);
            mFinishingDate = getArguments().getInt(CityzenContracts.FINISHING_DATE);
        }

        new GetCulturalInterests().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_image, container, false);

        listView = (ListView) view.findViewById(R.id.list_view);

        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CulturalInterestVideoDetailsActivity.class);
                adapterView.getItemAtPosition(position);

                TextView textView = (TextView) view.findViewById(R.id.text_view_cultural_item_title);
                intent.putExtra(CityzenContracts.TITLE, textView.getText());

//                ImageView imageView = (ImageView) view.findViewById(R.id.image_view_cultural_interest_image);
//                Drawable drawable = imageView.getDrawable();
//                BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
//                Bitmap bitmap = bitmapDrawable .getBitmap();
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                byte[] imageInByte = stream.toByteArray();
//                intent.putExtra(CityzenContracts.IMAGE, imageInByte);
                Log.i(TAG, String.valueOf(textView.getText()));
                startActivity(intent);
            }
        });

        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
                qb.append(" { ?digitalrepresentation dc:format \"video/quicktime\" } UNION \n");
                qb.append(" { ?digitalrepresentation dc:format \"video/mp4\" } . \n");
                qb.append(" ?digitalItem schema:thumbnail_url ?image . \n");
                qb.append(" ?digitalrepresentationAggregator owlTime:hasBeginning ?instant . \n");

                qb.append(" ?instant owlTime:inXSDDateTime ?date . ");

                qb.append(" FILTER ( ?date >= \"" + mStartingDate + "\" && ?date <= \"" + mFinishingDate + "\") } ");

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

                mCursorCulturalInterests.addRow(rowValues);
            }
//            Log.i(TAG + "number of rows", String.valueOf(cursorCulturalInterests.getCount()));
//            while(cursorCulturalInterests.moveToNext()) {
//                Log.i(TAG + " id: ", cursorCulturalInterests.getString(cursorCulturalInterests.getColumnIndex("_id")));
//                Log.i(TAG + " Title: ", cursorCulturalInterests.getString(cursorCulturalInterests.getColumnIndex("Title")));
//                Log.i(TAG + " Description: ", cursorCulturalInterests.getString(cursorCulturalInterests.getColumnIndex("Description")));
//                Log.i(TAG + " Image: ", cursorCulturalInterests.getString(cursorCulturalInterests.getColumnIndex("Image")));
//           }

            Log.i(TAG, "before handler");

//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Message msg = new Message();
//                    mHandler.sendMessage(msg);
//                }
//            });

            mAdapter = new CulturalInterestsAdapter(getActivity(), mCursorCulturalInterests, 0);
            listView.setAdapter(mAdapter);
            //adapter.changeCursor(cursorCulturalInterests);
        }
    }
}