package com.ivanhoecambridge.mall.crashReports;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.ivanhoecambridge.mall.BuildConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kay on 2017-02-09.
 */

public class CustomizedExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;
    private String localPath;
    private Context mContext;
    public CustomizedExceptionHandler(Context context, String localPath) {
        this.localPath = localPath;
        //Getting the the default exception handler
        //that's executed when uncaught exception terminates a thread
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        mContext = context;
    }

    public void uncaughtException(Thread t, Throwable e) {

        //Write a printable representation of this Throwable
        //The StringWriter gives the lock used to synchronize access to this writer.
        final Writer stringBuffSync = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringBuffSync);
        e.printStackTrace(printWriter);
        String stacktrace = stringBuffSync.toString();
        printWriter.close();

        if (localPath != null) {
            writeToFile(mContext, stacktrace);
        }

        //Used only to prevent from any code getting executed.
        // Not needed in this example
        defaultUEH.uncaughtException(t, e);
    }

    public static void writeToFile(Context context, String currentStacktrace) {
        try {
            if(!BuildConfig.DEBUG) return;
            File dir = new File(context.getExternalCacheDir(),
                    "Crash_Reports");

            boolean success = true;
            if (!dir.exists()) {
                success = dir.mkdirs();
            }

            if(success) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy_MM_dd_HH_mm_ss");
                Date date = new Date();
                String filename = dateFormat.format(date) + ".txt";

                // Write the file into the folder
                File reportFile = new File(dir, filename);
                FileWriter fileWriter = new FileWriter(reportFile);
                fileWriter.append(currentStacktrace);
                fileWriter.flush();
                fileWriter.close();
            }

        } catch (Exception e) {
            Log.e("ExceptionHandler", e.getMessage());
        }
    }

}
