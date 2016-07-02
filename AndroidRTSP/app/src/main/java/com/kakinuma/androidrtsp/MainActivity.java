package com.kakinuma.androidrtsp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import libvlc.*;

public class MainActivity extends AppCompatActivity {

    private LibVLC mLibVLC = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            EventHandler em = EventHandler.getInstance();
            em.addHandler(handler);

            mLibVLC = Util.getLibVlcInstance();

            if (mLibVLC != null) {
//				String pathUri = "rtsp://192.168.1.1/MJPG?W=640&H=360&Q=50&BR=3000000";  //流媒体地址
				String pathUri = "file:////sdcard/DCIM/Camera/20140530_210748.mp4";   //本地地址
                mLibVLC.playMyMRL(pathUri);
            }
        } catch (LibVlcException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.getData().getInt("event")) {
                case EventHandler.MediaPlayerVout:
                    if (msg.getData().getInt("data") > 0) {
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(),
                                VideoPlayerActivity.class);
                        startActivity(intent);
                    }
                    break;
                case EventHandler.MediaPlayerEncounteredError:
                    Log.d("MainHandler", "Fail Connect");
                    break;
                default:
                    Log.d("MainHandler", "Event not handled");
                    break;
            }
        }
    };

}
