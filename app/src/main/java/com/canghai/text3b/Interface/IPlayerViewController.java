package com.canghai.text3b.Interface;

public interface IPlayerViewController {
    /**
     * 播放状态改变
     */
    void onPlayerStateChange(int state);
    /**
     * 播放进度改变
     */
    void onSeekChange(int seek);
}
