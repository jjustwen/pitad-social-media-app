package com.example.doanmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.doanmobile.Adapter.ViewPagerAdapter;
import com.example.doanmobile.Fragment.HomeFragment;
import com.example.doanmobile.Fragment.NotificationFragment;
import com.example.doanmobile.Fragment.ProfileFragment;
import com.example.doanmobile.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
{

    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    Fragment selecterFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.fragment_container);
        viewPager.setAdapter(viewPagerAdapter);
//        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle intent = getIntent().getExtras();
        if (intent != null)
        {
            String publisher = intent.getString("publisherid");
            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
        }
        else
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                switch (position)
                {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.nav_search).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.nav_heart).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                int itemid = item.getItemId();
                if (itemid == R.id.nav_home)
                {
                    viewPager.setCurrentItem(0);
                }
                else if (itemid == R.id.nav_search)
                {
                    viewPager.setCurrentItem(1);
                }
                else if (itemid == R.id.nav_add)
                {
                    startActivity(new Intent(getBaseContext(), PostActivity.class));
                }
                else if (itemid == R.id.nav_heart)
                {
                    viewPager.setCurrentItem(2);
                }
                else if (itemid == R.id.nav_profile)
                {
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    viewPager.setCurrentItem(3);
                }
                return true;
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener()
            {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
                {

                    int id = menuItem.getItemId();
                    if (id == R.id.nav_home)
                    {

                        selecterFragment = new HomeFragment();
                    }
                    else if (id == R.id.nav_search)
                    {

                        selecterFragment = new SearchFragment();
                    }
                    else if (id == R.id.nav_add)
                    {
                        selecterFragment = null;
                        startActivity(new Intent(MainActivity.this, PostActivity.class));
                    }
                    else if (id == R.id.nav_heart)
                    {

                        selecterFragment = new NotificationFragment();
                    }
                    else if (id == R.id.nav_profile)
                    {

                        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        editor.apply();
                        selecterFragment = new ProfileFragment();
                    }

                    if (selecterFragment != null)
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selecterFragment).commit();
                    }

                    return true;
                }
            };
}