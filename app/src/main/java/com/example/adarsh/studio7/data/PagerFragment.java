package com.example.adarsh.studio7.data;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adarsh.studio7.R;

/**
 * Created by adarsh on 13/08/2017.
 */

public class PagerFragment extends Fragment implements ActionBar.TabListener {
    private ViewPager viewPager;
    private String[] tabs = {"Songs", "Albums"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager_layout, container, false);

        this.viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        ActionBar actionBar = getActivity().getActionBar();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        this.viewPager.setAdapter(adapter);

        if(actionBar !=null)
        {
            actionBar.setHomeButtonEnabled(false);
            TabLayout tabs = (TabLayout) getActivity().findViewById(R.id.tabs);
            tabs.setupWithViewPager(viewPager);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().getActionBar().show();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}

class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new All_Songs();
            case 1:
                return  new All_Albums();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        if (position==0)
            return "Songs";
        else if (position==1)
            return "Albums";
        return super.getPageTitle(position);
    }
}