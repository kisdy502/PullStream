package cn.fm.pullstream;

import java.util.ArrayList;
import java.util.List;

import cn.fm.pullstream.bean.Advert;
import cn.fm.pullstream.bean.AppInfo;
import cn.fm.pullstream.bean.BannerInfo;
import cn.fm.pullstream.bean.NewInfo;

/**
 * Created by Administrator on 2019/1/21.
 */

public class DataHelper {

    public static List<Advert> getAdvertList() {
        return advertList;
    }

    public static List<AppInfo> getAppInfoList() {
        return appInfoList;
    }

    public static List<BannerInfo> getBannerInfoList() {
        return bannerInfoList;
    }

    public static List<NewInfo> getNewInfoList() {
        return newInfoList;
    }

    private static List<Advert> advertList = new ArrayList<>();
    private static List<AppInfo> appInfoList = new ArrayList<>();
    private static List<BannerInfo> bannerInfoList = new ArrayList<>();
    private static List<NewInfo> newInfoList = new ArrayList<>();

    public static void createData() {
        initAdvertList(advertList);
        initAppInfoList(appInfoList);
        initBannerList(bannerInfoList);
        initNewInfoList(newInfoList);
    }


    private static void initAdvertList(List<Advert> list) {
        Advert advert01 = new Advert("ad01", ImgUrls.url01);
        Advert advert02 = new Advert("ad02", ImgUrls.url02);
        Advert advert03 = new Advert("ad03", ImgUrls.url03);
        Advert advert04 = new Advert("ad04", ImgUrls.url04);

        list.add(advert01);
        list.add(advert02);
        list.add(advert03);
        list.add(advert04);


    }


    private static void initAppInfoList(List<AppInfo> list) {

        AppInfo appInfo01 = new AppInfo();
        appInfo01.setAppName("蜂巢广告");
        appInfo01.setAppPackage("cn.fengchao.ad");
        appInfo01.setAppDate("2018-11");


        AppInfo appInfo02 = new AppInfo();
        appInfo02.setAppName("蜜蜂视频");
        appInfo02.setAppPackage("cn.beevideo");
        appInfo02.setAppDate("2015-02");


        AppInfo appInfo03 = new AppInfo();
        appInfo03.setAppName("爱看直播");
        appInfo03.setAppPackage("cn.aikan.live");
        appInfo03.setAppDate("2019-01");

        list.add(appInfo01);
        list.add(appInfo02);
        list.add(appInfo03);

    }


    private static void initBannerList(List<BannerInfo> list) {
        BannerInfo bannerInfo01 = new BannerInfo("创造最具有活力的平台", ImgUrls.url06);
        list.add(bannerInfo01);

    }

    private static void initNewInfoList(List<NewInfo> list) {
        NewInfo newInfo01 = new NewInfo();
        newInfo01.setNewId("0c701");
        newInfo01.setNewTitle("忘记了手机密码如何解决");
        newInfo01.setCommentCount(452);
        newInfo01.setNewDesc("乐山的王女士手机放在柜子里多日，今日拿起时发现记不起手机开机密码，拿到手机专卖店解决也无法决绝，现在陷入无法使用的困境");

        list.add(newInfo01);
    }
}
