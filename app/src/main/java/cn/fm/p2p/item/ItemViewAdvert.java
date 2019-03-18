package cn.fm.p2p.item;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import cn.fm.p2p.R;
import cn.fm.p2p.bean.Advert;
import me.drakeet.multitype.ItemViewBinder;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ItemViewAdvert extends ItemViewBinder<Advert, ItemViewAdvert.ViewHolder> {

    private Context mContext;

    public ItemViewAdvert(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_advert, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Advert item) {
        holder.tvTitle.setText(item.getAdTitle());
        Glide.with(mContext)
                .load(item.getAdImgUrl())
                .transition(withCrossFade())
                .apply(new RequestOptions().centerCrop())
                .into(holder.imgAdPoster);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView imgAdPoster;
        ViewHolder(View itemView) {
            super(itemView);
            tvTitle=itemView.findViewById(R.id.ad_title);
            imgAdPoster=itemView.findViewById(R.id.ad_src);
        }
    }
}
