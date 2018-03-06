package com.ihandy.a2014012710.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihandy.a2014012710.R;
import com.ihandy.a2014012710.fragment.ItemFragment.OnListFragmentInteractionListener;
import com.ihandy.a2014012710.fragment.fragment_list.FragmentList;
import com.ihandy.a2014012710.fragment.fragment_list.FragmentList.FragmentItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
/**
 * {@link RecyclerView.Adapter} that can display a {@link FragmentItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<FragmentList.FragmentItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(List<FragmentItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }
    public static int position;
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MyItemRecyclerViewAdapter.position=position;
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).content);

        final File bitmapFile=new File(Environment.getExternalStorageDirectory().getPath()+"/newsApp/newsPicture/",mValues.get(position).newsId+".png");
        final File path=new File(Environment.getExternalStorageDirectory().getPath()+"/newsApp/newsPicture/");
        if(!path.exists()){
            path.mkdirs();
        }
        if(!bitmapFile.exists()) {
            //加载图片

            Thread t = new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            String src = mValues.get(MyItemRecyclerViewAdapter.position).imgsUrl;
                            try {
                                URL url = new URL(src);
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setDoInput(true);
                                connection.connect();
                                InputStream input = connection.getInputStream();
                                Bitmap bitmap = BitmapFactory.decodeStream(input);
                                FileOutputStream out = new FileOutputStream(bitmapFile);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                                out.flush();
                                out.close();
                                try {
                                    //在指定的文件夹中创建文件
                                    bitmapFile.createNewFile();
                                }
                                catch (Exception e) {}
                                holder.imageView.setImageBitmap(bitmap);

                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e("Exception",e.getMessage());

                            }
                        }
                    });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {

            }
        }
        else {
            try {
                FileInputStream input = new FileInputStream(bitmapFile);
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                holder.imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
            }
        }



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final ImageView imageView;
        public FragmentList.FragmentItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
            imageView= (ImageView) view.findViewById(R.id.imageView);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
}