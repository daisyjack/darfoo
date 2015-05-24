package com.darfoo.darfoolauncher.activity;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.fragment.BaseContentFragment;
import com.darfoo.darfoolauncher.support.Utils;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import java.util.ArrayList;

public class TeamContentActivity extends BaseFragmentActivity {

    public static final String TEAM_ID = "team_id";

    public static final String TEAM_NAME = "team_name";

    private String mTeamName;

    private String requestUrl = Utils.BASE_URL + "/resources/author/videos/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_content);
        TextView textView = (TextView) findViewById(R.id.textview_team);

        int teamId = getIntent().getIntExtra(TEAM_ID, -1);
        if (teamId == -1) {

        } else {
            requestUrl = requestUrl + teamId;
            initContent();
        }

        mTeamName = getIntent().getStringExtra(TEAM_NAME);
        textView.setText(mTeamName);
    }

    private void initContent() {
        BaseContentFragment fragment = new BaseContentFragment();
        Bundle bundle = new Bundle();
        ArrayList<Integer> titleResIds, arrayResIds;

        titleResIds = new ArrayList<Integer>(3);
        titleResIds.add(R.string.by_speed);
        titleResIds.add(R.string.by_level);
        titleResIds.add(R.string.by_style);

        arrayResIds = new ArrayList<Integer>(3);
        arrayResIds.add(R.array.dance_speed);
        arrayResIds.add(R.array.dance_level);
        arrayResIds.add(R.array.video_style);

        bundle.putIntegerArrayList(BaseContentFragment.ARG_TITLE_LIST, titleResIds);
        bundle.putIntegerArrayList(BaseContentFragment.ARG_ARRAY_LIST, arrayResIds);
        bundle.putInt(BaseContentFragment.ARG_CONTENT_TYPE,
                BaseContentFragment.TYPE_TEAM);
        bundle.putString(BaseContentFragment.ARG_REQUEST_URL, requestUrl);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_content, fragment);
        transaction.commit();
    }
}
