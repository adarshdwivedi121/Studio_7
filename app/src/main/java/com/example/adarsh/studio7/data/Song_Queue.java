package com.example.adarsh.studio7.data;

import android.app.Fragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adarsh.studio7.R;

/**
 * Created by adarsh on 18/08/2017.
 */

public class Song_Queue extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        Log.i("All Songs", "Starting song list fragment");

        View view = inflater.inflate(R.layout.fragment_all_songs, container, false);

        SongsAdapter cursorAdapter = new SongsAdapter(getActivity(), null);
        cursorAdapter.swapCursor(PlayerControl.getSongList());
        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity().getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.all_songs_list);

        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(cursorAdapter);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(16));

        cursorAdapter.setListenerOption(true);

        return view;
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
