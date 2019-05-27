package com.canghai.text3b.Service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.canghai.text3b.Interface.IPlayController;
import com.canghai.text3b.Interface.IPlayerViewController;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.canghai.text3b.Interface.IPlayController.PLAY_STATE_STOP;

public class MusicService extends Service {
    private static final String TAG = "MusicService";
    private int currentState = PLAY_STATE_STOP;
    private MyBinder myBinder;
    @Override
    public void onCreate() {
        super.onCreate();
        myBinder = new MyBinder();
    }

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    private class MyBinder extends Binder implements IPlayController{
        private IPlayerViewController playerViewController;
        private MediaPlayer mediaPlayer;
        private Timer timer;
        private SeekTimerTask timerTask;

        @Override
        public void playOrPause() {
            Log.d(TAG, "playOrPause: ");
            if (currentState==PLAY_STATE_STOP){
                initPlayer();
                try {
                    mediaPlayer.setDataSource("/mnt/sdcard/Music/song.mp3");
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                currentState = PLAY_STATE_PLAY;
                playerViewController.onPlayerStateChange(currentState);
                startTimer();
            }else if (currentState==PLAY_STATE_PLAY){
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                    currentState = PLAY_STATE_PAUSE;
                    playerViewController.onPlayerStateChange(currentState);
                    stopTimer();
                }
            }else if (currentState==PLAY_STATE_PAUSE){
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                    currentState = PLAY_STATE_PLAY;
                    playerViewController.onPlayerStateChange(currentState);
                    startTimer();
                }
            }
        }

        private void initPlayer() {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
            }
        }

        @Override
        public void stop() {
            Log.d(TAG, "stop: ");
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            currentState=PLAY_STATE_STOP;
            playerViewController.onPlayerStateChange(currentState);
            stopTimer();
        }

        @Override
        public void seekTo(int seek) {
            Log.d(TAG, "seekTo: ");
            if (mediaPlayer != null) {
                int tarSeek = (int) (seek*1.0f/100*mediaPlayer.getDuration());
                mediaPlayer.seekTo(tarSeek);
            }
        }

        private void startTimer(){
            if (timer == null) {
                timer = new Timer();
            }
            if (timerTask == null) {
                timerTask = new SeekTimerTask();
            }
            timer.schedule(timerTask,0,500);
        }

        private void stopTimer(){
            if (timerTask!=null){
                timerTask.cancel();
                timerTask = null;
            }
            if (timer!=null){
                timer.cancel();
                timer=null;
            }
        }

        private class SeekTimerTask extends TimerTask{
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentMills = mediaPlayer.getCurrentPosition();
                    int tarPosition = (int) (currentMills*1.0f/mediaPlayer.getDuration()*100);
                    playerViewController.onSeekChange(tarPosition);
                }
            }
        }

        @Override
        public void registerViewController(IPlayerViewController iPlayerViewController) {
            playerViewController = iPlayerViewController;
        }

        @Override
        public void unRegisterViewController() {
            playerViewController = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myBinder = null;
    }
}
