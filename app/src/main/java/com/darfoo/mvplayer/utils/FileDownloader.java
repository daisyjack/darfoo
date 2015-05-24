package com.darfoo.mvplayer.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;
import android.util.Log;


/**
 * Created by YuXiaofei on 2014/11/24.
 */
public class FileDownloader {

    private  String mFilePath;
    private String mUrl;
    private String mDirPath;

    public FileDownloader(String url, String filePath, String dir) {
        mUrl = url;
		mFilePath = filePath;
        mDirPath = dir;
    }

    /**
     * 该函数返回整形
     *-1：代表下载文件出错
     * 0：代表下载文件成功
     */
    public int download()  {
        InputStream is = null;
        OutputStream os = null;

        try {
            if(!isFileExist(mDirPath)) {
                createSDDir(mDirPath);
            }
            URL myUrl = new URL(mUrl);
            HttpURLConnection urlConn = (HttpURLConnection) myUrl.openConnection();

            is = urlConn.getInputStream();
            is = new BufferedInputStream(is);
            os = new FileOutputStream(mFilePath);
            os = new BufferedOutputStream(os);

			byte[] b = new byte[4 * 1024];
            int length;
            while((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if(is != null) {
                    is.close();
                }
                if(os != null) {
                    os.close();
                }
                Log.d("filedownloader","finish");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return 0;

    }



    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     */
    public File createSDDir(String dirName) throws IOException
    {
        File file = new File(dirName);
        file.mkdirs();
        return file;
    }

    /**
     * 判断SD卡上的文件夹是否存在
     */
    public boolean isFileExist(String fileName)
    {
        File file = new File(fileName);
        return file.exists();
    }
}
