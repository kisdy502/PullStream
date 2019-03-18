package cn.fm.p2p.item;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.fm.p2p.App;
import cn.fm.p2p.R;
import cn.fm.p2p.UploadHelper;
import cn.fm.p2p.bean.AppInfo;
import cn.fm.p2p.download.DownloadCallback;
import cn.fm.p2p.download.DownloadInfo;
import cn.fm.udp.LogWriter;
import cn.fm.udp.Constant;
import cn.fm.udp.HttpTool;
import cn.fm.udp.ServiceManager;
import me.drakeet.multitype.ItemViewBinder;

public class ItemViewAppInfo extends ItemViewBinder<AppInfo, ItemViewAppInfo.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_app, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, @NonNull final AppInfo item) {
        holder.tvName.setText(item.getAppName());
//        holder.tvPackageName.setText(item.getAppPackage());
//        holder.tvCreated.setText(item.getAppDate());
//        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(160).start();
//                } else {
//                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(160).start();
//                }
//            }
//        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {

                String id = item.getId();
                if (id.equalsIgnoreCase("000")) {

                    boolean isLogin = App.getInstance().isP2pLogin();
                    if (isLogin) {
                        Toast.makeText(App.getInstance(), "已经登录成为服务器结点，无需再登录", 1).show();
                        return;
                    }

                    final String dir = App.getInstance().getExternalFilesDir("").getAbsolutePath();

                    final String strfiles = Constant.FILE_MP4 + "," + Constant.FILE_ZIP;

                    File[] fileList = HttpTool.getFiles(dir, strfiles);

                    int count = 0;
                    for (File f : fileList) {
                        if (f.exists()) {
                            count++;
                        }
                    }
                    if (count == 2) {
                        ServiceManager servicejanManager = new ServiceManager(App.getInstance());
                        servicejanManager.startService(Constant.SERVERHOST, dir, strfiles, Constant.SC_LOGIN);
                    } else {
                        for (File f : fileList) {
                            if (f.exists()) {
                                f.delete();
                            }
                        }
                        startp2pDonwloadAndLogin(holder, dir, strfiles);
                    }

                } else if (id.equalsIgnoreCase("001")) {

                    final String dir = App.getInstance().getExternalFilesDir("").getAbsolutePath();
                    final String strfiles = Constant.FILE_MP4 + "," + Constant.FILE_ZIP;
                    File[] files = HttpTool.getFiles(dir, strfiles);
                    if (files != null) {
                        for (File file : files) {
                            if (!file.exists()) {
                                Toast.makeText(App.getInstance(), "p2p提供方需要先下载好素材", 1).show();
                                return;
                            }
                        }
                    }
                    boolean isLogin = App.getInstance().isP2pLogin();
                    if (isLogin) {
                        Toast.makeText(App.getInstance(), "已经登录成为服务器结点，无需再登录", 1).show();
                        return;
                    }
                    ServiceManager servicejanManager = new ServiceManager(App.getInstance());
                    servicejanManager.startService(Constant.SERVERHOST, dir, strfiles, Constant.SC_LOGIN);

                } else if (id.equalsIgnoreCase("002")) {
                    LogWriter.getInstance().info("p2pDownload" + Constant.FILE_MP4);
                    String dir = App.getInstance().getExternalFilesDir("").getAbsolutePath();
                    String file = Constant.FILE_MP4;
                    ServiceManager servicejanManager = new ServiceManager(App.getInstance());
                    servicejanManager.startService(Constant.SERVERHOST, dir, file, Constant.SC_DOWNLOAD);

                } else if (id.equalsIgnoreCase("003")) {

                    String dir = App.getInstance().getExternalFilesDir("").getAbsolutePath();
                    String file = Constant.FILE_ZIP;
                    ServiceManager servicejanManager = new ServiceManager(App.getInstance());
                    servicejanManager.startService(Constant.SERVERHOST, dir, file, Constant.SC_DOWNLOAD);

                } else if (id.equalsIgnoreCase("004")) {
                    new Thread() {
                        @Override
                        public void run() {
                            UploadHelper.upload();
                        }
                    }.start();
                }

            }
        });

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvPackageName;
        TextView tvCreated;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.app_name);
            tvPackageName = itemView.findViewById(R.id.app_package_name);
            tvCreated = itemView.findViewById(R.id.app_other_info);
        }
    }

    private void startp2pDonwloadAndLogin(final ViewHolder holder, final String dir, final String
            strfiles) {
        new Thread() {
            @Override
            public void run() {
                HttpTool.checkAndDownloadFiles(dir, strfiles, new DownloadCallback() {

                    boolean tsOk = false, zipOk = false;

                    @Override
                    public void downloadStart(DownloadInfo downloadInfo) {

                    }

                    @Override
                    public void downloadProgress(final DownloadInfo downloadInfo, final float progress) {
                        holder.tvCreated.post(new Runnable() {
                            @Override
                            public void run() {
                                holder.tvCreated.setText("下载进度:" + String.valueOf(progress) + "%");
                            }
                        });
                    }

                    @Override
                    public void downloadSuccess(final DownloadInfo downloadInfo) {
                        holder.itemView.post(new Runnable() {
                            @SuppressLint("WrongConstant")
                            @Override
                            public void run() {
                                Toast.makeText(App.getInstance(), downloadInfo
                                        .getSaveFileName() + "下载完成", 1).show();
                            }
                        });

                        if (downloadInfo.getSaveFileName().equalsIgnoreCase(Constant.FILE_MP4)) {
                            tsOk = true;
                        } else if (downloadInfo.getSaveFileName().equalsIgnoreCase(Constant.FILE_ZIP)) {
                            zipOk = true;
                        }

                        if (tsOk && zipOk) {
                            ServiceManager servicejanManager = new ServiceManager(App.getInstance());
                            servicejanManager.startService(Constant.SERVERHOST, dir, strfiles, Constant.SC_LOGIN);
                        }
                    }

                    @Override
                    public void downloadFailed(final DownloadInfo downloadInfo) {
                        holder.itemView.post(new Runnable() {
                            @SuppressLint("WrongConstant")
                            @Override
                            public void run() {
                                Toast.makeText(App.getInstance(), downloadInfo
                                        .getSaveFileName() + "下载失败", 1).show();
                            }
                        });
                    }
                });
            }
        }.start();
    }
}
