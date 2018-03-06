package com.ihandy.a2014012710;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.ihandy.a2014012710.fragment.fragment_list.FragmentList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class WebPageActivity extends AppCompatActivity {

    public static String url;
    public static String title;
    public static long newsId;
    public static FragmentList.FragmentItem item;
    public static FragmentList.FragmentItem list[]=new FragmentList.FragmentItem[1000];
    public int list_num=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);


        setContentView(R.layout.webpage);
        WebView webview = (WebView) findViewById(R.id.webview);
        ImageButton imageButton=(ImageButton)findViewById(R.id.fuckbutton1);
        imageButton.setImageResource(R.drawable.red_heart);
        String cacheDirPath = getApplicationContext().getFilesDir()+File.separator+"MyData"+File.separator+"category"+File.separator+"cache";
        webview.getSettings().setAppCachePath(cacheDirPath);
        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        System.out.println(url);
        webview.loadUrl(url);
        getFromLocal();
        int i;
        for(i=0;i<list_num&&list[i].newsId!=newsId;i++);
        if(i>=list_num){
            imageButton.setImageResource(R.drawable.black_favorite);
        }
        else{
            imageButton.setImageResource(R.drawable.red_heart);
        }

    }
    protected void favoriteClicked(View v)
    {
        ImageButton imageButton=(ImageButton)findViewById(R.id.fuckbutton1);

        int i;
        for(i=0;i<list_num&&list[i].newsId!=newsId;i++);
        if(i>=list_num) {
            list[list_num] = new FragmentList.FragmentItem("",item.content,item.url,item.imgsUrl,item.newsId,item.number);
            list_num++;
            saveToLocal();
            imageButton.setImageResource(R.drawable.red_heart);
        }
        else{
            for(;i<list_num-1;i++){
                list[i]=list[i+1];
            }
            list_num--;
            saveToLocal();
            imageButton.setImageResource(R.drawable.black_favorite);
        }

    }

    protected void shareClicked(View v)
    {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(share, "Share url to..."));
        share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, item.content);
        startActivity(Intent.createChooser(share, "Share title to..."));

    }

    public void saveToLocal(){
        File myLog=new File(Environment.getExternalStorageDirectory().getPath()+"/newsApp/newsLog/favorites.txt");
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
            bfOutput.write(list_num+"\r\n");
            for(int i=0;i<list_num;i++){
                String s=list[i].newsId+"\r\n"+list[i].content+"\r\n"+list[i].url+"\r\n"+list[i].imgsUrl+"\r\n";
                bfOutput.write(s);
            }
            bfOutput.flush();
            bfOutput.close();
        }
        catch (Exception e){}
        return;
    }
    public void getFromLocal(){
        File myLog=new File(Environment.getExternalStorageDirectory().getPath()+"/newsApp/newsLog/favorites.txt");
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
        else {
            try {
                FileInputStream input = new FileInputStream(myLog);
                BufferedReader bfInput = new BufferedReader(new InputStreamReader(input));
                String s;
                if ((s = bfInput.readLine()) == null) return;
                list_num = Integer.parseInt(s);
                //list = new FragmentList.FragmentItem[list_num];
                int i = 0;
                while ((s = bfInput.readLine()) != null) {
                    long x;
                    String y, u1, u2;


                    x = Long.parseLong(s);
                    y = bfInput.readLine();
                    u1 = bfInput.readLine();
                    u2 = bfInput.readLine();
                    list[i] = new FragmentList.FragmentItem("", y, u1, u2, x, 0);
                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
