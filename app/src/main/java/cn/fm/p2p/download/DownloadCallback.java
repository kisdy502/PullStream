package cn.fm.p2p.download;


public interface DownloadCallback {

    int FAILED_EXIST = 0;
    int FAILED_IO_EXCEPTION = 1;
    int FAILED_EMPTY_RESPONSE = 2;
    int FAILED_EMPTY_STREAM = 3;
    int FAILED_RESPONSE_CODE = 4;

    void downloadStart(DownloadInfo downloadInfo);

    void downloadProgress(DownloadInfo downloadInfo, float progress);

    void downloadSuccess(DownloadInfo downloadInfo);

    void downloadFailed(DownloadInfo downloadInfo, int failedCode);
}
