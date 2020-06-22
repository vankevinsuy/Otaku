package com.wallaby.otaku.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.wallaby.otaku.PermissionAndUpdateDataActivity;
import com.wallaby.otaku.R;
import com.wallaby.otaku.internal_database.OtakuDatabase;

public class VideoPlayingActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    private VideoView vid;
    private boolean hideUI;
    private ImageButton playPauseButton;
    private boolean play;
    private RelativeLayout baseRelativeView;
    private SeekBar seekBar;
    private TextView videoName;
    private RelativeLayout controlPanelLayout;
    private OtakuDatabase otakuDatabase;
    int target;

    private GestureDetectorCompat mDetector;
    Display screen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        String video_path = getIntent().getStringExtra("video_path");

        otakuDatabase = new OtakuDatabase(getApplicationContext());

        screen = getWindowManager().getDefaultDisplay();

        mDetector = new GestureDetectorCompat(this,this);
        mDetector.setOnDoubleTapListener(this);

        playPauseButton = findViewById(R.id.play_and_stop);
        hideUI = false;
        play = true;

        videoName = findViewById(R.id.videoName);
        seekBar = findViewById(R.id.videoSeekBar);
        controlPanelLayout = findViewById(R.id.controlPanelLayout);
//        baseRelativeView = findViewById(R.id.baseRelativeView);
        vid = (VideoView)findViewById(R.id.videoView);
        playPauseButton.setBackgroundResource(R.drawable.pausebutton);

        videoName.setText(video_path.split("/")[video_path.split("/").length-1]);

        vid.setVideoPath(video_path);
        seekBar.setMin(0);

        //passer en fullscreen et cacher les buttons au lancement de la video
        hideSystemUI();


        // set seek bar max
        vid.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(vid.getDuration());
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                target = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                vid.seekTo(target);
            }
        });

        VideoPlayingActivity.this.runOnUiThread(new Runnable() {
            Handler mHandler = new Handler();
            @Override
            public void run() {
                seekBar.setProgress(vid.getCurrentPosition());
                mHandler.postDelayed(this, 1000);
            }

        });

        vid.start();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // parameter le bouton pause et play
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        playPauseButton.setClickable(true);
        playPauseButton.setVisibility(View.VISIBLE);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(play){
                    vid.pause();
                    play = false;
                    playPauseButton.setBackgroundResource(R.drawable.playbutton);
                }
                else {
                    vid.start();
                    play = true;
                    playPauseButton.setBackgroundResource(R.drawable.pausebutton);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //otakuDatabase.FirebaseSyncDatabasesToFirebase();

        Intent intent = new Intent(getApplicationContext(), PermissionAndUpdateDataActivity.class);
        finish();

        startActivity(intent);
    }



    // GESTURES


    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (this.mDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        if(!hideUI){
            showSystemUI();
            hideUI = true;
            playPauseButton.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.VISIBLE);
            controlPanelLayout.setVisibility(View.VISIBLE);

            seekBar.setProgress(vid.getCurrentPosition(), true);
        }
        else {
            hideUI = false;
            hideSystemUI();
            playPauseButton.setVisibility(View.GONE);
            controlPanelLayout.setVisibility(View.GONE);

            seekBar.setVisibility(View.GONE);
        }
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        int screenWidth = screen.getWidth();

        // -10 sec
        if(motionEvent.getX() < screenWidth/2){

            target = vid.getCurrentPosition() - 10000;
            vid.seekTo(target);
        }

        // +10 sec
        if(motionEvent.getX() > screenWidth/2){
            target = vid.getCurrentPosition() + 10000;
            vid.seekTo(target);
        }

        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

}