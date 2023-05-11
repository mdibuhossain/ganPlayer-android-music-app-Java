package com.example.ganplayer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    ArrayList<AudioModel> songsList;
    Context context;

    public MusicAdapter(ArrayList<AudioModel> songsList, Context context) {
        this.songsList = songsList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AudioModel songData = songsList.get(position);
        holder.musicTitle.setText(songData.getTitle());
        holder.itemView.setOnClickListener(v -> {
            MyMediaPlayer.getInstance().reset();
            MyMediaPlayer.currentIndex = position;
            Intent intent = new Intent(context, MusicPlayerActivity.class);
            intent.putExtra("LIST", songsList);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView musicTitle;
        ImageView musicIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            musicTitle = itemView.findViewById(R.id.music_title_view);
            musicIcon = itemView.findViewById(R.id.music_icon_view);
        }
    }
}
