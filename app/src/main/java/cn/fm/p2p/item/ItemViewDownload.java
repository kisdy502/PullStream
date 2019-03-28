package cn.fm.p2p.item;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.fm.p2p.R;
import cn.fm.p2p.download.DownloadInfo;
import cn.fm.p2p.download.DownloadManager;
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
        //TODO 下载过程中，滚动会导致item布局显示错乱，分析原因:下载进度没有保存在DownloadInfo对象中，后续优化
        holder.tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
//                        DownloadManagerOld.getInstance().startDownload(item, new DownloadCallback() {
//                            @Override
//                            public void downloadStart(DownloadInfo downloadInfo) {
//                                Log.d(TAG, "downloadStart");
//                            }
//
//                            @Override
//                            public void downloadProgress(DownloadInfo downloadInfo, final float
//                                    progress) {
//                                holder.progressBar.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        holder.progressBar.setProgress((int) progress);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void downloadSuccess(DownloadInfo downloadInfo) {
//                                Log.d(TAG, "downloadSuccess");
//                            }
//
//                            @Override
//                            public void downloadFailed(DownloadInfo downloadInfo, int failedCode) {
//                                Log.d(TAG, "downloadFailed:" + downloadInfo.getSaveFileName());
//                                Log.d(TAG, "downloadFailed:" + failedCode);
//                            }
//                        });

                        boolean result = DownloadManager.getInstance().download(
                                item.getDownloadUrl(),
                                item.getSaveDir(),
                                item.getSaveFileName(),
                                new DownloadManager.ProgressListener() {
                                    @Override
                                    public void onFailed(String downloadUrl, int result, String desc) {
                                        Log.d("item", "onFailed:" + result);
                                    }

                                    @Override
                                    public void onUpdate(String downloadUrl, final long bytesRead,
                                                         final long contentLength, final boolean done) {
                                        holder.progressBar.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                float progress = 100 * bytesRead * 1.0f / contentLength;
                                                Log.d("item", "progress:" + progress);
                                                holder.progressBar.setProgress((int) progress);
                                            }
                                        });
                                    }
                                });
                        Log.d(TAG, "下载文件结果:" + result);
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
