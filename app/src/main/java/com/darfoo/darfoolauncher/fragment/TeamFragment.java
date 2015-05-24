package com.darfoo.darfoolauncher.fragment;


import com.darfoo.darfoolauncher.support.Cryptor;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.activity.TeamContentActivity;
import com.darfoo.darfoolauncher.support.DanceTeam;
import com.darfoo.darfoolauncher.support.Utils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamFragment extends Fragment {

    private static final String request_url = Utils.BASE_URL + "/resources/author/index";

    private GridView mGridView;

    public TeamFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team, container, false);
        mGridView = (GridView) view.findViewById(R.id.gridview_teams);
        initTeamsContent();
        return view;
    }

    private void initTeamsContent() {
        Ion.with(this).load(request_url).asJsonArray().setCallback(new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                if (e != null) {
                    return;
                }

                List<DanceTeam> teamList = new ArrayList<DanceTeam>();
                for (int i = 0; i < result.size(); i++) {
                    JsonObject temp = (JsonObject) result.get(i);
                    Integer id = temp.get("id").getAsInt();
                    String name = temp.get("name").getAsString();
                    String description = temp.get("description").getAsString();
                    String image_url = Cryptor.decryptQiniuUrl(temp.get("image_url").getAsString());
                    DanceTeam team = new DanceTeam();
                    team.setId(id);
                    team.setName(name);
                    team.setDescription(description);
                    team.setImage_url(image_url);
                    teamList.add(team);
                }

                final TeamsAdapter adapter = new TeamsAdapter(teamList);
                mGridView.setAdapter(adapter);
                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
                        adapter.onItemClick(position);
                    }
                });
            }
        });
    }

    private class TeamsAdapter extends BaseAdapter {

        private List<DanceTeam> mTeamList;

        public TeamsAdapter(List<DanceTeam> teamList) {
            mTeamList = teamList;
        }

        @Override
        public int getCount() {
            return mTeamList.size();
        }

        @Override
        public Object getItem(int position) {
            return mTeamList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void onItemClick(int position) {
            int id = mTeamList.get(position).getId();
            Intent intent = new Intent(getActivity(), TeamContentActivity.class);
            intent.putExtra(TeamContentActivity.TEAM_ID, id);
            intent.putExtra(TeamContentActivity.TEAM_NAME, mTeamList.get(position).getName());
            startActivity(intent);
            Utils.sendStatistics(getActivity(), "author", mTeamList.get(position).getId());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.fragment_recommendation_famous_dance_group_item, parent,
                                false);
                holder = new ViewHolder();
                holder.mImageView = (ImageView) convertView
                        .findViewById(R.id.fragment_recommendation_famous_dance_group_item_image);
                holder.mTextView = (TextView) convertView
                        .findViewById(R.id.fragment_recommendation_famous_dance_group_item_text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.mTextView.setText(mTeamList.get(position).getName());
            Ion.with(getActivity())
                    .load(mTeamList.get(position).getImage_url()).asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            if (e != null) {
                                result = BitmapFactory.decodeResource(getResources(),
                                        R.drawable.ic_famous_dancegroup);
                            } else {
                                try {
                                    result = Utils.getRoundedCornerBitmap(result);
                                } catch (NullPointerException e1) {
                                    e1.printStackTrace();
                                    result = BitmapFactory.decodeResource(getResources(),
                                            R.drawable.ic_famous_dancegroup);
                                }
                            }
                            holder.mImageView.setImageBitmap(result);
                        }
                    });

            return convertView;
        }

        class ViewHolder {

            ImageView mImageView;

            TextView mTextView;
        }
    }

}
