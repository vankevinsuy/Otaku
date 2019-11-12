package com.wallaby.otaku.recyclerAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wallaby.otaku.SDcardAccess.ExternalStorage;
import com.wallaby.otaku.models.Chapitre;
import com.wallaby.otaku.ui.scan.LectureActivity;

import java.util.ArrayList;

public class FirstLevelScanAdapter extends RecyclerView.Adapter<FirstLevelScanAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Chapitre> allAnimeChapter;
    private String selected_manga;

    public FirstLevelScanAdapter(Context mContext, String selected_manga) {
        this.mContext = mContext;
        this.selected_manga = selected_manga;
        ExternalStorage externalStorage = new ExternalStorage();
        allAnimeChapter = externalStorage.getMangaByName(selected_manga).getChapitreArrayList();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        Button v = new Button(mContext);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.buttonSelection.setText(Integer.toString(allAnimeChapter.get(position).getNumChapitre()));
        holder.buttonSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, LectureActivity.class);
                intent.putExtra("selection_chapter", holder.buttonSelection.getText().toString());
                intent.putExtra("selection_manga", selected_manga);
                intent.putExtra("single_or_continue", "single");

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allAnimeChapter.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        Button buttonSelection;

        public MyViewHolder(@NonNull Button itemView) {
            super(itemView);
            buttonSelection = itemView;
        }
    }
}
