package ch.hevs.datasemlab.cityzen;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class CulturalInterestsGalleryActivity3 extends AppCompatActivity {

    private static int mStartingDate;
    private static int mFinishingDate;

    private FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cultural_interests_gallery3);

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
