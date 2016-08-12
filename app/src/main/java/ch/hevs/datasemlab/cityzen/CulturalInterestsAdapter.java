package ch.hevs.datasemlab.cityzen;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by Alex on 8/12/2016.
 */
public class CulturalInterestsAdapter extends BaseAdapter{

    private Context mContext;

    public CulturalInterestsAdapter(Context context){
        this.mContext = context;
    }

    public int getCount(){
        return 0;
    }

    public Object getItem(int position){
        return null;
    }

    public long getItemId(int position){
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        ImageView imageView = new ImageView(this.mContext);
        return imageView;
    }

}
