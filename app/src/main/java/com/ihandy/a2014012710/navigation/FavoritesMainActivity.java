package com.ihandy.a2014012710.navigation;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ihandy.a2014012710.R;
import com.ihandy.a2014012710.WebPageActivity;
import com.ihandy.a2014012710.fragment.FragmentAdapter;
import com.ihandy.a2014012710.fragment.ItemFragment;
import com.ihandy.a2014012710.fragment.fragment_list.FragmentList;

import java.util.ArrayList;
import java.util.List;

public class FavoritesMainActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {
    public TabLayout tabLayout;
    public ViewPager viewPager;
    public Thread t;
    public int localCategoryNumber;

    public List<Fragment> fragments;
    public ItemFragment itemFragments[];
    private SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void onResume(){
        super.onResume();
        setContentView(R.layout.activity_favorites_main);
        tabLayout=(TabLayout) findViewById(R.id.tabs_fav);
        viewPager = (ViewPager) findViewById(R.id.viewpager_fav);

        List<String> titles=new ArrayList<String>();
        titles.add("favorites");
        for(int i=0;i<titles.size();i++){
            tabLayout.addTab(tabLayout.newTab().setText(titles.get(i)));
        }


        fragments = new ArrayList<Fragment>();
        itemFragments=new ItemFragment[titles.size()];

        for(int i=0;i<titles.size();i++){
            itemFragments[i]=new ItemFragment();
            fragments.add(itemFragments[i]);
        }
        itemFragments[0].setString("favorites");

        FragmentAdapter mFragmentAdapteradapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(mFragmentAdapteradapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(mFragmentAdapteradapter);

    }


    public void onListFragmentInteraction(FragmentList.FragmentItem item){
        //item
        WebPageActivity.url=item.url;
        WebPageActivity.newsId=item.newsId;
        int position=tabLayout.getSelectedTabPosition();
        startActivity(new Intent(FavoritesMainActivity.this,WebPageActivity.class));
        tabLayout.setScrollPosition(position,0,true);

    }
}









