package com.wallaby.otaku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wallaby.otaku.SDcardAccess.ExternalStorage;
import com.wallaby.otaku.models.Anime;
import com.wallaby.otaku.recyclerAdapters.FirstLevelAnimeAdapter;
import com.wallaby.otaku.recyclerAdapters.FirstLevelScanAdapter;

public class ExploreFirstLevel extends AppCompatActivity {

    private Button goHomeButton;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        goHomeButton = findViewById(R.id.goHomeButton);
        goHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        switch (getIntent().getStringExtra("selection")){
            //voir les chapitres
            case "scan":
                String selected_manga = getIntent().getStringExtra("selected_manga");
                setTitle(selected_manga);

                recyclerView = (RecyclerView) findViewById(R.id.firstLevelEpisodeOrChapterRecyclerview);
                recyclerView.setHasFixedSize(true);
                layoutManager = new GridLayoutManager(getApplicationContext(), 3);
                recyclerView.setLayoutManager(layoutManager);

                mAdapter = new FirstLevelScanAdapter(getApplicationContext(), selected_manga);
                recyclerView.setAdapter(mAdapter);

                break;


            case "anime":
                String selected_anime = getIntent().getStringExtra("selected_anime");
                setTitle(selected_anime);

                ExternalStorage externalStorage = new ExternalStorage();
                Anime anime = externalStorage.getAnimeByName(selected_anime);

                //si on a que des épisodes
                if(anime.getListEpisodePath().size() > 0){
                    recyclerView = (RecyclerView) findViewById(R.id.firstLevelEpisodeOrChapterRecyclerview);
                    recyclerView.setHasFixedSize(true);
                    layoutManager = new GridLayoutManager(getApplicationContext(), 4);
                    recyclerView.setLayoutManager(layoutManager);

                    mAdapter = new FirstLevelAnimeAdapter(getApplicationContext(), selected_anime);
                    recyclerView.setAdapter(mAdapter);
                }

                // si on a des saisons
                if (anime.getListSaison().size() > 0){
                    recyclerView = (RecyclerView) findViewById(R.id.firstLevelEpisodeOrChapterRecyclerview);
                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);

                    mAdapter = new FirstLevelAnimeAdapter(getApplicationContext(), selected_anime);
                    recyclerView.setAdapter(mAdapter);
                }

                break;
        }
    }

    //méthode de retour au menu principal
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        finish();

        startActivity(intent);
    }
}
