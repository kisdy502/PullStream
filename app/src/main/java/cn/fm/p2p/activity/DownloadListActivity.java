package cn.fm.p2p.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.fm.p2p.App;
import cn.fm.p2p.R;
import cn.fm.p2p.download.DownloadInfo;
import cn.fm.p2p.item.ItemViewDownload;
import cn.fm.udp.Constant;
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

        items.addAll(initList2());
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

    private List<DownloadInfo> initList2() {
        final String dir = App.getInstance().getExternalFilesDir("").getAbsolutePath();
        List<DownloadInfo> list = new ArrayList<>();
        int length = URL_LIST.length;
        for (int i = 0; i < length; i++) {
            String url = URL_LIST[i];
            int idx = url.lastIndexOf("/");
            String name = url.substring(idx + 1);
            DownloadInfo downloadInfo = new DownloadInfo();
            downloadInfo.setSaveDir(dir);
            downloadInfo.setDownloadUrl(url);
            downloadInfo.setDesc("material");
            downloadInfo.setSaveFileName(name);
            list.add(downloadInfo);
            Log.d("test", downloadInfo.toString());
        }
        return list;
    }

    final static String[] URL_LIST = {
            "http://mifeng.skyworthbox.com:14000/adsys/video/41d/6e8a63b7a49b4e2e8576b27f39bb959b.ts",
            Constant.URL_MP4,
            "http://mifeng.skyworthbox.com:12000/adsys/pic/de9/7f4241280f13400ba7cb139535365b32.zip",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/c8/f03613ab118a49618c8708289f9d1be9.webp",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/fa/78add851153b4bcc96776f6bec10bc45.zip",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/b2/d4e8596dc45343289600ca04a6f88094.zip",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/95/972c8c66ee0f437a8ecab7cb63b15a4d.webp",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/ba/983f035dfd2740898b4cdc9e94a52179.webp",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/cc/2fe4e590d9a9410c973f81ad0a1decd0.webp",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/e2/b9e0748554274c768109c0cf3a7f3db1.webp",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/2c/a529996b221c4e2aa794a6a5573588a1.webp",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/f2/f8a6415eb91d4e22837731ee6db47662.webp",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/fb/927c6297d1364ac1b1a3dd39d6fa2f67.webp",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/a/df277353757c4efd83d9ad34906bd083.jpg",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/f/cd5fd34cfafa4e90bc10fd0c7121ce62.jpg",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/8/6870fc50fde747b39f77cd41603209c3.jpg",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/8/2c50cc08f6a54eb5903818c6dbfde127.jpg",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/4/ce437b2828464fe3b2e4ca9100fffae0.jpg",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/5/aa981b8a3c3940029d6bc97560758193.jpg",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/e/236068e8072e4c53b1289d490d3d5c7c.jpg",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/b/67ea0356bb5c497095de3ba00edd5077.jpg",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/e/ca28673a80f24161b04388bebdb43f30.jpg",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/9/a5fdbaf8cfa1402f938458a6cac1639d.jpg",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/1/a7ae83bdf33c4f599c0c288aa333a0ef.jpg"
    };

    //请求都是无法下载的url list
    final static String[] ERROR_URL_LIST = {
            "http://mifeng.skyworthbox.com:14000/adsys/video/d/6e8a63b7a49b4e2e8576b27f39bb959b.ts",
            "http://mifeng.skyworthboxed.cn:8999/adsys/video/d/6e8a63b7a49b4e2e8576b27f39bb959b.ts",
            "http://mifeng.skyworthbox.com:12000/adsys/pic/de9/7f4241280f13400ba7cb1395353652.zip",
            "http://mifeng.skyworthbox.com:1899/adsys/pic/de9/7f4241280f13400ba7cb1395353652.zip"
    };
}
