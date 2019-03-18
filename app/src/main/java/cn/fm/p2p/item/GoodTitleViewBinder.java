package cn.fm.p2p.item;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.fm.p2p.R;
import cn.fm.p2p.bean.EmptyValue;
import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by Administrator on 2019/1/21.
 */

public class GoodTitleViewBinder extends ItemViewBinder<EmptyValue, GoodTitleViewBinder.ViewHolder> {

    private Context mContext;

    public GoodTitleViewBinder(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_title, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull EmptyValue item) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_title);
        }
    }

}
