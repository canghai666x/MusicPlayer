package com.canghai.text3b;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.canghai.text3b.Interface.IPlayController;
import com.canghai.text3b.Interface.IPlayerViewController;
import com.canghai.text3b.Service.MusicService;

import static com.canghai.text3b.Interface.IPlayController.PLAY_STATE_PAUSE;
import static com.canghai.text3b.Interface.IPlayController.PLAY_STATE_PLAY;
import static com.canghai.text3b.Interface.IPlayController.PLAY_STATE_STOP;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";

    private Button stopBtn;
    private Button playBtn;
    private SeekBar seekBar;
    private IPlayController iPlayController;
    private Connection connection;
    private boolean userTouchSeekBar = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //初始化控件事件
        initEvent();
        //开启服务
        initStartService();
        //绑定服务
        initBindService();
        requestPermision();
        Toast.makeText(this,"杨柳千条拂面丝，绿烟金穗不胜吹。\n香随静婉歌尘起，影伴娇娆舞袖垂。\n羌管一声何处曲，流莺百啭最高枝。\n千门九陌花如雪，飞过宫墙两自知",Toast.LENGTH_LONG).show();
    }

    private void requestPermision() {
        String readPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        int hasPermission = checkSelfPermission(readPermission);
        if (hasPermission!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{readPermission},0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0:
                if (grantResults.length>0){
                    if (grantResults[0]==PackageManager.PERMISSION_GRANTED){

                    }else {
                        Toast.makeText(this,"无读取权限",Toast.LENGTH_SHORT);
                    }
                }
                break;
                default:super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initStartService() {
        Log.d(TAG, "initStartService: ");
        startService(new Intent(this,MusicService.class));
    }

    private void initBindService() {
        Log.d(TAG, "initBindService: ");
        Intent intent = new Intent(this, MusicService.class);
        if (connection == null) {
            connection = new Connection();
        }
        bindService(intent, connection,BIND_AUTO_CREATE);
    }

    private void initEvent() {
        //进度条改变
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //进度条改变
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //触摸
                userTouchSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch: ");
                //停止触摸
                int touchProgress = seekBar.getProgress();
                if (iPlayController != null) {
                    iPlayController.seekTo(touchProgress);
                }
                userTouchSeekBar =false;
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: play");
                //播放或者暂停
                if (iPlayController != null) {
                    iPlayController.playOrPause();
                }
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: stop");
                //停止播放
                if (iPlayController != null) {
                    iPlayController.stop();
                }
            }
        });
    }

    private void initView() {
        seekBar = findViewById(R.id.seekBar);
        playBtn = findViewById(R.id.play_or_stop_btn);
        stopBtn = findViewById(R.id.stop_btn);
    }

    private class Connection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            iPlayController = (IPlayController) service;
            iPlayController.registerViewController(playerViewController);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            iPlayController = null;
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        if (connection != null) {
            unbindService(connection);
        }
    }

    private IPlayerViewController playerViewController = new IPlayerViewController() {
        @Override
        public void onPlayerStateChange(int state) {
            switch (state){
                case PLAY_STATE_PLAY:
                    playBtn.setText("暂停");
                    break;
                case PLAY_STATE_PAUSE:
                    playBtn.setText("播放");
                    break;
                case PLAY_STATE_STOP:
                    playBtn.setText("播放");
                    seekBar.setProgress(0);
                    break;
            }
        }

        @Override
        public void onSeekChange(int seek) {
            seekBar.setProgress(seek);
        }
    };
}
