package com.darfoo.download.widgets;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.support.Cryptor;
import com.darfoo.download.db.DownloadVideoDBOperation;
import com.darfoo.download.utils.CommandUtils;
import com.darfoo.download.utils.TaskUtils;
import com.koushikdutta.ion.Ion;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import dafoo.video.DBOperation;
import dafoo.video.Video;

/**
 * Created by YuXiaofei on 2014/11/24.
 */
public class DownloadVideoAdapter extends BaseAdapter {

    private Context mContext;
    private Cursor downloadCursor;
    private Cursor cursor;

    public DownloadVideoAdapter(Context context, Cursor downloadCursor, Cursor cursor) {
        super();
        mContext = context;
        this.downloadCursor = downloadCursor;
        this.cursor = cursor;

    }

    @Override
    public int getCount() {
        return downloadCursor.getCount() + cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.video_card_item, null);
        }

        ImageView cover = (ImageView)convertView.findViewById(R.id.video_cover);
        TextView title = (TextView)convertView.findViewById(R.id.video_title);
        ImageView statusImage = (ImageView)convertView.findViewById(R.id.video_download_status_button);
        ImageButton resumeBtn = (ImageButton)convertView.findViewById(R.id.resume_download_button);
//        cover.setImageResource(R.drawable.video_cover);

        String imageUrl;
        String videoTitle;
        if (position < downloadCursor.getCount()) {
            statusImage.setVisibility(View.VISIBLE);
            downloadCursor.moveToPosition(position);
            imageUrl = downloadCursor.getString(downloadCursor.getColumnIndex(Video.image_url));
            videoTitle = downloadCursor.getString(downloadCursor.getColumnIndex(Video.title));
            int downloadStatus = downloadCursor.getInt(downloadCursor.getColumnIndex(Video.taskFlag));
            Log.d("DownloadVideoAdapter", downloadStatus + "");
            switch (downloadStatus) {
                case TaskUtils.WAITING:
                case TaskUtils.DOWNLOADING:
                    resumeBtn.setVisibility(View.INVISIBLE);
                    statusImage.setVisibility(View.VISIBLE);
                    statusImage.setImageResource(R.drawable.bg_video_downloading);
                    break;
                case TaskUtils.FAILED:
                    resumeBtn.setVisibility(View.VISIBLE);
                    resumeBtn.setOnClickListener(new ResumeListener());
                    statusImage.setVisibility(View.VISIBLE);
                    statusImage.setImageResource(R.drawable.bg_video_download_fail);
                    this.notifyDataSetChanged();
                    break;
                default:
//                    statusImage.setImageResource(R.drawable.bg_video_downloading);
                    break;
            }

        } else {
            resumeBtn.setVisibility(View.INVISIBLE);
            statusImage.setVisibility(View.INVISIBLE);
            cursor.moveToPosition(position - downloadCursor.getCount());
            imageUrl = cursor.getString(cursor.getColumnIndex(Video.image_url));
            videoTitle = cursor.getString(cursor.getColumnIndex(Video.title));

        }
        if(imageUrl != null) {
            imageUrl = Cryptor.decryptQiniuUrl(imageUrl);
        }
        Ion.with(mContext)
                .load(imageUrl)
                .withBitmap()
                .placeholder(R.drawable.wait)
                .error(R.drawable.wait)
                .intoImageView(cover);

        title.setText(videoTitle);
        return convertView;

    }

    public void updateList() {
        DBOperation dBOperation = new DBOperation(mContext);
        DownloadVideoDBOperation downloadDBOperation = new DownloadVideoDBOperation(mContext);
        dBOperation.openDatabase();
        downloadDBOperation.openDatabase();


        cursor = dBOperation.selectall();
        cursor.moveToFirst();
        downloadCursor = downloadDBOperation.selectall();
        downloadCursor.moveToFirst();
        dBOperation.closeDatabase();
        downloadDBOperation.closeDatabase();

        this.notifyDataSetChanged();
    }

    private class ResumeListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent serviceIntent = new Intent(mContext, com.darfoo.download.DownloadService.class);
            serviceIntent.putExtra(CommandUtils.COMMAND, CommandUtils.RESUME_TASK);
            serviceIntent.putExtra("URL", downloadCursor.getString(downloadCursor.getColumnIndex(Video.url)));
            mContext.startService(serviceIntent);
        }
    }



}
