package ch.hevs.datasemlab.cityzen.notifications;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import ch.hevs.datasemlab.cityzen.R;

import static ch.hevs.datasemlab.cityzen.IntroActivity2.NEAREST_INTEREST_LIST;

public class NotificationReceiverActivity extends AppCompatActivity {

    private final String TAG = NotificationReceiverActivity.class.getSimpleName();

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_receiver);

        textView = (TextView) findViewById(R.id.text_view_notification);

        Bundle extras = getIntent().getExtras();
        extras.setClassLoader(getClassLoader());

        if(extras==null){
            Log.i(TAG, "no extras added");
        }

        //String message = extras.getString(IntroActivity.GPS_POSITION);

        Log.i(TAG, "Size" + ", " + extras.size());

        if(extras.getParcelableArrayList(NEAREST_INTEREST_LIST)==null){
            Log.i(TAG, "No Nearest Interest List");
        }else {

            List<NearestCulturalInterestInfo> culturalInterestInfos = extras.getParcelableArrayList(NEAREST_INTEREST_LIST);
            Log.i(TAG, culturalInterestInfos.get(0).getDescription());

            textView.setText(culturalInterestInfos.get(0).getDescription());
        }

//        Log.i(TAG, "Size: " + String.valueOf(culturalInterestInfos.size()));
    }
}


