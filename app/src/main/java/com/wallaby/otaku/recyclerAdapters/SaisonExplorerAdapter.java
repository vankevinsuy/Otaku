package com.wallaby.otaku.recyclerAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wallaby.otaku.internal_database.OtakuDatabase;
import com.wallaby.otaku.models.Saison;
import com.wallaby.otaku.ui.VideoPlayingActivity;

public class SaisonExplorerAdapter extends RecyclerView.Adapter<SaisonExplorerAdapter.MyViewHolder> {
    private Context mContext;
    private Saison selectedSaison;
    private OtakuDatabase otakuDatabase;


    public SaisonExplorerAdapter(Context mContext, Saison selectedSaison) {
        this.mContext = mContext;
        this.selectedSaison = selectedSaison;
        otakuDatabase = new OtakuDatabase(mContext);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        Button button = null;
        MyViewHolder vh = null;

        button = new Button(mContext);
        vh = new MyViewHolder(button);


        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        String episodeName = selectedSaison.getListEpisodePath().get(position).split("/")[selectedSaison.getListEpisodePath().get(position).split("/").length-1];

        holder.buttonSelectionEpisode.setText(episodeName);
        holder.buttonSelectionEpisode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoPlayingActivity.class);
                intent.putExtra("video_path", selectedSaison.getListEpisodePath().get(position));
                otakuDatabase.updateResumeAnime(selectedSaison.getFromAnime(), selectedSaison.getListEpisodePath().get(position));

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedSaison.getListEpisodePath().size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        Button buttonSelectionEpisode;

        public MyViewHolder(@NonNull Button itemView) {
            super(itemView);
            buttonSelectionEpisode = itemView;
        }

    }
}
