package com.example.adarsh.studio7.CustomViews;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;

import com.example.adarsh.studio7.data.PlayerControl;
import com.example.adarsh.studio7.data.SearchItem;

/**
 * Created by adarsh on 21/08/2017.
 */

public class SearchAdapter extends ArrayAdapter<SearchItem> implements Filterable{

    private ArrayList<SearchItem> data;
    private ArrayList<SearchItem> mFilterList;
    private LayoutInflater inflator;
    private ValueFilter filter;
    private Activity parentActivity;

    public SearchAdapter(Activity activity, @NonNull ArrayList<SearchItem> objects) {
        super(activity.getApplicationContext(), 0, objects);
        this.data = objects;
        this.mFilterList = objects;
        this.parentActivity = activity;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Nullable
    @Override
    public SearchItem getItem(int position) {
        return data.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (inflator==null)
            inflator = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflator.inflate(android.R.layout.simple_list_item_2, parent, false);
        ((TextView)v.findViewById(android.R.id.text1)).setText(data.get(position).getTitle());
        ((TextView)v.findViewById(android.R.id.text2)).setText(data.get(position).getArtist());

        v.setOnClickListener(v1 -> {
            SearchItem item = data.get(position);
            PlayerControl.playSong(item.getId());
            parentActivity.getFragmentManager().popBackStack();
        });

        return v;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter==null)
            filter = new ValueFilter();
        return filter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<SearchItem> filterList = new ArrayList<>();
                int size = mFilterList.size();
                for (int i=0;i<size;i++)
                {
                    if (mFilterList.get(i).getTitle().toUpperCase().contains(constraint.toString().toUpperCase()))
                        filterList.add(mFilterList.get(i));
                }
                results.count = filterList.size();
                results.values = filterList;
            }
            else
            {
                results.count = mFilterList.size();
                results.values = mFilterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values!=null)
            {
                data = (ArrayList<SearchItem>)results.values;
                notifyDataSetChanged();
            }
        }
    }
}
