package com.darfoo.darfoolauncher.activity;

import com.darfoo.darfoolauncher.R;

import android.os.Bundle;
import android.widget.ImageView;

public class SingleImageActivity extends BaseFragmentActivity {

    public static final String IMG_RES = "image_res";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_instruction);
        ImageView imageView = (ImageView) findViewById(R.id.imageview);
        imageView.setImageResource(
                getIntent().getIntExtra(IMG_RES, R.drawable.connection_instruction));
    }
}