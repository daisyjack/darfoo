package com.darfoo.darfoolauncher.support;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.darfoo.mvplayer.VideoPlayerActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import dafoo.video.VideoFromCategory;

/**
 * Created by jewelzqiu on 12/9/14.
 */
public class OnVideoClickListener implements AdapterView.OnItemClickListener {

    private Context mContext;

    // private VideoFromId mVideoFromId;
    public VideoFromCategory _VideoFromCategory;

    public int[] _IdArray = new int[5];

    public String[] _UrlArray = new String[5];

    public String[] _TitleArray = new String[5];

    public String[] _ImageArray = new String[5];

    public int[] _UpdateArray = new int[5];

    public int[] typeArray = new int[5];

    public Bundle _Bundle = new Bundle();
    public String[] _AuthorArray=new String[5];

    public OnVideoClickListener(Context context) {
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
                _VideoFromCategory = new Gson().fromJson(_JsonObject,
                        VideoFromCategory.class);
                Log.i("online", _VideoFromCategory.toString()
                        + "  the id is" + _VideoFromCategory.getId());

                _IdArray[i + 2] = _VideoFromCategory.getId();
                Log.i("OnlineVideo", "" + _IdArray[i + 2]);
                _UrlArray[i + 2] = _VideoFromCategory.getVideo_url();
                Log.i("OnlineMusic", _UrlArray[i + 2]);
                _TitleArray[i + 2] = _VideoFromCategory.getTitle();
                Log.i("OnlineMusic", _TitleArray[i + 2]);
                _ImageArray[i + 2] = _VideoFromCategory.getImage_url();
                Log.i("Online", _ImageArray[i + 2]);
                _UpdateArray[i + 2] = _VideoFromCategory
                        .getUpdate_timestamp();
                Log.i("Online", "" + _UpdateArray[i + 2]);
                _AuthorArray[i+2]=_VideoFromCategory.getAuthorname();
                Log.i("online11", "start activity  " + _VideoFromCategory.getAuthorname());
                typeArray[i + 2] = _VideoFromCategory.getType();
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
                typeArray[i + 2] = -1;
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
        _Bundle.putIntArray("TypeArray", typeArray);
        Intent _Intent = new Intent(mContext, VideoPlayerActivity.class);
        _Intent.putExtra("message", _Bundle);
        mContext.startActivity(_Intent);
    }

}
