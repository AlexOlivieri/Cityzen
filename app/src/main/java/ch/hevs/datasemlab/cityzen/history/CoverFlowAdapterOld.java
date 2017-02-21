package ch.hevs.datasemlab.cityzen.history;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import ch.hevs.datasemlab.cityzen.R;

public class CoverFlowAdapterOld extends BaseAdapter {

    private static final String TAG = CoverFlowAdapterOld.class.getSimpleName();

    private ArrayList<CulturalItem> data = new ArrayList<CulturalItem>();
    private Context activity;

//    final Dialog dialog = new Dialog(activity);

    private int mPosition;

    public CoverFlowAdapterOld(Context context) {
        Log.i(TAG, "Array Size: ".concat(String.valueOf(data.size())));
        this.activity = context;
    }

    public CoverFlowAdapterOld(Context context, ArrayList<CulturalItem> objects) {
        this.activity = context;
        this.data = objects;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public CulturalItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_flow_view, null, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.titleView.setText(data.get(position).getTitle());
        byte[] imageAsByteArray = getItem(position).getImage();
        viewHolder.imageView.setImageBitmap((BitmapFactory.decodeByteArray(imageAsByteArray, 0, imageAsByteArray.length)));
        viewHolder.yearView.setText(data.get(position).getYear());
        viewHolder.descriptionView.setText(data.get(position).getDescription());

        convertView.setOnClickListener(onClickListener(position));

        return convertView;
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.dialog_game_info);
                dialog.setCancelable(true); // dimiss when touching outside
                dialog.setTitle("Game Details");

                mPosition = position;

//                TextView titleView = (TextView) dialog.findViewById(R.id.text_view_historical_title);
//                titleView.setText(getItem(position).getTitle());
                ImageView imageView = (ImageView) dialog.findViewById(R.id.image_view_historical_image);
                byte[] imageAsByteArray = getItem(position).getImage();
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsByteArray, 0, imageAsByteArray.length));
                TextView yearView = (TextView) dialog.findViewById(R.id.text_view_historical_year);
                yearView.setText(getItem(position).getYear());
//                TextView descriptionView = (TextView) dialog.findViewById(R.id.text_view_historical_description);
//                descriptionView.setText(getItem(position).getDescription());

                dialog.show();
            }
        };
    }

    public void updateEntries(ArrayList<CulturalItem> historicalCulturalItems){
        data = historicalCulturalItems;
        notifyDataSetChanged();
    }

//    private class ImageViewLoader extends AsyncTask<String, Void, Bitmap> {
//
//        @Override
//        protected Bitmap doInBackground(String... strings) {
//
//            String imageURL = strings[0];
//
//            URL url = null;
//            Bitmap bitmap = null;
//
//            try {
//                url = new URL(imageURL);
//                InputStream inputStream = url.openStream();
//                bitmap = BitmapFactory.decodeStream(inputStream);
//                //bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            }catch (MalformedURLException e){
//                e.printStackTrace();
//            }catch (IOException e) {
//                e.printStackTrace();
//                System.err.println("Title: " + imageURL);
//            }
//
//            return bitmap;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap result) {
//            super.onPostExecute(result);
//
//            TextView titleView = (TextView) dialog.findViewById(R.id.text_view_historical_title);
//            titleView.setText(getItem(mPosition).getTitle());
//            ImageView imageView = (ImageView) dialog.findViewById(R.id.image_view_historical_image);
//            imageView.setImageBitmap(result);
//            TextView yearView = (TextView) dialog.findViewById(R.id.text_view_historical_year);
//            yearView.setText(getItem(mPosition).getYear());
//            TextView descriptionView = (TextView) dialog.findViewById(R.id.text_view_historical_description);
//            descriptionView.setText(getItem(mPosition).getDescription());
//
//            dialog.show();
//        }
//    }

//    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
//
//        @Override
//        protected Bitmap doInBackground(String... strings) {
//
//            String imageURL = strings[0];
//
//            URL url = null;
//            Bitmap bitmap = null;
//
//            try {
//                url = new URL(imageURL);
//                InputStream inputStream = url.openStream();
//                bitmap = BitmapFactory.decodeStream(inputStream);
//                //bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            }catch (MalformedURLException e){
//                e.printStackTrace();
//            }catch (IOException e) {
//                e.printStackTrace();
//                System.err.println("Title: " + imageURL);
//            }
//
//            return bitmap;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap result) {
//            super.onPostExecute(result);
//
//            TextView titleView = (TextView) dialog.findViewById(R.id.text_view_historical_title);
//            titleView.setText(getItem(mPosition).getTitle());
//            ImageView imageView = (ImageView) dialog.findViewById(R.id.image_view_historical_image);
//            imageView.setImageBitmap(result);
//            TextView yearView = (TextView) dialog.findViewById(R.id.text_view_historical_year);
//            yearView.setText(getItem(mPosition).getYear());
//            TextView descriptionView = (TextView) dialog.findViewById(R.id.text_view_historical_description);
//            descriptionView.setText(getItem(mPosition).getDescription());
//
//            dialog.show();
//        }
//    }


    private static class ViewHolder {
        private TextView titleView;
        private ImageView imageView;
        private TextView yearView;
        private TextView descriptionView;

        public ViewHolder(View v) {
//            titleView = (TextView) v.findViewById(R.id.text_view_historical_title);
            imageView = (ImageView) v.findViewById(R.id.image_view_historical_image);
            yearView = (TextView) v.findViewById(R.id.text_view_historical_year);
//            descriptionView = (TextView) v.findViewById(R.id.text_view_historical_description);
        }
    }

    private byte[] fromBitmapToByteArray(Bitmap image){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private Bitmap fromByteArrayToBitmap(byte[] image){
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        return bitmap;
    }
}