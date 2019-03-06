package cn.fm.pullstream.bean;

/**
 * Created by Administrator on 2019/1/19.
 */

public class Advert {
    private String adTitle;
    private String adImgUrl;

    public Advert(String adTitle, String adImgUrl) {
        this.adTitle = adTitle;
        this.adImgUrl = adImgUrl;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public void setAdTitle(String adTitle) {
        this.adTitle = adTitle;
    }

    public String getAdImgUrl() {
        return adImgUrl;
    }

    public void setAdImgUrl(String adImgUrl) {
        this.adImgUrl = adImgUrl;
    }
}
