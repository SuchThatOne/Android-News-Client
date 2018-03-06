package com.ihandy.a2014012710.navigation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ihandy.a2014012710.MainPageActivity;
import com.ihandy.a2014012710.R;

public class CategoryManagementMainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management_main);
        ListView lv=(ListView) findViewById(R.id.lv_cm);
        String category[]= MainPageActivity.category;
        lv.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, category));
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        for(int i=0;i<category.length;i++) {
            lv.setItemChecked(i,MainPageActivity.isFavorited[i]);
        }


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                MainPageActivity.isFavorited[arg2]=!MainPageActivity.isFavorited[arg2];//点击后在标题上显示点击了第几行                     setTitle("你点击了第"+arg2+"行");
            }
        });
    }
}
