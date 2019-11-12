package com.wallaby.otaku.ui.anime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wallaby.otaku.MainActivity;
import com.wallaby.otaku.R;
import com.wallaby.otaku.SDcardAccess.ExternalStorage;
import com.wallaby.otaku.models.Anime;
import com.wallaby.otaku.models.Saison;
import com.wallaby.otaku.recyclerAdapters.FirstLevelScanAdapter;
import com.wallaby.otaku.recyclerAdapters.SaisonExplorerAdapter;

public class SaisonExplorerActivity extends AppCompatActivity {

    private Saison selected_saison;
    private Anime selected_anime;

    private Button goHomeButton;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saison_explorer);

        ExternalStorage externalStorage = new ExternalStorage();

        selected_anime = externalStorage.getAnimeByName(getIntent().getStringExtra("selected_anime"));
        for(Saison saison : selected_anime.getListSaison()){
            if(saison.getNameSaison().equals(getIntent().getStringExtra("selected_saison"))){
                selected_saison = saison;
            }
        }

        goHomeButton = findViewById(R.id.goHomeButton);
        goHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.SaisonExplorerRecyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getApplicationContext(), 4);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new SaisonExplorerAdapter(getApplicationContext(), selected_saison);
        recyclerView.setAdapter(mAdapter);
    }
}
