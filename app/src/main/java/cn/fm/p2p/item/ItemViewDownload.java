package cn.fm.p2p.item;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.fm.p2p.R;
import cn.fm.p2p.download.DownloadCallback;
import cn.fm.p2p.download.DownloadInfo;
import cn.fm.p2p.download.DownloadManager;
import cn.fm.p2p.download.DownloadService;
import me.drakeet.multitype.ItemViewBinder;

public class ItemViewDownload extends ItemViewBinder<DownloadInfo, ItemViewDownload.ViewHolder> {
    private final static String TAG = "Download";
    private Context mContext;

    public ItemViewDownload(Context context) {
        this.mContext = context.getApplicationContext();
    }

    protected ItemViewDownload(Parcel in) {
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_download_info, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, @NonNull final DownloadInfo item) {

        holder.tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        DownloadManager.getInstance().startDownload(item, new DownloadCallback() {
                            @Override
                            public void downloadStart(DownloadInfo downloadInfo) {
                                Log.d(TAG, "downloadStart");
                            }

                            @Override
                            public void downloadProgress(DownloadInfo downloadInfo, final float
                                    progress) {
                                holder.progressBar.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.progressBar.setProgress((int) progress);
                                    }
                                });
                            }

                            @Override
                            public void downloadSuccess(DownloadInfo downloadInfo) {
                                Log.d(TAG, "downloadSuccess");
                            }

                            @Override
                            public void downloadFailed(DownloadInfo downloadInfo) {
                                Log.d(TAG, "downloadFailed");
                            }
                        });
                    }
                }.start();

            }
        });

        holder.tvPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.getInstance().cancel(item.getDownloadUrl());
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStart;
        TextView tvPause;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.download_progress);
            tvStart = itemView.findViewById(R.id.start_download);
            tvPause = itemView.findViewById(R.id.pause_download);
        }
    }
}
