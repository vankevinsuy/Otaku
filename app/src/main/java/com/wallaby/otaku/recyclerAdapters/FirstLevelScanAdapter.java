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
import com.wallaby.otaku.R;
import com.wallaby.otaku.SDcardAccess.ExternalStorage;
import com.wallaby.otaku.models.Chapitre;
import com.wallaby.otaku.ui.scan.LectureActivity;

import java.io.File;
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
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scan_chapter_selection, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.num_chapter.setText(Integer.toString(allAnimeChapter.get(position).getNumChapitre()));
        File coverImg = new  File(allAnimeChapter.get(position).getFirstPagePath());
        final Bitmap myBitmap = BitmapFactory.decodeFile(coverImg.getAbsolutePath());
        holder.firstPageImg.setImageBitmap(myBitmap);

        holder.chapter_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, LectureActivity.class);
                intent.putExtra("selection_chapter", holder.num_chapter.getText());
                intent.putExtra("selection_manga", selected_manga);
                intent.putExtra("single_or_continue", "single");

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return allAnimeChapter.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        CardView chapter_cardview;
        ImageView firstPageImg;
        TextView num_chapter;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            chapter_cardview = itemView.findViewById(R.id.chapter_cardview);
            firstPageImg = itemView.findViewById(R.id.firstPageImg);
            num_chapter = itemView.findViewById(R.id.num_chapter);

        }
    }
}
