package ch.hevs.datasemlab.cityzen;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

public class CulturalInterestsGalleryActivityWithCursorLoader extends AppCompatActivity
                                                                implements LoaderManager.LoaderCallbacks<Cursor>{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cultural_interests_gallery_activity_with_cursor_loader);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle bundle){
        Uri CONTENT_URI = ContactsContract.RawContacts.CONTENT_URI;
        return new CursorLoader(this, CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the Cursor is being placed in a CursorAdapter, you should use the
        // swapCursor(null) method to remove any references it has to the
        // Loader's data.
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}
