package com.example.adarsh.studio7.data;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adarsh.studio7.CustomViews.RecyclerViewCursorAdapter;
import com.example.adarsh.studio7.R;

/**
 * Created by adarsh on 20/07/2017.
 */

public class CardAdapter extends RecyclerViewCursorAdapter<CardAdapter.ViewHolder> {

    private Activity activity;
    private String CALLING_TAB;

    public CardAdapter(Activity activity, Cursor cursor, String tab) {
        super(cursor);
        this.activity = activity;
        this.CALLING_TAB = tab;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView thumbnail;
        TextView title;
        TextView number;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            thumbnail = (ImageView) itemView.findViewById(R.id.card_thumbnail);
            title = (TextView) itemView.findViewById(R.id.card_title);
            number = (TextView) itemView.findViewById(R.id.card_number);
        }

        @Override
        public void onClick(View v) {
            activity.getActionBar().hide();
            int pos = getAdapterPosition();
            Cursor c = getCursor();
            c.moveToPosition(pos);
            Bundle b = new Bundle();

            b.putBoolean("ID_SPECIFIC", true);
            if(CALLING_TAB.compareTo("ALBUMS") == 0)
                b.putString("ALBUM_ID", c.getString(c.getColumnIndex(MediaStore.Audio.Albums._ID)));

            else if(CALLING_TAB.compareTo("ARTISTS") == 0)
                b.putString("ARTIST_ID", c.getString(c.getColumnIndex(MediaStore.Audio.Artists._ID)));

            All_Songs all_songs = new All_Songs();
            all_songs.setArguments(b);

            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.replace(R.id.fragment_container, all_songs);
            ft.commit();
        }
    }

    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_card, parent, false));
    }

    @Override
    protected void onBindViewHolder(CardAdapter.ViewHolder holder, Cursor cursor) {
        if (cursor.getPosition() == -1) cursor.moveToFirst();
        else if (cursor.getPosition() < cursor.getCount()) {
            if(CALLING_TAB.compareTo("ALBUMS") == 0) {
                holder.title.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
                holder.number.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
                holder.number.append(" Songs");
                new ImageLoader(holder.thumbnail, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))).execute();
            }
            else if(CALLING_TAB.compareTo("ARTISTS") == 0) {
                holder.title.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)));
                holder.number.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)));
                holder.number.append(" Songs");
                holder.thumbnail.setImageResource(R.drawable.default_artist);
            }
        }
    }
}
