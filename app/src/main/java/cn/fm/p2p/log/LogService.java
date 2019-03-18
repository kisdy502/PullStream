package cn.fm.p2p.log;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.fm.p2p.App;

public class LogService extends IntentService {
    private final static String TAG = "LogService";
    private final static String LOGDIR = "Log";
    private final static String LOGFILE = "all_log.txt";
    private final static String LOGFILE_BAK = "all_log_bak.txt";
    private final static int MAX_LEN = 16 * 1024 * 1024; //16MB
    private final static int MAX_LINES = 16 * 10000; //超过16万行不打印

    private File mLogFile;
    private boolean isRunning = false;
    private Process process;

    public LogService() {
        super("LogService");
    }

    public LogService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (process != null) {
            process.destroy();
        }

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        boolean init = initLogPath();
        if (init) {
            isRunning = true;
            System.out.println(mLogFile.getAbsoluteFile());
            startSaveLog();
        }
    }

    private void startSaveLog() {
        clearLogCache();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        createLogCollector();

    }

    private void clearLogCache() {
        Process proc = null;
        List<String> commandList = new ArrayList<String>();
        commandList.add("logcat");
        commandList.add("-c");
        try {
            proc = Runtime.getRuntime().exec(
                    commandList.toArray(new String[commandList.size()]));

            if (proc.waitFor() != 0) {
                Log.e("log", " clearLogCache proc.waitFor() != 0");
            }
        } catch (Exception e) {
            Log.e(TAG, "clearLogCache failed", e);
        } finally {
            try {
                proc.destroy();
            } catch (Exception e) {
                Log.e(TAG, "clearLogCache failed", e);

            }
        }
    }

    /**
     * 开始收集日志信息
     */
    public void createLogCollector() {
        List<String> commandList = new ArrayList<String>();
        commandList.add("logcat");
        commandList.add("-f");
        commandList.add(mLogFile.getAbsolutePath());
        commandList.add("-v");
        commandList.add("time");
        commandList.add("*:D");

        //commandList.add("*:E");// 过滤所有的错误信息

        // 过滤指定TAG的信息
        // commandList.add("MyAPP:V");
        // commandList.add("*:S");
        try {
            process = Runtime.getRuntime().exec(
                    commandList.toArray(new String[commandList.size()]));
        } catch (Exception e) {
            Log.e(TAG, "CollectorThread == >" + e.getMessage(), e);
        }

    }

    private boolean initLogPath() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File path = App.getInstance().getExternalFilesDir("");
            boolean success = false;
            if (!path.exists()) {
                success = path.mkdir();
            } else {
                success = true;
            }

            if (success) {
                File logDir = new File(path, LOGDIR);
                if (!logDir.exists()) {
                    success = logDir.mkdir();
                } else {
                    success = true;
                }

                if (success) {
                    mLogFile = new File(logDir, LOGFILE);

                    if (!mLogFile.exists()) {
                        try {
                            mLogFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (mLogFile.length() > MAX_LEN) {
                            File logBak = new File(logDir, LOGFILE_BAK);
                            if (logBak.exists()) {
                                logBak.delete();
                            }
                            mLogFile.renameTo(logBak);
                        }
                    }
                    return mLogFile.exists();
                }
            }
        }
        return false;
    }


}
