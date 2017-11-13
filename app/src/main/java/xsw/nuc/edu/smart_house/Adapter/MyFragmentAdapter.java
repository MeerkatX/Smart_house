package xsw.nuc.edu.smart_house.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Smart_house
 * Created by 11749 on 2017/9/11,下午 9:40.
 */

public class MyFragmentAdapter extends FragmentPagerAdapter{
    private Fragment[] fragments;
    public MyFragmentAdapter(FragmentManager fm, Fragment[] fragments) {
        super(fm);
        this.fragments=fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position % fragments.length];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
