package com.example.adarsh.studio7.data;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adarsh.studio7.*;

import static com.example.adarsh.studio7.data.PlayerControl.projection;

/**
 * Created by adarsh on 31/05/2017.
 */

public class All_Songs extends android.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private SongsAdapter cursorAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("All Songs", "Starting song list fragment");

        View view = inflater.inflate(R.layout.fragment_all_songs, container, false);

        cursorAdapter = new SongsAdapter(getActivity(), null);
        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity().getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.all_songs_list);

        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(cursorAdapter);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(16));

        if (getArguments() != null && getArguments().containsKey("ID_SPECIFIC"))
            getLoaderManager().initLoader(1001, getArguments(), this).forceLoad();
        else
            getLoaderManager().initLoader(1001, null, this).forceLoad();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i("All Songs", "Inside Loader");

        if (getArguments()!= null && getArguments().containsKey("ALBUM_ID")) {
            getActivity().getActionBar().hide();
            cursorAdapter.setState(true, getArguments().getString("ALBUM_ID"), "ALBUM");
            return new CursorLoader(
                    this.getActivity().getApplicationContext(),
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    "album_id IS " + getArguments().getString("ALBUM_ID"),
                    null,
                    null);
        }
        else if (getArguments()!= null && getArguments().containsKey("ARTIST_ID")) {
            getActivity().getActionBar().hide();
            cursorAdapter.setState(true, getArguments().getString("ARTIST_ID"), "ARTIST");
            return new CursorLoader(
                    this.getActivity().getApplicationContext(),
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    "artist_id IS " + getArguments().getString("ARTIST_ID"),
                    null,
                    null);
        }
        else {
            getActivity().getActionBar().show();
            return new CursorLoader(this.getActivity().getApplicationContext(),
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    MediaStore.Audio.Media.TITLE + " NOT LIKE 'AUD%' AND " + MediaStore.Audio.Media.DURATION + ">=60000 COLLATE NOCASE",
                    null,
                    MediaStore.Audio.Media.TITLE);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i("All Songs", "Loader finished");

        if (data != null && data.getCount() > 0) {
            getActivity().findViewById(R.id.empty_view).setVisibility(View.GONE);
            getActivity().findViewById(R.id.non_empty_view).setVisibility(View.VISIBLE);
            Log.i("All Songs", "Songs : " + String.valueOf(data.getCount()));
            cursorAdapter.swapCursor(data);
        } else {
            cursorAdapter.swapCursor(null);
            getActivity().findViewById(R.id.non_empty_view).setVisibility(View.GONE);
            getActivity().findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }
}