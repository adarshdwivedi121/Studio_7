package com.example.adarsh.studio7.data;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adarsh.studio7.CustomViews.RecyclerViewCursorAdapter;
import com.example.adarsh.studio7.PlayScreen;
import com.example.adarsh.studio7.R;

/**
 * Created by adarsh on 31/05/2017.
 */

class SongsAdapter extends RecyclerViewCursorAdapter<SongsAdapter.ViewHolder> {

    private Activity activity;

    public SongsAdapter(Activity activity, Cursor cursor) {
        super(cursor);
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_songs, parent, false));
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        if (cursor.getPosition() == -1) cursor.moveToFirst();
        else if(cursor.getPosition() < cursor.getCount()) {
            holder.title.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            holder.album.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            Cursor c = this.activity.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                    "_id IS " + cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                    null,
                    null
            );
            Log.i("cursor", String.valueOf(c));
            Log.i("ALBUM_CURSOR", String.valueOf(cursor.getPosition()));
            c.moveToNext();
            new ImageLoader(holder.albumArt, c).execute();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView albumArt;
        TextView title;
        TextView album;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            albumArt = (ImageView) itemView.findViewById(R.id.song_list_photo);
            title = (TextView) itemView.findViewById(R.id.song_list_name);
            album = (TextView) itemView.findViewById(R.id.song_list_album);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Cursor c = getCursor();
            PlayerControl.setSongList(c);
            c.moveToPosition(pos);

            Intent intent = new Intent(activity.getApplicationContext(), PlayScreen.class);
            intent.putExtra("NEW_SONG", true);
            intent.putExtra("SONG_ID", c.getString(0));
            intent.putExtra("POS", pos);
            activity.startActivity(intent);
        }
    }
}
