package com.example.adarsh.studio7.data;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
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
    private boolean state;
    private String id;
    private boolean listenerOption;

    public SongsAdapter(Activity activity, Cursor cursor) {
        super(cursor);
        this.activity = activity;
    }

    public void setState(boolean st, String id){
        this.state = st;
        this.id = id;
    }

    public void setListenerOption(boolean option){
        listenerOption = option;
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
            c.moveToPosition(pos);

            if(!listenerOption) {
                Intent intent = new Intent(activity.getApplicationContext(), PlayScreen.class);
                intent.putExtra("NEW_SONG", true);
                if (state) intent.putExtra("ID", id);
                else intent.putExtra("ID", "all");
                intent.putExtra("SONG_ID", c.getString(0));
                intent.putExtra("POS", pos);
                Pair<View, String> p1 = Pair.create(activity.findViewById(R.id.main_screen_previous_button), "prev_button");
                Pair<View, String> p2 = Pair.create(activity.findViewById(R.id.main_screen_play_pause_button), "play_button");
                Pair<View, String> p3 = Pair.create(activity.findViewById(R.id.main_screen_next_button), "next_button");
                Pair<View, String> p4 = Pair.create(activity.findViewById(R.id.main_screen_album_art), "album_art");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, p1, p2, p3, p4);
                ActivityCompat.startActivity(activity, intent, options.toBundle());
            }
            else{
                PlayerControl.setPos(pos, c.getString(0));
                PlayerControl.updatePlayPauseIcon();
            }
        }
    }
}
