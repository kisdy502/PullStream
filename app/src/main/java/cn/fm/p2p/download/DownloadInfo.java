package cn.fm.p2p.download;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2019/1/5.
 */

public class DownloadInfo implements Parcelable {
    public static final long TOTAL_ERROR = -1;

    public DownloadInfo() {
    }

    public DownloadInfo(String downloadUrl, String saveDir, String saveFileName) {
        this.downloadUrl = downloadUrl;
        this.saveDir = saveDir;
        this.saveFileName = saveFileName;
    }

    private String downloadUrl;
    private String saveDir;
    private String saveFileName;
    private String desc;

    protected DownloadInfo(Parcel in) {
        downloadUrl = in.readString();
        saveDir = in.readString();
        saveFileName = in.readString();
        desc = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(downloadUrl);
        dest.writeString(saveDir);
        dest.writeString(saveFileName);
        dest.writeString(desc);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DownloadInfo> CREATOR = new Creator<DownloadInfo>() {
        @Override
        public DownloadInfo createFromParcel(Parcel in) {
            return new DownloadInfo(in);
        }

        @Override
        public DownloadInfo[] newArray(int size) {
            return new DownloadInfo[size];
        }
    };

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getSaveDir() {
        return saveDir;
    }

    public void setSaveDir(String saveDir) {
        this.saveDir = saveDir;
    }

    public String getSaveFileName() {
        return saveFileName;
    }

    public void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "downloadUrl='" + downloadUrl + '\'' +
                ", saveDir='" + saveDir + '\'' +
                ", saveFileName='" + saveFileName + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
