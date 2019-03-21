package cn.fm.p2p;

import java.util.ArrayList;
import java.util.List;

import cn.fm.p2p.bean.Advert;
import cn.fm.p2p.bean.AppInfo;
import cn.fm.p2p.bean.BannerInfo;
import cn.fm.p2p.bean.NewInfo;

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

        AppInfo appInfo00 = new AppInfo();
        appInfo00.setId("000");
        appInfo00.setAppName("成为p2p提供节点");
        appInfo00.setAppPackage("cn.aikan.live");
        appInfo00.setAppDate("2019-01");

        AppInfo appInfo01 = new AppInfo();
        appInfo01.setId("001");
        appInfo01.setAppName("P2P成为提供方");
        appInfo01.setAppPackage("cn.fengchao.ad");
        appInfo01.setAppDate("2018-11");


        AppInfo appInfo02 = new AppInfo();
        appInfo02.setId("002");
        appInfo02.setAppName("P2P下载大文件5M");
        appInfo02.setAppPackage("cn.beevideo");
        appInfo02.setAppDate("2015-02");

        AppInfo appInfo03 = new AppInfo();
        appInfo03.setId("003");
        appInfo03.setAppName("P2P下载小文件60K)");
        appInfo03.setAppPackage("cn.beevideo");
        appInfo03.setAppDate("2015-02");

        AppInfo appInfo04 = new AppInfo();
        appInfo04.setId("004");
        appInfo04.setAppName("上传日志");
        appInfo04.setAppPackage("cn.aikan.live");
        appInfo04.setAppDate("2019-01");

        AppInfo appInfo05 = new AppInfo();
        appInfo05.setId("005");
        appInfo05.setAppName("下载功能测试");
        appInfo05.setAppPackage("cn.aikan.live");
        appInfo05.setAppDate("2019-01");

        list.add(appInfo00);
        list.add(appInfo02);
        list.add(appInfo03);
        list.add(appInfo04);
        list.add(appInfo05);

    }


    private static void initBannerList(List<BannerInfo> list) {
        BannerInfo bannerInfo01 = new BannerInfo("创造最具有活力的平台", ImgUrls.url06);
        list.add(bannerInfo01);

    }

    private static void initNewInfoList(List<NewInfo> list) {
        NewInfo newInfo01 = new NewInfo();
        newInfo01.setNewId("0c701");
        newInfo01.setNewTitle("忘记何解决");
        newInfo01.setCommentCount(452);
        newInfo01.setNewDesc("乐山的放解决的困境");

        list.add(newInfo01);
    }
}
