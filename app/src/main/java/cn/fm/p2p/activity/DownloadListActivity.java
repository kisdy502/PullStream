package cn.fm.p2p.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.fm.p2p.App;
import cn.fm.p2p.DataHelper;
import cn.fm.p2p.R;
import cn.fm.p2p.bean.Advert;
import cn.fm.p2p.bean.AppInfo;
import cn.fm.p2p.bean.EmptyValue;
import cn.fm.p2p.download.DownloadInfo;
import cn.fm.p2p.item.GoodTitleViewBinder;
import cn.fm.p2p.item.ItemViewAdvert;
import cn.fm.p2p.item.ItemViewAppInfo;
import cn.fm.p2p.item.ItemViewDownload;
import cn.fm.p2p.item.LineViewBinder;
import cn.fm.udp.Constant;
import me.drakeet.multitype.ClassLinker;
import me.drakeet.multitype.ItemViewBinder;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

public class DownloadListActivity extends Activity {

    RecyclerView mRecyclerView;
    MultiTypeAdapter multiTypeAdapter;
    Items items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_list);
        init();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void init() {
        mRecyclerView = findViewById(R.id.lvContent);

        multiTypeAdapter = new MultiTypeAdapter();
        multiTypeAdapter.register(DownloadInfo.class, new ItemViewDownload(this));

        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(lm);


        items = new Items();

        items.addAll(initList());
        mRecyclerView.setAdapter(multiTypeAdapter);

        multiTypeAdapter.setItems(items);
        multiTypeAdapter.notifyDataSetChanged();
    }

    private List<DownloadInfo> initList() {
        final String dir = App.getInstance().getExternalFilesDir("").getAbsolutePath();

        List<DownloadInfo> list = new ArrayList<>();
        DownloadInfo d0 = new DownloadInfo();
        d0.setSaveDir(dir);
        d0.setDownloadUrl(Constant.URL_MP4);
        d0.setDesc("mp4");
        d0.setSaveFileName(Constant.FILE_MP4);

        DownloadInfo d1 = new DownloadInfo();
        d1.setSaveDir(dir);
        d1.setDownloadUrl(Constant.URL_ZIP);
        d1.setDesc("zip");
        d1.setSaveFileName(Constant.FILE_ZIP);

        DownloadInfo d2 = new DownloadInfo();
        d2.setSaveDir(dir);
        d2.setDownloadUrl("http://material.mipt.cn/adsys/video/b0f/1aae99026ac444449f308c14d19f2cf5.ts");
        d2.setDesc("ts");
        d2.setSaveFileName("1aae99026ac444449f308c14d19f2cf5.ts");

        list.add(d0);
        list.add(d1);
        list.add(d2);

        return list;
    }
}
