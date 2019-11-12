package com.wallaby.otaku.recyclerAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.wallaby.otaku.R;
import com.wallaby.otaku.SDcardAccess.ExternalStorage;
import com.wallaby.otaku.models.Movie;
import com.wallaby.otaku.ui.VideoPlayingActivity;

import java.util.ArrayList;

public class FirstLevelMovieAdapter extends RecyclerView.Adapter<FirstLevelMovieAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Movie> allMovie;

    public FirstLevelMovieAdapter(Context mContext) {
        this.mContext = mContext;
        ExternalStorage externalStorage = new ExternalStorage();
        this.allMovie = externalStorage.getAllMovies();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_movie, parent, false);

        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.MovieTitle.setText(allMovie.get(position).getName());
        holder.MoviePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoPlayingActivity.class);
                intent.putExtra("video_path", allMovie.get(position).getMoviePath());

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allMovie.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{


        CardView cardviewHomeItemMovie;
        TextView MovieTitle;
        Button MoviePlay;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardviewHomeItemMovie = itemView.findViewById(R.id.cardviewHomeItemMovie);
            MovieTitle = itemView.findViewById(R.id.MovieName);
            MoviePlay = itemView.findViewById(R.id.MoviePlayButton);
        }
    }


}
