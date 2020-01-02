package com.wallaby.otaku.recyclerAdapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.wallaby.otaku.ExploreFirstLevel;
import com.wallaby.otaku.PermissionAndUpdateDataActivity;
import com.wallaby.otaku.R;
import com.wallaby.otaku.SDcardAccess.ExternalStorage;
import com.wallaby.otaku.internal_database.OtakuDatabase;
import com.wallaby.otaku.models.Anime;
import com.wallaby.otaku.ui.VideoPlayingActivity;

import java.io.File;
import java.util.ArrayList;

public class HomeAnimeAdapter extends RecyclerView.Adapter<HomeAnimeAdapter.MyViewHolder>  {

    private Context mContext;
    private ArrayList<Anime> allAnime;
    private OtakuDatabase otakuDatabase;

    public HomeAnimeAdapter(Context mContext) {
        this.mContext = mContext;
        ExternalStorage externalStorage = new ExternalStorage();
        this.allAnime = externalStorage.getAllAnime();
        otakuDatabase = new OtakuDatabase(mContext);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_anime, parent, false);

        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.AnimeTitle.setText(allAnime.get(position).getName());
        String episodeResumeDIsplay = otakuDatabase.getResumeAnimeEpisode(allAnime.get(position).getName()).split("/")[otakuDatabase.getResumeAnimeEpisode(allAnime.get(position).getName()).split("/").length-1];
        holder.currentEpisode.setText(mContext.getResources().getString(R.string.currentEpisode) + " " + episodeResumeDIsplay);

        File coverImg = new  File(allAnime.get(position).getCoverPath());
        Bitmap myBitmap = BitmapFactory.decodeFile(coverImg.getAbsolutePath());
        holder.Animecover.setImageBitmap(myBitmap);

        holder.Animeexplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ExploreFirstLevel.class);
                intent.putExtra("selection", "anime");
                intent.putExtra("selected_anime", allAnime.get(position).getName());
                mContext.startActivity(intent);
                ((PermissionAndUpdateDataActivity)mContext).finish();

            }
        });

        holder.Animeresume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoPlayingActivity.class);
                intent.putExtra("video_path", otakuDatabase.getResumeAnimeEpisode(allAnime.get(position).getName()));
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return allAnime.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{


        CardView cardViewAnime;
        ImageView Animecover;
        TextView AnimeTitle;
        TextView currentEpisode;
        Button Animeresume, Animeexplore;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewAnime = itemView.findViewById(R.id.cardviewHomeItemAnime);
            Animecover = itemView.findViewById(R.id.imageCoverAnime);
            AnimeTitle = itemView.findViewById(R.id.AnimeName);
            currentEpisode = itemView.findViewById(R.id.AnimecurrentChapter);
            Animeexplore = itemView.findViewById(R.id.AnimeexploreButton);
            Animeresume = itemView.findViewById(R.id.AnimeresumeButton);
        }
    }
}
