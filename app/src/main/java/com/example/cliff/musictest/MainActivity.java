package com.example.cliff.musictest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {

    private Button play;
    private Button stop;
    private MusicService.mBinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (MusicService.mBinder)iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this,MusicService.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);

        EventBus.getDefault().register(this);

        play = (Button)findViewById(R.id.btn_play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binder.isPlaying()){
                    binder.play();
                    play.setText("Pause");
                }else {
                    binder.pause();
                    play.setText("Play");
                }
            }
        });

        stop = (Button)findViewById(R.id.btn_stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binder.isPlaying()){
                    binder.stop();
                    play.setText("Play");
                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this,MusicService.class);
        unbindService(connection);
        stopService(intent);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(MusicControl control){
        play.setText(control.getControl());
    }
}
