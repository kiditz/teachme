package com.slerpio.teachme.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Standard Fragment for displaying ViewPager
 * @author kiditz on 28/02/18.
 */

public class ViewPagerFragmentAdapter extends FragmentPagerAdapter{
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    public ViewPagerFragmentAdapter(FragmentManager fm) {
        super(fm);

    }

    public void addFragment(Fragment fragment, String title){
        this.fragments.add(fragment);
        this.titles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    public void replace(int position, Fragment fragment){
        this.fragments.set(position, fragment);
        this.notifyDataSetChanged();
    }

    public List<Fragment> getFragments() {
        return fragments;
    }

    public List<String> getTitles() {
        return titles;
    }

    public String getTitle(int position){
        return getTitles().get(position);
    }

    public void clear(){
        this.fragments.clear();
    }
}
