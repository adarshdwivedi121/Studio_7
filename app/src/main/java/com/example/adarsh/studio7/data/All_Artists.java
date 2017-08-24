package com.example.adarsh.studio7.data;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adarsh.studio7.R;

/**
 * Created by adarsh on 22/08/2017.
 */

public class All_Artists extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private CardAdapter cursorAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_all_cards, container, false);
        Log.i("All Artists", "Starting song list fragment");
        cursorAdapter = new CardAdapter(getActivity(), null, "ARTISTS");
        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.all_cards);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity().getApplicationContext(), 2));
        recyclerView.setAdapter(cursorAdapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        getLoaderManager().initLoader(1001, null, this);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i("All Songs", "Inside Loader");

        String[] projection = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        };

        return new CursorLoader(this.getActivity().getApplicationContext(),
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.Audio.Artists.ARTIST + " NOT LIKE 'WhatsApp' COLLATE NOCASE",
                null,
                MediaStore.Audio.Artists.ARTIST);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i("All Albums", "Loader finished");

        if (data != null && data.getCount() > 0) {
            getActivity().findViewById(R.id.empty_view).setVisibility(View.GONE);
            getActivity().findViewById(R.id.non_empty_view).setVisibility(View.VISIBLE);
            Log.i("All Artists", "Artists: " + String.valueOf(data.getCount()));
            cursorAdapter.swapCursor(data);
        }

        else {
            cursorAdapter.swapCursor(null);
            getActivity().findViewById(R.id.non_empty_view).setVisibility(View.GONE);
            getActivity().findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
