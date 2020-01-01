package com.wallaby.otaku.recyclerAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wallaby.otaku.ExploreFirstLevel;
import com.wallaby.otaku.R;
import com.wallaby.otaku.SDcardAccess.ExternalStorage;
import com.wallaby.otaku.internal_database.OtakuDatabase;
import com.wallaby.otaku.models.Anime;
import com.wallaby.otaku.ui.VideoPlayingActivity;
import com.wallaby.otaku.ui.anime.SaisonExplorerActivity;

public class FirstLevelAnimeAdapter extends RecyclerView.Adapter<FirstLevelAnimeAdapter.MyViewHolder> {
    private Context mContext;
    private String selected_anime;
    private Anime anime;
    private OtakuDatabase otakuDatabase;


    public FirstLevelAnimeAdapter(Context mContext, String selected_anime) {
        this.mContext = mContext;
        this.selected_anime = selected_anime;

        ExternalStorage externalStorage = new ExternalStorage();
        this.anime = externalStorage.getAnimeByName(selected_anime);
        otakuDatabase = new OtakuDatabase(mContext);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        Button button = null;
        MyViewHolder vh = null;
        Button saisonButtonLayout = null;

        if(anime.getListEpisodePath().size() > 0){
            button = new Button(mContext);
            vh = new MyViewHolder(button);
        }

        if(anime.getListSaison().size() > 0){
            saisonButtonLayout = (Button) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_first_level_saison, parent, false);
            vh = new MyViewHolder(saisonButtonLayout);
        }


        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        if(anime.getListEpisodePath().size() > 0){
            holder.buttonSelectionEpisode.setText(anime.getListEpisode().get(position).toString());
            holder.buttonSelectionEpisode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, VideoPlayingActivity.class);
                    intent.putExtra("video_path", anime.getListEpisodePath().get(position));


                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    otakuDatabase.updateResumeAnime(selected_anime, anime.getListEpisodePath().get(position));
                    mContext.startActivity(intent);
                }
            });
        }

        if(anime.getListSaison().size() > 0){
            holder.buttonSelectionSaison.setText(anime.getListSaison().get(position).getNameSaison());
            holder.buttonSelectionEpisode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SaisonExplorerActivity.class);
                    intent.putExtra("selected_saison", anime.getListSaison().get(position).getNameSaison());
                    intent.putExtra("selected_anime", anime.getName());

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int res = 0;
        if(anime.getListEpisodePath().size() > 0){
            res = anime.getListEpisodePath().size();
        }
        if(anime.getListSaison().size() > 0){
            res = anime.getListSaison().size();
        }
        return res;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        Button buttonSelectionEpisode;
        Button buttonSelectionSaison;

        public MyViewHolder(@NonNull Button itemView) {
            super(itemView);
            buttonSelectionEpisode = itemView;
            buttonSelectionSaison = itemView;
        }

    }
}
