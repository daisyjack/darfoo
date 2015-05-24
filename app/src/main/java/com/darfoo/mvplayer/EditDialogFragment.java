package com.darfoo.mvplayer;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.darfoo.darfoolauncher.R;
import com.darfoo.mvplayer.widgets.RangeSeekBar;

import dafoo.music.MusicDBOp;


public class EditDialogFragment extends DialogFragment {

    private String mMusicPath;
    private String mTitle;
    private int mMusicId;
    private long duration;
    private long startPos;
    private long stopPos;
    private long currentPos;
    private TextView mDurationText;
    private Handler handler;
    private Runnable countRunnable;
    private MediaPlayer mMediaPlayer;
    private OnFinishEditMusic mOnFinishEditMusic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        mTitle = bundle.getString("edit_title");
        mMusicPath = bundle.getString("edit_url");
        mMusicId = bundle.getInt("edit_id");
        duration = bundle.getLong("edit_duration");
        startPos = bundle.getLong("edit_startPos");
        stopPos = bundle.getLong("edit_stopPos");

        if(stopPos - startPos <= 0) {
            startPos = 0;
            stopPos = duration;
        }
        currentPos = startPos;
        handler = new Handler();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_dialog, container, false);
        RelativeLayout rl = (RelativeLayout)v.findViewById(R.id.seekbar_layout);

        final RangeSeekBar<Long> editSeekBar = new RangeSeekBar<Long>(getActivity());
        editSeekBar.setRangeValues(0L, duration);
        editSeekBar.setSelectedMaxValue(stopPos);
        editSeekBar.setSelectedMinValue(startPos);
        editSeekBar.setNotifyWhileDragging(true);
        editSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Long>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Long minValue, Long maxValue) {
                startPos = minValue;
                stopPos = maxValue;
                Log.d("confirm time max", stopPos + "");
                Log.d("confirm time min", startPos + "");
                mDurationText.setText("截取时长：" + MusicPlayerActivity.generateTime(maxValue - minValue));
                currentPos = startPos;
                if(!editSeekBar.getIsDragging()) {
                    if(mMediaPlayer.isPlaying()) {
                        mMediaPlayer.seekTo((int)startPos);
                        mMediaPlayer.start();

                    }

                }

            }
        });

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        editSeekBar.setLayoutParams(layoutParams);
        rl.addView(editSeekBar);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        Log.d("confirm time max", startPos + "");
        Log.d("confirm time min", stopPos + "");
        Log.d("confirm time min", duration + "");
        final ImageButton confirmButton = (ImageButton)v.findViewById(R.id.edit_confirm);
        final ImageButton playButton = (ImageButton)v.findViewById(R.id.edit_play_button);
        final ImageButton closeButton = (ImageButton)v.findViewById(R.id.music_edit_close_button);
        mDurationText = (TextView)v.findViewById(R.id.edit_duration_text);
        mDurationText.setText("截取时长：" + MusicPlayerActivity.generateTime(stopPos - startPos));

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    handler.removeCallbacks(countRunnable);
                    playButton.setImageResource(R.drawable.mediacontroller_play_button);
                } else {
                    mMediaPlayer.seekTo((int)startPos);
                    mMediaPlayer.start();
                    currentPos = startPos;
                    handler.postDelayed(countRunnable, 500);
                    playButton.setImageResource(R.drawable.mediacontroller_pause_button);

                }
            }
        });
        TextView title = (TextView)v.findViewById(R.id.edit_title);
        title.setText(mTitle);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //如果编辑时，总截取的长度为0，将无法播放该音乐，因此强制设为原时长
                if(stopPos - startPos <= 0) {
                    startPos = 0;
                    stopPos = duration;
                }
                MusicDBOp mDBOperation = new MusicDBOp(EditDialogFragment.this.getActivity());
                mDBOperation.openDatabase();
                mDBOperation.updateDuration(mMusicId, startPos, stopPos);
                mDBOperation.closeDatabase();
                mOnFinishEditMusic.updatePlaylist(startPos, stopPos);
                EditDialogFragment.this.dismiss();
            }
        });

        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mMusicPath);
            mMediaPlayer.prepareAsync();
            playButton.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playButton.setEnabled(true);
                mMediaPlayer.seekTo((int)startPos);
            }
        });

        countRunnable = new Runnable() {
            @Override
            public void run() {
                if(mMediaPlayer.isPlaying()) {
                    currentPos = currentPos + 500;
                    Log.d("time now", MusicPlayerActivity.generateTime(currentPos));
                    if(currentPos >= stopPos) {
                        mMediaPlayer.pause();
                        playButton.setImageResource(R.drawable.mediacontroller_play_button);
                    }

                    handler.postDelayed(this, 500);
                }

            }
        };
        return v;
    }


    public interface OnFinishEditMusic {
        void updatePlaylist(long startPos, long stopPos);
    }

    public void setOnFinishEditMusic(OnFinishEditMusic onFinishEditMusic) {
        mOnFinishEditMusic = onFinishEditMusic;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.d("edit fragment", "dismiss");
        mMediaPlayer.reset();
        super.onDismiss(dialog);
    }

    @Override
    public void onResume() {
        getDialog().getWindow().setLayout(750, 400);
        super.onResume();
    }


}
