package com.projects.darknigh.imgapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    final String TAG = "myLog";
    AlertDialog.Builder builder;
    TextView textView;
    ProgressBar progressBar;
    ImageView downloadedImage;
    Link link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        downloadedImage = findViewById(R.id.downloadedImage);
        progressBar = findViewById(R.id.progressBar);
        builder = new AlertDialog.Builder(this);
        try {
            if (getIntent().getStringExtra("appName").equals("LinkApp")) {
                link = new Link(getIntent().getIntExtra("id", 0), getIntent().getStringExtra("link"), getIntent().getIntExtra("status", 0), getIntent().getIntExtra("open_time", 0));
                progressBar.setVisibility(VISIBLE);
                textView.setText(R.string.loading_text);
                final Link downloadLink = new Link();
                final int defaultStatus = link.getStatus();
                downloadLink.setLink(link.getLink());
                if (Patterns.WEB_URL.matcher(downloadLink.getLink()).matches()) {
                    Picasso.get().load(link.getLink()).into(downloadedImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            textView.setVisibility(GONE);
                            downloadLink.setStatus(1);
                            if (progressBar != null) {
                                progressBar.setVisibility(GONE);
                            }
                            long downloadEndTime = System.currentTimeMillis();
                            downloadLink.setOpenTime((downloadEndTime - getIntent().getLongExtra("start_time", 0)) / 1000);
                            switch (defaultStatus) {
                                case 0:
                                    insertData(downloadLink);
                                    break;
                                case 2:
                                    updateData(downloadLink);
                                    break;
                                case 1:
                                    deleteData();
                                    break;
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            downloadLink.setStatus(2);
                            textView.setText(R.string.error_downloading);
                            if (progressBar != null) {
                                progressBar.setVisibility(GONE);
                            }
                            long downloadEndTime = System.currentTimeMillis();
                            downloadLink.setOpenTime(Math.round((downloadEndTime - getIntent().getLongExtra("start_time", 0)) / 1000));
                            switch (defaultStatus) {
                                case 0:
                                    insertData(downloadLink);
                                    break;
                                case 1:
                                    updateData(downloadLink);
                                    break;
                            }
                        }
                    });
                } else {
                    if (progressBar != null) {
                        progressBar.setVisibility(GONE);
                    }
                    textView.setText(R.string.url_error);
                    downloadLink.setStatus(3);
                    insertData(downloadLink);
                }
            } else {
                showFinishDialog();
            }
        } catch (NullPointerException e) {
            showFinishDialog();
        }
    }


    public void insertData(Link link) {
        Uri contentUri = Uri.parse("content://com.projects.darknight.linkapp.database/links");

        ContentValues contentValues = new ContentValues();
        contentValues.put("link", link.getLink());
        contentValues.put("status", link.getStatus());
        contentValues.put("open_time", link.getOpenTime());
        getContentResolver().insert(contentUri, contentValues);
        Log.d(TAG, "Link inserted: " + link.toString());
    }

    public void updateData(Link link) {
        Uri contentUri = Uri.parse("content://com.projects.darknight.linkapp.database/links/101");
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", link.getStatus());
        getContentResolver().update(contentUri, contentValues, "_id=?", new String[]{String.valueOf(link.getId())});
        Log.d(TAG, "Link updated: " + link.toString());
    }

    public void deleteData() {
        Intent intent = new Intent(this, ImageDeleteService.class);
        intent.putExtra("id", link.getId());
        intent.putExtra("url", link.getLink());
        startService(intent);

    }

    void showFinishDialog() {
        builder.setMessage("Sorry, this app cant launch without LinkApp. It will de closed automaticaly in 10 seconds")
                .setCancelable(false)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        timerShow();
                    }
                })
                .create().show();
    }

    void timerShow() {
        new CountDownTimer(11000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textView.setTextSize(48);
                textView.setText(String.valueOf(millisUntilFinished / 1000));
                if (millisUntilFinished / 1000 < 4) {
                    textView.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }

            @Override
            public void onFinish() {
                finish();
            }
        }.start();
    }
}
