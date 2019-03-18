package cn.fm.p2p.download;

import android.os.Parcel;
import android.os.Parcelable;

public class MyCallback implements DownloadCallback, Parcelable {

    public MyCallback(Parcel in) {
    }

    public MyCallback() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyCallback> CREATOR = new Creator<MyCallback>() {
        @Override
        public MyCallback createFromParcel(Parcel in) {
            return new MyCallback(in);
        }

        @Override
        public MyCallback[] newArray(int size) {
            return new MyCallback[size];
        }
    };

    @Override
    public void downloadStart(DownloadInfo downloadInfo) {
    }

    @Override
    public void downloadProgress(DownloadInfo downloadInfo, float progress) {
    }

    @Override
    public void downloadSuccess(DownloadInfo downloadInfo) {
    }

    @Override
    public void downloadFailed(DownloadInfo downloadInfo) {
    }
}
