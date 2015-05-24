package com.darfoo.mvplayer;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.activity.BaseFragmentActivity;
import com.darfoo.darfoolauncher.support.Cryptor;
import com.darfoo.darfoolauncher.support.NetManager;
import com.darfoo.darfoolauncher.support.Utils;
import com.darfoo.download.utils.CommandUtils;
import com.darfoo.mvplayer.db.Music;
import com.darfoo.mvplayer.utils.FileDetail;
import com.darfoo.mvplayer.utils.FileDownloader;
import com.darfoo.mvplayer.widgets.LocalMusicAdapter;
import com.darfoo.mvplayer.widgets.MusicAdapter;
import com.darfoo.mvplayer.utils.StatisticsUtils;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import dafoo.music.MusicDBOp;

/**
 *
 */
public class MusicPlayerActivity extends BaseFragmentActivity {

    private ImageButton mPlayButton;
    private ImageButton mPlaymodeButton;
    private ImageButton mDownloadButton;
    private ImageButton mEditButton;
    private ImageButton mManagerButton;
    private ImageButton mRetryButton;
    private ImageView mMusicCover;
    private TextView mFileName;
    private TextView mCurrentTime;
    private TextView mEndTime;
    private TextView mTitle;
    private TextView mAuthor;
    private TextView mPlayerStateBar;
    private TextView mDurationTag;
    private SeekBar mProgress;
    private ProgressBar mBufferingLogo;
    private ListView mPlayList;
    private EditDialogFragment mEditDialogFragment;
    private MediaPlayer mMediaPlayer;

    private LocalMusicAdapter mLocalMusicAdapter;
    private CompletionListener mCompletionListener;
    private MusicAdapter mOnlineMusicAdapter;
    private MusicDBOp mDBOperation;
    public HashMap<String, AsyncTask<String, Void, Void>> mCheckMap;
    private Handler handleProgress;
    private Runnable mShowTime;
    private String mMusicPath;
    private String mDownloadPath;
    private String mNextPath;
    private long mStartPos;
    private long mStopPos;
    private long mDuration;

    private List<FileDetail> details;
    private Cursor mCursor;
    private FileDetail detail;
    private Bundle mBundle;

    private boolean isFromMainAct = true;
    private boolean isOnlineMusic = false;
    private boolean isDraging = false;
    private boolean isErrShow = false;
    private boolean isEditMode = false;
    private int currentPos;
    private int currentId;
    private final static int ORDER_MODE = 0;
    private final static int SHUFFLE_MODE = 1;
    private final static int REPEAT_MODE = 2;
    private static int mPlaymode = ORDER_MODE;
    public static final String MUSICDIR = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "Darfoo" + File.separator + "music" + File.separator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        initWidget();

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("message");
        mBundle = bundle;
        //  判断在线和本地视频,1为本地,2为在线，3为直接从Main Activity跳转， -1为跳转但无歌曲
        if(bundle.getInt("status") == 2) {
            isOnlineMusic = true;
            int[] idArray = bundle.getIntArray("IdArray");
            String[] urlArray = bundle.getStringArray("UrlArray");
            String[] titleArray = bundle.getStringArray("TitleArray");
            String[] imgUrlArray = bundle.getStringArray("ImageArray");
            int[] timeStampArray = bundle.getIntArray("UpdateArray");
            String[] authorArray = bundle.getStringArray("AuthorArray");

            details = new ArrayList<FileDetail>();
            for (int i = 0; i < idArray.length; i++) {
                if(idArray[i] != -1) {
                    FileDetail temp = new FileDetail(idArray[i], urlArray[i], titleArray[i], imgUrlArray[i], timeStampArray[i], authorArray[i], -1);
                    if(i == 2) {
                        detail = temp;
                    }
                    details.add(temp);
                }
            }

            currentPos = details.indexOf(detail);    // 记录在播放列表当中的位置
            mOnlineMusicAdapter = new MusicAdapter(this, details, currentPos);
            mPlayList.setAdapter(mOnlineMusicAdapter);

            mMusicPath = Cryptor.decryptQiniuUrl(urlArray[2]);
            mDownloadPath = MUSICDIR + titleArray[2] + "_" + idArray[2];
            setMusicInfo(titleArray[2], imgUrlArray[2], authorArray[2]);
            mPlayerStateBar.setText("在线音乐");
            mEditButton.setImageResource(R.drawable.mediacontroller_edit_disable);
            mManagerButton.setVisibility(View.INVISIBLE);
            mDurationTag.setVisibility(View.GONE);

            sendStatisticsInfo(idArray[2]);

        } else if(bundle.getInt("status") == 1 ||bundle.getInt("status") == 3){

            int id = bundle.getInt("id");
            String title = bundle.getString("title");
            String author = bundle.getString("author");

            //查询数据库
            mDBOperation = new MusicDBOp(this);
            mDBOperation.openDatabase();
            mCursor = mDBOperation.selectall();

//            Cursor tempCursor = mDBOperation.select(id);
//            String imageUrl = tempCursor.getString(tempCursor.getColumnIndex(Music.image_url));
            List<String> titles = new ArrayList<String>();

            while(mCursor.moveToNext()) {
                String temp = mCursor.getString(mCursor.getColumnIndex("title"));
                Log.d("playmode", temp);
                titles.add(temp);
            }
            currentPos = titles.indexOf(title);
            currentId = id;

            mLocalMusicAdapter = new LocalMusicAdapter(this, mCursor, isEditMode, currentPos);
            mPlayList.setAdapter(mLocalMusicAdapter);
            mDownloadButton.setImageResource(R.drawable.ic_media_download_disable);

            Cursor cursor4Pos = mDBOperation.select(id);
            cursor4Pos.moveToFirst();
            mStartPos = cursor4Pos.getLong(cursor4Pos.getColumnIndex(Music.start_pos));
            mStopPos = cursor4Pos.getLong(cursor4Pos.getColumnIndex(Music.stop_pos));
            Log.d("getduration", mStopPos + "--" + mStartPos + "");

            mDBOperation.closeDatabase();

            mMusicPath = MUSICDIR + title + "_" + id + ".mp3";
            setMusicInfo(title, null, author);

        }


        setAllListener();

        //必须在设置listener之后进行判断，否则在由main activity进入我的音乐，然后点击管理按钮将会失效
        if(bundle.getInt("status") == -1) {
            mManagerButton.setVisibility(View.INVISIBLE);
            mPlayButton.setImageResource(R.drawable.mediacontroller_play_button);
            mPlayButton.setEnabled(false);
            mDownloadButton.setEnabled(false);
            mDownloadButton.setImageResource(R.drawable.ic_media_download_disable);
            mPlaymodeButton.setEnabled(false);
            mProgress.setEnabled(false);
            mEditButton.setEnabled(false);
            return;
        }



        handleProgress = new Handler();
        mShowTime = new Runnable() {
            @Override
            public void run() {
                int position = 0;
                int duration = 0;
                if(mMediaPlayer.isPlaying()) {
                    position = mMediaPlayer.getCurrentPosition();
                    duration = mMediaPlayer.getDuration();
                    if(position >= mStopPos && !isOnlineMusic) {
                        mCompletionListener.onCompletion(mMediaPlayer);
                    }
                }


                if (mProgress != null) {
                    if (duration > 0) {
                        long pos = 1000L * position / duration;
                        mProgress.setProgress((int) pos);
                        mCurrentTime.setText(generateTime(position));
                        Log.d("music now time", generateTime(position));
                    }

                    handleProgress.postDelayed(this, 200);

                }
            }
        };

        initMediaPlayer();

    }



    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mCompletionListener = new CompletionListener();
        try {
            mMediaPlayer.setDataSource(mMusicPath);
            mMediaPlayer.prepareAsync();
            mBufferingLogo.setVisibility(View.VISIBLE);
            mProgress.setEnabled(false);
            mPlayButton.setEnabled(false);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    handleProgress.postDelayed(mShowTime, 200);
                }
            }).start();


        } catch (Exception e) {
            showErrorMessage();
        }

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mProgress.setEnabled(true);
                mPlayButton.setEnabled(true);
                mPlayList.setEnabled(true);
                mBufferingLogo.setVisibility(View.INVISIBLE);

                setFileName(mMusicPath);
                if(mStopPos - mStartPos <= 0) {
                    mStartPos = 0;
                    mStopPos = mp.getDuration();
                }
                if(mBundle.getInt("status") != 3 || !isFromMainAct) {
                    mDuration = mp.getDuration();
                    mp.seekTo((int)mStartPos);
                    mp.start();

                } else {
                    mp.seekTo((int)mStartPos);
                    isFromMainAct = false;
                    mPlayButton.setImageResource(R.drawable.mediacontroller_play_button);
                    mMusicCover.setImageResource(R.drawable.ic_music_cover);
                }

            }
        });

        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                mProgress.setSecondaryProgress(percent * 10);
            }
        });

        mMediaPlayer.setOnCompletionListener(mCompletionListener);

        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mPlayList.setEnabled(true);
                mBufferingLogo.setVisibility(View.INVISIBLE);
                if (!isErrShow) {
                    showErrorMessage();
                }
                mp.reset();
                mCurrentTime.setText(generateTime(0));
                mProgress.setProgress(0);
                return true;
            }
        });
    }

    private void initWidget() {
        mCheckMap = new HashMap<String, AsyncTask<String, Void, Void>>();
        mPlayButton = (ImageButton)findViewById(R.id.mediacontroller_play_pause);
        mPlaymodeButton = (ImageButton)findViewById(R.id.mediacontroller_playmode);
        mDownloadButton = (ImageButton)findViewById(R.id.mediacontroller_download);
        mManagerButton = (ImageButton)findViewById(R.id.manager_btn);
        mEditButton = (ImageButton)findViewById(R.id.mediacontroller_edit);
        mRetryButton = (ImageButton)findViewById(R.id.music_error_retry);

        mMusicCover = (ImageView)findViewById(R.id.music_cover);
        mFileName = (TextView)findViewById(R.id.mediacontroller_file_name);
        mCurrentTime = (TextView)findViewById(R.id.mediacontroller_time_current);
        mEndTime = (TextView)findViewById(R.id.mediacontroller_time_total);
        mTitle = (TextView)findViewById(R.id.music_title);
        mAuthor = (TextView)findViewById(R.id.music_author);
        mPlayerStateBar = (TextView)findViewById(R.id.music_player_stateBar);
        mDurationTag = (TextView)findViewById(R.id.music_duration);
        mPlayList = (ListView)findViewById(R.id.music_list);
        mEditDialogFragment = new EditDialogFragment();
        mProgress = (SeekBar)findViewById(R.id.mediacontroller_seekbar);
        mBufferingLogo = (ProgressBar) findViewById(R.id.progressbar_music_buffering);
        mProgress.setMax(1000);
//        mPlayList.setEnabled(false);

    }

    private void setAllListener() {

        mManagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout editLayout = (LinearLayout)findViewById(R.id.detail_layout);
                LinearLayout panelLayout = (LinearLayout)findViewById(R.id.music_edit_panel);
                if(isEditMode) {
                    editLayout.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                    if(panelLayout != null) {
                        panelLayout.requestFocus();
                    }
                    editLayout.setFocusable(false);
                    mManagerButton.setImageResource(R.drawable.edit_button);
                    isEditMode = false;
                    mLocalMusicAdapter.isEidtMode = isEditMode;
                    mLocalMusicAdapter.notifyDataSetChanged();
                } else  {
//                    editLayout.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                    mManagerButton.setImageResource(R.drawable.save);
                    isEditMode = true;
                    if(mLocalMusicAdapter != null) {
                        mLocalMusicAdapter.isEidtMode = isEditMode;
                        mLocalMusicAdapter.notifyDataSetChanged();
                    }

                }

            }
        });


        mPlayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mMediaPlayer != null) {
                    if(!mMediaPlayer.isPlaying()) {
                        mPlayButton.setImageResource(R.drawable.mediacontroller_pause_button);
                        mMediaPlayer.start();
                    } else {
                        mPlayButton.setImageResource(R.drawable.mediacontroller_play_button);
                        mMediaPlayer.pause();
                    }
                }
            }
        });

        mPlaymodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPlaymode = (mPlaymode + 1) % 3;

                switch (mPlaymode) {
                    case 0:
                        mPlaymodeButton.setImageResource(R.drawable.ic_playmode_order);
                        break;
                    case 1:
                        mPlaymodeButton.setImageResource(R.drawable.ic_playmode_shuffle);
                        break;
                    case 2:
                        mPlaymodeButton.setImageResource(R.drawable.ic_playmode_repeat);
                        break;
                    default:
                        break;
                }
            }
        });

        mDownloadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!isOnlineMusic) {
                    return;
                }

                Intent serviceIntent = new Intent(MusicPlayerActivity.this, com.darfoo.download.DownloadService.class);

                Bundle bundle = new Bundle();
                detail.setDuration(mDuration);
                bundle.putSerializable("FileInfo", detail);
                serviceIntent.putExtra(CommandUtils.COMMAND, CommandUtils.ADD_FILE);
                serviceIntent.putExtra("URL", detail.getUrl());
                serviceIntent.putExtra("Path", MUSICDIR);
                serviceIntent.putExtra("FileMessage", bundle);
                startService(serviceIntent);

                /*mDBOperation = new MusicDBOp(MusicPlayerActivity.this);
                mDBOperation.openDatabase();
                int count = mDBOperation.select(detail.getId()).getCount();
                File file = new File(mDownloadPath + ".mp3");
                Log.d("music donwload", count + " " + detail.getId());
                Log.d("music donwload", mDownloadPath);
                //暂时使用url作为HashMap的Key
                if(count == 0 && !mCheckMap.containsKey(currentURL)) {
                    DownloadTask task = new DownloadTask(detail);
                    mCheckMap.put(currentURL, task);
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentURL, mDownloadPath + ".mp3", MUSICDIR);  // 可以5个文件同时下载
                } else if(count == 0 && file.exists() && mCheckMap.containsKey(currentURL)){
                    Toast.makeText(MusicPlayerActivity.this, detail.getTitle() + "正在下载..", Toast.LENGTH_SHORT).show();
                } else if(count != 0 && file.exists()) {
                    Toast.makeText(MusicPlayerActivity.this, detail.getTitle() + "已下载", Toast.LENGTH_SHORT).show();

                }

                mDBOperation.closeDatabase();*/
            }
        });

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isOnlineMusic) {
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString("edit_title", mTitle.getText().toString());
                bundle.putString("edit_url", mMusicPath);
                bundle.putInt("edit_id", currentId);
                bundle.putLong("edit_duration", mMediaPlayer.getDuration());
                bundle.putLong("edit_startPos", mStartPos);
                bundle.putLong("edit_stopPos", mStopPos);
                mEditDialogFragment.setArguments(bundle);
                if(mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                }
                mPlayButton.setImageResource(R.drawable.mediacontroller_play_button);
                mEditDialogFragment.show(getFragmentManager(), "EidtDialog");
                Toast.makeText(MusicPlayerActivity.this, "编辑音乐", Toast.LENGTH_SHORT).show();
            }
        });

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mMediaPlayer.setDataSource(mMusicPath);
                    mMediaPlayer.prepareAsync();
                    mBufferingLogo.setVisibility(View.VISIBLE);
                    mProgress.setEnabled(false);
                    mPlayButton.setEnabled(false);
                    clearPlayErrorMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        mPlayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(currentPos == position) {
                    return;
                }
                currentPos = position;
                mMediaPlayer.reset();
                mPlayButton.setImageResource(R.drawable.mediacontroller_pause_button);
                clearPlayErrorMessage();
                if(isOnlineMusic) {

                    //列表按下的效果
                    mOnlineMusicAdapter.mCurrentPos = position;
                    mOnlineMusicAdapter.notifyDataSetChanged();
                    try {
                        detail = details.get(position);
                        mMusicPath = Cryptor.decryptQiniuUrl(detail.getUrl());
                        Log.d("mPlaylist", mMusicPath);
                        mDownloadPath = MUSICDIR + detail.getTitle() + "_" + detail.getId();

                        mMediaPlayer.setDataSource(mMusicPath);
                        mMediaPlayer.prepareAsync();
                        mProgress.setEnabled(false);
                        mPlayButton.setEnabled(false);

                        setMusicInfo(detail.getTitle(), detail.getImageUrl(), detail.getAuthor());
                        sendStatisticsInfo(detail.getId());

                    } catch (Exception e) {
                        showErrorMessage();
                    }
                } else {

                    mLocalMusicAdapter.mCurrentPos = position;
                    mLocalMusicAdapter.notifyDataSetChanged();
                    mDBOperation.openDatabase();
                    mCursor = mDBOperation.selectall();
                    try {
                        mCursor.moveToFirst();
                        mCursor.moveToPosition(position);
                        Log.d("playmode", "list pos" + position + "---" + "cursor pos" + mCursor.getPosition());

                        String title = mCursor.getString(mCursor.getColumnIndex("title"));
                        int video_id = mCursor.getInt(mCursor.getColumnIndex("id"));
                        currentId = video_id;
                        mStartPos = mCursor.getLong(mCursor.getColumnIndex(Music.start_pos));
                        mStopPos = mCursor.getLong(mCursor.getColumnIndex(Music.stop_pos));
                        mMusicPath = MUSICDIR + title + "_" + video_id + ".mp3";
                        Log.d("playmode", "list path" + mMusicPath);
                        mMediaPlayer.setDataSource(mMusicPath);
                        mMediaPlayer.prepareAsync();
                        setMusicInfo(title, mCursor.getString(mCursor.getColumnIndex(Music.image_url)),
                                    mCursor.getString(mCursor.getColumnIndex(Music.author)));

                    } catch (IOException e) {
                        showErrorMessage();
                    }

                }

//                mPlayList.setEnabled(false);
                mBufferingLogo.setVisibility(View.VISIBLE);
            }
        });

        mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progress;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDraging = false;
                mMediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDraging = true;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isDraging) {
                    this.progress = progress * mMediaPlayer.getDuration() / seekBar.getMax();
                    mMediaPlayer.seekTo(progress);
                    mCurrentTime.setText(generateTime(this.progress));
                }
            }
        });


        mEditDialogFragment.setOnFinishEditMusic(new FinishEditListener());
    }



    @Override
    protected void onDestroy() {
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            handleProgress.removeCallbacks(mShowTime);
        }
        super.onDestroy();
    }
   /*@Override
   protected void onStop() {
       if (mMediaPlayer != null) {
           mMediaPlayer.release();
           handleProgress.removeCallbacks(mShowTime);
       }
       super.onStop();
   }*/

    /**
     * 设置控制条上的文件名
     * @param currentPath
     */
    private void setFileName(String currentPath) {
        List<String> paths = Uri.parse(currentPath).getPathSegments();
        mFileName.setText(paths.get(paths.size() - 1));
        mEndTime.setText(generateTime(mMediaPlayer.getDuration()));
    }

    /**
     * 将以毫秒为单位的时间，转换为标准的时间样式
     * @param position 总毫秒数
     * @return 标准时间样式
     */
    public static String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes,
                    seconds).toString();
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds)
                    .toString();
        }
    }

    /**
     * 音乐下载任务
     */
    class DownloadTask extends AsyncTask<String, Void, Void> {

        private FileDetail tempDetail;

        private boolean isDownloadFinish = false;

        private MusicDBOp taskMusicDBOp;

        DownloadTask(FileDetail detail) {
            tempDetail = detail;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(MusicPlayerActivity.this, tempDetail.getTitle() + "开始下载", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            FileDownloader loader = new FileDownloader(params[0], params[1], params[2]);

            if(loader.download() == 0){
                isDownloadFinish = true;

                taskMusicDBOp = new MusicDBOp(MusicPlayerActivity.this);
                taskMusicDBOp.openDatabase();
                if(tempDetail.getTitle() == null) {
                    tempDetail.setTitle("untitle");
                }

                taskMusicDBOp.insert(tempDetail.getId(), tempDetail.getImageUrl(),
                        tempDetail.getTitle(), tempDetail.getUpdateTimeStamp(), tempDetail.getAuthor(),
                        0, mDuration);

                taskMusicDBOp.closeDatabase();
                mCheckMap.remove(params[0]);
            } else {
                File file = new File(params[1]);
                if(file.exists()) {
                    file.delete();
                }
                mCheckMap.remove(params[0]);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if(isDownloadFinish) {
                Toast.makeText(MusicPlayerActivity.this, tempDetail.getTitle() + "下载完成", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MusicPlayerActivity.this, tempDetail.getTitle() + "下载失败", Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(result);
        }

    }


    /**
     * 播放失败时，统一显示错误信息
     */
    private void showErrorMessage() {
        ImageView errorImage = (ImageView)findViewById(R.id.music_error_pattern);
        if(errorImage.getVisibility()  == View.INVISIBLE) {
            errorImage.setVisibility(View.VISIBLE);
        }

        ImageButton retryBtn = (ImageButton)findViewById(R.id.music_error_retry);
        if(retryBtn.getVisibility()  == View.INVISIBLE) {
            retryBtn.setVisibility(View.VISIBLE);
        }

        isErrShow = true;

    }

    public void clearPlayErrorMessage() {
        ImageView errorImage = (ImageView)findViewById(R.id.music_error_pattern);
        if(errorImage.getVisibility()  == View.VISIBLE) {
            errorImage.setVisibility(View.INVISIBLE);
        }

        ImageButton retryBtn = (ImageButton)findViewById(R.id.music_error_retry);
        if(retryBtn.getVisibility()  == View.VISIBLE) {
            retryBtn.setVisibility(View.INVISIBLE);
        }

        isErrShow = false;

    }

    /**
     * 设置播放器的音乐信息，包括音乐封面，音乐标题以及艺术家
     * @param title 标题
     * @param imageUrl 音乐的播放URL
     * @param author 艺术家
     */
    private void setMusicInfo(String title, String imageUrl, String author) {

        if(title == null) {
            mTitle.setText("untitle");
        } else {
            mTitle.setText(title);
        }

        if(author == null) {
            mAuthor.setText("无艺名");
        } else {
            mAuthor.setText(author);
        }

        if(isOnlineMusic && imageUrl != null) {
            imageUrl = Cryptor.decryptQiniuUrl(imageUrl);
        }
        Ion.with(this)
                .load(imageUrl)
                .withBitmap()
                .placeholder(R.drawable.ic_music_cover)
                .error(R.drawable.ic_music_cover)
                .intoImageView(mMusicCover);
    }


    /**
     * 发送播放音乐的统计信息
     * @param musicId 音乐的ID号
     */
    public void sendStatisticsInfo(int musicId) {
        String macAddress = NetManager.getMacAddress(getApplicationContext());
        String hostIp = NetManager.getHostIp();

        StringBuffer sbUrl = new StringBuffer(StatisticsUtils.STATISTICS_MUSIC_URL + musicId);

        sbUrl.append("/m/" + macAddress);

        sbUrl.append("/h/" + hostIp + "/u/" + Utils.getMyUUID(getApplicationContext()));

        String requestUrl = sbUrl.toString();

        try {

            Ion.with(this).load(requestUrl).asString();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class FinishEditListener implements EditDialogFragment.OnFinishEditMusic {
        @Override
        public void updatePlaylist(long startPos, long stopPos) {
            mDBOperation = new MusicDBOp(MusicPlayerActivity.this);
            mDBOperation.openDatabase();
            mLocalMusicAdapter.changeCursor(mDBOperation.selectall());

            mStartPos = startPos;
            mStopPos = stopPos;
            mDBOperation.closeDatabase();

            mMediaPlayer.seekTo((int)startPos);
            mMediaPlayer.start();
            mPlayButton.setImageResource(R.drawable.mediacontroller_pause_button);
        }
    }

    private class CompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            //进入播放页面时，系统会调用2次onCompletion，将产生错误
//            if(mProgress.getProgress() > mProgress.getMax() * 0.5) {
            if(true) {
                Toast.makeText(MusicPlayerActivity.this, "下一首", Toast.LENGTH_SHORT).show();
                mPlayButton.setImageResource(R.drawable.mediacontroller_play_button);

                int nextPos = currentPos;
                View nextView;
                Log.d("playmode next", mPlaymode + "");
                mDBOperation = new MusicDBOp(MusicPlayerActivity.this);
                mDBOperation.openDatabase();
                mCursor = mDBOperation.selectall();
                switch (mPlaymode) {
                    case ORDER_MODE:
                        if(isOnlineMusic) {
                            nextPos = (currentPos + 1) < details.size() ? currentPos + 1 : 0 ;
                        } else {
                            nextPos = (currentPos + 1) < mCursor.getCount() ? currentPos + 1 : 0;
                        }
                        break;

                    case SHUFFLE_MODE:
                        if(isOnlineMusic) {
                            nextPos = (int)(Math.random() * details.size());

                        } else {
                            nextPos = (int)(Math.random() * mCursor.getCount());
                        }
                        break;

                    case REPEAT_MODE:
                        nextPos = currentPos;
                        break;
                    default:
                        break;

                }

                if(isOnlineMusic) {
                    FileDetail nextDetail = details.get(nextPos);
                    mNextPath = Cryptor.decryptQiniuUrl(nextDetail.getUrl());
                    setMusicInfo(nextDetail.getTitle(),
                            nextDetail.getImageUrl(),
                            nextDetail.getAuthor());

                    mOnlineMusicAdapter.mCurrentPos = nextPos;
                    mOnlineMusicAdapter.notifyDataSetChanged();

                    sendStatisticsInfo(nextDetail.getId());
                } else {
                    mCursor.moveToPosition(nextPos);
                    currentId = mCursor.getInt(mCursor.getColumnIndex("id"));
                    mNextPath = MUSICDIR + mCursor.getString(mCursor.getColumnIndex("title"))
                            + "_" + mCursor.getString(mCursor.getColumnIndex("id")) + ".mp3";
                    mStartPos = mCursor.getLong(mCursor.getColumnIndex(Music.start_pos));
                    mStopPos = mCursor.getLong(mCursor.getColumnIndex(Music.stop_pos));
                    setMusicInfo(mCursor.getString(mCursor.getColumnIndex("title")),
                            mCursor.getString(mCursor.getColumnIndex(Music.image_url)),
                            mCursor.getString(mCursor.getColumnIndex(Music.author)));

                    mLocalMusicAdapter.mCurrentPos = nextPos;
                    mLocalMusicAdapter.notifyDataSetChanged();

                }
                currentPos = nextPos;

                mDBOperation.closeDatabase();
                try {
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(mNextPath);
                    mMediaPlayer.prepareAsync();
//                    mPlayList.setEnabled(false);
                    mBufferingLogo.setVisibility(View.VISIBLE);
                    mProgress.setEnabled(false);
                    mPlayButton.setEnabled(false);
                    mMusicPath = mNextPath;
                    mPlayButton.setImageResource(R.drawable.mediacontroller_pause_button);

                } catch (Exception e) {
                    showErrorMessage();
                }


            }

        }
    }


}