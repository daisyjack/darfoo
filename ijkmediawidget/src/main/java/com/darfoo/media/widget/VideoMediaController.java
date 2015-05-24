package com.darfoo.media.widget;

import tv.danmaku.ijk.media.widget.MediaController;
import tv.danmaku.ijk.media.widget.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;


public class VideoMediaController extends MediaController {
	private Context mContext;
	private MyMediaPlayerControl mPlayer;
	private MediaPlayerControlExtraFunction mExtraFunction;
	private ImageButton mDownloadButton;
	private ImageButton mFullscreenButton;
    private ImageButton mShareButton;
	private ControllerButtonListener mButtonListener;
	public boolean isFullscreen = false;
    public boolean isOnlineVideo = true;
	private View mMyRoot;

    private int mWindowWidth;

	public VideoMediaController(Context context) {
		super(context);
		mContext = context;
	}

    public VideoMediaController(Context context, boolean isOnlineVideo) {
        super(context);
        mContext = context;
        this.isOnlineVideo = isOnlineVideo;
    }


    @Override
    public void hide() {
        if(!isFullscreen) {

        } else {
            super.hide();
        }
    }

    @Override
	public void onFinishInflate() {
		super.onFinishInflate();
		if (mMyRoot != null)
            initDownloadButton(mMyRoot);
	}
	
	@Override
	public void setAnchorView(View view) {
		super.setAnchorView(view);
		initDownloadButton(mMyRoot);
        if(!isFullscreen) {
            mWindowWidth = mWindow.getWidth();
        }
	}

	/**
	 * 在MediaController上添加了下载功能
	 * @param v
	 */
	private void initDownloadButton(View v) {
		mDownloadButton = (ImageButton)v.findViewById(R.id.mediacontroller_download);
		mFullscreenButton = (ImageButton)v.findViewById(R.id.mediacontroller_fullscreen);
        mShareButton = (ImageButton)v.findViewById(R.id.mediacontroller_share);
		mButtonListener = new ControllerButtonListener();
		if(mDownloadButton != null) {
            if(!isOnlineVideo) {
                mDownloadButton.setEnabled(false);
                mDownloadButton.setImageResource(R.drawable.ic_media_download_disable);
            } else {
                mDownloadButton.requestFocus();
                mDownloadButton.setOnClickListener(mButtonListener);
            }
		}


		if(mFullscreenButton != null) {
			mFullscreenButton.requestFocus();
			mFullscreenButton.setOnClickListener(mButtonListener);
		}
        if(mShareButton != null) {
            if (!isOnlineVideo) {
                mShareButton.setImageResource(R.drawable.mediacontroller_share_disable);
            }
            mShareButton.requestFocus();
            mShareButton.setOnClickListener(mButtonListener);
		}
	}

	private class ControllerButtonListener implements OnClickListener {
		@Override
		public void onClick(View view) {
			int id = view.getId();
			if(id == R.id.mediacontroller_download) {
				mExtraFunction.download();
			} else if(id == R.id.mediacontroller_fullscreen) {

				if(!isFullscreen) {

                    mWindow.setWidth(LayoutParams.MATCH_PARENT);
					mFullscreenButton.setImageResource(R.drawable.mediacontroller_restorescreen_button);
					isFullscreen = true;
                    //宽度的改变将在下一次popupWindow显示时生效
                    hide();
                    show();


                } else {
                    mWindow.setWidth(mWindowWidth);
                    //宽度的改变将在下一次popupWindow显示时生效
                    hide();
                    show();
                    mFullscreenButton.setImageResource(R.drawable.mediacontroller_fullscreen_button);
					isFullscreen = false;

				}
                mExtraFunction.fullscreen();
			} else if(id == R.id.mediacontroller_share) {
                mExtraFunction.shareVideo();
            }
				
		}
		
	}

	
	@Override
	protected View makeControllerView() {
        if(!isFullscreen) {
            mMyRoot = ((LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.mymediacontroller, this);
        } else  {
            mMyRoot = ((LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.mymediacontroller_fullscreen, this);
        }

        return mMyRoot;

	}

	@Override
	public void setEnabled(boolean enabled) {
		
		if (mDownloadButton != null)
			mDownloadButton.setEnabled(enabled);
		super.setEnabled(enabled);
		
	}
	
	
	@Override
	public void setMediaPlayer(MediaPlayerControl player) {
		// TODO Auto-generated method stub
		super.setMediaPlayer(player);
		mPlayer = (MyMediaPlayerControl)player;
		
	}

	public interface MyMediaPlayerControl extends MediaPlayerControl {
		
    }

    public interface MediaPlayerControlExtraFunction {
        void download();

        void fullscreen();

        void shareVideo();

        void showPlayErrorMessage();
    }

    public void setExtraFunction(MediaPlayerControlExtraFunction extraFunction) {
        mExtraFunction = extraFunction;
    }

    public void showErrorInfo() {
        mExtraFunction.showPlayErrorMessage();
    }
}
