package cn.fm.p2p.bean;

/**
 * Created by Administrator on 2019/1/19.
 */

public class BannerInfo {
    private String title;
    private String bannerUrl;

    public BannerInfo(String title, String bannerUrl) {
        this.title = title;
        this.bannerUrl = bannerUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }
}
