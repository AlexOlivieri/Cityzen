package ch.hevs.datasemlab.cityzen;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Alex on 8/12/2016.
 */
public class CulturalInterestsAdapter extends CursorAdapter{

    private LayoutInflater cursorInflater;

    ImageView imageView;

    public CulturalInterestsAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, 0);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return cursorInflater.inflate(R.layout.list_item_cultural_interests2, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        imageView = (ImageView) view.findViewById(R.id.image_view_cultural_interest_image);
        TextView textViewTitle = (TextView) view.findViewById(R.id.text_view_cultural_item_title);
        //TextView textViewDescription = (TextView) view.findViewById(R.id.text_view_cultural_item_description);

        String title = cursor.getString(cursor.getColumnIndex("Title"));
        //String description = cursor.getString(cursor.getColumnIndex("Description"));

        byte[] imageByte = cursor.getBlob(cursor.getColumnIndex("Image"));
        //new ImageDownloader().execute(image);

        //TODO:  get image with handler

        Bitmap image = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        imageView.setImageBitmap(Bitmap.createScaledBitmap(image, 160, 160, false));
        textViewTitle.setText(title);
        //textViewDescription.setText(description);
    }

    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url = null;
            Bitmap image = null;
            try {
                url = new URL(strings[0]);
                InputStream inputStream = url.openStream();
                image = BitmapFactory.decodeStream(inputStream);
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }

            return image;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null) {
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));
            }
        }
    }

}
