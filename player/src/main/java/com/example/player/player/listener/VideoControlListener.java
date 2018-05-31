package com.example.player.player.listener;

public interface VideoControlListener {
    void startVideo();
    void pauseVideo();
    void stopVideo();
    void prepareVideo();
    void seekVideo(int progress);
    void updateVideoPosition();
    //void updateSeekPosition();
    void restartVideo();
    //void resetVideo();
    void toggleScreen(boolean isPortrait);
}
