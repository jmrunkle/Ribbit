package com.teamtreehouse.ribbit;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewImageActivity extends AppCompatActivity {

    @Bind(R.id.imageView)
    protected ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        ButterKnife.bind(this);

        Uri imageUri = getIntent().getData();

        Picasso.with(this).load(imageUri.toString()).into(mImageView);
    }

}
