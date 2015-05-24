package com.darfoo.darfoolauncher.activity;

import android.app.AlertDialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.darfoo.darfoolauncher.R;
import com.darfoo.mvplayer.VideoPlayerActivity;
import com.darfoo.mvplayer.utils.FileDetail;
import com.darfoo.mvplayer.utils.FileDownloader;

import android.database.Cursor;
import android.content.ContentResolver;
import android.provider.MediaStore;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import dafoo.music.MusicDBOp;
import dafoo.video.DBOperation;

/**
 * Created by william-wang on 2015/1/4.
 */
public class SdManagerActivity extends BaseFragmentActivity{

    private List<Map<String, Object>> mData;
    private ListView sdlist;
    private ImageButton sdbutton;


    int i;
    ContentResolver contentResolver;
    MyAdapter sdadapter;
    ArrayList<String> listStr = null;
    Timer timer = new Timer();
    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    mData = getData();
                    showCheckBoxListView();
                    break;
                case 3:
                    mData = getData();
                    showCheckBoxListView();
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "扫描完毕，请选择要复制的文件", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
            }
            super.handleMessage(msg);
        }

    };

    TimerTask task = new TimerTask(){
        public void run() {
            Message message = new Message();
            i++;
            message.what = i;
            handler.sendMessage(message);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sd);
        sdbutton = (ImageButton)findViewById(R.id.sd_button);
        sdlist = (ListView)findViewById(R.id.sd_media_list);
        Toast toast = Toast.makeText(getApplicationContext(),
                "正在扫描中，请稍等……", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        /*SystemClock.sleep(5000);*/
        i = 0;
        timer.schedule(task, 500,3000);
       /* mData = getData();
        showCheckBoxListView();*/
        sdbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mediaList = "";
                for(int i = 0; i <sdadapter.isSelected.size(); i++) {
                    if (sdadapter.isSelected.get(i)) {
                       mediaList += mData.get(i).get("Name");
                   /*  System.out.println(mData.get(i).get("Name"));*/
                    }
                }
                if(mediaList.length()==0) Toast.makeText(SdManagerActivity.this,"没有要导入的文件！！！",Toast.LENGTH_SHORT).show();
                else{SdImportDialog();}
            }
        });
    }
    private void SdImportDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.dialog);
        // 为确认按钮添加事件,执行退出应用操作
        ImageButton ok = (ImageButton) window.findViewById(R.id.positiveButton);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for (int i = 0; i < mData.size(); i++) {
                    if (sdadapter.isSelected.get(i)){
                        SDCopyTask task = new SDCopyTask((String)mData.get(i).get("Id"),(String) mData.get(i).get("Name"),(String) mData.get(i).get("Artist"),(int) mData.get(i).get("flag"),(String)mData.get(i).get("Duration"));
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String) mData.get(i).get("Path"),  mData.get(i).get("flag").toString(), (String) mData.get(i).get("Name"),(String)mData.get(i).get("Id"));
                        /*sdcopy((String) mData.get(i).get("Path"), (int) mData.get(i).get("flag"), (String) mData.get(i).get("Name"));*/
                    }
                }
                /*SDCopyTask task = new SDCopyTask(((VideoPlayerActivity) context).detail);*/
                dlg.cancel();
            }
        });
        // 关闭alert对话框架
        ImageButton cancel = (ImageButton) window.findViewById(R.id.negativeButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dlg.cancel();
            }
        });
    }
    private List<Map<String, Object>> getData() {
        /*try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        contentResolver = this.getContentResolver();
        String  SdPath = File.separator + "storage" + File.separator + "sdcard1";
        String[] projection = new String[]{
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION};
        String[] mediaColumns = new String[]{
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.ARTIST,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DATA};
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        Cursor media_cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null,null);
        //cursor.moveToFirst();
        List<Map<String, Object>>  medialist = new ArrayList<Map<String, Object>>();
        while (cursor.moveToNext()) {
            String sArtist;
            String sid = cursor.getString(2);//Id
            String sName = cursor.getString(0);//歌曲名
            if(cursor.getString(1)!=null)    sArtist = cursor.getString(1);//歌手名
            else sArtist = "无名称";
            String sPath = cursor.getString(4);//路径
            String sduration = cursor.getString(5);//时长
            System.out.println(sduration);
            Map<String, Object> map = new HashMap<String, Object>();
            if (sPath.substring(sPath.lastIndexOf(".")).equalsIgnoreCase(".mp3") && sPath.substring(0,SdPath.length()).equalsIgnoreCase(SdPath) ) {
                map.put("Id",sid);
                map.put("Name", sName);
                map.put("Artist", sArtist);
                map.put("Path", sPath);
                map.put("flag",0);
                map.put("Duration",sduration);
                medialist.add(map);
            }
        }
        cursor.close();

        //media_cursor.moveToFirst();
        while (media_cursor.moveToNext()) {
            String sArtist;
            String sid = media_cursor.getString(2);//Id
            String sName = media_cursor.getString(0);//歌曲名
            if(media_cursor.getString(1)!=null)    sArtist = media_cursor.getString(1);//歌手名
            else sArtist = "无名称";
            String sPath = media_cursor.getString(4);//路径
            Map<String, Object> map = new HashMap<String, Object>();
            System.out.println(sPath);
            if (sPath.substring(sPath.lastIndexOf(".")).equalsIgnoreCase(".mp4")&& sPath.substring(0,SdPath.length()).equalsIgnoreCase(SdPath)) {
                map.put("Id",sid);
                map.put("Name", sName);
                map.put("Artist", sArtist);
                map.put("Path", sPath);
                map.put("flag",1);
                map.put("Duration",10086+"");
                medialist.add(map);
            }
            else if(sPath.substring(sPath.lastIndexOf(".")).equalsIgnoreCase(".flv")&& sPath.substring(0,SdPath.length()).equalsIgnoreCase(SdPath)) {
                map.put("Id",sid);
                map.put("Name", sName);
                map.put("Artist", sArtist);
                map.put("Path", sPath);
                map.put("flag",2);
                map.put("Duration",10086+"");
                medialist.add(map);
            }
        }
        media_cursor.close();
        return medialist;
    }
    class SDCopyTask extends AsyncTask<String, Void, Void> {

        private String mtitle;
        private int mId;
        private String mAuthor;
        private int mflag;
        private String mduration;

        SDCopyTask(String Id,String title,String Author,int flag,String duration) {
            mtitle = title;
            mId = Integer.parseInt(Id);
            mAuthor = Author;
            mflag = flag;
            System.out.println(duration);
            mduration = duration;
            /*mduration = -1;*/
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(SdManagerActivity.this, "开始复制", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            sdcopy(params[0], Integer.parseInt(params[1]), params[2],params[3]);
            /*if(loader.download() == 0){
                ((VideoPlayerActivity)mContext).isDownFinished = true;
            } else {
                ((VideoPlayerActivity)mContext).isDownFinished = false;
                File file = new File(params[1]);
                if(file.exists()) {
                    file.delete();

                }*/

            return null;}


        @Override
        protected void onPostExecute(Void result) {
            int epoch =(int)(System.currentTimeMillis() / 1000);
            if(mflag==0) {
                MusicDBOp audioDBOperation = new MusicDBOp(SdManagerActivity.this);
                audioDBOperation.openDatabase();
                Cursor music = audioDBOperation.selectall();
                while(music.moveToNext()){
                    String temp = music.getString(music.getColumnIndex("title"));
                    if(temp.equalsIgnoreCase(mtitle)){
                        audioDBOperation.delete(music.getInt(music.getColumnIndex("id")));
                    }
                }
               // }
                audioDBOperation.insert(-1*mId, null, mtitle, epoch, mAuthor, 0, Long.parseLong(mduration));
                audioDBOperation.closeDatabase();
            }
            else{
                DBOperation mediaDBOperation = new DBOperation(SdManagerActivity.this);
                mediaDBOperation.openDatabase();
                Cursor vedio = mediaDBOperation.selectall();
                while(vedio.moveToNext()){
                    String temp = vedio.getString(vedio.getColumnIndex("title"));
                    if(temp.equalsIgnoreCase(mtitle))
                       mediaDBOperation.delete(vedio.getInt(vedio.getColumnIndex("id")));
                }
                mediaDBOperation.insert(-1*mId, null, mtitle, epoch, mAuthor,mflag);
                mediaDBOperation.closeDatabase();
            }
            Toast.makeText(SdManagerActivity.this, "复制完成", Toast.LENGTH_SHORT).show();
            super.onPostExecute(result);
        }

    }
    public void sdcopy(String FROMPATH,int flag,String Name,String Id) {
        String TOPATH = null;
        if(flag==0) {
            TOPATH =  Environment.getExternalStorageDirectory().getPath()
                    + File.separator + "Darfoo" + File.separator + "music" + File.separator;
            Name = Name+"_" +(-1)*Integer.parseInt(Id)+".mp3";
        }
        if(flag==1) {TOPATH = Environment.getExternalStorageDirectory().getPath()
                +File.separator + "Darfoo" + File.separator + "video" + File.separator;
            Name = Name+"_" +(-1)*Integer.parseInt(Id)+".mp4";
        }
        if(flag==2) {TOPATH = Environment.getExternalStorageDirectory().getPath()
                +File.separator + "Darfoo" + File.separator + "video" + File.separator;
            Name = Name+"_" +(-1)*Integer.parseInt(Id)+".flv";
        }
        copy(FROMPATH, TOPATH,Name);
       /* if(copy(FROMPATH, TOPATH,Name)==0)
        {
            Toast.makeText(getApplicationContext(),"文件拷贝成功！！！",Toast.LENGTH_SHORT).show();

        }else
        {
            Toast.makeText(getApplicationContext(), "文件拷贝失败！！！", Toast.LENGTH_SHORT).show();
        }*/

    }
    public int copy(String fromFile, String toFile,String Name){
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if(!root.exists())
        {
            return -1;
        }

        //目标目录
        File targetDir = new File(toFile);
        //创建目录
        if(!targetDir.exists())
        {
            targetDir.mkdirs();
        }
        CopySdcardFile(fromFile, toFile+Name);
        return 0;
    }
    public int CopySdcardFile(String fromFile, String toFile){

        try
        {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0)
            {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;

        } catch (Exception ex)
        {
            return -1;
        }
    }
    public final class ViewHolder {
        public CheckBox choose;
        public TextView num;
        public TextView name;
        public TextView author;
        public ImageView type;
    }
    public void showCheckBoxListView() {
        for (int i = 0; i < mData.size(); i++) {
            sdadapter = new MyAdapter(this,mData);
            sdlist.setAdapter(sdadapter);
            sdadapter.notifyDataSetChanged();
            listStr = new ArrayList<String>();
            sdlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view,
                                        int position, long arg3) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    // 在每次获取点击的item时改变checkbox的状态
                    holder.choose.toggle();
                    // 同时修改map的值保存状态
                    sdadapter.isSelected.put(position, holder.choose.isChecked());
                    if (holder.choose.isChecked() == true) {
                        listStr.add(mData.get(position).get("Name")+"");
                    } else {
                        listStr.remove(mData.get(position).get("Name")+"");
                    }
                    /*tv.setText("已选中"+listStr.size()+"项");*/
                }

            });
        }
    }
    public  class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        public  HashMap<Integer, Boolean> isSelected;
        private List<Map<String, Object>> list = null;
        private Context context = null;
        public MyAdapter(Context context,List<Map<String, Object>> list) {

            this.mInflater = LayoutInflater.from(context);
            this.list = list;
            this.context = context;
            init();
        }
        public void init() {
            isSelected = new HashMap<Integer, Boolean>();
            for (int i = 0; i < list.size(); i++) {
                isSelected.put(i, false);
            }
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stubpublic TextView num;
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (holder == null) {
                holder = new ViewHolder();
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.sd_item, null);
                }
                holder.num = (TextView)convertView.findViewById(R.id.media_num);
                holder.name = (TextView) convertView.findViewById(R.id.media_name);
                holder.author = (TextView) convertView.findViewById(R.id.media_author);
                holder.type = (ImageView) convertView.findViewById(R.id.media_type);
                holder.choose = (CheckBox)convertView.findViewById(R.id.media_choose);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Map<String, Object> map = list.get(position);
            if (map != null) {
                holder.num.setText(position+1+"");
                holder.name.setText((String) list.get(position).get("Name"));
                holder.author.setText((String) list.get(position).get("Artist"));
                if((int)list.get(position).get("flag")==0) holder.type.setImageResource(R.drawable.sd_audio);
                else holder.type.setImageResource(R.drawable.sd_media);
            }
            holder.choose.setChecked(isSelected.get(position));
            return convertView;
        }
        }

}
