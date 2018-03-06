package com.ihandy.a2014012710.fragment.fragment_list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */

//TODO 把之前的常量名改为普通变量名

public class FragmentList {

    /**
     * An array of sample (dummy) items.
     */
    public List<FragmentItem> ITEMS = new ArrayList<FragmentItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public Map<String, FragmentItem> ITEM_MAP = new HashMap<String, FragmentItem>();

    private int COUNT;
    private String titles[];
    private String url[];
    private String imgsUrl[];
    private long newsId[];

    //TODO 添加图片列表

    public FragmentList(String titles[], String url[], String imgsUrl[], long newsId[]){
        // Add some sample items.
        COUNT=titles.length;
        this.titles=titles;
        this.url=url;
        this.imgsUrl=imgsUrl;
        this.newsId=newsId;
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private void addItem(FragmentItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private FragmentItem createDummyItem(int position) {
        return new FragmentItem(String.valueOf(position), titles[position-1], url[position-1],imgsUrl[position-1],newsId[position-1],position-1);
    }



    /**
     * A dummy item representing a piece of content.
     */
    public static class FragmentItem {
        public final String id;
        public final String content;
        public final String url;
        public final String imgsUrl;
        public final long newsId;
        public int number;

        //TODO 添加图片

        public FragmentItem(String id, String content, String url, String imgsUrl, long newsId, int number) {
            this.id = id;
            this.content = content;
            this.url = url;
            this.number=number;
            this.imgsUrl=imgsUrl;
            this.newsId=newsId;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
