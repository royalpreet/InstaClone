package com.example.instaclone.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class SectionsPageAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList();

    public void addFragment(Fragment fragment) {
        this.mFragmentList.add(fragment);
    }

    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public Fragment getItem(int position) {
        return (Fragment) this.mFragmentList.get(position);
    }

    public int getCount() {
        return this.mFragmentList.size();
    }
}
