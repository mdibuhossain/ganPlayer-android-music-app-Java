package com.example.ganplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView titleView, mediaStartTime, mediaTotalTime;
    SeekBar seekbar;
    ImageView pausePlayBtn, previousBtn, nextBtn, musicArt;
    ArrayList<AudioModel> songsList;
    static AudioModel currentSong;

    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        titleView = findViewById(R.id.song_title);
        mediaStartTime = findViewById(R.id.media_time_start);
        mediaTotalTime = findViewById(R.id.media_total_time);
        seekbar = findViewById(R.id.seek_bar);
        pausePlayBtn = findViewById(R.id.media_play_pause);
        nextBtn = findViewById(R.id.media_next);
        previousBtn = findViewById(R.id.media_previous);
        musicArt = findViewById(R.id.media_art);
        titleView.setSelected(true);

        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        setResourcesWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekbar.setProgress(mediaPlayer.getCurrentPosition());
                    mediaStartTime.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));
                    if (mediaPlayer.isPlaying()) {
                        pausePlayBtn.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                    } else {
                        mediaPlayer.pause();
                        pausePlayBtn.setImageResource(R.drawable.baseline_play_circle_outline_24);
                    }
                    // auto next when current music ends
                    if (currentSong.getDuration().equalsIgnoreCase(String.valueOf(mediaPlayer.getCurrentPosition()))) {
                        nextSong();
                    }
                }
                new Handler().postDelayed(this, 100);
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    void setResourcesWithMusic() {
        if (MyMediaPlayer.currentIndex != MyMediaPlayer.previousIndex) {
            currentSong = songsList.get(MyMediaPlayer.currentIndex);
            MyMediaPlayer.previousIndex = MyMediaPlayer.currentIndex;
        }

        playSong();
        titleView.setText(currentSong.getTitle());
        mediaTotalTime.setText(convertToMMSS(currentSong.getDuration()));

        pausePlayBtn.setOnClickListener(v -> pausePlay());
        nextBtn.setOnClickListener(v -> nextSong());
        previousBtn.setOnClickListener(v -> previousSong());

    }

    private void pausePlay() {
        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
        else mediaPlayer.start();
    }

    private void playSong() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekbar.setProgress(mediaPlayer.getCurrentPosition());
            seekbar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void nextSong() {
        MyMediaPlayer.currentIndex = (MyMediaPlayer.currentIndex + 1) % songsList.size();
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void previousSong() {
        MyMediaPlayer.currentIndex--;
        if (MyMediaPlayer.currentIndex < 0) {
            MyMediaPlayer.currentIndex = MyMediaPlayer.currentIndex + songsList.size();
        }
        mediaPlayer.reset();
        setResourcesWithMusic();
    }


    @NonNull
    public static String convertToMMSS(String duration) {
        long millis = Long.parseLong(duration);
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1), TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

}