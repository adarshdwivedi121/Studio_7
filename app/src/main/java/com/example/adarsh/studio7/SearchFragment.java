package com.example.adarsh.studio7;

import android.app.Fragment;
import android.app.LoaderManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.adarsh.studio7.CustomViews.SearchAdapter;
import com.example.adarsh.studio7.data.SearchItem;

import java.util.ArrayList;

import static com.example.adarsh.studio7.data.PlayerControl.projection;

/**
 * Created by adarsh on 21/08/2017.
 */

public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private View rootView;
    private SearchAdapter adapter;
    private ArrayList<SearchItem> data = null;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        getActivity().getActionBar().hide();

        getLoaderManager().initLoader(1001, null, this).forceLoad();

        SearchView searchView = (SearchView)rootView.findViewById(R.id.search_bar);
        searchView.setActivated(true);
        searchView.onActionViewExpanded();
        searchView.setQueryHint("Search songs");
        searchView.setSubmitButtonEnabled(true);

        listView = (ListView)rootView.findViewById(R.id.search_lv);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this.getActivity().getApplicationContext(),
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.Audio.Media.TITLE + " NOT LIKE 'AUD%' AND " + MediaStore.Audio.Media.DURATION + ">=60000 COLLATE NOCASE",
                null,
                MediaStore.Audio.Media.TITLE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        data.moveToFirst();

        this.data = new ArrayList<>();
        this.data.add(new SearchItem(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
                data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
                data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))));

        while (data.moveToNext())
        {
            this.data.add(new SearchItem(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
                    data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
                    data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))));
        }
        adapter = new SearchAdapter(getActivity(), this.data);
        listView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.clear();
    }
}
