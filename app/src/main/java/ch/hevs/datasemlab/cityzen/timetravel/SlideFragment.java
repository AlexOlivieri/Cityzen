package ch.hevs.datasemlab.cityzen.timetravel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import ch.hevs.datasemlab.cityzen.CityzenContracts;
import ch.hevs.datasemlab.cityzen.R;

public class SlideFragment extends Fragment {
//    public static final String EXTRA_POSITION = "EXTRA_POSITION";
    private static final String TAG = SlideFragment.class.getSimpleName();

    private String mImageURL;

    private ImageView mImageView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slide, container, false);

        int position = getArguments().getInt(CityzenContracts.EXTRA_POSITION);
        Log.i(TAG, "Position: " +position);

        mImageURL = getArguments().getString(CityzenContracts.IMAGES_ARRAY_LIST);
        String date = getArguments().getString(CityzenContracts.DATES_ARRAY_LIST);
        executeImagesQuery();

//        String imageURL= getArguments().getStringArrayList(CityzenContracts.IMAGES_ARRAY_LIST).get(position);
//        String date= getArguments().getStringArrayList(CityzenContracts.DATES_ARRAY_LIST).get(position);

//        String imageURL= ScreenSlidePagerAdapter.culturalInterestsList.get(position);
//        String date= ScreenSlidePagerAdapter.datesList.get(position);

        TextView dateTextView = (TextView)rootView.findViewById(R.id.year_text_view);
        mImageView = (ImageView) rootView.findViewById(R.id.image_view_image_details);
        dateTextView.setText(date);
//        urlTextView.setText("ImageURL");
//        dateTextView.setText("Date");




        //set a random color to the background
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        rootView.setBackgroundColor(color); //set a random color to the background

        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(color), Color.green(color),
                Color.blue(color), hsv);
        hsv[0] = (hsv[0] + 180) % 360;
        int textColor = Color.HSVToColor(hsv);

        dateTextView.setTextColor(textColor);

        TextView publisherTextView = (TextView) rootView.findViewById(R.id.publisher_text_view);
        int provider = rnd.nextInt(100);
        if(provider < 50){
            publisherTextView.setText("Médiathèque du Valais");
        }else{
            publisherTextView.setText("Digital Valais/Wallis");
        }
        publisherTextView.setTextColor(textColor);

        return rootView; //return the slide view
    }

    Handler handlerImage = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] imageByteArray = bundle.getByteArray("Image");

            Bitmap image = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
            mImageView.setImageBitmap(image);
            //mImageView.setImageBitmap(Bitmap.createScaledBitmap(image, 800, 800, false));

        }
    };

    private void executeImagesQuery() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {


                URL url = null;
                Bitmap bitmap;

                byte[] bitmapArray = null;
                try {
                    url = new URL(mImageURL);
                    InputStream inputStream = url.openStream();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    options.inDither = true;
                    bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    bitmapArray = stream.toByteArray();
                }catch (MalformedURLException e){
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Wrong Title");
                }

                Message msg = new Message();
                msg = handlerImage.obtainMessage();

                Bundle bundle = new Bundle();
                bundle.putByteArray("Image", bitmapArray);

                msg.setData(bundle);
                handlerImage.sendMessage(msg);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}