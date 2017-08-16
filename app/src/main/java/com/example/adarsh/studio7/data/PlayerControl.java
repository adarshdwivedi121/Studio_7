package com.example.adarsh.studio7.data;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.adarsh.studio7.R;

/**
 * Created by adarsh on 31/05/2017.
 */

public class PlayerControl{
    public static final int SCREEN_MAIN = 0;
    public static final int SCREEN_PLAY = 1;

    public static final String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA
    };

    private static MediaPlayer mediaPlayer = null;
    private static Activity parentActivity = null;
    private static String music = null;
    private static Handler seekHandler = new Handler();
    private static ProgressBar progressBar;

    private static int SCREEN = SCREEN_MAIN;

    private static Cursor songList;
    private static int pos = -1;
    public static boolean listLoop = false;

    private static Bitmap albumArt = null;
    private static String songName = null;
    private static String albumName = null;
    private static String artistName = null;

    public static void setPos(int pos, String song_id) {
        if(PlayerControl.pos == -1 || music.compareTo(song_id) != 0) {
            stop();
            PlayerControl.pos = pos;
            updateData();
            play();
        }
    }

    public static boolean hasSong(){
        return music != null;
    }

    public PlayerControl(Activity activity, int screen){
        parentActivity = activity;
        progressBar = (ProgressBar) parentActivity.findViewById(R.id.progressBar);
        SCREEN = screen;
    }

    private static Runnable run = () -> {
        if(SCREEN == SCREEN_PLAY)   seekUpdate();
    };

    private static void seekUpdate() {
        progressBar.setProgress(mediaPlayer.getCurrentPosition());
        seekHandler.postDelayed(run, 500);
    }

    public static void repeat(Boolean req){
        if(mediaPlayer != null)
            mediaPlayer.setLooping(req);
    }

    public static boolean isPlaying(){
        return !(mediaPlayer == null || !mediaPlayer.isPlaying());
    }

    public static void setSongList(Cursor songList) {
        PlayerControl.songList = songList;
    }

    private static void updateData(){
        songList.moveToPosition(pos);
        songName = songList.getString(2);
        artistName = songList.getString(3);
        albumName = songList.getString(4);

        Cursor c = parentActivity.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                "_id IS " + songList.getString(1),
                null,
                null
        );
        c.moveToNext();
        Bitmap bitmap = null;

        if (c != null) {
            bitmap = BitmapFactory.decodeFile(c.getString(1));
            c.close();
        }

        if (bitmap == null)
            bitmap = BitmapFactory.decodeResource(parentActivity.getResources(), R.drawable.default_music);

        albumArt = bitmap;

        music = songList.getString(0);
        mediaPlayer = MediaPlayer.create(parentActivity.getApplicationContext(), Uri.parse(songList.getString(5)));
        mediaPlayer.setOnCompletionListener(new MediaCompletionListener());
    }

    public static void updateScreen() {
        if(SCREEN == SCREEN_MAIN){
            Bitmap bitmap = albumArt;
            ((ImageView) parentActivity.findViewById(R.id.main_screen_album_art)).setImageBitmap(bitmap);
            bitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() * 2 / 5, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
            ((ImageView) parentActivity.findViewById(R.id.main_screen_background)).setImageBitmap(bitmap);
            ((TextView) parentActivity.findViewById(R.id.main_screen_song_title)).setText(songName);
        }
        else if (SCREEN == SCREEN_PLAY){
            progressBar.setMax(mediaPlayer.getDuration());
            ((TextView) parentActivity.findViewById(R.id.player_song_title)).setText(songName);
            ((TextView) parentActivity.findViewById(R.id.player_song_album)).setText(albumName);
            ((TextView) parentActivity.findViewById(R.id.player_song_artist)).setText(artistName);

            Bitmap bitmap = albumArt;
            ((ImageView) parentActivity.findViewById(R.id.player_album_art)).setImageBitmap(bitmap);
            bitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() * 2 / 5, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
            ((ImageView) parentActivity.findViewById(R.id.player_screen_background)).setImageBitmap(bitmap);

            run.run();
        }
        updatePlayPauseIcon();
    }

    public static void updatePlayPauseIcon(){
        if (SCREEN == SCREEN_MAIN){
            if(isPlaying()) {
                ((ImageView) parentActivity.findViewById(R.id.main_screen_play_pause_button)).setImageResource(R.drawable.ic_pause);
                parentActivity.findViewById(R.id.main_screen_play_pause_button).setTag(Boolean.TRUE);
            }
            else {
                ((ImageView) parentActivity.findViewById(R.id.main_screen_play_pause_button)).setImageResource(R.drawable.ic_play);
                parentActivity.findViewById(R.id.main_screen_play_pause_button).setTag(Boolean.FALSE);
            }
        }

        else if (SCREEN == SCREEN_PLAY){
            if(isPlaying()) {
                ((ImageView) parentActivity.findViewById(R.id.player_play_pause_button)).setImageResource(R.drawable.ic_pause);
                parentActivity.findViewById(R.id.player_play_pause_button).setTag(Boolean.TRUE);
            }
            else {
                ((ImageView) parentActivity.findViewById(R.id.player_play_pause_button)).setImageResource(R.drawable.ic_play);
                parentActivity.findViewById(R.id.player_play_pause_button).setTag(Boolean.FALSE);
            }
        }
    }

    public static void play(){
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }

        if(isPlaying()){
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
            mediaPlayer.stop();
        }
    }
    public static void playNext(){
        stop();
        if(pos == songList.getCount()-1)
            pos = 0;
        else
            pos++;

        updateData();
        play();
        updateScreen();
    }
    public static void playPrevious(){
        stop();
        if(pos == 0)
            pos = songList.getCount()-1;
        else
            pos--;

        updateData();
        play();
        updateScreen();
    }

    public static boolean isListLoop() {
        return listLoop;
    }
}


class MediaCompletionListener implements  MediaPlayer.OnCompletionListener{

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mp.isLooping()) {
            PlayerControl.stop();
            PlayerControl.play();
        }
        else if(PlayerControl.isListLoop()){
            PlayerControl.stop();
            PlayerControl.playNext();
        }
        else{
            PlayerControl.stop();
            PlayerControl.playNext();
            PlayerControl.updatePlayPauseIcon();
        }
    }
}
