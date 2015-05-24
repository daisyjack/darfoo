package com.darfoo.mvplayer;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.activity.BaseFragmentActivity;
import com.darfoo.darfoolauncher.support.Cryptor;
import com.darfoo.darfoolauncher.support.NetManager;
import com.darfoo.darfoolauncher.support.Utils;
import com.darfoo.download.*;
import com.darfoo.download.utils.CommandUtils;
import com.darfoo.media.widget.VideoMediaController;
import com.darfoo.mvplayer.utils.FileDetail;
import com.darfoo.mvplayer.widgets.LocalCursorAdapter;
import com.darfoo.mvplayer.utils.StatisticsUtils;
import com.darfoo.mvplayer.widgets.VideoAdapter;
import com.darfoo.mvplayer.widgets.VideoReceiver;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import dafoo.video.DBOperation;
import tv.danmaku.ijk.media.widget.VideoView;


/**
 *
 */
public class VideoPlayerActivity extends BaseFragmentActivity implements VideoMediaController.MediaPlayerControlExtraFunction {
    private final String TAG = this.getClass().getSimpleName();
    private VideoView mVideoView;
    private View mBufferingIndicator;
    public VideoMediaController mVideoMediaController;
    private ListView mPlayList;
    public TextView mTitle;
    private ImageView mErrorImage;
    private ImageButton mRetryButton;
    private ImageButton mListUpButton;
    private ImageButton mListDownButton;
    private BroadcastReceiver mVideoReceiver;
    private DBOperation mDBOperation;
    private RelativeLayout mListLayout;
    private RelativeLayout mVideoLayout;

    public HashMap<String, AsyncTask<String, Void, Void>> mCheckMap;
    private List<FileDetail> details;
    private Cursor mCursor;
    public FileDetail detail;
    public boolean isDownFinished = false;
    public boolean isOnlineVideo = false;
    public boolean isFullScreen = false;
    private int currentIndex;
    private long currentPos;
    public String mVideoPath;
    public String mDownloadPath;

    public static final String VIDEODIR = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "Darfoo" + File.separator + "video" + File.separator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        initWidget();

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("message");

        //  判断在线和本地视频,2为在线，1为本地
        if(bundle.getInt("status") == 2) {
            isOnlineVideo = true;

            int[] idArray = bundle.getIntArray("IdArray");
            String[] urlArray = bundle.getStringArray("UrlArray");
            String[] titleArray = bundle.getStringArray("TitleArray");
            String[] imgUrlArray = bundle.getStringArray("ImageArray");
            int[] timeStampArray = bundle.getIntArray("UpdateArray");
            String[] authorArray = bundle.getStringArray("AuthorArray");
            int[] typeArray = bundle.getIntArray("TypeArray");

            details = new ArrayList<FileDetail>();
            for (int i = 0; i < idArray.length; i++) {
                if(idArray[i] != -1) {
                    FileDetail temp = new FileDetail(idArray[i], urlArray[i], titleArray[i],
                                imgUrlArray[i], timeStampArray[i], authorArray[i], typeArray[i]);
                    if(i == 2) {
                        detail = temp;
                    }
                    details.add(temp);
                }
            }
            currentIndex = details.indexOf(detail);
            Log.d(TAG, currentIndex + "");
            mPlayList.setAdapter(new VideoAdapter(this, details));

            mVideoPath = Cryptor.decryptQiniuUrl(urlArray[2]);  //解密URL
            Log.d("videoUrl", mVideoPath);
            mDownloadPath = VIDEODIR + titleArray[2] + "_" + idArray[2];
            mTitle.setText(titleArray[2] + "-" + authorArray[2]);

            sendStatisticsInfo(idArray[2], typeArray[2]);
            checkLocalVideo();

        } else if(bundle.getInt("status") == 1){

            isDownFinished = true;
            int id = bundle.getInt("id");
            String video_title = bundle.getString("title");
            String videoAuthor = bundle.getString("author");
            int type = bundle.getInt("type");
            String houzhui="";
            if(type==1)houzhui=".mp4";
            else if(type==2)houzhui=".flv";
            else houzhui ="";
            //查询数据库
            mDBOperation = new DBOperation(this);
            mDBOperation.openDatabase();
            mCursor = mDBOperation.selectall();
            mPlayList.setAdapter(new LocalCursorAdapter(this, mCursor));
            mDBOperation.closeDatabase();
           /* String[] mediaColumns = new String[]{
                    MediaStore.Video.Media.TITLE,
                    MediaStore.Video.Media.ARTIST,
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.MIME_TYPE,
                    MediaStore.Video.Media.DATA};
            mCursor = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null,null);
             mVideoPath = videoAuthor;*/
            mPlayList.setAdapter(new LocalCursorAdapter(this, mCursor));
            mVideoPath = VIDEODIR + video_title + "_" + id + houzhui;
            mTitle.setText(video_title + "-" + videoAuthor);
        }



        mPlayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentIndex = position;
                if(isOnlineVideo) {
                    detail = details.get(position);
                    mVideoPath = Cryptor.decryptQiniuUrl(detail.getUrl());
                    mDownloadPath = VIDEODIR + detail.getTitle() + "_" + detail.getId();
                    mVideoView.setVideoPath(mVideoPath);
                    mVideoView.requestFocus();
                    mVideoView.start();
                    mTitle.setText(detail.getTitle() + "-" + detail.getAuthor());
                    mErrorImage.setVisibility(View.INVISIBLE);
                    mRetryButton.setVisibility(View.INVISIBLE);
                    sendStatisticsInfo(detail.getId(), detail.getType());
                    checkLocalVideo();
                } else {
                    //cursor
                    mCursor.moveToFirst();
                    mCursor.moveToPosition(position);
                    String title = mCursor.getString(mCursor.getColumnIndex("title"));
                    int video_id = mCursor.getInt(mCursor.getColumnIndex("id"));
                    String author = mCursor.getString(mCursor.getColumnIndex("author"));
                    int type = mCursor.getInt(mCursor.getColumnIndex("type"));
                    String houzhui="";
                    if(type==1)houzhui=".mp4";
                    else if(type==2)houzhui=".flv";
                    else houzhui ="";
                    mVideoPath = VIDEODIR + title + "_" + video_id + houzhui;
                    mVideoView.setVideoPath(mVideoPath);
                    mTitle.setText(title + "-" + author);
                    System.out.println("mVideoPath->"+mVideoPath);
                }
            }
        });
        initBroadcastReceiver();
        initVideoPlayer();
        mVideoView.start();
        Log.d("videolife", "onCreate");


    }

    @Override
    protected void onPause() {
        super.onPause();
        currentPos = mVideoView.getCurrentPosition();
        mVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.seekTo(currentPos);
        mVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(mVideoReceiver);
        //关闭依赖于该activity的popupwindow(即mediacontroller)
        if(mVideoMediaController.mWindow != null && mVideoMediaController.mWindow.isShowing()) {
            mVideoMediaController.mWindow.dismiss();
        }
        Log.d("videolife", "onDestroy");
        super.onDestroy();
    }

    private void initBroadcastReceiver() {
        mVideoReceiver = new VideoReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("full_screen");
        filter.addAction("download_file");
        filter.addAction("show_error");
        filter.addAction("share_video");
        this.registerReceiver(mVideoReceiver, filter);
    }


    private void initWidget() {
        mPlayList = (ListView)findViewById(R.id.play_list);
        mTitle = (TextView)findViewById(R.id.title);
        mCheckMap = new HashMap<String, AsyncTask<String, Void, Void>>();
        mErrorImage = (ImageView)findViewById(R.id.error_pattern);
        mRetryButton = (ImageButton)findViewById(R.id.video_error_retry);
        mListUpButton = (ImageButton)findViewById(R.id.list_up_button);
        mListDownButton = (ImageButton)findViewById(R.id.list_down_button);

        mListLayout = (RelativeLayout)findViewById(R.id.list_relativelayout);
        mVideoLayout = (RelativeLayout)findViewById(R.id.video_relativeLayout);

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

           mVideoView.setVideoPath(mVideoPath);
           mVideoView.requestFocus();
           mVideoView.start();
           if(isFullScreen) {
               mVideoMediaController.isFullscreen = isFullScreen;
           }
           clearPlayErrorMessage();

            }
        });

        mListUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayList.smoothScrollByOffset(-1);
                Log.d("upbutton", "bbb");

            }
        });

        mListDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayList.smoothScrollByOffset(1);

                Log.d("upbutton", "aaa");
            }
        });

    }


    private void initVideoPlayer() {
        mBufferingIndicator = findViewById(R.id.buffering_indicator);
        mVideoMediaController = new VideoMediaController(this, isOnlineVideo);
        mVideoMediaController.setExtraFunction(this);
        mVideoView = (VideoView) findViewById(R.id.video_view);
        mVideoView.setMediaController(mVideoMediaController);
        mVideoView.setMediaBufferingIndicator(mBufferingIndicator);
        mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE);
        mVideoView.setOnPlayNextListener(new PlayNextVideoListener());
        mVideoView.setVideoPath(mVideoPath);
        mVideoView.requestFocus();
    }


    /**
     * 查询数据库，是否已经缓存
     */
    public void checkLocalVideo() {

        mDBOperation = new DBOperation(this);
        mDBOperation.openDatabase();
        if(mDBOperation.select(detail.getId()).getCount() != 0 && new File(mDownloadPath).exists()) {
            isDownFinished = true;
        } else{
            isDownFinished = false;
        }
        mDBOperation.closeDatabase();
    }

    /**
     * 转换到全屏状态需要改变一系列UI
     */
    public void convertToFullscreen() {
        isFullScreen = true;
        int width = mVideoView.getWidth();
        int height = mVideoView.getHeight();

        mTitle.setVisibility(View.GONE);
        mListLayout.setVisibility(View.GONE);
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        if(getActionBar() != null) {
            getActionBar().hide();
        }

        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        flp.setMargins(0,0,0,0);
        mVideoLayout.setLayoutParams(flp);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        if(width * size.y < height * size.x) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }

        mVideoView.setLayoutParams(lp);

        Log.d("videosize", mVideoView.getWidth() + "," + mVideoView.getHeight());
    }

    /**
     * 从全屏状态退出，修改一系列UI布局
     */
    public void cancelFromFullscreen() {
        isFullScreen = false;
        int width = mVideoView.getWidth();
        int height = mVideoView.getHeight();

        if(getActionBar() != null) {
            getActionBar().show();
        }

        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        flp.setMargins(0, 0, 0, 100);
        mVideoLayout.setLayoutParams(flp);

//                    mPlayList.setVisibility(View.VISIBLE);
        mTitle.setVisibility(View.VISIBLE);
        mListLayout.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width,height);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mVideoView.setLayoutParams(lp);

        Log.d("videosize", mVideoView.getWidth() + "," + mVideoView.getHeight());

    }



    public void clearPlayErrorMessage() {
        ImageView errorImage = (ImageView)findViewById(R.id.error_pattern);
        if(errorImage.getVisibility()  == View.VISIBLE) {
            errorImage.setVisibility(View.INVISIBLE);
        }

        ImageButton retryBtn = (ImageButton)findViewById(R.id.video_error_retry);
        if(retryBtn.getVisibility()  == View.VISIBLE) {
            retryBtn.setVisibility(View.INVISIBLE);
        }
    }

    public void sendStatisticsInfo(int videoId, int videoType) {

        String macAddress = NetManager.getMacAddress(getApplicationContext());
        String hostIp = NetManager.getHostIp();

        StringBuffer sbUrl;
        if(videoType == 0) {
            sbUrl = new StringBuffer(StatisticsUtils.STATISTICS_TEACH_URL + videoId);
        } else {
            sbUrl = new StringBuffer(StatisticsUtils.STATISTICS_VIDEO_URL + videoId);
        }

        sbUrl.append("/m/" + macAddress);

        sbUrl.append("/h/" + hostIp + "/u/" + Utils.getMyUUID(getApplicationContext()));

        String requestUrl = sbUrl.toString();

        Log.d("statistics", requestUrl);

        try {
            Ion.with(this).load(requestUrl).asString();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void download() {
        Intent serviceIntent = new Intent(this, com.darfoo.download.DownloadService.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("FileInfo", detail);
        serviceIntent.putExtra(CommandUtils.COMMAND, CommandUtils.ADD_FILE);
        serviceIntent.putExtra("URL", detail.getUrl());
        serviceIntent.putExtra("Path", VIDEODIR);
        serviceIntent.putExtra("FileMessage", bundle);
        startService(serviceIntent);
    }

    @Override
    public void fullscreen() {

        if(isFullScreen) {
            cancelFromFullscreen();
        } else {
            convertToFullscreen();
        }
    }

    @Override
    public void shareVideo() {
        if(!isOnlineVideo) {
            return;
        }
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.sharedialog);
        // 关闭alert对话框架
        ImageButton cancel = (ImageButton) window.findViewById(R.id.share_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dlg.cancel();
            }
        });
        TextView share_title = (TextView)window.findViewById(R.id.share_text);
        share_title.setText(mTitle.getText());
        ImageView image2D = (ImageView)window.findViewById(R.id.share_2code);
        image2D.setImageBitmap(createQRImage(mVideoPath));

    }

    private class PlayNextVideoListener implements VideoView.OnPlayNextListener {
        @Override
        public void playNextVideo() {
            currentIndex += 1;
            if(isOnlineVideo) {
                currentIndex %= details.size();
                detail = details.get(currentIndex);
                mVideoPath = Cryptor.decryptQiniuUrl(details.get(currentIndex).getUrl());
                mVideoView.setVideoPath((mVideoPath));
                mVideoView.requestFocus();
                mVideoView.start();

                mTitle.setText(detail.getTitle() + "-" + detail.getAuthor());

            } else {
                currentIndex %= mCursor.getCount();
                mCursor.moveToPosition(currentIndex);
                String title = mCursor.getString(mCursor.getColumnIndex("title"));
                int video_id = mCursor.getInt(mCursor.getColumnIndex("id"));
                String author = mCursor.getString(mCursor.getColumnIndex("author"));
                mVideoPath = VIDEODIR + title + "_" + video_id;

                mVideoView.setVideoPath(mVideoPath);
                mTitle.setText(title + "-" + author);
            }
            Log.d(TAG, currentIndex + "");
            Toast.makeText(VideoPlayerActivity.this, "下一个视频", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showPlayErrorMessage() {
        ImageView errorImage = (ImageView)findViewById(R.id.error_pattern);
        if(errorImage.getVisibility()  == View.INVISIBLE) {
            errorImage.setVisibility(View.VISIBLE);
        }

        ImageButton retryBtn = (ImageButton)findViewById(R.id.video_error_retry);
        if(retryBtn.getVisibility()  == View.INVISIBLE) {
            retryBtn.setVisibility(View.VISIBLE);
        }
    }
    public Bitmap createQRImage(String url)
    {
        int QR_WIDTH = 160;
        int QR_HEIGHT = 160;
        Bitmap bitmap = null;
        try
        {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1)
            {
                bitmap = null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++)
            {
                for (int x = 0; x < QR_WIDTH; x++)
                {
                    if (bitMatrix.get(x, y))
                    {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    }
                    else
                    {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            //显示到一个ImageView上面

        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }



}

