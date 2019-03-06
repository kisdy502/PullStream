package cn.fm.pullstream.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.fm.pullstream.DataHelper;
import cn.fm.pullstream.R;
import cn.fm.pullstream.bean.Advert;
import cn.fm.pullstream.bean.AppInfo;
import cn.fm.pullstream.bean.EmptyValue;
import cn.fm.pullstream.item.GoodTitleViewBinder;
import cn.fm.pullstream.item.ItemViewAdvert;
import cn.fm.pullstream.item.ItemViewAppInfo;
import cn.fm.pullstream.item.LineViewBinder;
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
                    return 3;
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
        items.addAll(titleList);
        items.addAll(DataHelper.getAdvertList());
        items.addAll(lineList);
        items.addAll(DataHelper.getAppInfoList());
        mRecyclerView.setAdapter(multiTypeAdapter);

        multiTypeAdapter.setItems(items);
        multiTypeAdapter.notifyDataSetChanged();


    }
}
