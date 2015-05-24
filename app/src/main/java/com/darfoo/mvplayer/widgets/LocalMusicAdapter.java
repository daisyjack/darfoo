package com.darfoo.mvplayer.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.darfoo.darfoolauncher.R;
import com.darfoo.mvplayer.MusicPlayerActivity;
import com.darfoo.mvplayer.db.Music;

import java.io.File;

import dafoo.music.MusicDBOp;

public class LocalMusicAdapter extends CursorAdapter {

    public boolean isEidtMode;
    public int mCurrentPos;

    public LocalMusicAdapter(Context context, Cursor c, boolean isEidtMode, int currentPos) {
        super(context, c);
        this.isEidtMode = isEidtMode;
        mCurrentPos = currentPos;
    }

    @Override
    public void bindView(View arg0, Context arg1, Cursor arg2) {

        Integer upId;
        Integer midId;
        Integer downId;

        if(arg2.getPosition() == mCurrentPos) {
            arg0.setBackgroundColor(Color.rgb(241, 241, 241));
        } else {
            arg0.setBackgroundColor(Color.rgb(255, 255, 255));
        }

        TextView pos = (TextView)arg0.findViewById(R.id.item_pos);
        TextView title = (TextView)arg0.findViewById(R.id.item_title);
        TextView author = (TextView)arg0.findViewById(R.id.item_artist);
        TextView duration = (TextView)arg0.findViewById(R.id.item_duration);
        ImageButton upBtn = (ImageButton)arg0.findViewById(R.id.music_up_button);
        ImageButton downBtn = (ImageButton)arg0.findViewById(R.id.music_down_button);
        ImageButton deleteBtn = (ImageButton)arg0.findViewById(R.id.music_delete_button);
        LinearLayout editLayout = (LinearLayout)arg0.findViewById(R.id.music_edit_panel);

        if(!isEidtMode) {
            editLayout.setVisibility(View.INVISIBLE);
        } else  {
            editLayout.setVisibility(View.VISIBLE);
        }

        pos.setText(arg2.getPosition() + 1 + "");
        title.setText(arg2.getString(arg2.getColumnIndex("title")));
        author.setText(arg2.getString(arg2.getColumnIndex("author")));

        long timeDuration = arg2.getLong(arg2.getColumnIndex(Music.stop_pos)) - arg2.getLong(arg2.getColumnIndex(Music.start_pos));
        duration.setText(MusicPlayerActivity.generateTime(timeDuration));

        int position = arg2.getPosition();

        midId = new Integer(arg2.getInt(arg2.getColumnIndex("id")));
        if (arg2.moveToPrevious()) {
            upId = new Integer(arg2.getInt(arg2.getColumnIndex("id")));
            arg2.moveToNext();
        } else {
            arg2.moveToNext();
            upId = null;
        }
        if (arg2.moveToNext()) {
            downId = Integer.valueOf(arg2.getInt(arg2.getColumnIndex("id")));
            arg2.moveToPrevious();
        } else {
            downId = null;
        }

        arg2.moveToFirst();
        arg2.moveToPosition(position);


        upBtn.setOnClickListener(new ExchangeOnclickListener(upId, midId));
        downBtn.setOnClickListener(new ExchangeOnclickListener(downId, midId));
        deleteBtn.setOnClickListener(new DeleteOnclickListener(arg2.getInt(arg2.getColumnIndex(Music.id))));

    }

    @Override
    public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {

        LayoutInflater _Inflater=LayoutInflater.from(arg0);
        return _Inflater.inflate(R.layout.music_item, arg2, false);

    }


    public class ExchangeOnclickListener implements View.OnClickListener {
        private Integer mId1;
        private Integer mId2;

        public ExchangeOnclickListener(Integer mId1, Integer mId2) {
            super();
            this.mId1 = mId1;
            this.mId2 = mId2;

        }

        @Override
        public void onClick(View v) {

            Log.d("testlistner", "clicked");

            int key1;
            int key2;
            Cursor cusor;
            if (mId1 != null) {

                MusicDBOp musicDBOp = new MusicDBOp(mContext);
                musicDBOp.openDatabase();

                cusor = musicDBOp.select(mId1.intValue());
                cusor.moveToFirst();
                key1 = cusor.getInt(cusor.getColumnIndex(Music.key));

                cusor = musicDBOp.select(mId2.intValue());
                cusor.moveToFirst();
                key2 = cusor.getInt(cusor.getColumnIndex(Music.key));


                musicDBOp.update(mId1.intValue(), key2);
                musicDBOp.update(mId2.intValue(), key1);
                changeCursor(musicDBOp.selectall());
            }

        }

    }

    public class DeleteOnclickListener implements View.OnClickListener {
        private int mId;

        public DeleteOnclickListener(int id) {
            mId = id;
        }

        @Override
        public void onClick(View v) {
            Log.i("rykdatabase", "click" + mId);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("是否要删除？");
            builder.setNegativeButton("是", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    StringBuffer sb = new StringBuffer(MusicPlayerActivity.MUSICDIR);
                    MusicDBOp musicDBOp = new MusicDBOp(mContext);
                    musicDBOp.openDatabase();
                    Cursor cursor = musicDBOp.select(mId);
                    if(cursor.moveToNext()) {
                        sb.append(cursor.getString(cursor.getColumnIndex(Music.title)));
                    }
                    sb.append("_" + mId + ".mp3");
                    File localFile = new File(sb.toString());
                    if(localFile.exists()) {
                        localFile.delete();
                    }
                    musicDBOp.delete(mId);
                    changeCursor(musicDBOp.selectall());
                }
            });
            builder.setPositiveButton("否", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();

        }
    }

}
