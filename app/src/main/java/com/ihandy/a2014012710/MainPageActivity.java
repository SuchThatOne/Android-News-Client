package com.ihandy.a2014012710;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ihandy.a2014012710.HttpRequest.HttpRequest;
import com.ihandy.a2014012710.fragment.FragmentAdapter;
import com.ihandy.a2014012710.fragment.ItemFragment;
import com.ihandy.a2014012710.fragment.fragment_list.FragmentList;
import com.ihandy.a2014012710.navigation.AboutMe;
import com.ihandy.a2014012710.navigation.CategoryManagementMainActivity;
import com.ihandy.a2014012710.navigation.FavoritesMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainPageActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener,ItemFragment.OnListFragmentInteractionListener
{
    public List<Fragment> fragments;
    public ItemFragment itemFragments[];
    private SwipeRefreshLayout mSwipeLayout;

    public static String category[];
    public String netCategory[];
    public static boolean isFavorited[];
    public TabLayout tabLayout;
    public ViewPager viewPager;
    public Thread t;
    public int localCategoryNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLocalCategory();
        if(isNetworkConnected(getBaseContext()))initialCategory();
        saveLocalCategory();
    }

    public void onRefresh() {

        int tempId=tabLayout.getSelectedTabPosition();
        if(!isNetworkConnected(this)){
            //AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //final AlertDialog dialog = builder.setTitle("提示").setMessage(
                    //"当前没有网络").create();
            //final AutoCloseDialog d = new AutoCloseDialog(dialog);
            //d.show(500);
        }
        else{itemFragments[tempId].downFreshInfo();}
        if(itemFragments[tempId].localNewsNumber>0)itemFragments[tempId].adjust();
        mSwipeLayout.setRefreshing(false);
    }
    //-------查找有哪些分类并获取-----
    public void initialCategory(){
//fake
/*        category=new String[]{"national","top_stories"};
        isFavorited=new boolean[]{true,true};

*/
        t=new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        HttpRequest request = HttpRequest.get("http://assignment.crazz.cn/news/en/category?timestamp="+System.currentTimeMillis());//导入URL
                        String body = request.body();
                        try {
                            JSONObject jsonObject = new JSONObject(body); //字符串转JSONObject, 但必须catch JSONException

                            JSONObject jCategory = jsonObject.getJSONObject("data").getJSONObject("categories");//获取Json格式的Image数组
                            //JSONArray jCategory = jsonObject.getJSONObject("data").getJSONArray("categories");//获取Json格式的Image数组

                            Iterator iterator=jCategory.keys();
                            Set<String> c=new HashSet<String>();
                            while(iterator.hasNext()){
                                c.add((String)iterator.next());
                            }
                            if(jCategory.length()!=0) {
                                netCategory = new String[jCategory.length()];
                                int i = 0;
                                for (String s : c) {
                                    netCategory[i] = s;
                                    //isFavorited[i] = true;
                                    i++;
                                }
                            }
                            //非主线程无法修改UI，所以使用handler将修改UI的代码抛到主线程做

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        t.start();

        try {
            t.join();
        }
        catch(InterruptedException e){}


// judge if exchange
        if(category==null){
            category=netCategory;

            if(category!=null){
                isFavorited=new boolean[category.length];
                for(int i=0;i<category.length;i++){
                    isFavorited[i]=true;
                }
            }
            return;
        }
        else if(netCategory==null){
            category=null;
            isFavorited=null;
            return;
        }
        else {
            Set<String> local = new HashSet<String>();
            for(int i=0;i<category.length;i++){
                local.add(category[i]);
            }
            boolean flag=false;
            for(int i=0;i<netCategory.length;i++){
                if(!local.contains(netCategory[i])){
                    flag=true;
                    break;
                }
            }
            if(flag){
                category=netCategory;
                isFavorited=new boolean[category.length];
                for(int i=0;i<category.length;i++){
                    isFavorited[i]=true;
                }
            }
        }


        return;
    }


    public void getLocalCategory(){
        File myLog=new File(Environment.getExternalStorageDirectory().getPath()+"/newsApp/newsLog/"+"category.txt");
        File path=new File(Environment.getExternalStorageDirectory().getPath()+"/newsApp/newsLog/");
        if(!path.exists()){
            path.mkdirs();
        }
        if(!myLog.exists()) {
            try {
                myLog.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        try {
            FileInputStream input = new FileInputStream(myLog);
            BufferedReader bfInput= new BufferedReader(new InputStreamReader(input));
            String s;
            if((s=bfInput.readLine())==null)return;
            localCategoryNumber=Integer.parseInt(s);
            category=new String[localCategoryNumber];
            isFavorited=new boolean[localCategoryNumber];

            int i=0;
            while((s=bfInput.readLine())!=null){
                category[i]=s;
                isFavorited[i]=Boolean.parseBoolean(bfInput.readLine());
                i++;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveLocalCategory(){
        File myLog=new File(Environment.getExternalStorageDirectory().getPath()+"/newsApp/newsLog/"+"category.txt");
        File path=new File(Environment.getExternalStorageDirectory().getPath()+"/newsApp/newsLog/");
        if(!path.exists()){
            path.mkdirs();
        }
        if(!myLog.exists()){
            try {
                myLog.createNewFile();
            } catch (Exception e) {
            }
        }
        try {
            FileOutputStream output = new FileOutputStream(myLog);
            BufferedWriter bfOutput= new BufferedWriter(new OutputStreamWriter(output));
            localCategoryNumber=category.length;
            bfOutput.write(localCategoryNumber+"\r\n");
            for(int i=0;i<localCategoryNumber;i++){
                String s=category[i]+"\r\n"+isFavorited[i]+"\r\n";
                bfOutput.write(s);
            }
            bfOutput.flush();
            bfOutput.close();
        }
        catch (Exception e){}
        return;
    }




    //-------根据bool值设置界面的分类-----
    public void initialTab(){
        if(category==null)return;
        tabLayout=(TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        List<String> titles=new ArrayList<String>();
        for(int i=0;i<category.length;i++){
            if(isFavorited[i])titles.add(category[i]);
        }
        for(int i=0;i<titles.size();i++){
            tabLayout.addTab(tabLayout.newTab().setText(titles.get(i)));
        }


        fragments = new ArrayList<Fragment>();
        itemFragments=new ItemFragment[titles.size()];

        for(int i=0;i<titles.size();i++){
            itemFragments[i]=new ItemFragment();
            fragments.add(itemFragments[i]);
            itemFragments[i].setString(titles.get(i));
        }
        FragmentAdapter mFragmentAdapteradapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(mFragmentAdapteradapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(mFragmentAdapteradapter);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);

        mSwipeLayout.setOnRefreshListener(this);



        // mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
        //  android.R.color.holo_orange_light, android.R.color.holo_red_light)
        return;
    }

    @Override
    public void onResume(){
        super.onResume();
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        if(isNetworkConnected(getBaseContext())&&category==null){
            initialCategory();
        }
        saveLocalCategory();
        initialTab();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favorites) {
            startActivity(new Intent(MainPageActivity.this, FavoritesMainActivity.class));
        } else if (id == R.id.nav_categoryManagement) {
            startActivity(new Intent(MainPageActivity.this, CategoryManagementMainActivity.class));
        } else if (id == R.id.nav_aboutMe) {
            startActivity(new Intent(MainPageActivity.this, AboutMe.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void onListFragmentInteraction(FragmentList.FragmentItem item){

        WebPageActivity.url=item.url;
        WebPageActivity.newsId=item.newsId;
        WebPageActivity.item=item;


        startActivity(new Intent(MainPageActivity.this,WebPageActivity.class));
        return;
    }
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}

