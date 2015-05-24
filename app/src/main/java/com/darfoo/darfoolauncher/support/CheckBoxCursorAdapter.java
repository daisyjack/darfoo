package com.darfoo.darfoolauncher.support;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/1/10 0010.
 */
public class CheckBoxCursorAdapter extends SimpleCursorAdapter {
    private ArrayList<Integer> selection = new ArrayList<Integer>();
    private int mCheckBoxId = 0;
    private int mnumber = 0;
    public boolean[] hasChecked;
    private String mIdColumn;

    public CheckBoxCursorAdapter(Context context, int layout, Cursor c,
                                 String[] from, int[] to, int number,int checkBoxId, String idColumn) {
        super(context, layout, c, from, to);
        mCheckBoxId = checkBoxId;
        mIdColumn = idColumn;
        mnumber = number;
        hasChecked = new boolean[getCount()];
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    private void checkedChange(int checkedID) {
        hasChecked[checkedID] = !hasChecked[checkedID];
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        CheckBox checkbox = (CheckBox)view.findViewById(mCheckBoxId);
        TextView mynumber = (TextView)view.findViewById(mnumber);
        mynumber.setText(position+1+"");
        checkbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                //记录物品选中状态
                checkedChange(position);
            }
        });
        /*checkbox.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Cursor cursor = getCursor();
                cursor.moveToPosition(position);
                int rowId = cursor.getInt(cursor.getColumnIndexOrThrow(mIdColumn));
                int index = selection.indexOf(rowId);
                if (index != -1) {
                    selection.remove(index);
                } else {
                    selection.add(rowId);
                }
            }
        });
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        int rowId = cursor.getInt(cursor.getColumnIndexOrThrow(mIdColumn));
        if (selection.indexOf(rowId)!= -1) {
            checkbox.setChecked(true);
        } else {
            checkbox.setChecked(false);
        }*/

        return view;
    }

     ArrayList<Integer> getSelectedItems(){
        return selection;
    }
}
