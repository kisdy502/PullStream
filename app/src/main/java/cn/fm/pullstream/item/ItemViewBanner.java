package cn.fm.pullstream.item;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.fm.pullstream.bean.BannerInfo;
import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by Administrator on 2019/1/19.
 */

public class ItemViewBanner extends ItemViewBinder<BannerInfo,ItemViewBanner.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return null;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull BannerInfo item) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
