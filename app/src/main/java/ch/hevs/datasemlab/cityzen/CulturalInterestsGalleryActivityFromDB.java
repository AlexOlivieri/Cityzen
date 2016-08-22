package ch.hevs.datasemlab.cityzen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class CulturalInterestsGalleryActivityFromDB extends AppCompatActivity {

    private final String TAG = CulturalInterestsGalleryActivityFromDB.class.getSimpleName();

    private static int mStartingDate;
    private static int mFinishingDate;

    private FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cultural_interests_gallery_from_db);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        Bundle extras = getIntent().getExtras();

        mStartingDate = extras.getInt(CityzenContracts.STARTING_DATE);
        mFinishingDate = extras.getInt(CityzenContracts.FINISHING_DATE);

        ViewPager viewPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new CulturalInterestsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_cultural_interests_gallery_from_db, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if(id == R.id.menu_interval) {

            SharedPreferences sharedPreferences = this.getSharedPreferences(CityzenContracts.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(CityzenContracts.STARTING_DATE, -1);
            editor.putInt(CityzenContracts.FINISHING_DATE, -1);
            editor.commit();

            Log.i(TAG, "Item Menu_Interval");

            Intent intent = new Intent(this, TemporalActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class CulturalInterestsPagerAdapter extends FragmentPagerAdapter{

        private static int PAGES_NUMBER = 3;

        public CulturalInterestsPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public int getCount(){
            return PAGES_NUMBER;
        }

        @Override
        public Fragment getItem(int position){
            switch (position){
                case 0:
                    return ImageFragment.newInstance(mStartingDate, mFinishingDate);
                case 1:
                    return VideoFragment.newInstance(mStartingDate, mFinishingDate);
                case 2:
                    return AudioFragment.newInstance(mStartingDate, mFinishingDate);
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position){
            switch (position){
                case 0:
                    return "Images";
                case 1:
                    return "Videos";
                case 2:
                    return "Audio";
                default:
                    return null;
            }
        }
    }
}
