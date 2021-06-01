package com.example.instaclone.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectionsStatePageAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList();
    private final HashMap<Integer, String> mFragmentNames = new HashMap();
    private final HashMap<String, Integer> mFragmentNumbers = new HashMap();
    private final HashMap<Fragment, Integer> mFragments = new HashMap();

    public void addFragment(Fragment fragment, String fragmentName) {
        this.mFragmentList.add(fragment);
        this.mFragments.put(fragment, Integer.valueOf(this.mFragmentList.size() - 1));
        this.mFragmentNumbers.put(fragmentName, Integer.valueOf(this.mFragmentList.size() - 1));
        this.mFragmentNames.put(Integer.valueOf(this.mFragmentList.size() - 1), fragmentName);
    }

    public SectionsStatePageAdapter(FragmentManager fm) {
        super(fm);
    }

    public Fragment getItem(int position) {
        return (Fragment) this.mFragmentList.get(position);
    }

    public int getCount() {
        return this.mFragmentList.size();
    }

    public Integer getFragmentNumber(Fragment fragment) {
        if (this.mFragments.containsKey(fragment)) {
            return (Integer) this.mFragments.get(fragment);
        }
        return null;
    }

    public Integer getFragmentNumber(String fragmentName) {
        if (this.mFragmentNumbers.containsKey(fragmentName)) {
            return (Integer) this.mFragmentNumbers.get(fragmentName);
        }
        return null;
    }

    public String getFragmentName(Integer fragmentNumber) {
        if (this.mFragmentNames.containsKey(fragmentNumber)) {
            return (String) this.mFragmentNames.get(fragmentNumber);
        }
        return null;
    }
}
