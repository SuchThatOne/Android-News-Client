package com.ihandy.a2014012710.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ihandy.a2014012710.HttpRequest.HttpRequest;
import com.ihandy.a2014012710.R;
import com.ihandy.a2014012710.fragment.fragment_list.FragmentList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private String category;
    private String localTitles[];
    private String localUrl[];
    private String localImgsUrl[];
    private long localNewsId[];
    public int localNewsNumber=0;

    public FragmentList dummyContent;
    public MyItemRecyclerViewAdapter myItemRecyclerViewAdapter;
    public RecyclerView recyclerView;
    public int maxNewsNumberFromNet=5;


    private String netTitles[];
    private String netUrl[];
    private String netimgsUrl[];
    private long netNewsId[];
    public Thread t;
    public JSONArray newsArray;

    public LinearLayoutManager layoutManager;




    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);





        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;


            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            getFromLocal();
            if(!category.equals("favorites")){
                if(localNewsId==null){
                    if(!isNetworkConnected(getContext())){
                        /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        final AlertDialog dialog = builder.setTitle("提示").setMessage(
                                "当前没有网络").create();
                        final AutoCloseDialog d = new AutoCloseDialog(dialog);
                        d.show(500);*/
                    }
                    else upFreshInfo();
                }

            }
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(category.equals("favorites"))return;
                    if(localNewsId==null) {
                        if (isNetworkConnected(getContext())) {
                            upFreshInfo();
                        }
                            if(localNewsId!=null) adjust();

                    }
                    else{
                        final int position=myItemRecyclerViewAdapter.getItemCount();
                        if (newState ==RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 ==myItemRecyclerViewAdapter.getItemCount()) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isNetworkConnected(getActivity())) {
                                    } else {
                                        upFreshInfo();
                                        if (localNewsNumber > 0) adjust();
                                        if (position > 4) layoutManager.scrollToPosition(position - 3);
                                        // else layoutManager.scrollToPosition(0);
                                    }
                                }
                            }, 1000);
                        }
                    }
                }

                public int lastVisibleItem;
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView,dx, dy);
                    layoutManager=(LinearLayoutManager) recyclerView.getLayoutManager();
                    lastVisibleItem =layoutManager.findLastVisibleItemPosition();
                }
            });

            if(localNewsId==null)return view;
            adjust();
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void getFromLocal(){
        File myLog=new File(Environment.getExternalStorageDirectory().getPath()+"/newsApp/newsLog/"+category+".txt");
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
            localNewsNumber=Integer.parseInt(s);
            localNewsId=new long[localNewsNumber];
            localTitles=new String[localNewsNumber];
            localImgsUrl=new String[localNewsNumber];
            localUrl=new String[localNewsNumber];
            int i=0;
            while((s=bfInput.readLine())!=null){
                localNewsId[i]=Long.parseLong(s);
                localTitles[i]=bfInput.readLine();
                localUrl[i]=bfInput.readLine();
                localImgsUrl[i]=bfInput.readLine();
                i++;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }






    public void getNews(long maxId){
        if(maxId>=0) {
            final long maxIdTemp=maxId;
            t = new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            HttpRequest request;

                            request = HttpRequest.get("http://assignment.crazz.cn/news/query?locale=en&category=" + category + "&max_news_id=" + maxIdTemp);//导入URL

                            //                request = HttpRequest.get("http://assignment.crazz.cn/news/query?locale=en&category=" + category);//导入URL

                            String body = request.body();
                            try {
                                JSONObject jsonObject = new JSONObject(body); //字符串转JSONObject, 但必须catch JSONException

                                newsArray = jsonObject.getJSONObject("data").getJSONArray("news"); //获取Json格式的Image数组

                                //非主线程无法修改UI，所以使用handler将修改UI的代码抛到主线程做

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            t.start();

            try {
                t.join();
            } catch (Exception e) {
            }


            //--------get id,title,url,images
            if(newsArray.length()==0)return;
            try{
                int max;
                if(newsArray.length()-1>maxNewsNumberFromNet)max=maxNewsNumberFromNet;
                else{max=newsArray.length()-1
                ;}
                netTitles=new String[max];
                netUrl=new String[max];
                netNewsId=new long[max];
                netimgsUrl=new String[max];
                for(int i=0;i<netTitles.length;i++) {
                    JSONObject jo = newsArray.getJSONObject(i+1);
                    netTitles[i]=jo.optString("title");
                    if(!jo.optString("source").equals("null")){
                        netUrl[i]=jo.getJSONObject("source").optString("url");
                    }
                    else{
                        netUrl[i]=null;
                    }
                    netNewsId[i]=Long.parseLong(jo.optString("news_id"));
                    if(!jo.optString("imgs").equals("null")){
                        netimgsUrl[i]=jo.getJSONArray("imgs").getJSONObject(0).optString("url");
                    }
                    else{
                        netimgsUrl[i]=null;
                    }}

            }
            catch (JSONException ej){
                return;
            }

        }
        else{
            t = new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            HttpRequest request;

//                            request = HttpRequest.get("http://assignment.crazz.cn/news/query?locale=en&category=" + category + "&max_news_id=" + maxId);//导入URL

                            request = HttpRequest.get("http://assignment.crazz.cn/news/query?locale=en&category=" + category);//导入URL

                            String body = request.body();
                            try {
                                JSONObject jsonObject = new JSONObject(body); //字符串转JSONObject, 但必须catch JSONException

                                newsArray = jsonObject.getJSONObject("data").getJSONArray("news"); //获取Json格式的Image数组

                                //非主线程无法修改UI，所以使用handler将修改UI的代码抛到主线程做

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            t.start();

            try {
                t.join();
            } catch (Exception e) {
            }

            //--------get id,title,url,images
            if(newsArray.length()==0)return;
            try{
                int max;
                if(newsArray.length()>maxNewsNumberFromNet)max=maxNewsNumberFromNet;
                else{max=newsArray.length()
                ;}
                netTitles=new String[max];
                netUrl=new String[max];
                netNewsId=new long[max];
                netimgsUrl=new String[max];
                for(int i=0;i<netTitles.length;i++) {
                    JSONObject jo = newsArray.getJSONObject(i);
                    netTitles[i]=jo.optString("title");
                    if(!jo.optString("source").equals("null")){
                        netUrl[i]=jo.getJSONObject("source").optString("url");
                    }
                    else{
                        netUrl[i]=null;
                    }
                    netNewsId[i]=Long.parseLong(jo.optString("news_id"));
                    if(!jo.optString("imgs").equals("null")){
                        netimgsUrl[i]=jo.getJSONArray("imgs").getJSONObject(0).optString("url");
                    }
                    else{
                        netimgsUrl[i]=null;
                    }}

            }
            catch (JSONException ej){
                return;
            }

        }


        return;
    }




    public void saveToLocal(){
        File myLog=new File(Environment.getExternalStorageDirectory().getPath()+"/newsApp/newsLog/"+category+".txt");
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
            localNewsNumber=localNewsId.length;
            bfOutput.write(localNewsNumber+"\r\n");
            for(int i=0;i<localNewsId.length;i++){
                String s=localNewsId[i]+"\r\n"+localTitles[i]+"\r\n"+localUrl[i]+"\r\n"+localImgsUrl[i]+"\r\n";
                bfOutput.write(s);
            }
            bfOutput.flush();
            bfOutput.close();
        }
        catch (Exception e){}
        return;
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(FragmentList.FragmentItem item);
    }

    public String[] merge(String a[],String b[]){
        if(a==null) return b;
        if(b==null) return a;
        String c[]=new String[a.length+b.length];
        int i=0;
        for(;i<a.length;i++){
            c[i]=a[i];
        }
        for(;i<c.length;i++){
            c[i]=b[i-a.length];
        }
        return c;
    }

    public long[] merge(long a[],long b[]){
        if(a==null) return b;
        if(b==null) return a;
        long c[]=new long[a.length+b.length];
        int i=0;
        for(;i<a.length;i++){
            c[i]=a[i];
        }
        for(;i<c.length;i++){
            c[i]=b[i-a.length];
        }
        return c;
    }

    public String[] cutArray(String a[],int flag){
        if(a==null)return null;
        String ret[]=new String[flag];
        for(int i=0;i<ret.length;i++){
            ret[i]=a[i];
        }
        return ret;
    }


    public long[] cutArray(long a[],int flag){
        if(a==null)return null;
        long ret[]=new long[flag];
        for(int i=0;i<ret.length;i++){
            ret[i]=a[i];
        }
        return ret;
    }





    public void upFreshInfo(){
        if(localNewsId!=null){
            getNews(localNewsId[localNewsId.length-1]);

        }
        else{
            getNews(-1);
        }
        int older=localNewsNumber;
        localTitles=merge(localTitles,netTitles);
        localUrl=merge(localUrl,netUrl);
        localNewsId=merge(localNewsId,netNewsId);
        localImgsUrl=merge(localImgsUrl,netimgsUrl);
        saveToLocal();



    }

    public void adjust(){
        dummyContent=new FragmentList(localTitles,localUrl,localImgsUrl,localNewsId);
        myItemRecyclerViewAdapter=new MyItemRecyclerViewAdapter(dummyContent.ITEMS, mListener);
        recyclerView.setAdapter(myItemRecyclerViewAdapter);
    }


    public void downFreshInfo(){
        getNews(-1);
        long tempNetNewsId[];
        String tempTitles[];
        String tempUrl[];
        String tempImgsUrl[];
        while(netNewsId[netNewsId.length-1]>localNewsId[0]){
            tempNetNewsId=netNewsId;
            tempTitles=netTitles;
            tempUrl=netUrl;
            tempImgsUrl=netimgsUrl;
            getNews(netNewsId[netNewsId.length-1]);
            netNewsId=merge(tempNetNewsId,netNewsId);
            netTitles=merge(tempTitles,netTitles);
            netUrl=merge(tempUrl,netUrl);
            netimgsUrl=merge(tempImgsUrl,netimgsUrl);
            if(netNewsId==null){
                return;
            }
        }
        int i=netNewsId.length-1;
        while(localNewsId[0]!=netNewsId[i]){
            i--;
        }

        int older=localNewsNumber;

        localNewsId=merge(cutArray(netNewsId,i),localNewsId);
        localImgsUrl=merge(cutArray(netimgsUrl,i),localImgsUrl);
        localUrl=merge(cutArray(netUrl,i),localUrl);
        localTitles=merge(cutArray(netTitles,i),localTitles);
        saveToLocal();



    }

    public void setString(String s){
        category=s;
    }
}
