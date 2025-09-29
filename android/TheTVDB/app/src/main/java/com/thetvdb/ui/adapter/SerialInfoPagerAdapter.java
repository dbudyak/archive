package com.thetvdb.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thetvdb.R;
import com.thetvdb.ui.BaseActivity;
import com.thetvdb.ui.fragment.ActorsFragment;
import com.thetvdb.ui.fragment.EpisodesFragment;
import com.thetvdb.ui.fragment.SerialInfoFragment;

import butterknife.BindArray;
import butterknife.ButterKnife;

/**
 * Created by dbudyak on 27.06.16.
 */
public class SerialInfoPagerAdapter extends FragmentPagerAdapter {

    @BindArray(R.array.pager_elements) String[] tabTitles;

    private Bundle extras;

    public SerialInfoPagerAdapter(BaseActivity context, FragmentManager fm, Bundle extras) {
        super(fm);
        ButterKnife.bind(this, context);
        this.extras = extras;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new EpisodesFragment();
                break;
            case 1:
                fragment = new SerialInfoFragment();
                break;
            case 2:
                fragment = new ActorsFragment();
                break;
        }
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
