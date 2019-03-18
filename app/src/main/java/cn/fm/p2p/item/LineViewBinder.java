package cn.fm.p2p.item;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.fm.p2p.R;
import cn.fm.p2p.bean.EmptyValue;
import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by Administrator on 2019/1/21.
 */

public class LineViewBinder extends ItemViewBinder<EmptyValue, LineViewBinder.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_line, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull EmptyValue item) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.line);
        }
    }
}
