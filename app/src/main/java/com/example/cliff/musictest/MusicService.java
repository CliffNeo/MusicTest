package com.example.cliff.musictest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by cliff on 2016/5/11.
 */
public class MusicService extends Service {

    private MediaPlayer player = new MediaPlayer();
    private mBinder binder = new mBinder();
    NotificationManager manager = null;
    ControlBroadcast receiver = null;
    Notification notification = null;
    NotificationCompat.Builder builder = null;

    @Override
    public void onCreate() {
        super.onCreate();
        iniPlayer();
        iniReceiver();
        iniNotification();
        //EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(player != null) {
            player.stop();
            player.release();
        }
        unregisterReceiver(receiver);
        manager.cancelAll();
        //EventBus.getDefault().unregister(this);
    }

    class mBinder extends Binder{

        public void play(){
            if(!player.isPlaying()){
                player.start();
                RemoteViews remoteViews = iniRemoteView("Pause");
                builder.setContent(remoteViews);
                notification = builder.build();
                manager.notify(200,notification);
                EventBus.getDefault().post(new MusicControl(MusicControl.play));
            }
        }

        public void pause(){
            if(player.isPlaying()){
                player.pause();
                RemoteViews remoteViews = iniRemoteView("Play");
                builder.setContent(remoteViews);
                notification = builder.build();
                manager.notify(200,notification);
                EventBus.getDefault().post(new MusicControl(MusicControl.pause));
            }
        }

        public void stop(){
            if(player.isPlaying()){
                player.reset();
                iniPlayer();
                RemoteViews remoteViews = iniRemoteView("Play");
                builder.setContent(remoteViews);
                notification = builder.build();
                manager.notify(200,notification);
                EventBus.getDefault().post(new MusicControl(MusicControl.stop));
            }
        }

        public  boolean isPlaying(){
            return player.isPlaying();
        }
    }

    public void iniPlayer(){
        try{
            File file = new File(Environment.getExternalStorageDirectory(),"/music/eminem - the re up.mp3.");
            player.setDataSource(file.getPath());
            player.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static final String Tag = "tag";
    private static final int tag_play = 1;
    private static final int tag_stop = 2;

    private void iniNotification(){
        RemoteViews remoteViews = iniRemoteView("Play");

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this);

        builder.setContent(remoteViews)
                //.setContentIntent()
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = builder.build();

        notification.flags = Notification.FLAG_ONGOING_EVENT;
        manager.notify(200,notification);
        Log.d("ddd","notification");
    }

    private RemoteViews iniRemoteView(String change){
        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notification_panel);
        Intent buttonIntent = new Intent("com.example.cliff.music.controlbroadcast");

        buttonIntent.putExtra(Tag,tag_play);
        PendingIntent play = PendingIntent.getBroadcast(this,1,buttonIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.no_btn_play,play);

        buttonIntent.putExtra(Tag,tag_stop);
        PendingIntent stop = PendingIntent.getBroadcast(this,2,buttonIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.no_btn_stop,stop);

        remoteViews.setCharSequence(R.id.no_btn_play,"setText",change);
        return remoteViews;
    }

    public class ControlBroadcast extends BroadcastReceiver{
        private mBinder binder = new MusicService.mBinder();

        @Override
        public void onReceive(Context context, Intent intent) {
            int tag = intent.getIntExtra(Tag,0);
            switch (tag){
                case tag_play:
                    if(binder.isPlaying()){
                        binder.pause();
                    }else {
                        binder.play();
                    }
                    break;
                case tag_stop:
                    binder.stop();
            }
        }
    }

    private void iniReceiver(){
        receiver = new ControlBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.cliff.music.controlbroadcast");
        registerReceiver(receiver,intentFilter);
    }
}
