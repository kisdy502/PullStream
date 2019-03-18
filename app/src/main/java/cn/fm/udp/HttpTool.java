package cn.fm.udp;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import cn.fm.p2p.download.DownloadCallback;
import cn.fm.p2p.download.DownloadInfo;
import cn.fm.p2p.download.DownloadManager;


public class HttpTool {
    public static void checkAndDownloadFiles(String dir, String files, DownloadCallback callback) {
        File[] fileArrays = getFiles(dir, files);
        if (fileArrays != null && fileArrays.length > 0) {
            for (int i = 0, n = fileArrays.length; i < n; i++) {
                File file = fileArrays[i];
                System.out.println("file:" + file.getAbsolutePath());
                if (!file.exists()) {
                    httpDownloadFile(dir, file, callback);
                }
            }
        }
    }

    private static void httpDownloadFile(String dir, File file, DownloadCallback callback) {
        String url = getUrlByName(file.getName());
        DownloadInfo downloadInfo = new DownloadInfo(url, dir, file.getName());
        boolean result = DownloadManager.getInstance().startDownload(downloadInfo, callback);
        if (result) {
            String md5 = getFileMD5String(file);
            System.out.println("downloadInfo md5:" + md5);
        }
    }


    public static File[] getFiles(String dir, String files) {
        if (TextUtils.isEmpty(dir) || TextUtils.isEmpty(files)) {
            return null;
        }
        String[] strFiles = files.trim().split(",");
        int length = strFiles.length;
        File[] fileArrays = new File[length];
        for (int i = 0; i < length; i++) {
            File file = new File(dir, strFiles[i]);
            fileArrays[i] = file;
        }
        return fileArrays;
    }

    private static String getUrlByName(String fileName) {
        if (fileName.equalsIgnoreCase(Constant.FILE_MP4)) {
            return Constant.URL_MP4;
        } else if (fileName.equalsIgnoreCase(Constant.FILE_ZIP)) {
            return Constant.URL_ZIP;
        } else {
            throw new IllegalArgumentException("文件名不正确:" + fileName);
        }
    }


    public static String getFileMD5String(File file) {
        InputStream fis = null;
        String md5 = "";
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            byte[] buffer = new byte[SIZE1024];
            int numRead = 0;
            while ((numRead = fis.read(buffer, 0, SIZE1024)) != -1) {
                messagedigest.update(buffer, 0, numRead);
            }
            md5 = bufferToHex(messagedigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return md5;
    }

    private static String bufferToHex(byte[] bytes) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte[] bytes, int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static final int SIZE1024 = 1024;

    public static final int INT_0XF0 = 0xf0;
    public static final int INT_4 = 4;
    public static final int INT_0XF = 0xf;

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        // 取字节中高 4 位的数字转换, >>>
        // 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
        char c0 = hexDigits[(bt & INT_0XF0) >> INT_4];
        // 取字节中低 4 位的数字转换
        char c1 = hexDigits[bt & INT_0XF];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    protected static char[] hexDigits = {'0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'a',
            'b',
            'c',
            'd',
            'e',
            'f'};

}
