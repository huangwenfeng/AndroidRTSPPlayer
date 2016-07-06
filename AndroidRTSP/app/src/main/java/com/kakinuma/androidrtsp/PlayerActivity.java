package com.kakinuma.androidrtsp;

import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import libvlc.EventHandler;
import libvlc.IVideoPlayer;
import libvlc.LibVLC;
import libvlc.LibVlcException;
import libvlc.Util;
import libvlc.WeakHandler;

public abstract class PlayerActivity extends AppCompatActivity implements OnClickListener,
        IVideoPlayer {

    private SurfaceHolder surfaceHolder = null;
    private LibVLC mLibVLC = null;
    private SurfaceView surfaceView = null;

    private int mUiVisibility = -1;

    private int mVideoHeight;
    private int mVideoWidth;
    private int mSarDen;
    private int mSarNum;

    private final Handler eventHandler = new PlayerEventHandler(this);
    private static class PlayerEventHandler extends
            WeakHandler<PlayerActivity> {
        public PlayerEventHandler(PlayerActivity owner) {
            super(owner);
        }
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    };

    private final SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            mLibVLC.attachSurface(holder.getSurface(),
                    PlayerActivity.this);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mLibVLC.detachSurface();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFormat(PixelFormat.RGBX_8888);
        surfaceHolder.addCallback(mSurfaceCallback);

        if (Util.isICSOrLater())
            getWindow()
                    .getDecorView()
                    .findViewById(android.R.id.content)
                    .setOnSystemUiVisibilityChangeListener(
                            new View.OnSystemUiVisibilityChangeListener() {

                                @Override
                                public void onSystemUiVisibilityChange(
                                        int visibility) {
                                    if (visibility == mUiVisibility)
                                        return;


                                    int dw = getWindow().getDecorView().getWidth();
                                    int dh = getWindow().getDecorView().getHeight();
                                    boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
                                    if (dw > dh && isPortrait || dw < dh && !isPortrait) {
                                        int d = dw;
                                        dw = dh;
                                        dh = d;
                                    }
                                    if (dw * dh == 0)
                                        return;
                                    double ar, vw;
                                    double density = (double) mSarNum / (double) mSarDen;
                                    if (density == 1.0) {
                                        vw = mVideoWidth;
                                        ar = (double) mVideoWidth / (double) mVideoHeight;
                                    } else {
                                        vw = mVideoWidth * density;
                                        ar = vw / mVideoHeight;
                                    }
                                    double dar = (double) dw / (double) dh;
                                    ar = 16.0 / 9.0;
                                    if (dar < ar)
                                        dh = (int) (dw / ar);
                                    else
                                        dw = (int) (dh * ar);


                                    surfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
                                    ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
                                    lp.width = dw;
                                    lp.height = dh;
                                    surfaceView.setLayoutParams(lp);
                                    surfaceView.invalidate();


                                    if (visibility == View.SYSTEM_UI_FLAG_VISIBLE) {
                                        Log.d("player", "onSystemUiVisibilityChange");
                                    }
                                    mUiVisibility = visibility;
                                }
                            });

        try {
            mLibVLC = LibVLC.getInstance();
            if (mLibVLC != null) {
                EventHandler em = EventHandler.getInstance();
                em.addHandler(eventHandler);
                handler.sendEmptyMessageDelayed(0, 1000);
            }
        } catch (LibVlcException e) {
            e.printStackTrace();
        }



    }

}
