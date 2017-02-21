package ch.hevs.datasemlab.cityzen.history_old;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import ch.hevs.datasemlab.cityzen.R;

public class GalleryActivity extends Activity
{
    LinearLayout mGallery;
    LinearLayout count_layout;
    TextView page_text[];
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mGallery = (LinearLayout) findViewById(R.id.my_gallery);
        count_layout = (LinearLayout) findViewById(R.id.image_count);

        for(int i=0; i<6; i++){
            mGallery.addView(insertPhoto(R.drawable.image1));
        }

//        count = mGallery.getAdapter().getCount();
//        System.out.println("Gallery Image Count======>>>" + count);
//        page_text = new TextView[count];
//        for (int i = 0; i < count; i++)
//        {
//            page_text[i] = new TextView(this);
//            page_text[i].setText(".");
//            page_text[i].setTextSize(45);
//            page_text[i].setTypeface(null, Typeface.BOLD);
//            page_text[i].setTextColor(android.graphics.Color.GRAY);
//            count_layout.addView(page_text[i]);
//        }
//        mGallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view,
//                                       int pos, long id) {
//                // TODO Auto-generated method stub
//                System.out.println("Item Selected Position=======>>>" + pos);
//                for (int i = 0; i < count; i++) {
//                    page_text[i]
//                            .setTextColor(android.graphics.Color.GRAY);
//                }
//                page_text[pos]
//                        .setTextColor(android.graphics.Color.WHITE);
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//            }
//        });
    }

    View insertPhoto(int photo){

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new LayoutParams(600, 400));
        layout.setGravity(Gravity.CENTER);

        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(photo);

        layout.addView(imageView);
        return layout;
    }

    View insertPhoto(String path){
        Bitmap bm = decodeSampledBitmapFromUri(path, 220, 220);

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new LayoutParams(250, 250));
        layout.setGravity(Gravity.CENTER);

        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams(new LayoutParams(220, 220));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bm);

        layout.addView(imageView);
        return layout;
    }


    private Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
        Bitmap bm = null;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    private int calculateInSampleSize(

            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }

        return inSampleSize;
    }
}
