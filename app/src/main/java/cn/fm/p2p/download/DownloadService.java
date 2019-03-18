package cn.fm.p2p.download;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

public class DownloadService extends IntentService {

    private static final String ACTION_START_DOWNLOAD = "cn.fm.p2p.download.action.start";
    private static final String ACTION_PAUSE_DOWNLOAD = "cn.fm.p2p.download.action.pause";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "cn.fm.p2p.download.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "cn.fm.p2p.download.extra.PARAM2";

    public DownloadService() {
        super("DownloadService");
    }

    public static void startDownloadAction(Context context, DownloadInfo downloadInfo) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_START_DOWNLOAD);
        intent.putExtra(EXTRA_PARAM1, downloadInfo);
        context.startService(intent);
    }

    public static void pauseDownloadAction(Context context, DownloadInfo downloadInfo) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_PAUSE_DOWNLOAD);
        intent.putExtra(EXTRA_PARAM1, downloadInfo);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_DOWNLOAD.equals(action)) {
                final DownloadInfo downloadInfo = intent.getParcelableExtra(EXTRA_PARAM1);
                DownloadManager.getInstance().startDownload(downloadInfo, null);
            } else if (ACTION_PAUSE_DOWNLOAD.equals(action)) {
                final DownloadInfo downloadInfo = intent.getParcelableExtra(EXTRA_PARAM1);
                DownloadManager.getInstance().cancel(downloadInfo.getDownloadUrl());
            }
        }
    }
}
