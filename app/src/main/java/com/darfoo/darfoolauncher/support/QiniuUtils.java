package com.darfoo.darfoolauncher.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by zjh on 14-12-21.
 */
public class QiniuUtils {
    private static UploadManager uploadManager;
    private static String key;
    private static ResponseInfo info;
    private static JSONObject resp;
    private static Context context;
    private static String uploadToken;
    private static String mac;
    private static HttpClient client = null;

    public QiniuUtils(Context mcontext) {
        context = mcontext;
        uploadManager = new UploadManager();
    }

    public static void uploadResource(Context mcontext,String houzhui,String title,int epoch,String resourcePath ){
        uploadToken = getToken();
        mac = NetManager.getMacAddress(mcontext);
        context = mcontext;
        String filename = title +"-"+ epoch +"-"+ mac + houzhui;
        String bmpname = title + "-"+ epoch +"-"+ mac;
        Log.e("uploadresource", uploadToken);
        if (uploadToken.equals("error")){
            //return this.uploadToken;
            Log.e("uploadresource", "getuploadtoken failed");
        }else{
            try {
                template(filename,resourcePath,bmpname);
                //return "ok";
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                //return "error";
                Log.e("uploadresource", "upload resource failed");
            }
        }
    }

    private static void template(String filename,String videoPath,String bmpname) throws Throwable {
        //final String expectKey = "r=" + size + "k";
        uploadManager = new UploadManager();
        final String expectKey = filename;
        final File videofile = new File(videoPath);
        Bitmap mbitmap = getVideoThumbnail(videoPath, 236, 128,
                MediaStore.Images.Thumbnails.MICRO_KIND);
        final String bmpfilename = bmpname + ".png";
        String imgpath = Environment.getExternalStorageDirectory().getPath()
                + File.separator + "Darfoo" + File.separator + "video" + File.separator+"temp.png";
        File file = new File(imgpath);
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(imgpath);
        mbitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        final File img = new File(imgpath);
        new Runnable() { // THIS IS THE KEY TO SUCCESS
            public void run() {
                uploadManager.put(videofile, expectKey, uploadToken, new UpCompletionHandler() {
                    public void complete(String k, ResponseInfo rinfo, JSONObject response) {
                        Log.i("qiniutest", k + rinfo);
                        key = k;
                        info = rinfo;
                        resp = response;
                        System.out.println("assert key -> " + expectKey.equals(key));
                        System.out.println("assert is ok -> " + info.isOK());
                        System.out.println("request id -> " + info.reqId);
                        System.out.println("response -> " + resp);
                        uploadManager.put(img, bmpfilename, uploadToken, new UpCompletionHandler() {
                            public void complete(String k, ResponseInfo rinfo, JSONObject response) {
                                Log.i("qiniutest", k + rinfo);
                                key = k;
                                info = rinfo;
                                resp = response;
                                System.out.println("assert key -> " + expectKey.equals(key));
                                System.out.println("assert is ok -> " + info.isOK());
                                System.out.println("request id -> " + info.reqId);
                                System.out.println("response -> " + resp);
                            }
                        }, null);
                        callbackAfterUpload(expectKey,bmpfilename);
                    }
                }, null);
            }
        }.run();
        img.delete();
    }
    private static Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                     int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
    private static String getToken() {
       /* try {
            JsonObject json = Ion.with(this.context)
                    .load("http://112.124.68.27:8080/darfoobackend/rest/uploadresource/gettk").asJsonObject().get();
            return Cryptor.base64DecodeStr(json.get("tk").toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "error";
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "error";
        }*/
        String https = "https://darfoo.com/uploadresource/gettkna";
        HttpParams httpParameters = new BasicHttpParams();
        // 设置连接超时
        HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
        // 设置socket超时
        HttpConnectionParams.setSoTimeout(httpParameters, 3000);
        // 获取HttpClient对象 （认证）
        HttpClient hc = initHttpClient(httpParameters);
        StringBuilder builder = new StringBuilder();
        HttpGet get = new HttpGet(https);
        try {
            HttpResponse response = hc.execute(get);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                builder.append(s);
            }
            Log.i("json_str", builder.toString());
            String token = new JSONObject(builder.toString()).getString("tk");
            return Cryptor.base64DecodeStr(token);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    public static synchronized HttpClient initHttpClient(HttpParams params) {
        if(client == null){
            try {

                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
                HttpProtocolParams.setUseExpectContinue(params, true);
                // 设置http和https支持
                SchemeRegistry registry = new SchemeRegistry();
                registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                registry.register(new Scheme("https", SSLTrustAllSocketFactory.getSocketFactory(), 443));

                ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

                return new DefaultHttpClient(ccm, params);
            } catch (Exception e) {
                e.printStackTrace();
                return new DefaultHttpClient(params);
            }
        }
        return client;
    }

    private static void callbackAfterUpload(String vedioKey,String imagekey){
        /*StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                .build());*/
        //System.out.println("start to post data");
        Log.e("post", "start to post data");

            JsonObject  json = new JsonObject ();
            json.addProperty("videokey", vedioKey);
            json.addProperty("imagekey", imagekey);
            Ion.with(context)
                .load(Utils.BASE_URL+"/uploadresource/finishcallbackna")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        //System.out.println("post successful");
                        //System.out.println(result.get("status").toString());
                        Log.e("post", "successful");
                        Log.e("post", result.get("status").toString());
                        // do stuff with the result or error
                    }
                });

       /* String https = "https://darfoo.com/uploadresource/finishcallbackna";
        HttpParams httpParameters = new BasicHttpParams();
        // 设置连接超时
        HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
        // 设置socket超时
        HttpConnectionParams.setSoTimeout(httpParameters, 3000);
        // 获取HttpClient对象 （认证）
        HttpClient httpClient = initHttpClient(httpParameters);
       *//*StringBuilder builder = new StringBuilder();*//*
        HttpPost post = new HttpPost(https);
        try {
            JSONObject  json = new JSONObject ();
            json.put("videokey", vedioKey);
            json.put("imagekey", imagekey);
            *//*nameValuePair.add(new BasicNameValuePair("jsonString", json.toString()));*//*
            StringEntity se = new StringEntity(json.toString());
            post.setEntity(se);
            HttpResponse response = httpClient.execute(post);
            String retSrc = EntityUtils.toString(response.getEntity());
            System.out.println("status->"+retSrc);
            String status = new JSONObject(retSrc).getString("status");
            System.out.println("status->"+status);
            //String token = json.getString("tk");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("post", "error");
        }*/


    }

}
