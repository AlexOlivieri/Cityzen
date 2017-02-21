package ch.hevs.datasemlab.cityzen.history_new;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by Alex on 2/21/2017.
 */

public class CulturalInterestCursorPagerAdapter<F extends Fragment> extends FragmentStatePagerAdapter {

    private static final String TAG = CulturalInterestCursorPagerAdapter.class.getSimpleName();

    private final Class<F> fragmentClass;
    private final String[] projection;
    private Cursor cursor;

    public CulturalInterestCursorPagerAdapter(FragmentManager fm, Class<F> fragmentClass, String[] projection, Cursor cursor) {

        super(fm);

        Log.i(TAG, "Constructor");

        this.fragmentClass = fragmentClass;
        this.projection = projection;
        this.cursor = cursor;
    }

    @Override
    public F getItem(int position) {

        Log.i(TAG, "getItem 1");

        if (cursor == null) // shouldn't happen
            return null;

        Log.i(TAG, "getItem 2");

        cursor.moveToPosition(position);
        F frag;
        try {
            frag = fragmentClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        Bundle args = new Bundle();
//        for (int i = 0; i < projection.length; ++i) {
//            Log.i(TAG, "Item: " + cursor.getString(i));
//            args.putString(projection[i], cursor.getString(i));
//        }
        Log.i(TAG, "Item: " + cursor.getString(0));
        Log.i(TAG, "Item: " + cursor.getString(1));
        Log.i(TAG, "Item: " + cursor.getBlob(2).length);
        Log.i(TAG, "Item: " + cursor.getString(3));
        args.putString(projection[0], cursor.getString(0));
        args.putString(projection[1], cursor.getString(1));
        args.putByteArray(projection[2], cursor.getBlob(2));
        args.putString(projection[3], cursor.getString(3));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public int getCount() {

        Log.i(TAG, "getCount");

        if (cursor == null) {
            Log.i(TAG, "Cursor Null");
            return 0;
        }else {
            Log.i(TAG, "Count: " + cursor.getCount());
            return cursor.getCount();
        }
    }

    public void swapCursor(Cursor c) {
        Log.i(TAG, "Swap Cursor :" +c.getCount());
//        if (cursor == c)
//            return;


        Log.i(TAG, "cursor not null");
        this.cursor = c;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return cursor;
    }
}
