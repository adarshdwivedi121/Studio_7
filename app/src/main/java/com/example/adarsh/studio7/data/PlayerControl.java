package com.example.adarsh.studio7.data;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.adarsh.studio7.R;

/**
 * Created by adarsh on 31/05/2017.
 */

public class PlayerControl{
    private static MediaPlayer mediaPlayer = null;
    private static Activity parentActivity = null;
    private static String music = null;
    private static Handler seekHandler = new Handler();
    private static ProgressBar progressBar;

    private static Bitmap albumArt = null;
    private static String songName = null;

    public static String getAlbumName() {
        return albumName;
    }

    public static void setAlbumName(String albumName) {
        PlayerControl.albumName = albumName;
    }

    public static String getArtistName() {
        return artistName;
    }

    public static void setArtistName(String artistName) {
        PlayerControl.artistName = artistName;
    }

    private static String albumName = null;
    private static String artistName = null;

    public static Bitmap getImage(){
        return albumArt;
    }

    public static String getSongName(){
        return songName;
    }

    public static void setImage(Bitmap image){
        albumArt = image;
    }
    public static void setName(String name){
        songName = name;
    }

    public static boolean hasSong(){
        return music != null;
    }


    public PlayerControl(Activity activity){
        parentActivity = activity;
        progressBar = (ProgressBar) parentActivity.findViewById(R.id.progressBar);
    }

    private static Runnable run = PlayerControl::seekUpdate;

    private static void seekUpdate() {
        progressBar.setProgress(mediaPlayer.getCurrentPosition());
        seekHandler.postDelayed(run, 500);
    }

    public static void addMusicFile(Context context, String file){
        music = file;
        mediaPlayer = MediaPlayer.create(context, Uri.parse(file));
        mediaPlayer.setOnCompletionListener(new MediaCompletionListener());
        progressBar.setMax(mediaPlayer.getDuration());
    }

    public static String getFilePath(){
        return music;
    }

    public static void play(){
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }

        if(isPlaying()){
            progressBar.setMax(mediaPlayer.getDuration());
            run.run();
        }

    }

    public static void pause(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public static void stop(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
    }

    public static void repeat(Boolean req){
        if(mediaPlayer != null)
            mediaPlayer.setLooping(req);
    }

    public static void release(){
        if (mediaPlayer != null)
            mediaPlayer.release();
    }

    public static boolean isPlaying(){
        return !(mediaPlayer == null || !mediaPlayer.isPlaying());
    }

    public static Activity getActivity() {
        return parentActivity;
    }
}


class MediaCompletionListener implements  MediaPlayer.OnCompletionListener{

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mp.isLooping()) {
            PlayerControl.stop();
            PlayerControl.play();
        }
        else {
            PlayerControl.stop();

            PlayerControl.getActivity().findViewById(R.id.player_play_pause_button).setTag(Boolean.FALSE);
            ((ImageButton)PlayerControl.getActivity().findViewById(R.id.player_play_pause_button)).setImageResource(R.drawable.ic_play);
        }
    }
}
