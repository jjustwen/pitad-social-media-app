package com.example.doanmobile.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.doanmobile.Fragment.HomeFragment;
import com.example.doanmobile.Fragment.NotificationFragment;
import com.example.doanmobile.Fragment.ProfileFragment;
import com.example.doanmobile.Fragment.SearchFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter
{
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior)
    {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                return new HomeFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new NotificationFragment();
            case 3:
                return new ProfileFragment();
            default:
                return new HomeFragment();
        }

    }

    @Override
    public int getCount()
    {
        return 4;
    }
}
