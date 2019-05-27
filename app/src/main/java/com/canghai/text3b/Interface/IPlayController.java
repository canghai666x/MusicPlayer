package com.canghai.text3b.Interface;

public interface IPlayController {
    /**
     * 状态
     */
    int PLAY_STATE_PLAY = 0;
    int PLAY_STATE_PAUSE= 1;
    int PLAY_STATE_STOP = 2;


    /**
     * 播放或暂停
     */
    void playOrPause();

    /**
     * 停止播放
     */
    void stop();
    /**
     * 设置进度
     */
    void seekTo(int seek);

    /**
     * 获得UI控制
     */
    void registerViewController(IPlayerViewController iPlayerViewController);

    void unRegisterViewController();
}
