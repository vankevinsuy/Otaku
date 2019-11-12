package com.wallaby.otaku.ui.movie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wallaby.otaku.R;
import com.wallaby.otaku.recyclerAdapters.FirstLevelMovieAdapter;
import com.wallaby.otaku.recyclerAdapters.HomeAnimeAdapter;

public class MovieFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movie, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.movie_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new FirstLevelMovieAdapter(getContext());
        recyclerView.setAdapter(mAdapter);


        return root;
    }

}
