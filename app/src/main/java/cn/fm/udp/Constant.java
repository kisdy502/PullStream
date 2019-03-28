package cn.fm.udp;

public class Constant {

    public static final String SERVERHOST = "gt.beevideo.tv";

    /** 检测nat类型 */
    public static final int SC_NAT_TYPE = 200;
    /** 检测nat类型回包 */
    public static final int SC_NAT_TYPE_BACK = 201;

    /**
     * 心跳
     */
    public static final int SC_HEARTBEAT = 100;
    /**
     * 登录code
     */
    public static final int SC_LOGIN = 101;
    /**
     * 请求下载文件
     */
    public static final int SC_DOWNLOAD = 102;
    /**
     * 回复消息
     */
    public static final int SC_REP = 103;
    /**
     * 下发source列表
     */
    public static final int SC_SLIST = 104;
    /**
     * A准备打洞消息
     */
    public static final int SC_NATA = 105;
    /**
     * B打洞消息
     */
    public static final int SC_NATB = 106;
    /**
     * 回复打洞成功+文件长度
     */
    public static final int SC_FILE_SIZE = 107;
    /**
     * 开始接收文件
     */
    public static final int SC_DOWNLOAD_READY = 108;
    /**
     * 文件头
     */
    public static final int SC_FILE_CODE = 109;
    /**
     * 下载完成
     */
    public static final int SC_DOWNLOAD_FINISH = 110;
    /**
     * 碎片段下载完成
     */
    public static final int SC_DOWNLOAD_NODE_FINISH = 111;


    /**
     *  回调状态码
     */
    /**
     * 下载成功
     */
    public static final int CALL_BACK_SUCCESS = 0;
    /**
     * 下载失败
     */
    public static final int CALL_BACK_FAIL = 1;
    /**
     * 打洞失败
     */
    public static final int CALL_BACK_NAT_FAIL = 2;
    /**
     * 没有可下载的源
     */
    public static final int CALL_BACK_NO_SOURCE = 3;
    /**
     * 已经重新登录
     */
    public static final int CALL_BACK_RELOGIN = 4;
    /**  打印nat类型 */
    public static final int CALL_BACK_NAT_TYPE = 5;

    //public static final String FILE_TS = "2b0b5e3e48a5433d9344e3616278f993.TS";
    public static final String FILE_ZIP = "2ac70c215b79466e90dcd3b8cd9cab48.zip";
    public static final String URL_ZIP = "http://mifeng.skyworthbox" +
            ".com:12000/adsys/pic/1/2ac70c215b79466e90dcd3b8cd9cab48.zip";
//    public static final String URL_TS = "http://mifeng.skyworthbox" +
//            ".com:14000/adsys/video/0ec/2b0b5e3e48a5433d9344e3616278f993.TS";
    public static final String URL_MP4 = "http://mifeng.skyworthbox" +
            ".com:14000/adsys/video/f1e/7758296157e046aeb8f1fd82abb07ef6.mp4";
    public static final String FILE_MP4 = "7758296157e046aeb8f1fd82abb07ef6.mp4";

}
