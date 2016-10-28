package ch.hevs.datasemlab.cityzen.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hevs.datasemlab.cityzen.R;

public class ItineraryActivity extends AppCompatActivity {

    private final String TAG = ItineraryActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.itinery), Context.MODE_PRIVATE);

        Map<String, ?> sharedPrefMap = sharedPreferences.getAll();

        Log.i(TAG, "OnCreate: sharedPreferences size = " + sharedPreferences.getAll().size());
        Log.i(TAG, "OnCreate: SharedPrefMap size = " + sharedPrefMap.size());

        String[] interestsForItinerary = new String[sharedPrefMap.size()];

        for(int i=0; i<sharedPrefMap.size(); i++) {
            Object objectValue = sharedPrefMap.get(String.valueOf(i));
            String value = objectValue.toString();
            if(objectValue == null){
                Log.i(TAG, "Object Value is null");
            }else{
                Log.i(TAG, "Object Value is not null");
            }
            Log.i(TAG, "Preference Value: " + value);
            interestsForItinerary[i] = value;
        }

        final ListView listView = (ListView) findViewById(R.id.list_view_itinerary);

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i<interestsForItinerary.length; i++){
            list.add(interestsForItinerary[i]);
        }

        final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

    }

    private class StableArrayAdapter extends ArrayAdapter<String>{
        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects){
            super(context, textViewResourceId, objects);

            for(int i=0; i<objects.size(); ++i){
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position){
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds(){
            return true;
        }
    }
}


