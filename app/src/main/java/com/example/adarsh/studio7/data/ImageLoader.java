package com.example.adarsh.studio7.data;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.example.adarsh.studio7.R;

/**
 * Created by adarsh on 20/07/2017.
 */

public class ImageLoader extends AsyncTask<Object, Void, Drawable> {

    private ImageView imageview;
    private Cursor cursor = null;
    private String path = null;

    public ImageLoader(ImageView imageview, Cursor cursor){
        this.imageview = imageview;
        this.cursor = cursor;
        Log.i("Cursor", String.valueOf(cursor));
    }

    ImageLoader(ImageView imageView, String path){
        this.imageview = imageView;
        this.path = path;
    }

    @Override
    protected Drawable doInBackground(Object... params) {
        if(this.path != null)
            return Drawable.createFromPath(this.path);

        else if (this.cursor != null && this.cursor.getCount() == 1) {
            this.path = this.cursor.getString(this.cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            this.cursor.close();
            return Drawable.createFromPath(this.path);
        }
        else
            return null;
    }

    @Override
    protected void onPostExecute(Drawable drawable) {
        super.onPostExecute(drawable);
        if(drawable == null) {
            this.imageview.setImageResource(R.drawable.default_music);
            this.imageview.setBackgroundColor(Color.DKGRAY);
        }
        else
            this.imageview.setImageDrawable(drawable);
    }
}
