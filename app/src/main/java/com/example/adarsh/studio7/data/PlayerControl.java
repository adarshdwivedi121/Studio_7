package com.example.adarsh.studio7.data;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.adarsh.studio7.CustomViews.SeekBar;
import com.example.adarsh.studio7.MainScreen;
import com.example.adarsh.studio7.R;

import java.util.Random;

/**
 * Created by adarsh on 31/05/2017.
 */

public class PlayerControl{
    public static final int SCREEN_MAIN = 0;
    public static final int SCREEN_PLAY = 1;

    public static final int REPEAT_OFF = 0;
    public static final int REPEAT_ALL = 1;
    public static final int REPEAT_ONE = 2;

    public static final String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST_ID
    };

    private static MediaPlayer mediaPlayer = null;
    private static Activity parentActivity = null;
    private static String music = null;
    private static SeekBar progressBar;
    private static Handler seekHandler = new Handler();

    private static int SCREEN = SCREEN_MAIN;
    private static int REPEAT_STATUS = REPEAT_OFF;

    private static Cursor songList;
    private static int pos = -1;
    public static boolean listLoop = false;

    private static Bitmap albumArt = null;
    private static String songName = null;
    private static String albumName = null;
    private static String artistName = null;
    private static boolean shuffle = false;

    public static void setPos(int pos, String song_id) {
        if (PlayerControl.pos == -1 || music.compareTo(song_id) != 0) {
            stop();
            PlayerControl.pos = pos;
            updateData();
            play();
        }
    }

    public static Cursor getSongList(){
        return songList;
    }

    private static Runnable run = () -> {
        if (SCREEN == SCREEN_PLAY) seekUpdate();
    };

    private static void seekUpdate() {
        progressBar.setProgress(mediaPlayer.getCurrentPosition());
        seekHandler.postDelayed(run, 500);
    }

    public static boolean hasSong() {
        return music != null;
    }

    public PlayerControl(Activity activity, int screen) {
        parentActivity = activity;
        SCREEN = screen;
        progressBar = (SeekBar) parentActivity.findViewById(R.id.progressBar);
    }

    public static void repeat(Boolean req) {
        if (mediaPlayer != null)
            mediaPlayer.setLooping(req);
    }

    public static boolean isPlaying() {
        return !(mediaPlayer == null || !mediaPlayer.isPlaying());
    }

    public static void updateSongList(String Query, String id){
        Cursor c = null;
        if (id ==null)
            c = parentActivity.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    PlayerControl.projection,
                    MediaStore.Audio.Media.TITLE + " NOT LIKE 'AUD%' AND " + MediaStore.Audio.Media.DURATION + ">=60000 COLLATE NOCASE",
                    null,
                    MediaStore.Audio.Media.TITLE);
        else if(id.compareTo("ALBUM") == 0)
            c = parentActivity.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    PlayerControl.projection,
                    "album_id IS " + Query,
                    null,
                    null);
        else if(id.compareTo("ARTIST") == 0)
            c = parentActivity.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    PlayerControl.projection,
                    "artist_id IS " + Query,
                    null,
                    null);
        if(songList != null && !songList.isClosed())    songList.close();
        songList = c;
    }

    private static void updateData() {
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
        Bitmap bitmap = BitmapFactory.decodeFile(c.getString(1));
        c.close();

        if (bitmap == null)
            bitmap = BitmapFactory.decodeResource(parentActivity.getResources(), R.drawable.default_music);

        albumArt = bitmap;

        music = songList.getString(0);
        mediaPlayer = MediaPlayer.create(parentActivity.getApplicationContext(), Uri.parse(songList.getString(5)));
        mediaPlayer.setOnCompletionListener(new MediaCompletionListener());
    }

    public static void updateScreen() {
        if (SCREEN == SCREEN_MAIN) {
            Bitmap bitmap = albumArt;
            ((ImageView) parentActivity.findViewById(R.id.main_screen_album_art)).setImageBitmap(bitmap);
            bitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() * 2 / 5, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
            ((ImageView) parentActivity.findViewById(R.id.main_screen_background)).setImageBitmap(bitmap);
            ((TextView) parentActivity.findViewById(R.id.main_screen_song_title)).setText(songName);
        } else if (SCREEN == SCREEN_PLAY) {
            progressBar.setMax(mediaPlayer.getDuration());

            final int[] x = new int[1];
            final int[] y = new int[1];

            Rect r= new Rect();
            progressBar.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        public void onGlobalLayout() {
                            //Remove the listener before proceeding
                            progressBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                            // measure your views here
                            progressBar.getGlobalVisibleRect(r);
                            x[0] = (r.left + r.right) / 2;
                            y[0] = (r.top + r.bottom) / 2;
                        }
                    }
            );

            progressBar.setOnTouchListener((v, event) -> {
                int x1 = (int) event.getRawX();
                int y1 = (int) event.getRawY();

                double hypo = Math.sqrt(Math.pow(x1-x[0], 2) + Math.pow(y1-y[0], 2));
                double rad = r.right - x[0];
                if(rad - hypo >= -rad/5 && rad - hypo < rad/4) {
                    int hdist = x1 - x[0];
                    double angle = 0;
                    if (x1 >= x[0] && y1 <= y[0])
                        angle = Math.asin(hdist / hypo);
                    else if (x1 >= x[0] && y1 >= y[0])
                        angle = Math.PI - Math.asin(hdist / hypo);
                    else if (x1 <= x[0] && y1 >= y[0])
                        angle = Math.PI + Math.asin(-hdist / hypo);
                    else if (x1 <= x[0] && y1 <= y[0])
                        angle = 2 * Math.PI - Math.asin(-hdist / hypo);
                    int progress = (int) ((angle / (2 * Math.PI)) * mediaPlayer.getDuration());
                    progressBar.setProgress(progress);
                    mediaPlayer.seekTo(progress);
                }
                return true;
            });
            ((TextView) parentActivity.findViewById(R.id.player_song_title)).setText(songName);
            ((TextView) parentActivity.findViewById(R.id.player_song_album)).setText(albumName);
            ((TextView) parentActivity.findViewById(R.id.player_song_artist)).setText(artistName);

            Bitmap bitmap = albumArt;
            ((ImageView) parentActivity.findViewById(R.id.player_album_art)).setImageBitmap(bitmap);
            bitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() * 2 / 5, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
            ((ImageView) parentActivity.findViewById(R.id.player_screen_background)).setImageBitmap(bitmap);
            run.run();
        }
        updateNotification_Icons();
    }

    public static void updateNotification_Icons() {
        MainScreen.setUpNotification(parentActivity.getApplicationContext());
        if (SCREEN == SCREEN_MAIN) {
            if (isPlaying()) {
                ((ImageView) parentActivity.findViewById(R.id.main_screen_play_pause_button)).setImageResource(R.drawable.ic_pause);
                parentActivity.findViewById(R.id.main_screen_play_pause_button).setTag(Boolean.TRUE);
            } else {
                ((ImageView) parentActivity.findViewById(R.id.main_screen_play_pause_button)).setImageResource(R.drawable.ic_play);
                parentActivity.findViewById(R.id.main_screen_play_pause_button).setTag(Boolean.FALSE);
            }
        } else if (SCREEN == SCREEN_PLAY) {
            if (isPlaying()) {
                ((ImageView) parentActivity.findViewById(R.id.player_play_pause_button)).setImageResource(R.drawable.ic_pause);
                parentActivity.findViewById(R.id.player_play_pause_button).setTag(Boolean.TRUE);
            } else {
                ((ImageView) parentActivity.findViewById(R.id.player_play_pause_button)).setImageResource(R.drawable.ic_play);
                parentActivity.findViewById(R.id.player_play_pause_button).setTag(Boolean.FALSE);
            }
        }
    }

    public static void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        if (isPlaying()) {
            run.run();
        }
    }

    public static void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public static void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
        }
    }

    public static void playNext(boolean stat, int p) {
        stop();

        if(shuffleStatus()){
            randomPos();
        }
        else {
            if (pos == songList.getCount() - 1)
                pos = 0;
            else
                pos++;
        }

        updateData();
        if(stat || p < songList.getCount() - 1)    play();
        updateScreen();
    }

    public static void playPrevious() {
        stop();
        if(shuffleStatus()){
            randomPos();
        }
        else{
            if (pos == 0)
                pos = songList.getCount() - 1;
            else
                pos--;
        }

        updateData();
        play();
        updateScreen();
    }

    public static void setShuffle(boolean b) {
        shuffle = b;
    }

    public static boolean shuffleStatus(){
        return shuffle;
    }

    public static void randomPos() {
        Random r = new Random();
        int p = r.nextInt(songList.getCount());
        while(p == pos)
            p = r.nextInt(songList.getCount());
        pos = p;
        updateData();
    }

    public static void setListLoop(boolean listLoop) {
        PlayerControl.listLoop = listLoop;
    }

    public static int repeatStatus() {
        return REPEAT_STATUS;
    }

    public static void setRepeatStatus(int state){
        REPEAT_STATUS = state;
    }

    public static int getPos() {
        return pos;
    }

    public static void lastSongList(String prev_list, int p) {

        if(!prev_list.isEmpty()){
            Cursor c = parentActivity.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    PlayerControl.projection,
                    "_id IN " + prev_list,
                    null,
                    MediaStore.Audio.Media.TITLE);
            if(songList != null && !songList.isClosed())    songList.close();
            songList = c;
            pos = p;
            Log.e("Query", prev_list);
            Log.e("Last Played List", "Length : " + String.valueOf(songList.getCount()) + " Cols : " + String.valueOf(songList.getColumnCount()));
            updateData();
        }
    }

    public static void setUpNotification(RemoteViews view, RemoteViews bigView) {
        view.setImageViewBitmap(R.id.notification_album_art, albumArt);
        view.setTextViewText(R.id.notification_song_title, songName);

        bigView.setImageViewBitmap(R.id.large_notification_album_art, albumArt);
        bigView.setTextViewText(R.id.large_notification_title, songName);
        bigView.setTextViewText(R.id.large_notification_artist, artistName);
        bigView.setTextViewText(R.id.large_notification_album, albumName);

        if(isPlaying()) {
            view.setImageViewResource(R.id.notification_play_pause_button, R.drawable.ic_pause);
            bigView.setImageViewResource(R.id.large_notification_play_pause_button, R.drawable.ic_pause);
        }
        else {
            view.setImageViewResource(R.id.notification_play_pause_button, R.drawable.ic_play);
            bigView.setImageViewResource(R.id.large_notification_play_pause_button, R.drawable.ic_play);
        }
    }

    public static void playSong(String id) {
        PlayerControl.stop();
        Cursor c = parentActivity.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                PlayerControl.projection,
                "_id IS " + id,
                null,
                null);

        if(songList != null && !songList.isClosed())    songList.close();
            songList = c;
        pos = 0;
        updateData();
        play();
        updateScreen();
    }
}

class MediaCompletionListener implements MediaPlayer.OnCompletionListener {

    @Override
    public void onCompletion(MediaPlayer mp) {
        PlayerControl.stop();
        if(PlayerControl.shuffleStatus()) {
            PlayerControl.randomPos();
            PlayerControl.play();
        }
        else{
            switch(PlayerControl.repeatStatus()){
                case PlayerControl.REPEAT_ALL:
                    PlayerControl.playNext(true, PlayerControl.getPos());
                    break;
                case PlayerControl.REPEAT_ONE:
                    PlayerControl.play();
                    break;
                case PlayerControl.REPEAT_OFF:
                    PlayerControl.playNext(false, PlayerControl.getPos());
            }
        }
        PlayerControl.updateNotification_Icons();
    }
}