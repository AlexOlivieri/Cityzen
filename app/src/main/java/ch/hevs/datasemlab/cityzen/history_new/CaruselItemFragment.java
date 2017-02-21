package ch.hevs.datasemlab.cityzen.history_new;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ch.hevs.datasemlab.cityzen.R;

public class CaruselItemFragment extends Fragment {

    private static final String TAG = CaruselItemFragment.class.getSimpleName();

    private final static String YEAR = "year";
    private final static String IMAGE = "image";
    private final static String DESCRIPTION = "description";

    private String mYear;
    private byte[] mImage;
    private String mDescription;

    private int mHeight;
    private int mWidth;

    public static CaruselItemFragment newInstance() {
        CaruselItemFragment fragment = new CaruselItemFragment();

        Log.i(TAG, "new Instance");

        return fragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            mDescription = savedInstanceState.getString(DESCRIPTION);
            mImage = savedInstanceState.getByteArray(IMAGE);
            mYear = savedInstanceState.getString(YEAR);
        }else{
            mDescription = getArguments().getString("description", "2016");
            Log.i(TAG, "on Create Argument1: " +mDescription);
            mImage = getArguments().getByteArray("image");
            Log.i(TAG, "on Create Argument2: " +mImage.length);
            mYear = getArguments().getString("year", "2016");
            Log.i(TAG, "on Create Argument3: " +mYear);
        }

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mHeight = dm.heightPixels;
        mWidth = dm.widthPixels;



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_carusel_item, container, false);

        TextView descriptionView = (TextView) view.findViewById(R.id.text_view_historical_description_new);
        if(descriptionView != null)
            descriptionView.setText(mDescription);

        TextView yearView = (TextView) view.findViewById(R.id.text_view_historical_year_new);
        if(yearView != null)
            yearView.setText(mYear);



        ImageView imageView = (ImageView) view.findViewById(R.id.image_view_historical_image_new);
        Bitmap image = BitmapFactory.decodeByteArray(mImage, 0, mImage.length);
//        imageView.setImageBitmap(image);
        imageView.setImageBitmap(Bitmap.createScaledBitmap(image, mWidth/2, mHeight/2, false));

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(DESCRIPTION, mDescription);
        outState.putByteArray(IMAGE, mImage);
        outState.putString(YEAR, mYear);
    }

}
