package dafoo.music;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.activity.BaseFragmentActivity;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class OnlineMusic extends BaseFragmentActivity {

    private String mUrl;
    private JsonArray mJsonArray;
    private GridView mGridView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_online_video);
        mTextView=(TextView)findViewById(R.id.online_textView);
        mTextView.setText("网上音乐");
	/*	mUrl="baseurl/resources/video/recommend";
		Ion.with(this).load(mUrl).asJsonArray().setCallback(new FutureCallback<JsonArray>() {
			
			@Override
			public void onCompleted(Exception e, JsonArray result) {
				// TODO Auto-generated method stub
				OnlineMusic.this.mJsonArray=result;
			}
		});*/

        String str ="[{'id':3,'image_url':'http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg','title':'musictest','update_timestamp':140908}]";
        JsonElement _Element=new Gson().fromJson(str, JsonElement.class);
        mJsonArray=_Element.getAsJsonArray();

        Log.i("rykdatabase", "1");
        OnlineMusicAdapter _OnlineMusicAdapter=new OnlineMusicAdapter(this, mJsonArray,1);
        mGridView=(GridView)findViewById(R.id.online_video_girdview);
        mGridView.setAdapter(_OnlineMusicAdapter);
        mGridView.setOnItemClickListener(new ItemClick());
    }

    public class ItemClick implements AdapterView.OnItemClickListener
    {

        MusicFromId mMusicFromId;
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            // TODO Auto-generated method stub
            String _Url="baseurl/resources/music/";
            JsonObject _JsonObject;
            MusicFromCategory _MusicFromCategory;
            int[] _IdArray=new int[5];
            String[] _UrlArray=new String[5];
            String[] _TitleArray=new String[5];
            String[] _ImageArray=new String[5];
            int [] _UpdateArray=new int[5];
            Bundle _Bundle = new Bundle();
			/*_JsonObject = (JsonObject) parent
					.getItemAtPosition(position);
			_VideoFromCategory = new Gson().fromJson(
					_JsonObject, MusicFromCategory.class);
			_Url = _Url + String.valueOf(_MusicFromCategory.getId());
			Log.i("ryk", _Url);
			Ion.with(OnlineMusic.this).load(_Url).asJsonObject()
					.setCallback(new FutureCallback<JsonObject>() {
						@Override
						public void onCompleted(Exception e, JsonObject result) {
							// do stuff with the result or error
							mMusicFromId = new Gson().fromJson(result,
									VideoFromId.class);
						}
					});
			Intent _Intent = new Intent(OnlineVideo.this, LocalVideo.class);
			_Bundle = new Bundle();
			_Bundle.putInt("status", 2);
			_Bundle.putInt("id", _VideoFromCategory.getId());
			_Bundle.putString("video_url", _VideoFromId.getVideo_url());
			_Bundle.putString("title", _VideoFromCategory.getTitle());
			_Intent.putExtra("message", _Bundle);
			startActivity(_Intent);*/

            for (int i=-2;i<=2;i++)
            {
                int _position=position+i;

                if (_position>=0&&_position<parent.getCount()) {
                    _JsonObject = (JsonObject) parent
                            .getItemAtPosition(_position);
                    _MusicFromCategory = new Gson().fromJson(_JsonObject,
                            MusicFromCategory.class);
                    _Url = _Url + String.valueOf(_MusicFromCategory.getId());
                    Ion.with(OnlineMusic.this).load(_Url).asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    // do stuff with the result or error
                                    mMusicFromId = new Gson().fromJson(result,
                                            MusicFromId.class);
                                }
                            });
                    _IdArray[i+2]=mMusicFromId.getId();
                    _UrlArray[i+2]=mMusicFromId.getMusic_url();
                    _TitleArray[i+2]=mMusicFromId.getTitle();
                    _ImageArray[i+2]=_MusicFromCategory.getImage_url();
                    _UpdateArray[i+2]=_MusicFromCategory.getUpdate_timestamp();
                }
                else {
                    _IdArray[i+2]=-1;
                    _UrlArray[i+2]=null;
                    _TitleArray[i+2]=null;
                    _ImageArray[i+2]=null;
                    _UpdateArray[i+2]=-1;
                }
            }
            _Bundle.putInt("status", 2);
            _Bundle.putIntArray("IdArray", _IdArray);
            _Bundle.putStringArray("UrlArray", _UrlArray);
            _Bundle.putStringArray("TitleArray", _TitleArray);
            _Bundle.putStringArray("ImageArray", _ImageArray);
            _Bundle.putIntArray("UpdateArray", _IdArray);
            Intent _Intent = new Intent(OnlineMusic.this, LocalMusic.class);
            _Intent.putExtra("message", _Bundle);
            startActivity(_Intent);
        }

    }
}
