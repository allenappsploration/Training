
package com.example.appsplorationdev.imademo;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

public class MultipleItemsList extends ListActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, VideoControllerView.MediaPlayerControl, MediaPlayer.OnCompletionListener,AnimationListener {

    SurfaceView videoSurface;
    int textViewCount = 100;
    TextView[] textViewArray;

    MediaPlayer player;
    VideoControllerView controller;
    Boolean isFullScreen = false;
    Boolean startAnimation = true;
    int duration;

    // Animation
    Animation animSideDown;
    private MyCustomAdapter mAdapter;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        player = new MediaPlayer();
        controller = new VideoControllerView(MultipleItemsList.this);

        textViewArray = new TextView[textViewCount];
        // load the animation
        animSideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        // set animation listener
        animSideDown.setAnimationListener(this);

        for(int i = 0; i <= 50; i++) {

            textViewArray[i] = new TextView(this);

        }

        mAdapter = new MyCustomAdapter();
        for (int i = 0; i <= 50; i++) {
            if(!isFullScreen)
            {
                mAdapter.addItem("  News Article " + i);
            }
            if ((i % 27 == 0) && (i!=0)) {
                mAdapter.addSeparatorItem("android.resource://com.example.appsplorationdev.imademo/"+R.raw.k);
            }
        }
        setListAdapter(mAdapter);

    }

    // Handle your functionality here.
    public void onClickHandler(View v)
    {
        switch (v.getId()) {
            case R.id.video_container:

                if(player.isPlaying())
                {
                    if(!controller.isShowing())
                    {
                        videoSurface.setZOrderOnTop(false);
                        controller.show();
                    }

                    else
                    {
                        videoSurface.setZOrderOnTop(false);
                        controller.hide();
                    }
                }

                else
                {
                    controller.show(0);
                }

                break;

            case R.id.list_container:

                if(player.isPlaying())
                {
                    if(controller.isShowing())
                    {
                        controller.hide();
                    }
                }

                else
                {
                    controller.show(0);
                }

                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v("tag", "surfaceDestroyed Called");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.v("tag", "surfaceChanged Called");
    }

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        //videoSurface.setBackgroundColor(Color.TRANSPARENT);
        // Adjust the size of the video
        // so it fits on the screen

        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        android.view.ViewGroup.LayoutParams lp = videoSurface.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        videoSurface.setLayoutParams(lp);
        duration = mp.getDuration();
        mp.start();
        // videoSurface.setZOrderOnTop(false);
        controller.setMediaPlayer(MultipleItemsList.this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        controller.setEnabled(true);
        controller.show();
    }

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        player.start();
    }


    // End SurfaceHolder.Callback

    public void onCompletion(MediaPlayer mp) {
        Log.v("tag", "onCompletion Called");
        controller.show(0);
        //finish();
    }
    // End MediaPlayer.OnPreparedListener

    @Override
    public boolean onError(MediaPlayer mp, int whatError, int extra) {
        Log.v("tag", "onError Called");

        if (whatError == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            Log.v("tag", "Media Error, Server Died " + extra);
        } else if (whatError == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
            Log.v("tag", "Media Error, Error Unknown " + extra);
        }

        return false;
    }

    // Implement VideoMediaController.MediaPlayerControl
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public boolean isFullScreen() {
        return isFullScreen;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){

        if(isFullScreen())
        {
            if(ev.getAction()== MotionEvent.ACTION_MOVE)
                return true;
        }

        return super.dispatchTouchEvent(ev);
    }

    @SuppressLint("NewApi")
    @Override
    public void toggleFullScreen() {
        if (!isFullScreen())
        {

            isFullScreen = true;

            for(int i = 0; i <= 50; i++) {

                textViewArray[i].setVisibility(View.GONE);
            }

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

            ActionBar actionBar = getActionBar();
            actionBar.hide();
            // Adjust the size of the video
            // so it fits on the screen
            int videoWidth = player.getVideoWidth();
            int videoHeight = player.getVideoHeight();
            float videoProportion = (float) videoWidth / (float) videoHeight;
            int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
            int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
            float screenProportion = (float) screenWidth / (float) screenHeight;
            android.view.ViewGroup.LayoutParams lp = videoSurface.getLayoutParams();

            if (videoProportion > screenProportion) {
                lp.width = screenWidth;
                lp.height = (int) ((float) screenWidth / videoProportion);
            } else {
                lp.width = (int) (videoProportion * (float) screenHeight);
                lp.height = screenHeight;
            }
            videoSurface.setLayoutParams(lp);

            if(!player.isPlaying())
            {
                controller.show(0);
            }
        }
        else{
            isFullScreen = false;

            for(int i = 0; i <= 50; i++) {

                textViewArray[i].setVisibility(View.VISIBLE);
            }

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            ActionBar actionBar = getActionBar();
            actionBar.show();

            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) videoSurface.getLayoutParams();
            params.width = width;
            params.height=height / 3;
            params.setMargins(0, 0, 0, 0);

            if(!player.isPlaying())
            {
                controller.show(0);
            }
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // Take any action after completing the animation

        // check for zoom in animation
        if (animation == animSideDown) {
        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player.isPlaying()) {
            player.pause();
        } else {
            return;
        }
    }

    public static class ViewHolder {
        public TextView textView;
        public SurfaceView surfaceView;
    }

    private class MyCustomAdapter extends BaseAdapter {


        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

        ArrayList<String> mData = new ArrayList<String>();
        private LayoutInflater mInflater;

        private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();

        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final String item) {
            if(!isFullScreen)
            {
                mData.add(item);
                notifyDataSetChanged();
            }

        }

        public void addSeparatorItem(final String item) {

            mData.add(item);
            // save separator position
            mSeparatorsSet.add(mData.size() - 1);
            notifyDataSetChanged();

        }

        @Override
        public int getItemViewType(int position) {
            return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            int type = getItemViewType(position);
            System.out.println("getView " + position + " " + convertView + " type = " + type);
            if (convertView == null) {
                holder = new ViewHolder();
                switch (type) {
                    case TYPE_ITEM:

                        convertView = mInflater.inflate(R.layout.itemlist, null);

                        break;
                    case TYPE_SEPARATOR:
                        convertView = mInflater.inflate(R.layout.videoview, null);

                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            if(type==TYPE_ITEM)
            {

                textViewArray[position] = (TextView)convertView.findViewById(R.id.text);
                if(!isFullScreen)
                {
                    textViewArray[position].setText(mData.get(position));
                }

                //textview = (TextView)convertView.findViewById(R.id.text);
                //textview.setText(mData.get(position));
            }

            else if(type==TYPE_SEPARATOR)
            {

                //Log.e("prob...", "video");
                videoSurface = (SurfaceView) convertView.findViewById(R.id.videoSurface);

                // start the animation
                videoSurface.setVisibility(View.VISIBLE);

                if(startAnimation == true)
                {
                    videoSurface.startAnimation(animSideDown);
                    startAnimation = false;
                }

                SurfaceHolder videoHolder = videoSurface.getHolder();
                videoHolder.addCallback(MultipleItemsList.this);
                videoHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                videoSurface.getHolder().setFormat(PixelFormat.RGBA_8888);

                String path = "android.resource://com.example.appsplorationdev.imademo/"+R.raw.video1;
                try {

                    player.setOnCompletionListener(MultipleItemsList.this);
                    player.setOnErrorListener(MultipleItemsList.this);
                    player.setOnPreparedListener(MultipleItemsList.this);
                    player.setWakeMode(getApplicationContext(),
                            PowerManager.PARTIAL_WAKE_LOCK);
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.setDataSource(MultipleItemsList.this, Uri.parse(path));
                    player.prepareAsync();

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(!player.isPlaying())
            {
                controller.show(0);
            }

            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        if (isFullScreen == false) {
            super.onBackPressed();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_left_to_right, R.anim.enter_right_to_left);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
                overridePendingTransition(R.anim.enter_left_to_right, R.anim.enter_right_to_left);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

