package cn.fm.p2p.download;

import java.io.Closeable;

/**
 * Created by Administrator on 2019/3/11.
 */

public class IOUtils {
    public static void close(Closeable... closeable) {
        for (Closeable c : closeable) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
