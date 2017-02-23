package ch.hevs.datasemlab.cityzen;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

/**
 * Created by Alex on 8/12/2016.
 */
public class CulturalInterestsAdapter extends CursorAdapter{

    private static final String TAG = CulturalInterestsAdapter.class.getSimpleName();

    private LayoutInflater cursorInflater;

    ImageView imageView;
    RatingBar ratingBar;

    public CulturalInterestsAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, 0);
        if (context != null) {
            cursorInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return cursorInflater.inflate(R.layout.list_item_cultural_interests2, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        imageView = (ImageView) view.findViewById(R.id.image_view_cultural_interest_image);
        TextView textViewTitle = (TextView) view.findViewById(R.id.text_view_cultural_item_title);

        ratingBar = (RatingBar) view.findViewById(R.id.rating_bar_2);
        ratingBar.setFocusable(false);

        String title = cursor.getString(cursor.getColumnIndex("Title"));

        byte[] imageByte = cursor.getBlob(cursor.getColumnIndex("Image"));

        //TODO:  get image with handler

        if(imageByte == null) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.image_not_found);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageByte = stream.toByteArray();
        }

        Bitmap image = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        imageView.setImageBitmap(Bitmap.createScaledBitmap(image, 200, 200, false));
        textViewTitle.setText(title);
    }

}