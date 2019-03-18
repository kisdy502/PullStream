package cn.fm.p2p.bean;

/**
 * Created by Administrator on 2019/1/21.
 */

public class EmptyValue {
    public static final int TYPE_GOODTITLE = 1;
    public static final int TYPE_LINE = 2;

    public int type;

    public EmptyValue(int type) {
        this.type = type;
    }
}
