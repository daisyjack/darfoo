package com.darfoo.download.widgets;

import com.darfoo.darfoolauncher.R;
import com.darfoo.download.db.DownloadMusicDBOperation;
import com.darfoo.download.utils.CommandUtils;
import com.darfoo.download.utils.TaskUtils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import dafoo.music.Music;
import dafoo.music.MusicDBOp;

/**
 * Created by YuXiaofei on 2014/11/24.
 */
public class DownloadMusicAdapter extends BaseAdapter {

    private Context mContext;
    public Cursor downloadCursor;
    public Cursor cursor;

    public DownloadMusicAdapter(Context context, Cursor downloadCursor, Cursor cursor) {
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
            convertView = inflater.inflate(R.layout.music_resume_download_item, null);
        }


        TextView pos = (TextView)convertView.findViewById(R.id.item_pos);
        TextView title = (TextView)convertView.findViewById(R.id.item_title);
        TextView author = (TextView)convertView.findViewById(R.id.item_artist);
        TextView status = (TextView)convertView.findViewById(R.id.music_download_status);
        ImageView resumeBtn = (ImageView)convertView.findViewById(R.id.music_resume_download_button);

        String musicTitle;
        String musicAuthor;
        if (position < downloadCursor.getCount()) {
            downloadCursor.moveToPosition(position);
            musicAuthor = downloadCursor.getString(downloadCursor.getColumnIndex(Music.author));
            musicTitle = downloadCursor.getString(downloadCursor.getColumnIndex(Music.title));

            int downloadStatus = downloadCursor.getInt(downloadCursor.getColumnIndex(Music.taskFlag));
            switch (downloadStatus) {
                case TaskUtils.WAITING:
                case TaskUtils.DOWNLOADING:
                    resumeBtn.setVisibility(View.INVISIBLE);
                    status.setText("正在下载");
                    break;
                case TaskUtils.FAILED:
                    resumeBtn.setVisibility(View.VISIBLE);
                    resumeBtn.setOnClickListener(new ResumeListener());
                    status.setText("下载失败");
                    this.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        } else {
            status.setText("已下载");
            cursor.moveToPosition(position - downloadCursor.getCount());
            musicAuthor = cursor.getString(cursor.getColumnIndex(Music.author));
            musicTitle = cursor.getString(cursor.getColumnIndex(Music.title));

        }
        pos.setText(position + 1 + "");
        title.setText(musicTitle);
        author.setText(musicAuthor);
        return convertView;

    }


    public void updateList() {
        MusicDBOp dBOperation = new MusicDBOp(mContext);
        DownloadMusicDBOperation downloadDBOperation = new DownloadMusicDBOperation(mContext);
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
            serviceIntent.putExtra("URL", downloadCursor.getString(downloadCursor.getColumnIndex(Music.url)));
            mContext.startService(serviceIntent);
        }
    }

}
