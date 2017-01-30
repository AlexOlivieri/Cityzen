package ch.hevs.datasemlab.cityzen.notifications;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import ch.hevs.datasemlab.cityzen.R;

import static ch.hevs.datasemlab.cityzen.IntroActivity2.NEAREST_INTEREST_LIST;

public class TestParcelable extends AppCompatActivity {

    private final String TAG = TestParcelable.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_parcelable);

        Bundle extras = getIntent().getExtras();
        extras.setClassLoader(NearestCulturalInterestInfo.class.getClassLoader());

        if(extras==null){
            Log.i(TAG, "no extras added");
        }else{
            List<NearestCulturalInterestInfo> infos = extras.getParcelableArrayList(NEAREST_INTEREST_LIST);
            Log.i(TAG, infos.get(0).getDescription());
        }
    }
}
