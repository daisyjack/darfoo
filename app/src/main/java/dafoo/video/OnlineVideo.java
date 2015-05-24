package dafoo.video;

import org.json.JSONArray;

import com.darfoo.mvplayer.VideoPlayerActivity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.koushikdutta.ion.Ion;
import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.activity.BaseFragmentActivity;

import dafoo.video.LocalVideo.ItemClick;

public class OnlineVideo extends BaseFragmentActivity {
	private String mUrl;
	private JsonArray mJsonArray;
	private GridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_online_video);
		  mUrl="baseurl/resources/video/recommend";
		  Ion.with(this).load(mUrl).asJsonArray().setCallback(new
		  FutureCallback<JsonArray>() {

		  @Override public void onCompleted(Exception e, JsonArray result) { //
		  //TODO Auto-generated method stub
              OnlineVideo.this.mJsonArray=result;

              Log.i("rykdatabase", "1");
              OnlineJsonAdapter _OnlineJsonAdapter = new OnlineJsonAdapter(OnlineVideo.this,
                      mJsonArray);
              mGridView = (GridView) findViewById(R.id.online_video_girdview);
              Log.i("rykdatabase", "2");
              mGridView.setAdapter(_OnlineJsonAdapter);
              Log.i("rykdatabase", "3");
              mGridView.setOnItemClickListener(new ItemClick());

          }
		  });

		/*String str = "[{'id':1,'image_url':'http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg','author_name':'jack','title':'videoone','update_timestamp':140908},"
				+ "{'id':2,'image_url':'http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg','author_name':'jack','title':'videotwo','update_timestamp':140908},"
				+ "{'id':3,'image_url':'http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg','author_name':'jack','title':'videothree','update_timestamp':140908}]";
		JsonElement _Element=new Gson().fromJson(str, JsonElement.class);
		mJsonArray=_Element.getAsJsonArray();*/

	}

	public class ItemClick implements AdapterView.OnItemClickListener {

		private VideoFromId mVideoFromId;

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			String _Url = "baseurl/resources/video/";
			JsonObject _JsonObject;
			VideoFromCategory _VideoFromCategory;
			int[] _IdArray=new int[5];
			String[] _UrlArray=new String[5];
			String[] _TitleArray=new String[5];
			String[] _ImageArray=new String[5];
			int [] _UpdateArray=new int[5];
			Bundle _Bundle = new Bundle();
			/*_JsonObject = (JsonObject) parent
					.getItemAtPosition(position);
			_VideoFromCategory = new Gson().fromJson(
					_JsonObject, VideoFromCategory.class);
			_Url = _Url + String.valueOf(_VideoFromCategory.getId());
			Log.i("ryk", _Url);
			Ion.with(OnlineVideo.this).load(_Url).asJsonObject()
					.setCallback(new FutureCallback<JsonObject>() {
						@Override
						public void onCompleted(Exception e, JsonObject result) {
							// do stuff with the result or error
							mVideoFromId = new Gson().fromJson(result,
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
			Log.i("OnlineVideo", "positon clicked"+position+"all"+parent.getCount());
			for (int i=-2;i<=2;i++)
			{
				int _position=position+i;
				
				if (_position>=0&&_position<parent.getCount()) {
					_JsonObject = (JsonObject) parent
							.getItemAtPosition(_position);
					_VideoFromCategory = new Gson().fromJson(_JsonObject,
							VideoFromCategory.class);
					_Url = _Url + String.valueOf(_VideoFromCategory.getId());
					Ion.with(OnlineVideo.this).load(_Url).asJsonObject()
					.setCallback(new FutureCallback<JsonObject>() {
						@Override
						public void onCompleted(Exception e, JsonObject result) {
							// do stuff with the result or error
							mVideoFromId = new Gson().fromJson(result,
									VideoFromId.class);
						}
						});
					//_IdArray[i+2]=mVideoFromId.getId();
					_IdArray[i+2]=_VideoFromCategory.getId();
					Log.i("OnlineVideo", ""+_IdArray[i+2]);
					//_UrlArray[i+2]=mVideoFromId.getVideo_url();
					//Log.i("OnlineMusic",_UrlArray[i+2]);
					//_TitleArray[i+2]=mVideoFromId.getTitle();
					//Log.i("OnlineMusic",_TitleArray[i+2]);
					_ImageArray[i+2]=_VideoFromCategory.getImage_url();
					_UpdateArray[i+2]=_VideoFromCategory.getUpdate_timestamp();
				}
				else {
					_IdArray[i+2]=-1;
					Log.i("OnlineVideo", ""+_IdArray[i+2]);
					//_UrlArray[i+2]=null;
					//Log.i("OnlineMusic",_UrlArray[i+2]);
					//_TitleArray[i+2]=null;
					//Log.i("OnlineMusic",_TitleArray[i+2]);
					_ImageArray[i+2]=null;
					_UpdateArray[i+2]=-1;
				}
			}
			_Bundle.putInt("status", 2);
			_Bundle.putIntArray("IdArray", _IdArray);
			_Bundle.putStringArray("UrlArray", _UrlArray);
			_Bundle.putStringArray("TitleArray", _TitleArray);
			Intent _Intent = new Intent(OnlineVideo.this, VideoPlayerActivity.class);
			_Bundle.putStringArray("ImageArray", _ImageArray);
			_Bundle.putIntArray("UpdateArray", _IdArray);
			_Intent.putExtra("message", _Bundle);
			startActivity(_Intent);
		}

	}

}
