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
import com.wallaby.otaku.MainActivity;
import com.wallaby.otaku.R;
import com.wallaby.otaku.SDcardAccess.ExternalStorage;
import com.wallaby.otaku.internal_database.OtakuDatabase;
import com.wallaby.otaku.models.Manga;
import com.wallaby.otaku.ui.scan.LectureActivity;

import java.io.File;
import java.util.ArrayList;

public class HomeScanAdapter extends RecyclerView.Adapter<HomeScanAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Manga> allManga;


    public HomeScanAdapter(Context context) {
        mContext = context;
        ExternalStorage externalStorage = new ExternalStorage();
        this.allManga = externalStorage.getAllManga();
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_scan, parent, false);

        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        OtakuDatabase otakuDatabase = new OtakuDatabase(mContext);

        holder.book_title.setText(allManga.get(position).getName());
        holder.lastChapter.setText(mContext.getResources().getString(R.string.lastChapitre)+ " " + Integer.toString(allManga.get(position).getLastChapter()));

        holder.currentChapter.setText(mContext.getResources().getString(R.string.currentChapitre) + " " + Integer.toString(otakuDatabase.getResumeChapterByManga(allManga.get(position).getName())));

        File coverImg = new  File(allManga.get(position).getImageCoverPath());
        Bitmap myBitmap = BitmapFactory.decodeFile(coverImg.getAbsolutePath());
        holder.img_book_cover.setImageBitmap(myBitmap);

        holder.Bexplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ExploreFirstLevel.class);
                intent.putExtra("selection", "scan");
                intent.putExtra("selected_manga", allManga.get(position).getName());
                mContext.startActivity(intent);
                ((MainActivity)mContext).finish();

            }
        });

        holder.Bresume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LectureActivity.class);
                intent.putExtra("selection_manga", holder.book_title.getText().toString());
                intent.putExtra("single_or_continue", "continue");

                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return  allManga.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardViewScan;
        ImageView img_book_cover;
        TextView book_title;
        TextView lastChapter;
        TextView currentChapter;
        Button Bresume, Bexplore;


        public MyViewHolder(View itemView) {
            super(itemView);

            cardViewScan = itemView.findViewById(R.id.cardviewHomeItemScan);
            img_book_cover = itemView.findViewById(R.id.imageCover);
            book_title = itemView.findViewById(R.id.mangaName);
            lastChapter = itemView.findViewById(R.id.mangaLastChapter);
            currentChapter = itemView.findViewById(R.id.currentChapter);
            Bresume = itemView.findViewById(R.id.resumeButton);
            Bexplore = itemView.findViewById(R.id.exploreButton);

        }
    }
}
