package com.darfoo.mvplayer.widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.darfoo.darfoolauncher.R;
import com.darfoo.mvplayer.utils.FileDetail;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by YuXiaofei on 2014/11/24.
 */
public class MusicAdapter extends BaseAdapter {

    private Context mContext;
    private List<FileDetail> details;

    public int mCurrentPos;

    public MusicAdapter(Context context, List<FileDetail> details, int currentPos) {
        super();
        mContext = context;
        this.details = details;
        mCurrentPos = currentPos;

    }

    @Override
    public int getCount() {
        return details.size();
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
            convertView = inflater.inflate(R.layout.music_item, null);
        }

        if(position == mCurrentPos) {
            convertView.setBackgroundColor(Color.rgb(241, 241, 241));
        } else {
            convertView.setBackgroundColor(Color.rgb(255, 255, 255));

        }
        TextView pos = (TextView)convertView.findViewById(R.id.item_pos);
        TextView title = (TextView)convertView.findViewById(R.id.item_title);
        TextView author = (TextView)convertView.findViewById(R.id.item_artist);
        TextView duration = (TextView)convertView.findViewById(R.id.item_duration);
        pos.setText(position + 1 + "");
        title.setText(details.get(position).getTitle());
        author.setText(details.get(position).getAuthor());
        duration.setVisibility(View.GONE);
        return convertView;

    }

}
