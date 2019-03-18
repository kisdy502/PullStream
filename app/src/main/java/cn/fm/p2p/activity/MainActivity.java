package cn.fm.p2p.activity;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.fm.p2p.App;
import cn.fm.p2p.DataHelper;
import cn.fm.p2p.PermissionsUtil;
import cn.fm.p2p.R;
import cn.fm.p2p.bean.Advert;
import cn.fm.p2p.bean.AppInfo;
import cn.fm.p2p.bean.EmptyValue;
import cn.fm.p2p.item.GoodTitleViewBinder;
import cn.fm.p2p.item.ItemViewAdvert;
import cn.fm.p2p.item.ItemViewAppInfo;
import cn.fm.p2p.item.LineViewBinder;
import cn.fm.udp.Constant;
import cn.fm.udp.HttpTool;
import cn.fm.udp.LogWriter;
import me.drakeet.multitype.ClassLinker;
import me.drakeet.multitype.ItemViewBinder;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    MultiTypeAdapter multiTypeAdapter;
    Items items;

    int SPAN_COUNT = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionsUtil.verifyStoragePermissions(this);      //API23+权限获取
        }
        initUI();
        checkFile();
        printfDevice();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initUI() {
        mRecyclerView = findViewById(R.id.lvContent);

        multiTypeAdapter = new MultiTypeAdapter();
        multiTypeAdapter.register(Advert.class, new ItemViewAdvert(this));
        multiTypeAdapter.register(AppInfo.class, new ItemViewAppInfo());
        multiTypeAdapter.register(EmptyValue.class)
                .to(new GoodTitleViewBinder(this),
                        new LineViewBinder())
                .withClassLinker(new ClassLinker<EmptyValue>() {

                    @NonNull
                    @Override
                    public Class<? extends ItemViewBinder<EmptyValue, ?>> index(int position,
                                                                                @NonNull EmptyValue emptyValue) {
                        if (emptyValue.type == EmptyValue.TYPE_GOODTITLE) {
                            return GoodTitleViewBinder.class;
                        } else if (emptyValue.type == EmptyValue.TYPE_LINE) {
                            return LineViewBinder.class;
                        }
                        return LineViewBinder.class;
                    }
                });


        GridLayoutManager layoutManager = new GridLayoutManager(this, SPAN_COUNT,
                GridLayoutManager.VERTICAL, false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (items.get(position) instanceof Advert) {
                    return 2;
                } else if (items.get(position) instanceof AppInfo) {
                    return SPAN_COUNT;
                } else if (items.get(position) instanceof EmptyValue) {
                    return SPAN_COUNT;
                } else {
                    return SPAN_COUNT;
                }
            }
        });

        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        DataHelper.createData();

        List<EmptyValue> lineList = new ArrayList<>();
        lineList.add(new EmptyValue(EmptyValue.TYPE_LINE));
        List<EmptyValue> titleList = new ArrayList<>();
        titleList.add(new EmptyValue(EmptyValue.TYPE_GOODTITLE));

        items = new Items();
        //items.addAll(titleList);
        //items.addAll(DataHelper.getAdvertList());
        //items.addAll(lineList);
        items.addAll(DataHelper.getAppInfoList());
        mRecyclerView.setAdapter(multiTypeAdapter);

        multiTypeAdapter.setItems(items);
        multiTypeAdapter.notifyDataSetChanged();
    }

    private void checkFile() {
        final String dir = App.getInstance().getExternalFilesDir("").getAbsolutePath();
        final String strfiles = Constant.FILE_MP4 + "," + Constant.FILE_ZIP;
        File[] files = HttpTool.getFiles(dir, strfiles);
        if (files != null) {
            for (File file : files) {
                if (!file.exists()) {
                    Toast.makeText(App.getInstance(), "file:" + file.getName() + ",还未下载", 1).show();
                }
            }
        }
    }

    private void printfDevice() {
        String model = Build.MODEL;
        String brand = android.os.Build.BRAND;
        String manufacturer = Build.MANUFACTURER;
        LogWriter.getInstance().info("model:" + model);
        LogWriter.getInstance().info("brand:" + brand);
        LogWriter.getInstance().info("manufacturer:" + manufacturer);
        LogWriter.getInstance().info("VERSION:" + Build.VERSION.SDK);
        LogWriter.getInstance().info("VERSION:" + Build.VERSION.RELEASE);

    }
}
