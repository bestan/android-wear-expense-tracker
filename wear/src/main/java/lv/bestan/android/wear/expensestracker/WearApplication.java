package lv.bestan.android.wear.expensestracker;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Stan on 03/08/2014.
 */
public class WearApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("Campton.Light.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        final Context context = this;

        final Thread.UncaughtExceptionHandler originalUncaughtException = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {

                String trace = "";
                Throwable t = throwable;
                try {
                    while (t != null) {
                        trace += t.getClass().getName() + " "
                                + t.getLocalizedMessage();
                        for (StackTraceElement stackTrace : t
                                .getStackTrace()) {
                            trace += "\n" + stackTrace.toString();
                        }
                        trace += "\n";
                        t = t.getCause();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Calendar c = Calendar.getInstance();
                String y = formatNumber(c.get(Calendar.YEAR));
                String m = formatNumber(c.get(Calendar.MONTH) + 1);
                String d = formatNumber(c.get(Calendar.DAY_OF_MONTH));
                String h = formatNumber(c.get(Calendar.HOUR_OF_DAY));
                String min = formatNumber(c.get(Calendar.MINUTE));
                String s = formatNumber(c.get(Calendar.SECOND));


                String time =  y + "-" + m + "-" + d + " " + h + ":" + min + ":" + s;
                String output = "time: " + time + "\n"
                        + "model: " + Build.MODEL + "\n"
                        + "trace" + trace + "\n\n\n";

                byte[] data = output.getBytes();
                DataOutputStream out = null;
                File file = new File(context.getFilesDir() + "/crash.data");
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    out = new DataOutputStream(new FileOutputStream(file.getPath(), true));
                    out.write(data); //data is String variable
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                originalUncaughtException.uncaughtException(thread, throwable);

                WearUtils.getInstance(context).sendCrash(output);
            }
        });

        checkForCrashes();
    }

    private void checkForCrashes() {
        //See if there is a crash to send
        File file = new File(this.getFilesDir() + "/crash.data");
        if (file.exists()) {
            byte[] fileData = new byte[(int) file.length()];
            DataInputStream dis = null;
            try {
                dis = new DataInputStream(new FileInputStream(file));
                dis.readFully(fileData);
                dis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                file.delete();
            } catch (IOException e) {
                e.printStackTrace();
                file.delete();
            }

            WearUtils.getInstance(this).sendCrash(new String(fileData));
        }
    }

    private String formatNumber(int k) {
        if (k < 10)
            return "0" + k;
        else
            return "" + k;
    }
}
