package com.example.ganplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerview;
    TextView noMusicText;
    SearchView searchView;
    ArrayList<AudioModel> songList = new ArrayList<>();
    ArrayList<AudioModel> filterSongList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerview = findViewById(R.id.recycler_view);
        noMusicText = findViewById(R.id.no_songs_text);
        searchView = findViewById(R.id.searchView);

        if (!checkPermission()) {
            requestPermission();
            return;
        }

        String[] projection = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION};
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);
        while (cursor.moveToNext()) {
            AudioModel songData = new AudioModel(cursor.getString(0), cursor.getString(1), cursor.getString(2));
            if (new File(songData.getPath()).exists()) songList.add(songData);
        }
        MusicAdapter musicAdapter = new MusicAdapter(this.songList, getApplicationContext());
        if (songList.size() == 0) {
            noMusicText.setVisibility(View.VISIBLE);
        } else {
//            Log.d("myTag", "Songs size"+ songList.size());
            recyclerview.setLayoutManager(new LinearLayoutManager(this));
            recyclerview.setAdapter(musicAdapter);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                filterSongList = new ArrayList<>();
                for (AudioModel item : songList) {
                    if (newText.isEmpty()) {
                        filterSongList.add(item);
                    } else if (item.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                        filterSongList.add(item);
                    }
                }
                musicAdapter.addAll(filterSongList);
                musicAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSongList = new ArrayList<>();
                for (AudioModel item : songList) {
                    if (newText.isEmpty()) {
                        filterSongList.add(item);
                    } else if (item.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                        filterSongList.add(item);
                    }
                }
                musicAdapter.addAll(filterSongList);
                musicAdapter.notifyDataSetChanged();
                return true;
            }
        });

    }

    boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))
            Toast.makeText(MainActivity.this, "READ PERMISSION IS REQUIRED, PLEASE ALLOW FROM SETTINGS.", Toast.LENGTH_LONG).show();
        else
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
    }

}