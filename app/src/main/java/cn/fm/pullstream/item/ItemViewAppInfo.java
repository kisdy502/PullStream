package cn.fm.pullstream.item;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.fm.pullstream.R;
import cn.fm.pullstream.bean.AppInfo;
import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by Administrator on 2019/1/19.
 */

public class ItemViewAppInfo extends ItemViewBinder<AppInfo, ItemViewAppInfo.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_app, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull AppInfo item) {
        holder.tvName.setText(item.getAppName());
        holder.tvPackageName.setText(item.getAppPackage());
        holder.tvCreated.setText(item.getAppDate());

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvPackageName;
        TextView tvCreated;

        ViewHolder(View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.app_name);
            tvPackageName=itemView.findViewById(R.id.app_package_name);
            tvCreated=itemView.findViewById(R.id.app_created_date);
        }
    }
}
