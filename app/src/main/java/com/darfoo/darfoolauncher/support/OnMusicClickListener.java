package com.darfoo.darfoolauncher.support;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.darfoo.mvplayer.MusicPlayerActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import dafoo.music.MusicFromCategory;

/**
 * Created by jewelzqiu on 12/9/14.
 */
public class OnMusicClickListener implements AdapterView.OnItemClickListener {


    private Context mContext;

    public MusicFromCategory _MusicFromCategory;

    public int[] _IdArray = new int[5];

    public String[] _UrlArray = new String[5];

    public String[] _TitleArray = new String[5];

    public String[] _ImageArray = new String[5];

    public int[] _UpdateArray = new int[5];

    public Bundle _Bundle = new Bundle();
    public String[] _AuthorArray=new String[5];

    public OnMusicClickListener(Context context) {
        mContext = context;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        // TODO Auto-generated method stub

        JsonObject _JsonObject;

        for (int i = -2; i <= 2; i++) {
            Log.i("online", "i=" + i);
            int _position = position + i;

            if (_position >= 0 && _position < parent.getCount()) {
                _JsonObject = (JsonObject) parent
                        .getItemAtPosition(_position);
                _MusicFromCategory = new Gson().fromJson(_JsonObject,
                        MusicFromCategory.class);
                Log.i("online", _MusicFromCategory.toString()
                        + "  the id is" + _MusicFromCategory.getId());

                _IdArray[i + 2] = _MusicFromCategory.getId();
                Log.i("OnlineVideo", "" + _IdArray[i + 2]);
                _UrlArray[i + 2] = _MusicFromCategory.getMusic_url();
                Log.i("OnlineMusic", _UrlArray[i + 2]);
                _TitleArray[i + 2] = _MusicFromCategory.getTitle();
                Log.i("OnlineMusic", _TitleArray[i + 2]);
                _ImageArray[i + 2] = _MusicFromCategory.getImage_url();
//                Log.i("Online", _ImageArray[i + 2]);
                _UpdateArray[i + 2] = _MusicFromCategory
                        .getUpdate_timestamp();
                Log.i("Online", "" + _UpdateArray[i + 2]);
                _AuthorArray[i+2]=_MusicFromCategory.getAuthorname();
            } else {
                _IdArray[i + 2] = -1;
                Log.i("OnlineVideo", "" + _IdArray[i + 2]);
                _UrlArray[i + 2] = null;
                Log.i("OnlineMusic", "null");
                _TitleArray[i + 2] = null;
                Log.i("OnlineMusic", "null");
                _ImageArray[i + 2] = null;
                Log.i("OnlineMusic", "null");
                _UpdateArray[i + 2] = -1;
                Log.i("OnlineMusic", "" + _UpdateArray[i + 2]);
                _AuthorArray[i+2]=null;
            }
        }
        Log.i("online", "start activity  " + _IdArray[2]);
        _Bundle.putInt("status", 2);
        _Bundle.putIntArray("IdArray", _IdArray);
        _Bundle.putStringArray("UrlArray", _UrlArray);
        _Bundle.putStringArray("TitleArray", _TitleArray);
        _Bundle.putStringArray("ImageArray", _ImageArray);
        _Bundle.putIntArray("UpdateArray", _IdArray);
        _Bundle.putStringArray("AuthorArray",_AuthorArray);
        Intent _Intent = new Intent(mContext, MusicPlayerActivity.class);
        _Intent.putExtra("message", _Bundle);
        mContext.startActivity(_Intent);
    }
}
