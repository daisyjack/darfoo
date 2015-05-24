package com.darfoo.darfoolauncher.support;

import com.google.gson.JsonObject;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.UUID;

/**
 * Created by jewelzqiu on 11/22/14.
 */
public class Utils {

    public static final String BASE_URL = "http://115.29.238.12:83";

    public static final String NEED_UPDATE = "need_update";

    public static final String NEW_VERSION = "new_version";

    public static final int TYPE_TEACH = 0;

    public static final int TYPE_VIDEO = 1;

    private static final String updateUrl = BASE_URL + "/resources/version/latest";

    private static boolean isDownloading = false;

    public static Bitmap getRoundedCornerBitmap(Bitmap srcBitmap) throws NullPointerException {
        SoftReference<Bitmap> softReference;
        if (srcBitmap.getWidth() >= srcBitmap.getHeight()) {
            softReference = new SoftReference<Bitmap>(Bitmap.createBitmap(
                    srcBitmap,
                    srcBitmap.getWidth() / 2 - srcBitmap.getHeight() / 2,
                    0,
                    srcBitmap.getHeight(),
                    srcBitmap.getHeight()
            ));
        } else {
            softReference = new SoftReference<Bitmap>(Bitmap.createBitmap(
                    srcBitmap,
                    0,
                    srcBitmap.getHeight() / 2 - srcBitmap.getWidth() / 2,
                    srcBitmap.getWidth(),
                    srcBitmap.getWidth()
            ));
        }
        Bitmap bitmap = softReference.get();
        softReference = new SoftReference<Bitmap>(Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888));
        Bitmap output = softReference.get();
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2,
                bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static void checkAppUpdate(final Context context) {
        Ion.with(context).load(updateUrl).asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            return;
                        }

                        int versionCode = result.get("version").getAsInt();
                        File dir = context.getExternalCacheDir();
                        if (dir == null) {
                            dir.mkdirs();
                        }
                        boolean needDownload = true;
                        for (File file : dir.listFiles()) {
                            String filename = file.getName();
                            if (filename.toLowerCase().endsWith(".apk")) {
                                try {
                                    int fileVersion = Integer
                                            .parseInt(filename.substring(0, filename.length() - 4));
                                    if (fileVersion == versionCode) {
                                        needDownload = false;
                                    } else {
                                        file.delete();
                                    }
                                } catch (NumberFormatException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        if (!needDownload) {
                            return;
                        }
                        try {
                            int currentVersion = context.getPackageManager()
                                    .getPackageInfo(context.getPackageName(), 0).versionCode;
                            if (versionCode > currentVersion) {
                                System.out.println("Found new version: " + versionCode);
                                downloadApk(context, result.get("version_url").getAsString(),
                                        versionCode);
                            } else {
                                SharedPreferences preferences = PreferenceManager
                                        .getDefaultSharedPreferences(context);
                                preferences.edit()
                                        .putBoolean(NEED_UPDATE, false)
                                        .apply();
                            }
                        } catch (PackageManager.NameNotFoundException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    private static void downloadApk(final Context context, String url, final int version) {
        if (isDownloading) {
            return;
        } else {
            isDownloading = true;
        }
        System.out.println("Download new version: " + version);
        Ion.with(context).load(url).write(
                new File(context.getExternalCacheDir().getAbsolutePath() + "/" + version + ".apk"))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File result) {
                        isDownloading = false;
                        if (e != null) {
                            System.out.println("Download failed");
                            return;
                        }
                        System.out.println(
                                context.getExternalCacheDir().getAbsolutePath() + "/" + version
                                        + ".apk");
                        SharedPreferences preferences = PreferenceManager
                                .getDefaultSharedPreferences(context);
                        preferences.edit()
                                .putBoolean(NEED_UPDATE, true)
                                .putInt(NEW_VERSION, version)
                                .apply();
                    }
                });
    }

    public static void sendStatistics(Context context, String type, int index) {
        String url = BASE_URL + "/statistics/" + type + "/" + index + "/m/" +
                NetManager.getMacAddress(context) + "/h/" + NetManager.getHostIp() +
                "/u/" + getMyUUID(context);
        Ion.with(context).load(url).asString();
    }

    public static String getMyUUID(Context context) {
        final TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }
}
