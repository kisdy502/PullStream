package cn.fm.udp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class LogWriter {

    private static Logger logger = Logger.getLogger(UDPClient.class.getName());

    public static LogWriter getInstance() {
        return Holder.writer;
    }

    private File logDir;

    private File logFile;

    public void init(File rootDir) {
        logDir = rootDir;
    }

    private final static class Holder {
        private final static LogWriter writer = new LogWriter();
    }


    public File getLogDir() {
        return logDir;
    }

    public void writeLog(String data) {
        if (logFile == null || !logFile.exists()) {
            logFile = createLogFile();
        }
        writeData(logFile, data);
    }

    public void consoleLog(String data) {
        logger.info(data);
    }

    public File createLogFile() {
        String logFileName = "Log_".concat(timestampToDateString(System
                .currentTimeMillis(), "yyyyMMdd")).concat(".txt");
        File logFile = new File(logDir, logFileName);
        return logFile;
    }

    private void writeData(File logFile, String data) {
        boolean created = true;
        if (!logFile.exists()) {
            try {
                created = logFile.createNewFile();
                if (created) {
                    logFile.setReadable(true, true);
                    logFile.setWritable(true, true);    //可读可写
                }
            } catch (IOException e) {
                e.printStackTrace();
                created = false;
                return;
            }
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(logFile, true);  //向日志追加内容
            writer.write(data);
            writer.write("\n");
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != writer) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String timestampToDateString(long stamp, String format) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = new Date(stamp);
        res = simpleDateFormat.format(date);
        return res;
    }
}