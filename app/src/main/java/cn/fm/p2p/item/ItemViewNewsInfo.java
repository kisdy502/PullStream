package cn.fm.p2p.item;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.fm.p2p.bean.NewInfo;
import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by Administrator on 2019/1/19.
 */

public class ItemViewNewsInfo extends ItemViewBinder<NewInfo,ItemViewNewsInfo.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return null;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull NewInfo item) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
