package cn.fm.p2p.download;


public interface DownloadCallback {

    void downloadStart(DownloadInfo downloadInfo);

    void downloadProgress(DownloadInfo downloadInfo, float progress);

    void downloadSuccess(DownloadInfo downloadInfo);

    void downloadFailed(DownloadInfo downloadInfo);
}
